package com.google.step.coffee.entity;

import com.google.auto.value.AutoValue;

import java.time.Duration;
import java.util.List;

@AutoValue
abstract public class EventRequest implements Availability {
  public abstract List<DateRange> getDateRanges();

  public abstract Duration getDuration();

  public abstract String getUserId();

  public static Builder builder() {
    return new AutoValue_EventRequest.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public Builder setDateRanges(List<DateRange> value);
    abstract public Builder setDuration(Duration value);
    abstract public Builder setUserId(String value);
    abstract public EventRequest build();
  }
}
