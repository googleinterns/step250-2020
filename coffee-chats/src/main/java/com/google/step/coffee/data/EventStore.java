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

  public List<Event> getUpcomingEventsForGroup(Group group) {
    return getUpcomingEventsForGroups(Collections.singletonList(group.id()));
  }

  public List<Event> getUpcomingEventsForGroups(List<String> groupIds) {
    if (groupIds.isEmpty()) {
      return new ArrayList<>();
    }

    Query.Filter filter;

    if (groupIds.size() > 1) {
      filter = new Query.CompositeFilter(Query.CompositeFilterOperator.OR,
          groupIds.stream().map(id -> new Query.FilterPredicate(
              "group", Query.FilterOperator.EQUAL, KeyFactory.stringToKey(id)
          )).collect(Collectors.toList()));
    } else {
      filter = new Query.FilterPredicate(
          "group", Query.FilterOperator.EQUAL, KeyFactory.stringToKey(groupIds.get(0)));
    }

    Query query = new Query("event")
        .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND,
            Arrays.asList(
                new Query.FilterPredicate(
            "start", Query.FilterOperator.GREATER_THAN_OR_EQUAL, Instant.now().getEpochSecond()),
                filter
            )));

    List<Event> result = new ArrayList<>();

    for (Entity entity : datastore.prepare(query).asIterable()) {
      result.add(Event.fromEntity(entity));
    }

    return result;
  }
}
