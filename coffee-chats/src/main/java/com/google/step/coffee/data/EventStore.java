package com.google.step.coffee.data;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.entity.Event;
import com.google.step.coffee.entity.Group;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventStore {
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Saves the event to the database. Returns an <code>Event</code>
   * that has its id set, which the group passed to the function might not.
   */
  public Event put(Event event) {
    Key key = event.key();
    Entity entity = key != null ? new Entity(key) : new Entity("event");
    entity.setProperty("description", new Text(event.description()));
    entity.setProperty("start", event.start().getEpochSecond());
    entity.setProperty("duration", event.duration().toMinutes());
    entity.setProperty("group", KeyFactory.stringToKey(event.groupId()));
    entity.setProperty("calendarId", event.calendarId());

    datastore.put(entity);

    return event.modify()
        .setId(KeyFactory.keyToString(entity.getKey()))
        .build();
  }

  /**
   * Removes the event from Datastore and Google Calendar
   * @param event The event to remove
   * @param group The group that the event belongs to
   */
  public void delete(Event event, Group group) {
    assert event.groupId().equals(group.id());

    CalendarUtils.removeEvent(group.ownerId(), event.calendarId());
    datastore.delete(event.key());
  }

  public List<Event> getUpcomingEventsForGroup(Group group) {
    return getUpcomingEventsForGroups(Collections.singletonList(group.id()));
  }

  public List<Event> getUpcomingEventsForGroups(List<String> groupIds) {
    if (groupIds.isEmpty()) {
      return new ArrayList<>();
    }

    Query query = new Query("event")
        .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND,
            Arrays.asList(
                new Query.FilterPredicate(
            "start", Query.FilterOperator.GREATER_THAN_OR_EQUAL, Instant.now().getEpochSecond()),
                new Query.FilterPredicate(
                    "group", Query.FilterOperator.IN,
                    groupIds.stream().map(KeyFactory::stringToKey).collect(Collectors.toList())
                )
            )));

    List<Event> result = new ArrayList<>();

    for (Entity entity : datastore.prepare(query).asIterable()) {
      result.add(Event.fromEntity(entity));
    }

    return result;
  }
}
