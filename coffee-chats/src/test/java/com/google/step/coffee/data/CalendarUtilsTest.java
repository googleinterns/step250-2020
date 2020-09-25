package com.google.step.coffee.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.Calendar.Events.Insert;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.step.coffee.TestHelper;
import com.google.step.coffee.UserManager;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
import com.google.step.coffee.entity.TimeSlot;
import com.google.step.coffee.entity.User;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class CalendarUtilsTest extends TestHelper {
  private final String testUser1Id = "1111";
  private final String testUser2Id = "2222";
  private final String testUser3Id = "3333";

  private final String testUser1Email = "testUser1@example.com";
  private final String testUser2Email = "testUser2@example.com";
  private final String testUser3Email = "testUser3@example.com";

  private final User testUser1 = User.builder()
      .setId(testUser1Id).setEmail(testUser1Email).build();
  private final User testUser2 = User.builder()
      .setId(testUser2Id).setEmail(testUser2Email).build();
  private final User testUser3 = User.builder()
      .setId(testUser3Id).setEmail(testUser3Email).build();

  @Test
  public void eventDescriptionContainsCommonTags() {
    final TimeSlot timeslot = new TimeSlot(ZonedDateTime.now(), Duration.ofMinutes(30));
    final List<String> participantIds = new ArrayList<>();
    final List<String> commonTags = new ArrayList<>();

    participantIds.add(testUser1Id);
    participantIds.add(testUser2Id);
    participantIds.add(testUser3Id);

    commonTags.add("Football");
    commonTags.add("Photography");

    // UserStore entities required as creating event also creates attendants list using emails
    // retrieved via datastore using userId.
    UserStore userStore = new UserStore();
    userStore.addNewUser(testUser1);
    userStore.addNewUser(testUser2);
    userStore.addNewUser(testUser3);

    Event event = CalendarUtils.createEvent(timeslot, participantIds, commonTags);

    assertThat(event.getDescription(), containsString("Football"));
    assertThat(event.getDescription(), containsString("Photography"));
  }

  @Test
  public void groupEventsGetScheduled() throws IOException {
    Duration duration = Duration.ofMinutes(30);
    Instant start = Instant.ofEpochSecond(1_600_000_000);
    TimeSlot timeslot = new TimeSlot(ZonedDateTime.ofInstant(start, ZoneId.of("UTC")), duration);

    Calendar service = mock(Calendar.class);
    Events events = mock(Events.class);
    Insert insertReturn = mock(Insert.class);
    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

    when(service.events()).thenReturn(events);
    when(service.events()).thenReturn(events);
    when(events.insert(eq("primary"), any())).thenReturn(insertReturn);
    when(insertReturn.setConferenceDataVersion(1)).thenReturn(insertReturn);

    GroupStore groupStore = new GroupStore();
    EventStore eventStore = new EventStore();
    UserStore userStore = new UserStore();

    userStore.addNewUser(testUser1);
    userStore.addNewUser(testUser2);
    userStore.addNewUser(testUser3);
    userStore.addNewUser(UserManager.getCurrentUser());

    Group group = groupStore.create("test group");
    groupStore.updateMembershipStatus(group, testUser1, GroupMembership.Status.REGULAR_MEMBER);
    groupStore.updateMembershipStatus(group, testUser2, GroupMembership.Status.REGULAR_MEMBER);
    groupStore.updateMembershipStatus(group, testUser3, GroupMembership.Status.REGULAR_MEMBER);
    User owner = UserManager.getCurrentUser();

    com.google.step.coffee.entity.Event event =
        eventStore.put(com.google.step.coffee.entity.Event.builder()
        .setGroupId(group.id())
        .setStart(start)
        .setDuration(duration)
        .setDescription("bla bla bla")
        .build());

    CalendarUtils.updateGroupEvent(event, service, group);

    verify(events, times(1)).insert(eq("primary"), eventCaptor.capture());
    Event calEvent = eventCaptor.getValue();

    assertThat(calEvent.getSummary(), containsString("test group"));
    assertThat(calEvent.getDescription(), containsString("bla bla bla"));
    assertThat(calEvent.getStart().getDateTime(), equalTo(timeslot.getDatetimeStart()));
    assertThat(calEvent.getEnd().getDateTime(), equalTo(timeslot.getDatetimeEnd()));
    assertThat(calEvent.getAttendees(), containsInAnyOrder(
        new EventAttendee().setEmail(testUser1Email),
        new EventAttendee().setEmail(testUser2Email),
        new EventAttendee().setEmail(testUser3Email),
        new EventAttendee().setEmail(UserManager.getCurrentUser().email())
    ));
  }

  @Test
  public void addingEventAddsOnceOnSuccess() throws IOException {
    Event event = mock(Event.class);
    when(event.getId()).thenReturn(null);
    Calendar service = mock(Calendar.class);
    Events events = mock(Events.class);
    Insert insertReturn = mock(Insert.class);

    when(service.events()).thenReturn(events);
    when(events.insert("primary", event)).thenReturn(insertReturn);
    when(insertReturn.setConferenceDataVersion(1)).thenReturn(insertReturn);

    CalendarUtils.addEvent(service, testUser1Id, event, true);

    verify(events, times(1)).insert("primary", event);
    verify(insertReturn, times(1)).execute();
  }

  @Test
  public void addingAnEventRetriesIfFails() throws IOException {
    Event event = mock(Event.class);
    when(event.getId()).thenReturn(null);
    Calendar service = mock(Calendar.class);
    Events events = mock(Events.class);
    Insert insertReturn = mock(Insert.class);

    when(service.events()).thenReturn(events);

    when(events.insert("primary", event))
        .thenThrow(new IOException("failed"))
        .thenReturn(insertReturn);

    when(insertReturn.setConferenceDataVersion(1)).thenReturn(insertReturn);

    CalendarUtils.addEvent(service, testUser1Id, event, true);

    verify(events, times(2)).insert("primary", event);
    verify(insertReturn, times(1)).execute();
  }
}
