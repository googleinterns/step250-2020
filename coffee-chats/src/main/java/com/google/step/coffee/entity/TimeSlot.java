package com.google.step.coffee.entity;

import java.time.ZonedDateTime;

public class TimeSlot {
  private final ZonedDateTime datetimeStart;
  private final int duration;

  public TimeSlot(ZonedDateTime datetimeStart, int duration) {
    this.datetimeStart = datetimeStart;
    this.duration = duration;
  }

  public ZonedDateTime getDatetimeStart() {
    return datetimeStart;
  }

  public int getDuration() {
    return duration;
  }
}
