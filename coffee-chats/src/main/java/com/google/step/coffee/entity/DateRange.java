package com.google.step.coffee.entity;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.step.coffee.InvalidEntityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Immutable representation of range between two points in time.
 */
public class DateRange implements Comparable<DateRange> {

  public static final DateRange EMPTY = new DateRange(new Date(0L), new Date(0L));

  private final Date start;
  private final Date end;

  public DateRange(Date start, Date end) {
    this.start = start;
    this.end = end;

    if (start.after(end)) {
      throw new InstantiationError("Start must be before end");
    }
  }

  public DateRange(TimePeriod period) {
    this.start = new Date(period.getStart().getValue());
    this.end = new Date(period.getEnd().getValue());

    if (start.after(end)) {
      throw new InstantiationError("Start must be before end");
    }
  }

  public Date getStart() {
    return start;
  }

  public DateTime getDateTimeStart() {
    return new DateTime(start);
  }

  public Date getEnd() {
    return end;
  }

  public DateTime getDateTimeEnd() {
    return new DateTime(end);
  }

  public Duration getDuration() {
    return Duration.between(start.toInstant(), end.toInstant());
  }

  public TimePeriod toTimePeriod() {
    return new TimePeriod().setStart(this.getDateTimeStart()).setEnd(this.getDateTimeEnd());
  }

  /**
   * Combines two lists of corresponding start and end dates into a list of DateRange objects.
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

  /**
   * Converts TimePeriods returned by Google Calendar API to list of DateRanges.
   */
  public static List<DateRange> toDateRanges(List<TimePeriod> timePeriods) {
    return timePeriods.stream().map(DateRange::new).collect(Collectors.toList());
  }

  /**
   * Creates a new DateRange which combines the range covered by two given ranges.
   */
  public static DateRange getMergedRange(DateRange range1, DateRange range2) {
    if (range1.overlaps(range2) || range1.adjacentTo(range2)) {
      Date mergedStart = range1.getStart().before(range2.getStart()) ?
          range1.getStart() :
          range2.getStart();
      Date mergedEnd = range1.getEnd().after(range2.getEnd()) ?
          range1.getEnd() :
          range2.getEnd();

      return new DateRange(mergedStart, mergedEnd);
    } else {
      return DateRange.EMPTY;
    }
  }

  /**
   * Returns a DateRange that covers the intersection of two ranges.
   */
  public static DateRange getIntersectingRange(DateRange range1, DateRange range2) {
    if (range1.overlaps(range2)) {
      Date overlappingStart = range1.getStart().after(range2.getStart()) ?
          range1.getStart() :
          range2.getStart();

      Date overlappingEnd = range1.getEnd().before(range2.getEnd()) ?
          range1.getEnd() :
          range2.getEnd();

      return new DateRange(overlappingStart, overlappingEnd);
    } else {
      return DateRange.EMPTY;
    }
  }

  @Override
  public int compareTo(DateRange otherRange) {
    return this.getStart().compareTo(otherRange.getStart());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.start, this.end);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DateRange) {
      DateRange otherDateRange = (DateRange) obj;

      return this.start.equals(otherDateRange.start) && this.end.equals(otherDateRange.end);
    }

    return false;
  }

  @Override
  public String toString() {
    return "{Start: " + getStart().toString() + ", End: " + getEnd().toString() + "}";
  }

  /**
   * Removes any time within this range that overlaps with the given range to remove.
   *
   * @param removeRange DateRange of time to remove from the current range.
   * @return List of ranges left as a result of removing anything in removeRange from this range.
   */
  public List<DateRange> removeRange(DateRange removeRange) {
    List<DateRange> splitRanges = new ArrayList<>();

    if (this.overlaps(removeRange)) {
      if (this.getStart().before(removeRange.getStart())) {
        splitRanges.add(new DateRange(this.getStart(), removeRange.getStart()));
      } else if (removeRange.getEnd().before(this.getEnd())) {
        splitRanges.add(new DateRange(removeRange.getEnd(), this.getEnd()));
      }
    } else {
      splitRanges.add(this);
    }

    return splitRanges;
  }

  /**
   * Returns whether given range is strictly adjacent to this range.
   */
  public boolean adjacentTo(DateRange dateRange) {
    return this.getEnd().equals(dateRange.getStart()) ||
        this.getStart().equals(dateRange.getEnd());
  }

  /**
   * Returns whether either this or given range intersects or is adjacent with the other.
   */
  public boolean overlaps(DateRange otherRange) {
    return contains(this, otherRange.getStart()) || contains(otherRange, this.start);
  }

  /**
   * Returns whether this DateRange wholly contains otherRange
   */
  public boolean contains(DateRange otherRange) {
    if (getDuration().isZero()) {
      return false;
    }

    if (otherRange.getDuration().isZero()) {
      return contains(this, otherRange.getStart());
    }

    return contains(this, otherRange.getStart()) &&
        contains(this, getInclusiveEnd(otherRange.getEnd()));
  }

  /**
   * Returns whether the given datetime point is completely contained within this DateRange.
   */
  public boolean contains(Date point) {
    return contains(this, point);
  }

  /**
   * Returns whether the given datetime point is completely contained within the DateRange.
   */
  private static boolean contains(DateRange dateRange, Date point) {
    if (dateRange.getDuration().isZero()) {
      return false;
    }

    if (point.before(dateRange.getStart())) {
      return false;
    }

    return dateRange.getEnd().after(point);
  }

  private static Date getInclusiveEnd(Date date) {
    return Date.from(date.toInstant().minusNanos(1));
  }
}
