package com.google.step.coffee.data;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.entity.Event;

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
}
