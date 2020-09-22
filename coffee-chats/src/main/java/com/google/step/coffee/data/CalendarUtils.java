package com.google.step.coffee.data;

import static com.google.step.coffee.APIUtils.APPLICATION_NAME;
import static com.google.step.coffee.APIUtils.HTTP_TRANSPORT;
import static com.google.step.coffee.APIUtils.JSON_FACTORY;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
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
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.TimeSlot;
import com.google.step.coffee.entity.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utilities to access Google Calendar API for an authorised user.
 */
public class CalendarUtils {
  private static final UserStore userStore = new UserStore();
  private static final GroupStore groupStore = new GroupStore();

  private static final int MAX_RETRIES = 2;

  /**
   * Adds (or updates if exists) given event to user's primary calendar.
   *
   * @param userId String of user's id whose calendar to add event into.
   * @param event Event to add into user's calendar.
   * @return the inserted Event object (or null on failure)
   */
  public static Event addEvent(String userId, Event event) {
    return addEvent(getCalendarService(userId), userId, event, true);
  }

  /**
   * Removes given event from user's primary calendar.
   *
   * @param userId String of user's id whose calendar to remove the event from.
   * @param eventId id of the event to remove.
   */
  public static void removeEvent(String userId, String eventId) {
    Calendar service = getCalendarService(userId);
    for (int retry = 0; retry < MAX_RETRIES; ++retry) {
      try {
        service.events().delete("primary", eventId).execute();
        return;
      } catch (IOException ignored) {}
    }
  }

  /**
   * Adds (or updates if exists) given event to user's primary calendar using provided calendar service.
   *
   * @param service Calendar service to use to insert event.
   * @param userId String of user's id whose calendar to add event into.
   * @param event Event to add into user's calendar.
   * @param retry boolean of whether to retry adding event on failure.
   * @return the inserted Event object (or null on failure)
   */
  public static Event addEvent(Calendar service, String userId, Event event, boolean retry) {
    try {
      if (event.getId() == null || event.getId().isEmpty()) {
        return service.events()
            .insert("primary", event)
            .setConferenceDataVersion(1)
            .execute();
      } else {
        return service.events()
            .update("primary", event.getId(), event)
            .setConferenceDataVersion(1)
            .execute();
      }
    } catch (IOException e) {
      System.out.println("Event could not be created: " + e.getMessage());

      if (retry) {
        System.out.println("Retrying...");
        return addEvent(service, userId, event, false);
      }
    }

    return null;
  }

  /**
   * Creates an event with video conferencing for given participants at selected time slot.
   *
   * @param timeSlot TimeSlot of when the event has been scheduled for.
   * @param participantIds List of user Ids for all users to be invited to the event.
   * @param commonTags List of tags which are shared by all participants.
   */
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

  /**
   * Creates (or updates if it already exists) a group event in Google Calendar with information provided.
   */
  public static com.google.step.coffee.entity.Event updateGroupEvent(com.google.step.coffee.entity.Event event) {
    Group group = groupStore.get(event.groupId());

    return updateGroupEvent(event, getCalendarService(group.ownerId()), group);
  }

  /**
   * The implementation of updateGroupEvent, so that it can be tested separately
   */
  static com.google.step.coffee.entity.Event updateGroupEvent(
      com.google.step.coffee.entity.Event event,
      Calendar service,
      Group group
  ) {
    Event calendarEvent = null;
    boolean isNewEvent = false;

    if (event.calendarId() != null) {
      for (int retry = 0; retry < MAX_RETRIES; ++retry) {
        try {
          calendarEvent = service.events().get("primary", event.calendarId()).execute();
          break;
        } catch (IOException ignored) {}
      }
    }

    if (calendarEvent == null) {
      calendarEvent = new Event();
      isNewEvent = true;
    }

    List<EventAttendee> attendees = getAttendees(
        groupStore.getMembers(group).stream()
            .map(member -> member.user().id())
            .collect(Collectors.toList()));

    calendarEvent
        .setSummary(group.name() + " Event")
        .setConferenceData(new ConferenceData()
            .setCreateRequest(new CreateConferenceRequest()
                .setRequestId(generateRandomUUID())
                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))))
        .setDescription("This event was scheduled in Coffee Chats\n" + event.description())
        .setGuestsCanModify(true)
        .setAttendees(attendees)
        .setStart(new EventDateTime().setDateTime(
            new DateTime(event.start().toEpochMilli())
        ))
        .setEnd(new EventDateTime().setDateTime(
            new DateTime(event.start().toEpochMilli() + event.duration().toMillis())
        ));

    Event insertedEvent = addEvent(service, group.ownerId(), calendarEvent, true);

    if (insertedEvent != null && isNewEvent) {
      event = event.modify()
          .setCalendarId(insertedEvent.getId())
          .build();
    }

    return event;
  }

  /**
   * Fetches ranges of time for which user is busy on primary calendar between given dates.
   *
   * @param userId Id of user who's primary calendar is being checked.
   * @param start Start time for which to check calendar for.
   * @param end End time for which to check calendar for.
   * @return List of time periods coinciding with events on user's calendar.
   */
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
        .map(userStore::getUser)
        .map(User::email)
        .map(email -> new EventAttendee().setEmail(email))
        .collect(Collectors.toList());
  }
}
