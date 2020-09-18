package com.google.step.coffee.data;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.entity.Event;

import java.time.Duration;
import java.time.Instant;

public class EventStore {
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  public Event create(String groupId, Instant start, Duration duration) {
    return put(Event.builder()
        .setGroupId(groupId)
        .setStart(start)
        .setDuration(duration)
        .setDescription("")
        .build());
  }

  /**
   * Saves the event to the database. Returns an <code>Event</code>
   * that has its id set, which the group passed to the function might not.
   */
  public Event put(Event event) {
    Key key = event.key();
    Entity entity = key != null ? new Entity(key) : new Entity("event");
    entity.setProperty("description", new Text(event.description()));
    entity.setProperty("start", event.start());
    entity.setProperty("duration", event.duration().toMinutes());
    entity.setProperty("groupId", event.groupId());

    datastore.put(entity);

    return Event.fromEntity(entity);
  }
}
