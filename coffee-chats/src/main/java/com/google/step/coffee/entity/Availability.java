package com.google.step.coffee.entity;

import java.time.Duration;
import java.util.List;

public interface Availability {
  /**
   * Get date ranges for the request.
   *
   * @return A list of DateRange objects representing each datetime range selected by the user.
   */
  List<DateRange> getDateRanges();

  /**
   * Get maximum duration of the request.
   *
   * @return A Duration object representing max chat duration, must be positive.
   */
  Duration getDuration();

  /**
   * Get id of the user whose availability this request describes
   */
  String getUserId();
}
