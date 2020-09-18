package com.google.step.coffee.tasks;

import com.google.api.services.calendar.model.Event;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.DateRange;
import com.google.step.coffee.entity.TimeSlot;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Matches ChatRequests together accounting for group sizes, date ranges and matching tags. Attempts
 * to find available TimeSlot using availability from CalendarUtils.
 */
public class RequestMatcher {

  // Number of hours between each attempt at request matching triggered by cron job
  private static final long MATCHING_FREQUENCY = 6;

  /**
   * Take current list of chat requests and store any matched requests into datastore.
   *
   * @param requestList  list of currently un-matched chat requests from datastore.
   * @param requestStore interface to datastore to handle requests.
   */
  void matchRequests(List<ChatRequest> requestList, RequestStore requestStore) {
    // Sort so that requests that need to be matched sooner are prioritised
    requestList.sort((req1, req2) -> {
      Date req1End = req1.getLastEndDate();
      Date req2End = req2.getLastEndDate();

      return req1End.compareTo(req2End);
    });

    removeExpiredRequests(requestList, requestStore);

    Set<ChatRequest> matched = new HashSet<>();
    Map<String, List<ChatRequest>> tagMap = buildTagMap(requestList);
    Map<ChatRequest, Set<ChatRequest>> rangeIntersections = buildIntersectionMap(requestList,
        tagMap);

    matchTaggedRequests(requestList, requestStore, matched, tagMap, rangeIntersections);
    requestList.removeAll(matched);

    matchRandomRequests(requestList, requestStore, matched, tagMap, rangeIntersections);

    requestList.removeAll(matched);
    removeWillExpireRequests(requestList, requestStore);
  }

  /**
   * Builds a mapping of which requests have overlapping date ranges with other requests which have
   * matching tags.
   *
   * @param requestList Collection of all ChatRequests in this round of matching.
   * @param tagMap      Mapping of which requests share the same tags.
   * @return Mapping of a ChatRequest to a set of matching-tag ChatRequests that also overlap in
   * date ranges.
   */
  Map<ChatRequest, Set<ChatRequest>> buildIntersectionMap(Collection<ChatRequest> requestList,
      Map<String, List<ChatRequest>> tagMap) {
    AvailabilityScheduler scheduler = new AvailabilityScheduler();
    Map<ChatRequest, Set<ChatRequest>> intersectMap = new HashMap<>();

    for (ChatRequest request : requestList) {
      if (request.getTags().isEmpty()) {
        addIntersections(request, "Random", tagMap, scheduler, intersectMap);
      } else {
        for (String tag : request.getTags()) {
          addIntersections(request, tag, tagMap, scheduler, intersectMap);
        }
      }
    }

    return intersectMap;
  }

  /**
   * Creates mapping from tags in given requests, to all requests in given list which share the same
   * tag.
   *
   * @param requestList List of ChatRequests to be grouped by tag.
   * @return Mapping of tag's string to list of ChatRequests with matching tag.
   */
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

  /**
   * Removes a single request from the tag mapping, so that the request is removed from all mappings
   * of tags to list of matching requests.
   */
  void removeFromTagMap(Map<String, List<ChatRequest>> tagMap, ChatRequest request) {
    if (request.getTags().isEmpty()) {
      tagMap.get("Random").remove(request);
    } else {
      for (String tag : request.getTags()) {
        tagMap.get(tag).remove(request);
      }
    }
  }

  /**
   * Tests whether the proposed group size is compatible with the given request.
   */
  boolean groupSizeInRange(int groupSize, ChatRequest request) {
    return request.getMinPeople() <= (groupSize - 1) && (groupSize - 1) <= request.getMaxPeople();
  }

  /**
   * Creates calendar events and datastore entries for matched requests and their details
   * Package-private for testing purposes, not intended for direct usage but can be used.
   *
   * @param requestStore   Instance of RequestStore providing access to entities in datastore.
   * @param participantIds List of Strings of user Ids for participants involved in this matching.
   * @param meetingSlot    Time slot found to be available for participants.
   * @param commonTags     List of Strings for the commonTags.
   * @param reqs           ChatRequests in this matching.
   */
  void addMatchingRequests(RequestStore requestStore, List<String> participantIds,
      TimeSlot meetingSlot, List<String> commonTags, Collection<ChatRequest> reqs) {
    Event event = CalendarUtils.createEvent(meetingSlot, participantIds, commonTags);

    for (ChatRequest req : reqs) {
      requestStore.addMatchedRequest(req, meetingSlot, participantIds, commonTags);
      CalendarUtils.addEvent(req.getUserId(), event);
    }
  }

