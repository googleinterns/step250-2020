package com.google.step.coffee.entity;

import com.google.api.client.util.DateTime;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

public class TimeSlot {
  public static final TimeSlot EMPTY = new TimeSlot(new Date(0L), Duration.ZERO);

  private final ZonedDateTime datetimeStart;
  private final ZonedDateTime datetimeEnd;
  private final Duration duration;

  public TimeSlot(ZonedDateTime datetimeStart, Duration duration) {
    this.datetimeStart = datetimeStart;
    this.datetimeEnd = datetimeStart.plusMinutes(duration.toMinutes());
    this.duration = duration;
  }

  public TimeSlot(Date datetimeStart, Duration duration) {
    this.datetimeStart = ZonedDateTime.ofInstant(datetimeStart.toInstant(), ZoneId.systemDefault());
    this.datetimeEnd = this.datetimeStart.plusMinutes(duration.toMinutes());
    this.duration = duration;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.datetimeStart, this.duration);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TimeSlot) {
      TimeSlot otherSlot = (TimeSlot) obj;

      return datetimeStart.equals(otherSlot.datetimeStart) && duration.equals(otherSlot.duration);
    }

    return false;
  }

  public ZonedDateTime getZonedDatetimeStart() {
    return datetimeStart;
  }

  public ZonedDateTime getZonedDatetimeEnd() {
    return datetimeEnd;
  }

  public DateTime getDatetimeStart() {
    return new DateTime(Date.from(datetimeStart.toInstant()));
  }

  public DateTime getDatetimeEnd() {
    return new DateTime(Date.from(datetimeEnd.toInstant()));
  }

  public Duration getDuration() {
    return duration;
  }

  public boolean isEmpty() {
    return this.equals(EMPTY);
  }
}
