package com.google.step.coffee.entity;

import com.google.api.client.util.DateTime;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeSlot {
  private final ZonedDateTime datetimeStart;
  private final Duration duration;

  public TimeSlot(ZonedDateTime datetimeStart, Duration duration) {
    this.datetimeStart = datetimeStart;
    this.duration = duration;
  }

  public ZonedDateTime getZonedDatetimeStart() {
    return datetimeStart;
  }

  public DateTime getDatetimeStart() {
    return new DateTime(Date.from(datetimeStart.toInstant()));
  }

  public DateTime getDatetimeEnd() {
    ZonedDateTime end = datetimeStart.plusMinutes(getDuration().toMinutes());

    return new DateTime(Date.from(end.toInstant()));
  }

  public Duration getDuration() {
    return duration;
  }
}
