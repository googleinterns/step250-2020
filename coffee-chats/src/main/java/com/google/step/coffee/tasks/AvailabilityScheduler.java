package com.google.step.coffee.tasks;

import static com.google.step.coffee.entity.DateRange.getMergedRange;
import static com.google.step.coffee.entity.DateRange.toDateRanges;

import com.google.api.services.calendar.model.TimePeriod;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.entity.Availability;
import com.google.step.coffee.entity.DateRange;
import com.google.step.coffee.entity.EventRequest;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class AvailabilityScheduler {
  private static class TimePoint implements Comparable<TimePoint> {
    public enum Kind {
      START, END
    }

    private Availability availability;
    private Date date;
    private Kind kind;

    public TimePoint(Availability availability, Date date, Kind kind) {
      this.availability = availability;
      this.date = date;
      this.kind = kind;
    }

    public int compareTo(TimePoint that) {
      if (this.date.compareTo(that.date) != 0) {
        return this.date.compareTo(that.date);
      }

      if (getKind() == Kind.END && that.getKind() == Kind.START) {
        return -1;
      }

      if (getKind() == Kind.START && that.getKind() == Kind.END) {
        return 1;
      }

      return 0;
    }

    public boolean equals(TimePoint that) {
      return compareTo(that) == 0;
    }

    Availability getAvailability() {
      return availability;
    }

    Date getDate() {
      return date;
    }

    Kind getKind() {
      return kind;
    }
  }

  private List<String> userIds;
  private List<Availability> availabilities;

  private CalendarUtils utils = new CalendarUtils();

  public AvailabilityScheduler() {
  }

  ;

  public AvailabilityScheduler(List<Availability> requests) {
    this.availabilities = requests;
    this.userIds = requests.stream().map(Availability::getUserId).collect(Collectors.toList());
  }

  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
  }

  public void setAvailabilities(List<Availability> requests) {
    this.availabilities = requests;
  }

  public void setUtils(CalendarUtils utils) {
    this.utils = utils;
  }

  /**
   * Finds suitable ranges of at least minDuration length that is available by all participants.
   */
  public List<DateRange> findAvailableRanges(Duration minDuration) {
    List<DateRange> commonRanges = findCommonRanges(availabilities);
    if (commonRanges.isEmpty()) {
      return Collections.emptyList();
    }

    List<DateRange> rangeOptions = commonRanges.stream()
        .filter(dateRange -> dateRange.getDuration().compareTo(minDuration) >= 0)
        .collect(Collectors.toList());

    List<DateRange> busyRanges = fetchBusyRanges(rangeOptions);

    return removeBusyRanges(rangeOptions, busyRanges).stream()
        .filter(dateRange -> dateRange.getDuration().compareTo(minDuration) >= 0)
        .collect(Collectors.toList());
  }

  /**
   * Finds suitable ranges of at least minDuration length that are available
   * for the maximum number of participants
   */
  public List<DateRange> findAvailableRangesBestEffort(Duration minDuration) {
    if (availabilities.isEmpty()) {
      return new ArrayList<>();
    }

    // Query each user's calendar and remove busy ranges from each request.
    // Then collect it into the list of TimePoints for sorting for further use.
    List<TimePoint> timePoints = getRequestsTimePoints(availabilities.stream()
        .map(request -> EventRequest.builder()
            .setDateRanges(
                removeBusyRanges(
                    request.getDateRanges(),
                    fetchBusyRanges(request.getDateRanges(),
                        Collections.singletonList(request.getUserId()))))
            .setDuration(request.getDuration())
            .setUserId(request.getUserId())
            .build())
        .collect(Collectors.toList()));

    int lowerBound = 0;
    int upperBound = availabilities.size() + 1;

    List<DateRange> result = new ArrayList<>();

    while (upperBound - lowerBound > 1) {
      int midpoint = (upperBound + lowerBound) / 2;

      List<DateRange> ranges = findCommonRangesWithMinimumRequests(timePoints, midpoint).stream()
          .filter(range -> range.getDuration().compareTo(minDuration) >= 0)
          .collect(Collectors.toList());

      if (ranges.isEmpty()) {
        upperBound = midpoint;
      } else {
        lowerBound = midpoint;
        result = ranges;
      }
    }

    return result;
  }

  /**
   * Removes given busy ranges from any range that overlaps in the given options of possible ranges.
   * Package-private scope used for testing purposes only, but can be used directly.
   *
   * @param options    List of DateRanges to attempt to use, which will have busyRanges removed from.
   * @param busyRanges List of DateRanges considered to be 'busy' and to be removed if overlapping.
   * @return List of DateRanges originating from input options, but do not overlap with any of
   * busyRanges.
   */
  List<DateRange> removeBusyRanges(List<DateRange> options, List<DateRange> busyRanges) {
    if (options.isEmpty()) {
      return new ArrayList<>();
    }

    List<DateRange> freeRanges = new ArrayList<>();

    int curIdx = 0;
    DateRange current = options.get(curIdx);

    for (DateRange busy : busyRanges) {
      while (!current.overlaps(busy)) {
        freeRanges.add(current);

        ++curIdx;
        if (curIdx == options.size()) {
          return freeRanges;
        }

        current = options.get(curIdx);
      }

      List<DateRange> split = current.removeRange(busy);

      if (split.size() == 2) {
        freeRanges.add(split.get(0));
        current = split.get(1);
      } else if (split.size() == 1) {
        current = split.get(0);
      } else {
        ++curIdx;
        if (curIdx == options.size()) {
          return freeRanges;
        }

        current = options.get(curIdx);
      }
    }

    freeRanges.add(current);

    for (++curIdx; curIdx < options.size(); ++curIdx) {
      freeRanges.add(options.get(curIdx));
    }

    return freeRanges;
  }

  /**
   * Fetch busy ranges within the given ranges.
   * Package-private scope used for testing purposes rather than direct usage.
   *
   * @param ranges  List of ranges to search for busy ranges within.
   * @param userIds List of user ids to consider busy
   * @return list of ranges where at least one user within participants is busy within the given
   * ranges.
   */
  List<DateRange> fetchBusyRanges(List<DateRange> ranges, List<String> userIds) {
    List<DateRange> busyRanges = new ArrayList<>();

    for (DateRange range : ranges) {
      for (String id : userIds) {
        List<TimePeriod> timePeriods =
            utils.getFreeBusy(id, range.getDateTimeStart(), range.getDateTimeEnd());

        busyRanges.addAll(toDateRanges(timePeriods));
      }
    }

    return coalesceRanges(busyRanges);
  }

  List<DateRange> fetchBusyRanges(List<DateRange> ranges) {
    return fetchBusyRanges(ranges, userIds);
  }

  /**
   * Find intersecting ranges common to all requests' DateRanges.
   *
   * @param requests Array of ChatRequest objects from which to find common ranges.
   * @return List of DateRanges which are contained within all requests' possible ranges.
   */
  public List<DateRange> findCommonRanges(List<Availability> requests) {
    List<DateRange> commonRanges = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      List<DateRange> coalescedRanges = coalesceRanges(requests.get(i).getDateRanges());

      if (i != 0) {
        commonRanges = extractIntersectingRanges(commonRanges, coalescedRanges);

        if (commonRanges.isEmpty()) {
          return Collections.emptyList();
        }
      } else {
        commonRanges.addAll(coalescedRanges);
      }
    }

    return commonRanges;
  }

  private List<TimePoint> getRequestsTimePoints(List<Availability> requests) {
    List<TimePoint> result = new ArrayList<>();

    for (Availability request : requests) {
      for (DateRange range : coalesceRanges(request.getDateRanges())) {
        result.add(new TimePoint(request, range.getStart(), TimePoint.Kind.START));
        result.add(new TimePoint(request, range.getEnd(), TimePoint.Kind.END));
      }
    }

    result.sort(TimePoint::compareTo);

    return result;
  }

  /**
   * Find intersecting ranges, that at least the specified number of requests
   *
   * @param points List of TimePoints of corresponding requests, sorted in ascending order
   * @param num    Minimum number of ranges to intersect, must be > 0
   * @return List of DateRanges which are contained within at least num requests' possible ranges.
   */
  private List<DateRange> findCommonRangesWithMinimumRequests(List<TimePoint> points, int num) {
    assert num > 0;

    List<DateRange> result = new ArrayList<>();
    int availableNow = 0;

    Date segmentStart = new Date(0);

    for (TimePoint point : points) {
      Date segmentEnd = point.getDate();

      if (availableNow >= num) {
        result.add(new DateRange(segmentStart, segmentEnd));
      }

      if (point.getKind() == TimePoint.Kind.START) {
        availableNow++;
      } else {
        availableNow--;
      }

      segmentStart = segmentEnd;
    }

    return coalesceRanges(result);
  }

  /**
   * Takes two lists of coalesced DateRanges and returns a list of DateRanges which contain times
   * common to both lists.
   * Note this is a package-private method to allow for testing, but could be used directly given
   * ranges have been coalesced and sorted.
   */
  List<DateRange> extractIntersectingRanges(List<DateRange> ranges1, List<DateRange> ranges2) {
    List<DateRange> intersectingRanges = new ArrayList<>();

    int i = 0;
    int j = 0;

    while (i != ranges1.size() && j != ranges2.size()) {
      DateRange range1 = ranges1.get(i);
      DateRange range2 = ranges2.get(j);

      if (range1.overlaps(range2)) {
        intersectingRanges.add(DateRange.getIntersectingRange(range1, range2));
      }

      // Remove range with earliest end, so that other range may find an intersection.
      if (range1.getEnd().before(range2.getEnd())) {
        i++;
      } else if (range2.getEnd().before(range1.getEnd())) {
        j++;
      } else {
        i++;
        j++;
      }
    }

    return intersectingRanges;
  }

  /**
   * Combine DateRanges which have adjacent or overlapping times.
   *
   * @return sorted list of DateRange objects which are the minimal number of DateRanges to describe
   * the given ranges.
   */
  public List<DateRange> coalesceRanges(List<DateRange> dateRanges) {
    Collections.sort(dateRanges);

    List<DateRange> combinedRanges = new ArrayList<>();

    for (DateRange dateRange : dateRanges) {
      mergeWithLastRange(combinedRanges, dateRange);
    }

    return combinedRanges;
  }

  private void mergeWithLastRange(List<DateRange> combinedRanges, DateRange dateRange) {
    if (combinedRanges.isEmpty()) {
      combinedRanges.add(dateRange);
      return;
    }

    DateRange lastRange = combinedRanges.get(combinedRanges.size() - 1);

    if (lastRange.overlaps(dateRange) || lastRange.adjacentTo(dateRange)) {
      if (!lastRange.contains(dateRange)) {
        DateRange mergedRange = getMergedRange(dateRange, lastRange);

        combinedRanges.set(combinedRanges.size() - 1, mergedRange);
      }
    } else {
      combinedRanges.add(dateRange);
    }
  }
}
