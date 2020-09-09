package com.google.step.coffee.data;

import static com.google.step.coffee.APIUtils.APPLICATION_NAME;
import static com.google.step.coffee.APIUtils.HTTP_TRANSPORT;
import static com.google.step.coffee.APIUtils.JSON_FACTORY;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.step.coffee.OAuthService;
import com.google.step.coffee.entity.TimeSlot;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utilities to access Google Calendar API for an authorised user.
 */
public class CalendarUtils {
  private static final UserStore userStore = new UserStore();

  public static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_SETTINGS_READONLY,
      CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR_EVENTS);

  /**
   * Adds given event to user's primary calendar.
   *
   * @param userId String of user's id whose calendar to add event into.
   * @param event Event to add into user's calendar.
   */
  public static void addEvent(String userId, Event event) {
    addEvent(getCalendarService(userId), userId, event, true);
  }

  /**
   * Adds given event to user's primary calendar using provided calendar service.
   *
   * @param service Calendar service to use to insert event.
   * @param userId String of user's id whose calendar to add event into.
   * @param event Event to add into user's calendar.
   * @param retry boolean of whether to retry adding event on failure.
   */
  public static void addEvent(Calendar service, String userId, Event event, boolean retry) {
    try {
      service.events().insert("primary", event).setConferenceDataVersion(1).execute();
    } catch (IOException e) {
      System.out.println("Event could not be created: " + e.getMessage());

      if (retry) {
        System.out.println("Retrying...");
        addEvent(service, userId, event, false);
      }
    }
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
