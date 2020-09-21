package com.google.step.coffee.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;

@AutoValue
public abstract class Event {
  @Nullable
  public abstract String id();

  @Nullable
  public abstract String calendarId();

  public abstract String groupId();

  public abstract String description();

  public abstract Duration duration();

  public abstract Instant start();

  public static Builder builder() {
    return new AutoValue_Event.Builder();
  }

  public Builder modify() {
    return new AutoValue_Event.Builder()
        .setId(id())
        .setCalendarId(calendarId())
        .setDescription(description())
        .setDuration(duration())
        .setStart(start());
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public Builder setId(String value);
    abstract public Builder setCalendarId(String value);
    abstract public Builder setGroupId(String value);
    abstract public Builder setDescription(String value);
    abstract public Builder setDuration(Duration value);
    abstract public Builder setStart(Instant value);
    abstract public Event build();
  }

  public static Event fromEntity(Entity entity) {
    return Event.builder()
        .setId(KeyFactory.keyToString(entity.getKey()))
        .setDescription(((Text) entity.getProperty("description")).getValue())
        .setStart(Instant.ofEpochSecond((long) entity.getProperty("start")))
        .setDuration(Duration.ofMinutes((long) entity.getProperty("duration")))
        .setGroupId(KeyFactory.keyToString((Key) entity.getProperty("group")))
        .build();
  }

  public Key key() {
    if (id() == null) {
      return null;
    }

    return KeyFactory.stringToKey(id());
  }
}
