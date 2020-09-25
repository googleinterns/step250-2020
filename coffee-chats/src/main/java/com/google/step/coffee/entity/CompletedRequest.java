package com.google.step.coffee.entity;

import java.time.Duration;
import java.util.List;

public interface CompletedRequest {

  boolean isMatched();

  DateRange getFirstDateRange();

  Duration getDuration();

  String getUserId();
}
