package com.google.step.coffee.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.Calendar.Events.Insert;
import com.google.api.services.calendar.model.Event;
import com.google.appengine.api.users.User;
import com.google.step.coffee.TestHelper;
import com.google.step.coffee.entity.TimeSlot;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class CalendarUtilsTest extends TestHelper {

  private final String testUser1Id = "1111";
  private final String testUser2Id = "2222";
  private final String testUser3Id = "3333";

  @Test
  public void eventDescriptionContainsCommonTags() {
    final TimeSlot timeslot = new TimeSlot(ZonedDateTime.now(), Duration.ofMinutes(30));
    final List<String> participantIds = new ArrayList<>();
    final List<String> commonTags = new ArrayList<>();

    setUpEventInfo(participantIds, commonTags);
    addTestUsersInfo();

    Event event = CalendarUtils.createEvent(timeslot, participantIds, commonTags);

    assertThat(event.getDescription(), containsString("Football"));
    assertThat(event.getDescription(), containsString("Photography"));
  }

  @Test
  public void addingEventAddsOnceOnSuccess() throws IOException {
    Event event = mock(Event.class);
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

  private void setUpEventInfo(List<String> participantIds, List<String> commonTags) {
    participantIds.add(testUser1Id);
    participantIds.add(testUser2Id);
    participantIds.add(testUser3Id);

    commonTags.add("Football");
    commonTags.add("Photography");
  }

  private void addTestUsersInfo() {
    User testUser1 = mock(User.class);
    User testUser2 = mock(User.class);
    User testUser3 = mock(User.class);

    when(testUser1.getUserId()).thenReturn(testUser1Id);
    when(testUser1.getEmail()).thenReturn("testUser1@example.com");
    when(testUser2.getUserId()).thenReturn(testUser2Id);
    when(testUser2.getEmail()).thenReturn("testUser2@example.com");
    when(testUser3.getUserId()).thenReturn(testUser3Id);
    when(testUser3.getEmail()).thenReturn("testUser3@example.com");

    UserStore userStore = new UserStore();
    userStore.addNewUser(testUser1);
    userStore.addNewUser(testUser2);
    userStore.addNewUser(testUser3);
  }
}