  /**
   * Finds ChatRequests with matching tag to given request and adds any ChatRequests which also
   * overlap in DateRanges to the intersection mapping.
   *
   * @param request      Given request to check overlapping DateRanges with.
   * @param tag          String for tag to lookup matching requests.
   * @param tagMap       Mapping of tags to matching requests.
   * @param scheduler    Scheduler to check if requests have intersecting ranges.
   * @param intersectMap Mapping to insert overlapping requests into.
   */
  private void addIntersections(ChatRequest request, String tag,
      Map<String, List<ChatRequest>> tagMap,
      AvailabilityScheduler scheduler,
      Map<ChatRequest, Set<ChatRequest>> intersectMap) {
    if (!intersectMap.containsKey(request)) {
      Set<ChatRequest> intersectingReqs = new HashSet<>();

      intersectingReqs.add(request);
      intersectMap.put(request, intersectingReqs);
    }

    List<ChatRequest> tagReqs = tagMap.get(tag);
    Set<ChatRequest> intersectingReqs = intersectMap.get(request);

    for (ChatRequest tagReq : tagReqs) {
      if (!intersectingReqs.contains(tagReq)) {
        if (scheduler.haveIntersectingRanges(request, tagReq)) {
          intersectingReqs.add(tagReq);
        }
      }
    }
  }

