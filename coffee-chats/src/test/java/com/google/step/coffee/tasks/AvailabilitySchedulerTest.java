package com.google.step.coffee.tasks;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.step.coffee.InvalidEntityException;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.ChatRequestBuilder;
import com.google.step.coffee.entity.DateRange;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;

public class AvailabilitySchedulerTest {

  AvailabilityScheduler scheduler = new AvailabilityScheduler();

  Date DAY1_MORNING = new Date(1601888400000L);
  Date DAY1_AFTERNOON = new Date(1601899200000L);
  Date DAY1_EVENING = new Date(1601920800000L);
  Date DAY2_MORNING = new Date(1601974800000L);
  Date DAY2_AFTERNOON = new Date(1601985600000L);
  Date DAY2_EVENING = new Date(1602007200000L);
  Date DAY3_MORNING = new Date(1602061200000L);
  Date DAY3_AFTERNOON = new Date(1602072000000L);
  Date DAY3_EVENING = new Date(1602093600000L);
  Date DAY4_MORNING = new Date(1602147600000L);
  Date DAY4_AFTERNOON = new Date(1602158400000L);
  Date DAY4_EVENING = new Date(1602180000000L);

  /**
   * Tests case of merging completely disjoint ranges.
   *
   * Ranges:    |-----|   |----|
   * Expected:  |-----|   |----|
   */
  @Test
  public void separateRangesDoNotMerge() {
    DateRange range1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange range2 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);

    List<DateRange> ranges = Arrays.asList(range1, range2);

