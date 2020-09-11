package com.google.step.coffee.tasks;

import com.google.api.services.calendar.model.Event;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.DateRange;
import com.google.step.coffee.entity.TimeSlot;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet triggered as cron job to match current chat requests together. Triggered periodically by
 * fetch.
 */
@WebServlet("/api/tasks/request-matching")
public class RequestMatcher extends HttpServlet {

  // Number of hours between each attempt at request matching triggered by cron job
  private static final long MATCHING_FREQUENCY = 6;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    RequestStore requestStore = new RequestStore();

    List<ChatRequest> requestList = requestStore.getUnmatchedRequests();

    matchRequests(requestList, requestStore);
  }

  /**
   * Take current list of chat requests and store any matched requests into datastore.
   *
   * @param requestList list of currently un-matched chat requests from datastore.
   * @param requestStore interface to datastore to handle requests.
   */
  private void matchRequests(List<ChatRequest> requestList, RequestStore requestStore) {
    // Sort so that requests that need to be matched sooner are prioritised
    requestList.sort((req1, req2) -> {
      Date req1End = req1.getLastEndDate();
      Date req2End = req2.getLastEndDate();

      return req1End.compareTo(req2End);
    });

    removeExpiredRequests(requestList, requestStore);

    Set<ChatRequest> matched = new HashSet<>();
    Map<String, List<ChatRequest>> tagMap = buildTagMap(requestList);

    matchTaggedRequests(requestList, requestStore, matched, tagMap);
    requestList.removeAll(matched);

    matchRandomRequests(requestList, requestStore, matched, tagMap);
  }

  private void matchRandomRequests(List<ChatRequest> requestList,
      RequestStore requestStore, Set<ChatRequest> matched,
      Map<String, List<ChatRequest>> tagMap) {
    List<ChatRequest> randomRequests = tagMap.getOrDefault("Random", new ArrayList<>());
    requestList.removeAll(randomRequests);

    List<ChatRequest> tryMatchRandomReqs = requestList.stream()
        .filter(req -> req.getLastEndDate().before(weekFromNow()))
        .collect(
        Collectors.toList());

    randomRequests.addAll(tryMatchRandomReqs);

    // Sort to prioritise sooner requests
    randomRequests.sort((req1, req2) -> {
      Date req1End = req1.getLastEndDate();
      Date req2End = req2.getLastEndDate();

      return req1End.compareTo(req2End);
    });

    // Iterate through copy of requests to enable modification
    for (ChatRequest request : new ArrayList<>(randomRequests)) {
      if (!matched.contains(request)) {
        List<ChatRequest> compatibleReqs = findMaxSizeRequests(request, randomRequests);

        if (compatibleReqs.size() >= (request.getMinPeople() + 1)) {
          TimeSlot meetingSlot = findSharedTimeSlot(compatibleReqs);

          if (!meetingSlot.isEmpty()) {
            createMatching(requestStore, compatibleReqs, meetingSlot);

            matched.addAll(compatibleReqs);
            randomRequests.removeAll(compatibleReqs);
          }
        }
      }
    }
  }

  private void matchTaggedRequests(List<ChatRequest> requestList, RequestStore requestStore,
      Set<ChatRequest> matched, Map<String, List<ChatRequest>> tagMap) {
    for (ChatRequest request : requestList) {
      if (!matched.contains(request)) {
        for (String tag : request.getTags()) {
          List<ChatRequest> sameTagReqs = tagMap.get(tag);

          if (request.getMinPeople() <= sameTagReqs.size() - 1) {
            List<ChatRequest> compatibleReqs = findMaxSizeRequests(request, sameTagReqs);

            if (compatibleReqs.size() >= (request.getMinPeople() + 1)) {
              TimeSlot meetingSlot = findSharedTimeSlot(compatibleReqs);

              if (!meetingSlot.isEmpty()) {
                createMatching(requestStore, compatibleReqs, meetingSlot);

                matched.addAll(compatibleReqs);
                compatibleReqs.forEach(req -> removeFromTagMap(tagMap, req));
              }
            }
          }
        }
      }
    }
  }

  private List<ChatRequest> findMaxSizeRequests(ChatRequest request, List<ChatRequest> tagReqs) {
    int groupSize = Math.min(request.getMaxPeople() + 1, tagReqs.size());
    boolean compatible = false;
    List<ChatRequest> compatibleReqs;

    do {
      // Lambdas cannot use non-final variables
      int attemptGroupSize = groupSize;
      compatibleReqs = tagReqs.stream()
          .filter(req -> groupSizeInRange(attemptGroupSize, req))
          .filter(req -> request.getRequestId() != req.getRequestId())
          .limit(groupSize - 1)
          .collect(Collectors.toList());

      if (compatibleReqs.size() == groupSize - 1) {
        compatible = true;
        compatibleReqs.add(request);
      } else {
        groupSize--;
      }

    } while (!compatible && request.getMinPeople() < groupSize);

    return compatibleReqs;
  }

  Map<String, List<ChatRequest>> buildTagMap(List<ChatRequest> requestList) {
    Map<String, List<ChatRequest>> tagMap = new HashMap<>();

    for (ChatRequest request : requestList) {
      if (request.getTags().isEmpty()) {
        List<ChatRequest> randomReqs = tagMap.getOrDefault("Random", new ArrayList<>());

        randomReqs.add(request);
        tagMap.put("Random", randomReqs);
      } else {
        for (String tag : request.getTags()) {
          List<ChatRequest> tagReqs = tagMap.getOrDefault(tag, new ArrayList<>());

          tagReqs.add(request);
          tagMap.put(tag, tagReqs);
        }
      }
    }

    return tagMap;
  }

  private void removeFromTagMap(Map<String, List<ChatRequest>> tagMap, ChatRequest request) {
    if (request.getTags().isEmpty()) {
      tagMap.get("Random").remove(request);
    } else {
      for (String tag : request.getTags()) {
        tagMap.get(tag).remove(request);
      }
    }
  }

  private Date weekFromNow() {
    return Date.from(Instant.now().plus(Duration.ofDays(7)));
  }

  private boolean groupSizeInRange(int groupSize, ChatRequest request) {
    return request.getMinPeople() <= (groupSize - 1) && (groupSize - 1) <= request.getMaxPeople();
  }

  private void removeExpiredRequests(List<ChatRequest> requestList, RequestStore requestStore) {
    for (ChatRequest request : requestList) {
      List<DateRange> dateRanges = request.getDateRanges();
      Date lastEndDate = dateRanges.get(dateRanges.size() - 1).getEnd();

      if (lastEndDate.before(Date.from(Instant.now().plus(Duration.ofHours(MATCHING_FREQUENCY))))) {
        requestStore.expiredRequest(request);
      }
    }
  }

  private void createMatching(RequestStore requestStore, List<ChatRequest> requests,
      TimeSlot slot) {
    List<String> commonTags = requests.stream()
        .map(ChatRequest::getTags).map(HashSet::new)
        .reduce((tags1, tags2) -> {
          tags1.retainAll(tags2);
          return tags1;
        })
        .map(ArrayList::new).get();

    List<String> participants = requests.stream()
        .map(ChatRequest::getUserId)
        .collect(Collectors.toList());

    addMatchingRequests(requestStore, participants, slot, commonTags, requests);

    requestStore.removeChatRequests(requests);
  }

  private void addMatchingRequests(RequestStore requestStore, List<String> participantIds,
      TimeSlot meetingSlot, List<String> commonTags, List<ChatRequest> reqs) {
    Event event = CalendarUtils.createEvent(meetingSlot, participantIds, commonTags);

    for (ChatRequest req : reqs) {
      requestStore.addMatchedRequest(req, meetingSlot, participantIds, commonTags);
      CalendarUtils.addEvent(req.getUserId(), event);
    }
  }

  /**
   * Given matched requests, find availability for all users in the selected date ranges.
   */
  private TimeSlot findSharedTimeSlot(List<ChatRequest> reqs) {
    AvailabilityScheduler scheduler = new AvailabilityScheduler(reqs);

    Duration minDuration = reqs.stream()
        .map(ChatRequest::getDuration)
        .min(Duration::compareTo)
        .orElse(Duration.ofMinutes(15));

     List<DateRange> rangeOptions = scheduler.findAvailableRanges(minDuration);

     if (rangeOptions.isEmpty()) {
       return TimeSlot.EMPTY;
     } else {
       return new TimeSlot(rangeOptions.get(0).getStart(), minDuration);
     }
  }
}
