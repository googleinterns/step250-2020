package com.google.step.coffee.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import org.junit.Test;

public class DateRangeTest {

  Date DAY1_MORNING = new Date(1601888400000L);
  Date DAY1_AFTERNOON = new Date(1601899200000L);
  Date DAY1_EVENING = new Date(1601920800000L);
  Date DAY2_MORNING = new Date(1601974800000L);
  Date DAY2_AFTERNOON = new Date(1601985600000L);
  Date DAY2_EVENING = new Date(1602007200000L);

  /**
   * Tests if a point inside a range is contained.
   *
   * Range:   |-----|
   * Point:      x
   */
  @Test
  public void rangeContainsPointInside() {
    DateRange range = new DateRange(DAY1_MORNING, DAY1_EVENING);

    assertThat(range.contains(DAY1_AFTERNOON), is(true));
  }

  /**
   * Tests if a point the same as the range's start is contained.
   *
   * Range:   |-----|
   * Point:   x
   */
  @Test
  public void rangeContainsStartPoint() {
    DateRange range = new DateRange(DAY1_MORNING, DAY1_EVENING);

    assertThat(range.contains(DAY1_MORNING), is(true));
  }

  /**
   * Tests if a point the same as range's end is contained.
   * In this implementation, the range's end represents the point at which the range ends
   * (i.e. it is an exclusive point end of the range).
   *
   * Range:   |-----|
   * Point:         x
   */
  @Test
  public void rangeDoesNotContainEndPoint() {
    DateRange range = new DateRange(DAY1_MORNING, DAY1_EVENING);

    assertThat(range.contains(DAY1_EVENING), is(false));
  }

  /**
   * Tests if a point before a range is contained.
   *
   * Range:   |-----|
   * Point: x
   */
  @Test
  public void rangeDoesNotContainPointBefore() {
    DateRange range = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);

