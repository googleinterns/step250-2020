package com.google.step.coffee.tasks;

import com.google.api.services.calendar.model.Event;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.entity.Availability;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.DateRange;
import com.google.step.coffee.entity.TimeSlot;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    // Implement naive matching, matching consecutive requests together
    for (int i = 0; (i + 1) < requestList.size(); i += 2) {
      ChatRequest req1 = requestList.get(i);
      ChatRequest req2 = requestList.get(i + 1);

      List<Availability> reqs = new ArrayList<>();
      reqs.add(req1);
      reqs.add(req2);

      TimeSlot meetingSlot = findSharedTimeSlot(reqs);

      if (!meetingSlot.equals(TimeSlot.EMPTY)) {
        List<String> commonTags = new ArrayList<>(req1.getTags());
        commonTags.retainAll(req2.getTags());

        List<String> participantIds = new ArrayList<>();
        Collections.addAll(participantIds, req1.getUserId(), req2.getUserId());

        createMatchingRequests(requestStore, participantIds, meetingSlot, commonTags, req1, req2);

        requestStore.removeRequests(req1.getRequestKey(), req2.getRequestKey());
      }
    }
  }

  private void createMatchingRequests(RequestStore requestStore, List<String> participantIds,
      TimeSlot meetingSlot, List<String> commonTags, ChatRequest ...reqs) {
    Event event = CalendarUtils.createEvent(meetingSlot, participantIds, commonTags);

    for (ChatRequest req : reqs) {
      requestStore.addMatchedRequest(req, meetingSlot, participantIds, commonTags);
      CalendarUtils.addEvent(req.getUserId(), event);
    }
  }

  /**
   * Given matched requests, find availability for all users in the selected date ranges.
   */
  private TimeSlot findSharedTimeSlot(List<Availability> reqs) {
    AvailabilityScheduler scheduler = new AvailabilityScheduler(reqs);

    Duration minDuration = reqs.stream()
        .map(Availability::getDuration)
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
