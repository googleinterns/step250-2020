package com.google.step.coffee.data;

import static com.google.step.coffee.APIUtils.APPLICATION_NAME;
import static com.google.step.coffee.APIUtils.HTTP_TRANSPORT;
import static com.google.step.coffee.APIUtils.JSON_FACTORY;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.step.coffee.OAuthService;
import com.google.step.coffee.entity.TimeSlot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** Utilities to access Google Calendar API for an authorised user. */
public class CalendarUtils {
  private static final UserStore userStore = new UserStore();

  public static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_SETTINGS_READONLY,
      CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR_EVENTS);

  /** Add given event to user's primary calendar. */
  public static void addEvent(String userId, Event event) {
    Calendar service = getCalendarService(userId);

    try {
      service.events().insert("primary", event).setConferenceDataVersion(1).execute();
    } catch (IOException e) {
      System.out.println("Event could not be created: " + e.getMessage());
    }
  }

  /** Create an event with video conferencing for given participants at selected time slot. */
  public static Event createEvent(TimeSlot timeSlot, List<String> participantIds,
      List<String> commonTags) {
    List<EventAttendee> attendees = getAttendees(participantIds);

    return new Event()
        .setSummary("Coffee Chat")
        .setConferenceData(new ConferenceData()
            .setCreateRequest(new CreateConferenceRequest()
                .setRequestId(generateRandomUUID())
                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))))
        .setDescription("You've been matched for a chat! Please contact the participants if you "
            + "wish to make any amendments.\n"
            + (commonTags.isEmpty() ?
                "You and the other participant(s) did not match on a specific tag, "
                    + "however you may still have common interests or topics of conversation." :
                "You and the other participant(s) matched on the following tags: "
                    + commonTags.toString())
            + "\nEnjoy!")
        .setGuestsCanModify(true)
        .setAttendees(attendees)
        .setStart(new EventDateTime().setDateTime(timeSlot.getDatetimeStart()))
        .setEnd(new EventDateTime().setDateTime(timeSlot.getDatetimeEnd()));
  }

  public List<TimePeriod> getFreeBusy(String userId, DateTime start, DateTime end) {
    Calendar service = getCalendarService(userId);

    List<FreeBusyRequestItem> calendars = new ArrayList<>();
    calendars.add(new FreeBusyRequestItem().setId("primary"));

    FreeBusyRequest request = new FreeBusyRequest()
        .setTimeMin(start)
        .setTimeMax(end)
        .setItems(calendars);

    FreeBusyResponse response;

    try {
      response = service.freebusy().query(request).execute();
    } catch (IOException e) {
      System.out.println("FreeBusy query failed: " + e.getMessage());
      System.out.println("Retrying...");

      try {
        response = service.freebusy().query(request).execute();
      } catch (IOException ex) {
        return Collections.emptyList();
      }
    }

    FreeBusyCalendar cal = response.getCalendars().get("primary");

    return cal.getBusy();
  }

  private static Calendar getCalendarService(String userId) {
    return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, OAuthService.getCredentials(userId))
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  private static String generateRandomUUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private static List<EventAttendee> getAttendees(List<String> participantIds) {
    return participantIds.stream()
        .map(userStore::getEmail)
        .map(email -> new EventAttendee().setEmail(email))
        .collect(Collectors.toList());
  }
}
