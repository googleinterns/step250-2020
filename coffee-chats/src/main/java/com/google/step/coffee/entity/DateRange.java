package com.google.step.coffee.entity;

import com.google.step.coffee.InvalidEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateRange {

  private final Date start;
  private final Date end;

  public DateRange(Date start, Date end) {
    this.start = start;
    this.end = end;
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

  /**
   * Combine two lists of corresponding start and end dates into a single list of DateRange objects.
   */
  public static List<DateRange> combineLists(List<Date> startDates, List<Date> endDates)
      throws InvalidEntityException {
    List<DateRange> dateRanges = new ArrayList<>();

    for (int i = 0; i < startDates.size(); i++) {
      if (startDates.get(i).before(endDates.get(i))) {
        DateRange dateRange = new DateRange(startDates.get(i), endDates.get(i));
        dateRanges.add(dateRange);
      } else {
        throw new InvalidEntityException("Start date must be before corresponding end date");
      }
    }

    return dateRanges;
  }
}