    assertThat(scheduler.coalesceRanges(ranges), contains(range1, range2));
  }

  /**
   * Tests case of merging touching ranges.
   *
   * Ranges:    |-----|
   *                  |----|
   * Expected:  |----------|
   */
  @Test
  public void touchingRangesMerge() {
    DateRange range1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange range2 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);

    List<DateRange> ranges = Arrays.asList(range1, range2);

    DateRange expectedRange = new DateRange(DAY1_MORNING, DAY1_EVENING);

    assertThat(scheduler.coalesceRanges(ranges), contains(expectedRange));
  }

  /**
   * Tests case of varying ranges being coalesced.
   *
   * Ranges:      |-1-| |--3--| |-4-| |--5--|
   *            |----2----|           |-6-|
   * Expected:  |------A------| |-B-| |--C--|
   */
  @Test
  public void mergeVariedRanges() {
    DateRange range1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange range2 = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange range3 = new DateRange(DAY1_EVENING, DAY2_AFTERNOON);
    DateRange range4 = new DateRange(DAY2_EVENING, DAY3_MORNING);
    DateRange range5 = new DateRange(DAY3_AFTERNOON, DAY4_MORNING);
    DateRange range6 = new DateRange(DAY3_AFTERNOON, DAY3_EVENING);

    List<DateRange> ranges = Arrays.asList(range1, range2, range3, range4, range5, range6);

    DateRange expectedRangeA = new DateRange(DAY1_MORNING, DAY2_AFTERNOON);
    DateRange expectedRangeB = range4;
    DateRange expectedRangeC = range5;

    assertThat(scheduler.coalesceRanges(ranges),
        contains(expectedRangeA, expectedRangeB, expectedRangeC));
  }

  /**
   * Tests finding intersections of ranges between two non-overlapping lists of ranges.
   *
   * RangesA:   |-A1-|    |-A2-|
   * RangesB:        |-B1-|       |-B2-|
   * Exepected: none
   */
  @Test
  public void noOverlappingRangesGivesNoIntersections() {
    DateRange rangeA1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange rangeA2 = new DateRange(DAY1_EVENING, DAY2_MORNING);
    DateRange rangeB1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange rangeB2 = new DateRange(DAY2_AFTERNOON, DAY2_EVENING);

    List<DateRange> rangesA = Arrays.asList(rangeA1, rangeA2);
    List<DateRange> rangesB = Arrays.asList(rangeB1, rangeB2);

    assertThat(scheduler.extractIntersectingRanges(rangesA, rangesB), is(Collections.emptyList()));
  }

  /**
   * Tests finding intersections of ranges between two overlapping lists of ranges.
   *
   * RangesA:   |--A1--|  |-A2-| |-A3-|
   * RangesB:      |-B1-| |--B2--|
   * Exepected:    |-1-|  |--2-|
   */
  @Test
  public void overlappingRangesGivesIntersections() {
    DateRange rangeA1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange rangeA2 = new DateRange(DAY2_AFTERNOON, DAY2_EVENING);
    DateRange rangeA3 = new DateRange(DAY3_MORNING, DAY3_AFTERNOON);
    DateRange rangeB1 = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);
    DateRange rangeB2 = new DateRange(DAY2_AFTERNOON, DAY3_MORNING);

    List<DateRange> rangesA = Arrays.asList(rangeA1, rangeA2, rangeA3);
    List<DateRange> rangesB = Arrays.asList(rangeB1, rangeB2);

    DateRange expectedRange1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange expectedRange2 = rangeA2;

    assertThat(scheduler.extractIntersectingRanges(rangesA, rangesB),
        contains(expectedRange1, expectedRange2));
  }

  /**
   * Tests whether common ranges can be found between several requests.
   *
   * RequestA:    |--A1--|  |---A2---|
   * RequestB:        |--B1--|  |-B2-|
   * RequestC:        |C1|      |--C2--|
   * Expected:        |1-|      |-2--|
   */
  @Test
  public void findsCommonRangesBetweenRequests() throws InvalidEntityException {
    DateRange rangeA1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange rangeA2 = new DateRange(DAY2_MORNING, DAY3_MORNING);
    DateRange rangeB1 = new DateRange(DAY1_AFTERNOON, DAY2_AFTERNOON);
    DateRange rangeB2 = new DateRange(DAY2_EVENING, DAY3_MORNING);
    DateRange rangeC1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange rangeC2 = new DateRange(DAY2_EVENING, DAY3_AFTERNOON);

    List<DateRange> rangesA = Arrays.asList(rangeA1, rangeA2);
    List<DateRange> rangesB = Arrays.asList(rangeB1, rangeB2);
    List<DateRange> rangesC = Arrays.asList(rangeC1, rangeC2);

    ChatRequest requestA = new ChatRequestBuilder().onDates(rangesA).build();
    ChatRequest requestB = new ChatRequestBuilder().onDates(rangesB).build();
    ChatRequest requestC = new ChatRequestBuilder().onDates(rangesC).build();

    DateRange expectedRange1 = rangeC1;
    DateRange expectedRange2 = rangeB2;

    assertThat(scheduler.findCommonRanges(Arrays.asList(requestA, requestB, requestC)),
        contains(expectedRange1, expectedRange2));
  }


  /**
   * Tests whether common ranges can be found between several non-overlapping requests.
   *
   * RequestA:    |--A1--|  |--A2---|
   * RequestB:       |--B1--|  |-B2-|
   * RequestC:    |C1|      |C2|      |--C3--|
   * Expected: none
   */
  @Test
  public void noCommonRangesBetweenRequests() throws InvalidEntityException {
    DateRange rangeA1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange rangeA2 = new DateRange(DAY2_MORNING, DAY2_EVENING);
    DateRange rangeB1 = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);
    DateRange rangeB2 = new DateRange(DAY2_AFTERNOON, DAY2_EVENING);
    DateRange rangeC1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange rangeC2 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);
    DateRange rangeC3 = new DateRange(DAY3_MORNING, DAY3_AFTERNOON);

    List<DateRange> rangesA = Arrays.asList(rangeA1, rangeA2);
    List<DateRange> rangesB = Arrays.asList(rangeB1, rangeB2);
    List<DateRange> rangesC = Arrays.asList(rangeC1, rangeC2, rangeC3);

    ChatRequest requestA = new ChatRequestBuilder().onDates(rangesA).build();
    ChatRequest requestB = new ChatRequestBuilder().onDates(rangesB).build();
    ChatRequest requestC = new ChatRequestBuilder().onDates(rangesC).build();

    assertThat(scheduler.findCommonRanges(Arrays.asList(requestA, requestB, requestC)),
        is(Collections.emptyList()));
  }

  /**
   * Tests whether combined busy ranges are combined correctly and overlap with given ranges.
   *
   * Given:               |--------|    |--|   |-------|
   * userA Busy Ranges:      |--|     |------|
   * userB Busy Ranges:   none
   * userC Busy Ranges:         |--|         |---|  |--|
   * Expected:               |-----|  |----------|  |--|
   * */
  @Test
  public void fetchedBusyRangesLieWithinGivenRanges() {
    List<String> participants = new ArrayList<>();
    participants.add("userA");
    participants.add("userB");
    participants.add("userC");

    scheduler.setUserIds(participants);

    DateRange range1 = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange range2 = new DateRange(DAY2_EVENING, DAY3_MORNING);
    DateRange range3 = new DateRange(DAY3_EVENING, DAY4_EVENING);

    List<DateRange> givenRanges = Arrays.asList(range1, range2, range3);

    TimePeriod periodA1 = new TimePeriod().setStart(new DateTime(DAY1_AFTERNOON)).setEnd(new DateTime(DAY1_EVENING));
    TimePeriod periodA2 = new TimePeriod().setStart(new DateTime(DAY2_AFTERNOON)).setEnd(new DateTime(DAY3_AFTERNOON));
    TimePeriod periodC1 = new TimePeriod().setStart(new DateTime(DAY1_EVENING)).setEnd(new DateTime(DAY2_MORNING));
    TimePeriod periodC2 = new TimePeriod().setStart(new DateTime(DAY3_AFTERNOON)).setEnd(new DateTime(DAY4_MORNING));
    TimePeriod periodC3 = new TimePeriod().setStart(new DateTime(DAY4_AFTERNOON)).setEnd(new DateTime(DAY4_EVENING));

    List<TimePeriod> freeBusyA1 = Collections.singletonList(periodA1);
    List<TimePeriod> freeBusyA2 = Collections.singletonList(periodA2);
    List<TimePeriod> freeBusyC1 = Collections.singletonList(periodC1);
    List<TimePeriod> freeBusyC3 = Arrays.asList(periodC2, periodC3);

    CalendarUtils utils = Mockito.mock(CalendarUtils.class);
    when(utils.getFreeBusy("userA", range1.getDateTimeStart(), range1.getDateTimeEnd()))
        .thenReturn(freeBusyA1);
    when(utils.getFreeBusy("userA", range2.getDateTimeStart(), range2.getDateTimeEnd()))
        .thenReturn(freeBusyA2);
    when(utils.getFreeBusy("userA", range3.getDateTimeStart(), range3.getDateTimeEnd()))
        .thenReturn(Collections.emptyList());
    when(utils.getFreeBusy(eq("userB"), any(), any())).thenReturn(Collections.emptyList());
    when(utils.getFreeBusy("userC", range1.getDateTimeStart(), range1.getDateTimeEnd()))
        .thenReturn(freeBusyC1);
    when(utils.getFreeBusy("userC", range2.getDateTimeStart(), range2.getDateTimeEnd()))
        .thenReturn(Collections.emptyList());
    when(utils.getFreeBusy("userC", range3.getDateTimeStart(), range3.getDateTimeEnd()))
        .thenReturn(freeBusyC3);

    scheduler.setUtils(utils);

    DateRange expectedRange1 = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);
    DateRange expectedRange2 = new DateRange(DAY2_AFTERNOON, DAY4_MORNING);
    DateRange expectedRange3 = new DateRange(DAY4_AFTERNOON, DAY4_EVENING);

    assertThat(scheduler.fetchBusyRanges(givenRanges),
        contains(expectedRange1, expectedRange2, expectedRange3));
  }

  /**
   * Tests what resultant ranges are when removing ranges that do no overlap.
   *
   * Given Ranges:    |--|  |--|      |----|
   * Busy Ranges:        |--|    |--|
   * Expected:        |--|  |--|      |----|
   * */
  @Test
  public void removesNoRangesIfNoIntersect() {
    DateRange range1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange range2 = new DateRange(DAY1_EVENING, DAY2_MORNING);
    DateRange range3 = new DateRange(DAY3_MORNING, DAY3_EVENING);

    DateRange busyRange1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange busyRange2 = new DateRange(DAY2_AFTERNOON, DAY2_EVENING);

    List<DateRange> options = Arrays.asList(range1, range2, range3);
    List<DateRange> busyRanges = Arrays.asList(busyRange1, busyRange2);

    assertThat(scheduler.removeBusyRanges(options, busyRanges), contains(range1, range2, range3));
  }

  /**
   * Tests what resultant ranges are when removing ranges that overlap.
   *
   * Given Ranges:    |-----|  |--|  |----|
   * Busy Ranges:        |--|  |-----|
   * Expected:        |--|           |----|
   * */
  @Test
  public void removesRangesInIntersections() {
    DateRange range1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange range2 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);
    DateRange range3 = new DateRange(DAY2_EVENING, DAY3_AFTERNOON);

    DateRange busyRange1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange busyRange2 = new DateRange(DAY2_MORNING, DAY2_EVENING);

    List<DateRange> options = Arrays.asList(range1, range2, range3);
    List<DateRange> busyRanges = Arrays.asList(busyRange1, busyRange2);

    DateRange expectedRange1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange expectedRange2 = range3;

    assertThat(scheduler.removeBusyRanges(options, busyRanges),
        contains(expectedRange1, expectedRange2));
  }

  /**
   * Tests if can correctly find available slot common to all users.
   *
   * UserA ranges:  |-----|  |--|  |-------|
   * UserA busy:    |--|        |-----|
   * UserB ranges:  |-----|     |----------|
   * UserB busy:             |--|  |--|
   * UserC ranges:     |--|     |-----|
   * UserC busy:    |--|
   *
   * Expected:         |--|
   */
  @Test
  public void findsSuitableAvailableRange() throws InvalidEntityException {
    DateRange rangeA1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange rangeA2 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);
    DateRange rangeA3 = new DateRange(DAY2_EVENING, DAY3_EVENING);
    DateRange rangeB1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange rangeB2 = new DateRange(DAY2_AFTERNOON, DAY3_EVENING);
    DateRange rangeC1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange rangeC2 = new DateRange(DAY2_AFTERNOON, DAY3_MORNING);

    DateRange busyA1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange busyA2 = new DateRange(DAY2_AFTERNOON, DAY3_MORNING);
    DateRange busyB1 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);
    DateRange busyB2 = new DateRange(DAY2_EVENING, DAY3_MORNING);
    DateRange busyC1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);

    ChatRequest requestA = new ChatRequestBuilder()
        .onDates(Arrays.asList(rangeA1, rangeA2, rangeA3))
        .build();
    ChatRequest requestB = new ChatRequestBuilder()
        .onDates(Arrays.asList(rangeB1, rangeB2))
        .build();
    ChatRequest requestC = new ChatRequestBuilder()
        .onDates(Arrays.asList(rangeC1, rangeC2))
        .build();

    scheduler.setUserIds(Arrays.asList("userA", "userB", "userC"));
    scheduler.setChatRequests(requestA, requestB, requestC);

    DateRange commonRange1 = rangeC1;
    DateRange commonRange2 = busyB2;

    CalendarUtils utils = Mockito.mock(CalendarUtils.class);
    when(utils.getFreeBusy("userA", commonRange1.getDateTimeStart(), commonRange1.getDateTimeEnd()))
        .thenReturn(Collections.emptyList());
    when(utils.getFreeBusy("userB", commonRange1.getDateTimeStart(), commonRange1.getDateTimeEnd()))
        .thenReturn(Collections.emptyList());
    when(utils.getFreeBusy("userC", commonRange1.getDateTimeStart(), commonRange1.getDateTimeEnd()))
        .thenReturn(Collections.emptyList());
    when(utils.getFreeBusy("userA", commonRange2.getDateTimeStart(), commonRange2.getDateTimeEnd()))
        .thenReturn(Collections.singletonList(busyA2.toTimePeriod()));
    when(utils.getFreeBusy("userB", commonRange2.getDateTimeStart(), commonRange2.getDateTimeEnd()))
        .thenReturn(Collections.singletonList(busyB2.toTimePeriod()));
    when(utils.getFreeBusy("userC", commonRange2.getDateTimeStart(), commonRange2.getDateTimeEnd()))
        .thenReturn(Collections.emptyList());

    scheduler.setUtils(utils);

    DateRange expected = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);

    assertThat(scheduler.findAvailableRanges(Duration.ofMinutes(30)), contains(expected));
  }
}
