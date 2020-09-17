package com.google.step.coffee.tasks;

import static com.google.step.coffee.entity.DateRange.getMergedRange;
import static com.google.step.coffee.entity.DateRange.toDateRanges;

import com.google.api.services.calendar.model.TimePeriod;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.entity.Availability;
import com.google.step.coffee.entity.DateRange;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AvailabilityScheduler {

  private List<String> userIds;
  private List<Availability> availabilities;

  private CalendarUtils utils = new CalendarUtils();

  public AvailabilityScheduler() {};

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
   * Removes given busy ranges from any range that overlaps in the given options of possible ranges.
   * Package-private scope used for testing purposes only, but can be used directly.
   *
   * @param options List of DateRanges to attempt to use, which will have busyRanges removed from.
   * @param busyRanges List of DateRanges considered to be 'busy' and to be removed if overlapping.
   * @return List of DateRanges originating from input options, but do not overlap with any of
   * busyRanges.
   */
  List<DateRange> removeBusyRanges(List<DateRange> options, List<DateRange> busyRanges) {
    List<DateRange> freeRanges = new ArrayList<>();

    int i = 0;
    int j = 0;

    while (i != options.size() && j != busyRanges.size()) {
      DateRange freeRange = options.get(i);
      DateRange busyRange = busyRanges.get(j);

      if (freeRange.overlaps(busyRange)) {
        List<DateRange> splitRanges = freeRange.removeRange(busyRange);

        if (splitRanges.isEmpty()) {
          i++;
        } else {
          options.set(i, splitRanges.remove(splitRanges.size() - 1));
          freeRanges.addAll(splitRanges);
        }
      } else {
        if (!busyRange.getEnd().after(freeRange.getStart())) {
          j++;
        } else if (!busyRange.getStart().before(freeRange.getEnd())) {
          freeRanges.add(freeRange);
          i++;
        }
      }
    }

    for (; i < options.size(); i++) {
      freeRanges.add(options.get(i));
    }

    return freeRanges;
  }

  /**
   * Fetch busy ranges within the given ranges.
   * Package-private scope used for testing purposes rather than direct usage.
   *
   * @param ranges List of ranges to search for busy ranges within.
   * @return list of ranges where at least one user within participants is busy within the given
   * ranges.
   * */
  List<DateRange> fetchBusyRanges(List<DateRange> ranges) {
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
      } else if(range2.getEnd().before(range1.getEnd())) {
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
