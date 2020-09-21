package com.google.step.coffee.entity;

import com.google.auto.value.AutoValue;

import java.time.Duration;
import java.time.Instant;

@AutoValue
public abstract class Event {
  public abstract String id();

  public abstract String groupId();

  public abstract String description();

  public abstract Duration duration();

  public abstract Instant start();

  public static Builder builder() {
    return new AutoValue_Event.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public Builder setId(String value);
    abstract public Builder setGroupId(String value);
    abstract public Builder setDescription(String value);
    abstract public Builder setDuration(Duration value);
    abstract public Builder setStart(Instant value);
    abstract public Event build();
  }
}