  private void matchRandomRequests(List<ChatRequest> requestList,
      RequestStore requestStore, Set<ChatRequest> matched,
      Map<String, List<ChatRequest>> tagMap,
      Map<ChatRequest, Set<ChatRequest>> rangeIntersections) {
    List<ChatRequest> randomRequests = tagMap.getOrDefault("Random", new ArrayList<>());
    requestList.removeAll(randomRequests);

    List<ChatRequest> tryMatchRandomReqs = requestList.stream()
        .filter(ChatRequest::shouldMatchRandom)
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
        Set<ChatRequest> selectedCombo = findAndCreateMatching(request, randomRequests,
            requestStore, matched, rangeIntersections);

        if (selectedCombo != null) {
          randomRequests.removeAll(selectedCombo);
        }
      }
    }
  }

  /**
   * Iterates through given unmatched requests and their tags to attempt to create a matching.
   */
  private void matchTaggedRequests(List<ChatRequest> requestList, RequestStore requestStore,
      Set<ChatRequest> matched, Map<String, List<ChatRequest>> tagMap,
      Map<ChatRequest, Set<ChatRequest>> rangeIntersections) {
    for (ChatRequest request : requestList) {
      if (!matched.contains(request)) {
        for (String tag : request.getTags()) {
          List<ChatRequest> sameTagReqs = tagMap.get(tag);

          if (request.getMinPeople() <= sameTagReqs.size() - 1) {
            Set<ChatRequest> selectedCombo = findAndCreateMatching(request, sameTagReqs,
                requestStore, matched, rangeIntersections);

            if (selectedCombo != null) {
              selectedCombo.forEach(req -> removeFromTagMap(tagMap, req));
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Finds combinations of requests compatible with the given request and it's group size and date
   * ranges, and creates the events if a suitable time slot and matching is found. Iterates through
   * possible combinations until a viable time slot is found, starting with combinations with
   * largest group size.
   *
   * @param request            ChatRequest that is currently chosen to find a matching for.
   * @param sameTagReqs        List of ChatRequests that have a matching tag to the given request.
   * @param requestStore       RequestStore to handle creating new matchings and removing satisfied
   *                           requests.
   * @param matched            Set of ChatRequests that have already been matched in this run, if a
   *                           matching is found it is also added to this set.
   * @param rangeIntersections Mapping of ChatRequests to a collection of other ChatRequests which
   *                           have overlapping DateRanges and at least one matching tag.
   * @return Finalised combination of ChatRequests that have been matched together.
   */
  private Set<ChatRequest> findAndCreateMatching(ChatRequest request, List<ChatRequest> sameTagReqs,
      RequestStore requestStore, Set<ChatRequest> matched,
      Map<ChatRequest, Set<ChatRequest>> rangeIntersections) {
    List<Set<ChatRequest>> compatibleReqCombos =
        findMatchingCombinations(request, sameTagReqs, rangeIntersections);

    TimeSlot meetingSlot = TimeSlot.EMPTY;
    Set<ChatRequest> selectedCombo = null;

    for (Set<ChatRequest> combo : compatibleReqCombos) {
      meetingSlot = findSharedTimeSlot(combo);

      if (!meetingSlot.isEmpty()) {
        selectedCombo = combo;
        break;
      }
    }

    // In case of no combinations were found
    if (!meetingSlot.isEmpty()) {
      assert selectedCombo != null;
      createMatching(requestStore, selectedCombo, meetingSlot);

      matched.addAll(selectedCombo);
    }

    return selectedCombo;
  }

  /**
   * Finds combinations of requests in a matching list which are compatible in group size and
   * overlapping date ranges.
   *
   * @return List of combinations (set of ChatRequests) sorted from largest matching to smallest.
   */
  private List<Set<ChatRequest>> findMatchingCombinations(ChatRequest request,
      List<ChatRequest> tagReqs, Map<ChatRequest, Set<ChatRequest>> rangeIntersections) {
    int groupSize = Math.min(request.getMaxPeople() + 1, tagReqs.size());
    boolean compatible = false;
    List<ChatRequest> compatibleReqs;

    do {
      // Lambdas cannot use non-final variables
      int attemptGroupSize = groupSize;
      compatibleReqs = tagReqs.stream()
          .filter(req -> !request.getUserId().equals(req.getUserId()))
          .filter(req -> rangeIntersections.get(request).contains(req))
          .filter(req -> groupSizeInRange(attemptGroupSize, req))
          .collect(Collectors.toList());

      if (compatibleReqs.size() >= groupSize - 1) {
        compatible = true;
      } else {
        groupSize--;
      }

    } while (!compatible && request.getMinPeople() < groupSize);

    if (!compatible) {
      return Collections.emptyList();
    }

    return findCompatibleDateRequests(request, compatibleReqs, groupSize, rangeIntersections);
  }

  /**
   * Iterates through combinations of possible matching requests to return combinations which have
   * common date ranges between them.
   *
   * @param request        Main ChatRequest to satisfy in this matching.
   * @param compatibleReqs List of candidate ChatRequests to match with, having compatible
   *                       groupSizes.
   * @param groupSize      Maximum group size possible that is constrained by minimum of either by
   *                       request's maximum or number of other requests with matching tags.
   * @return List of combinations of ChatRequests
   */
  private List<Set<ChatRequest>> findCompatibleDateRequests(ChatRequest request,
      List<ChatRequest> compatibleReqs, int groupSize,
      Map<ChatRequest, Set<ChatRequest>> rangeIntersections) {
    Set<Set<ChatRequest>> combinations = new HashSet<>();

    for (ChatRequest req : compatibleReqs) {
      Set<ChatRequest> combination = new HashSet<>();
      combination.add(request);
      combination.add(req);
      combinations.add(combination);

      if (combination.size() < groupSize) {
        findNestedCombinations(req, combination, combinations, groupSize, rangeIntersections);
      }
    }

    // Sort in descending order of size, attempt largest matching first.
    return combinations.stream()
        .filter(combo -> combo.size() >= request.getMinPeople() + 1)
        .sorted((c1, c2) -> c2.size() - c1.size())
        .collect(Collectors.toList());
  }

  private void findNestedCombinations(ChatRequest currReq,
      Set<ChatRequest> currCombination,
      Set<Set<ChatRequest>> combinations, int maxGroupSize,
      Map<ChatRequest, Set<ChatRequest>> rangeIntersections) {
    for (ChatRequest req : rangeIntersections.get(currReq)) {
      Set<ChatRequest> newCombination = new HashSet<>(currCombination);
      newCombination.add(req);
      if (!combinations.contains(newCombination) && distinctUsers(currCombination, req)) {
        if (intersectWithCombination(req, currCombination, rangeIntersections)) {
          combinations.add(newCombination);

          if (newCombination.size() < maxGroupSize) {
            findNestedCombinations(req, newCombination, combinations, maxGroupSize,
                rangeIntersections);
          }
        }
      }
    }
  }

  private boolean distinctUsers(Set<ChatRequest> currCombination, ChatRequest newRequest) {
    return !currCombination.stream()
        .map(ChatRequest::getUserId)
        .collect(Collectors.toSet())
        .contains(newRequest.getUserId());
  }

  private boolean intersectWithCombination(ChatRequest request, Set<ChatRequest> combination,
      Map<ChatRequest, Set<ChatRequest>> rangeIntersections) {
    for (ChatRequest req : combination) {
      if (!rangeIntersections.get(req).contains(request)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Expires requests made that have already expired such that their last end date is in the past.
   */
  private void removeExpiredRequests(List<ChatRequest> requestList, RequestStore requestStore) {
    Date nextCronRunDate = Date.from(Instant.now());

    expireRequestsBeforeTime(nextCronRunDate, requestList, requestStore);
  }

  /**
   * Expires requests that would expire before the next time requests are matched.
   */
  private void removeWillExpireRequests(List<ChatRequest> requestList, RequestStore requestStore) {
    Date nextCronRunDate = Date.from(Instant.now().plus(Duration.ofHours(MATCHING_FREQUENCY)));

    expireRequestsBeforeTime(nextCronRunDate, requestList, requestStore);
  }

  /**
   * Expires ChatRequests that have their last end date before the given expiry date.
   */
  private void expireRequestsBeforeTime(Date expiryDate, List<ChatRequest> requestList,
      RequestStore requestStore) {
    for (ChatRequest request : requestList) {
      List<DateRange> dateRanges = request.getDateRanges();
      Date lastEndDate = dateRanges.get(dateRanges.size() - 1).getEnd();

      if (lastEndDate.before(expiryDate)) {
        requestStore.expiredRequest(request);
      }
    }
  }

  /**
   * Finds common tags between requests and adds matching requests and removes old Chat Requests
   * from store.
   */
  private void createMatching(RequestStore requestStore, Collection<ChatRequest> requests,
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

  private Date weekFromNow() {
    return Date.from(Instant.now().plus(Duration.ofDays(7)));
  }

  /**
   * Given matched requests, finds availability for all users in the selected date ranges.
   */
  private TimeSlot findSharedTimeSlot(Collection<ChatRequest> reqs) {
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
