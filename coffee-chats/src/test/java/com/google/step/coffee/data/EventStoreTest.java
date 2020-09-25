package com.google.step.coffee.data;

import com.google.step.coffee.TestHelper;
import com.google.step.coffee.entity.Event;
import com.google.step.coffee.entity.Group;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EventStoreTest extends TestHelper {
  private EventStore eventStore = new EventStore();
  private GroupStore groupStore = new GroupStore();

  @Test
  public void testInsertSetsId() throws Exception {
    Group group = groupStore.create("test group");

    Event event = Event.builder()
        .setDescription("bla bla bla")
        .setDuration(Duration.ofMinutes(30))
        .setStart(Instant.ofEpochSecond(1_600_000_000))
        .setGroupId(group.id())
        .build();

    assertThat(eventStore.put(event).id(), notNullValue());
  }

  @Test
  public void testFutureEventsReturned() throws Exception {
    Group group = groupStore.create("test group");
    Group anotherGroup = groupStore.create("unrelated group");

    // round to the nearest second
    Instant now = Instant.ofEpochSecond(Instant.now().getEpochSecond());

    Event eventPast = Event.builder()
        .setDescription("past")
        .setDuration(Duration.ofMinutes(30))
        .setStart(now.minusSeconds(100000))
        .setGroupId(group.id())
        .build();

    Event eventFuture = Event.builder()
        .setDescription("future")
        .setDuration(Duration.ofMinutes(30))
        .setStart(now.plusSeconds(100000))
        .setGroupId(group.id())
        .build();

    Event eventFutureOtherGroup = Event.builder()
        .setDescription("future other group")
        .setDuration(Duration.ofMinutes(30))
        .setStart(now.plusSeconds(100000))
        .setGroupId(anotherGroup.id())
        .build();

    eventPast = eventStore.put(eventPast);
    eventFuture = eventStore.put(eventFuture);
    eventFutureOtherGroup = eventStore.put(eventFutureOtherGroup);

    assertThat(eventStore.getUpcomingEventsForGroup(group), contains(
        eventFuture
    ));
  }
}