    assertThat(range.contains(DAY1_MORNING), is(false));
  }

  /**
   * Tests if a point after a range is contained.
   *
   * Range:   |-----|
   * Point:           x
   */
  @Test
  public void rangeDoesNotContainPointAfter() {
    DateRange range = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);

    assertThat(range.contains(DAY1_EVENING), is(false));
  }

  /**
   * Tests if a bigger range completely contains a smaller range.
   *
   * Enclosing Range:    |-------|
   * Contained Range:      |---|
   */
  @Test
  public void rangeContainsCompletelyContainedRange() {
    DateRange enclosingRange = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange containedRange = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);

    assertThat(enclosingRange.contains(containedRange), is(true));
  }

  /**
   * Tests if a range contains a smaller range with the same start.
   *
   * Enclosing Range:    |-------|
   * Contained Range:    |---|
   */
  @Test
  public void rangeContainsSameStartRange() {
    DateRange enclosingRange = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange containedRange = new DateRange(DAY1_MORNING, DAY1_EVENING);

    assertThat(enclosingRange.contains(containedRange), is(true));
  }

  /**
   * Tests if a range contains a smaller range with the same end.
   *
   * Enclosing Range:    |-------|
   * Contained Range:        |---|
   */
  @Test
  public void rangeContainsSameEndRange() {
    DateRange enclosingRange = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange containedRange = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);

    assertThat(enclosingRange.contains(containedRange), is(true));
  }

  /**
   * Tests if a range contains an identical range.
   *
   * Enclosing Range:    |-------|
   * Contained Range:    |-------|
   */
  @Test
  public void rangeContainsIdenticalRange() {
    DateRange enclosingRange = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange containedRange = new DateRange(DAY1_MORNING, DAY2_MORNING);

    assertThat(enclosingRange.contains(containedRange), is(true));
  }

  /**
   * Tests if a range contains an overlapping range.
   *
   * Enclosing Range:       |------|
   * Overlapping Ranges: |--1--||--2--|
   */
  @Test
  public void rangeDoesNotContainOnlyOverlappingRanges() {
    DateRange enclosingRange = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);
    DateRange overlappingRange1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange overlappingRange2 = new DateRange(DAY1_EVENING, DAY2_AFTERNOON);

    assertThat(enclosingRange.contains(overlappingRange1), is(false));
    assertThat(enclosingRange.contains(overlappingRange2), is(false));
  }

  /**
   * Tests if a range contains a touching range.
   *
   * Enclosing Range:         |---|
   * Touching Ranges:   |--1--|   |--2--|
   */
  @Test
  public void rangeDoesNotContainTouchingRanges() {
    DateRange enclosingRange = new DateRange(DAY1_EVENING, DAY2_MORNING);
    DateRange touchingRange1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange touchingRange2 = new DateRange(DAY2_MORNING, DAY2_EVENING);

    assertThat(enclosingRange.contains(touchingRange1), is(false));
    assertThat(enclosingRange.contains(touchingRange2), is(false));
  }

  /**
   * Tests if a range contains a disjoint range.
   *
   * Enclosing Range:        |--|
   * Disjoint Ranges:   |-1-|    |-2-|
   */
  @Test
  public void rangeDoesNotContainDisjointRanges() {
    DateRange enclosingRange = new DateRange(DAY1_EVENING, DAY2_MORNING);
    DateRange disjointRange1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange disjointRange2 = new DateRange(DAY2_AFTERNOON, DAY2_EVENING);

    assertThat(enclosingRange.contains(disjointRange1), is(false));
    assertThat(enclosingRange.contains(disjointRange2), is(false));
  }

  /**
   * Tests if a range overlaps contained ranges.
   *
   * Range:               |---------|
   * Contained Ranges:       |-1-|
   *                      |----2----|
   *                      |-3-| |-4-|
   */
  @Test
  public void rangeOverlapsContainedRanges() {
    DateRange range = new DateRange(DAY1_MORNING, DAY2_MORNING);

    DateRange containedRange1 = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange containedRange2 = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange containedRange3 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange containedRange4 = new DateRange(DAY1_EVENING, DAY2_MORNING);

    assertThat(range.overlaps(containedRange1), is(true));
    assertThat(range.overlaps(containedRange2), is(true));
    assertThat(range.overlaps(containedRange3), is(true));
    assertThat(range.overlaps(containedRange4), is(true));
  }

  /**
   * Tests if a range overlaps overlapping ranges.
   *
   * Range:                 |-------|
   * Overlapping Ranges:  |-1-|   |-2-|
   */
  @Test
  public void rangeOverlapsOverlappingRanges() {
    DateRange range = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);

    DateRange overlappingRange1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange overlappingRange2 = new DateRange(DAY1_EVENING, DAY2_AFTERNOON);

    assertThat(range.overlaps(overlappingRange1), is(true));
    assertThat(range.overlaps(overlappingRange2), is(true));
  }

  /**
   * Tests if a range overlaps adjacent ranges.
   *
   * Range:                 |----|
   * Adjacent Ranges: |--1--|    |--2--|
   */
  @Test
  public void rangeDoesNotOverlapAdjacentRanges() {
    DateRange range = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);

    DateRange touchingRangeStart = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange touchingRangeEnd = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);

    assertThat(range.overlaps(touchingRangeStart), is(false));
    assertThat(range.overlaps(touchingRangeEnd), is(false));
  }

  /**
   * Tests if a range is adjacent to overlapping ranges.
   *
   * Range:                    |-------|
   * Overlapping Ranges:   |--1--|   |--2--|
   * Contained Ranges:         |---1---|
   *                             |-2-|
   */
  @Test
  public void rangeNotAdjacentToOverlappingRanges() {
    DateRange range = new DateRange(DAY1_AFTERNOON, DAY2_AFTERNOON);

    DateRange overlappingRange1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange overlappingRange2 = new DateRange(DAY2_MORNING, DAY2_EVENING);
    DateRange containedRange1 = new DateRange(DAY1_AFTERNOON, DAY2_AFTERNOON);
    DateRange containedRange2 = new DateRange(DAY1_EVENING, DAY2_MORNING);

    assertThat(range.adjacentTo(overlappingRange1), is(false));
    assertThat(range.adjacentTo(overlappingRange2), is(false));
    assertThat(range.adjacentTo(containedRange1), is(false));
    assertThat(range.adjacentTo(containedRange2), is(false));
  }

  /**
   * Tests if a range is adjacent to touching ranges.
   *
   * Range:                    |-------|
   * Touching Ranges:    |--1--|       |--2--|
   */
  @Test
  public void rangeAdjacentToTouchingRanges() {
    DateRange range = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);
    DateRange touchingRangeStart = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange touchingRangeEnd = new DateRange(DAY1_EVENING, DAY2_MORNING);

    assertThat(range.adjacentTo(touchingRangeStart), is(true));
    assertThat(range.adjacentTo(touchingRangeEnd), is(true));
  }

  /**
   * Tests if a range is adjacent to disjoint ranges.
   *
   * Range:                  |---|
   * Disjoint Ranges:  |-1-|       |-2-|
   */
  @Test
  public void rangeNotAdjacentToDisjointRanges() {
    DateRange range = new DateRange(DAY1_EVENING, DAY2_MORNING);
    DateRange disjointRange1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange disjointRange2 = new DateRange(DAY2_AFTERNOON, DAY2_EVENING);

    assertThat(range.adjacentTo(disjointRange1), is(false));
    assertThat(range.adjacentTo(disjointRange2), is(false));
  }

  @Test
  public void intersectionOfDisjointAndAdjacentRangesIsEmpty() {
    DateRange range1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange range2 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);
    DateRange adjacentRange = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);

    assertThat(DateRange.getIntersectingRange(range1, range2), is(DateRange.EMPTY));
    assertThat(DateRange.getIntersectingRange(range1, adjacentRange), is(DateRange.EMPTY));
    assertThat(DateRange.getIntersectingRange(adjacentRange, range2), is(DateRange.EMPTY));
  }

  @Test
  public void intersectionOfOverlappingRanges() {
    DateRange range1 = new DateRange(DAY1_MORNING, DAY1_EVENING);
    DateRange range2 = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);
    DateRange enclosingRange = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange containedRange = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);

    DateRange expectedIntersection = new DateRange(DAY1_AFTERNOON, DAY1_EVENING);

    assertThat(DateRange.getIntersectingRange(range1, range1), is(range1));
    assertThat(DateRange.getIntersectingRange(range1, range2), is(expectedIntersection));
    assertThat(DateRange.getIntersectingRange(enclosingRange, containedRange), is(containedRange));
  }

  @Test
  public void mergedRangeOfConnectedRanges() {
    DateRange range = new DateRange(DAY1_AFTERNOON, DAY2_MORNING);

    DateRange overlappingRange1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange overlappingRange2 = new DateRange(DAY1_EVENING, DAY2_AFTERNOON);
    DateRange adjacentRange1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange adjacentRange2 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);

    DateRange expectedOverlapMerge1 = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange expectedOverlapMerge2 = new DateRange(DAY1_AFTERNOON, DAY2_AFTERNOON);
    DateRange expectedAdjacentMerge1 = new DateRange(DAY1_MORNING, DAY2_MORNING);
    DateRange expectedAdjacentMerge2 = new DateRange(DAY1_AFTERNOON, DAY2_AFTERNOON);

    assertThat(DateRange.getMergedRange(range, overlappingRange1), is(expectedOverlapMerge1));
    assertThat(DateRange.getMergedRange(range, overlappingRange2), is(expectedOverlapMerge2));
    assertThat(DateRange.getMergedRange(range, adjacentRange1), is(expectedAdjacentMerge1));
    assertThat(DateRange.getMergedRange(range, adjacentRange2), is(expectedAdjacentMerge2));
  }

  @Test
  public void mergingDisjointRangesReturnsEmpty() {
    DateRange range1 = new DateRange(DAY1_MORNING, DAY1_AFTERNOON);
    DateRange range2 = new DateRange(DAY2_MORNING, DAY2_AFTERNOON);

    assertThat(DateRange.getMergedRange(range1, range2), is(DateRange.EMPTY));
  }
}
