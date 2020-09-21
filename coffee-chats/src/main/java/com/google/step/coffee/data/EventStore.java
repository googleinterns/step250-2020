package com.google.step.coffee.data;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.entity.Event;
import com.google.step.coffee.entity.Group;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    Query query = new Query("event")
        .setFilter(new Query.FilterPredicate(
            "start", Query.FilterOperator.GREATER_THAN_OR_EQUAL, Instant.now().getEpochSecond()
        ));

    List<Event> result = new ArrayList<>();

    for (Entity entity : datastore.prepare(query).asIterable()) {
      result.add(Event.fromEntity(entity));
    }

    return result;
  }
}
