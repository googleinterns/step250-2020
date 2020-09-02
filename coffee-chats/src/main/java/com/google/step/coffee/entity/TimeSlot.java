package com.google.step.coffee.entity;

import java.time.Duration;
import java.time.ZonedDateTime;

public class TimeSlot {
  private final ZonedDateTime datetimeStart;
  private final Duration duration;

  public TimeSlot(ZonedDateTime datetimeStart, Duration duration) {
    this.datetimeStart = datetimeStart;
    this.duration = duration;
  }

  public ZonedDateTime getDatetimeStart() {
    return datetimeStart;
  }

  public Duration getDuration() {
    return duration;
  }
}
