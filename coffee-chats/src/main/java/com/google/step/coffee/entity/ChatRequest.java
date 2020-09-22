package com.google.step.coffee.entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/** Represents a request made by a user for a chat on given topics. */
public class ChatRequest implements Availability {

  private final List<String> tags;
  private Date lastEndDate;
  private final List<DateRange> dateRanges;
  private final int minPeople;
  private final int maxPeople;
  private final Duration duration;
  private final boolean matchRandom;
  private final boolean matchRecents;
  private final String userId;

  private long requestId;
  private boolean hasRequestId;

  public ChatRequest(List<String> tags, List<DateRange> dateRanges, int minPeople, int maxPeople,
      Duration duration, boolean matchRandom, boolean matchRecents, String userId) {
    this.tags = tags;
    this.dateRanges = dateRanges;
    this.minPeople = minPeople;
    this.maxPeople = maxPeople;
    this.duration = duration;
    this.matchRandom = matchRandom;
    this.matchRecents = matchRecents;
    this.userId = userId;
  }

  /**
   * Get topics specified in the request.
   *
   * @return A list of strings representing each tagged interest.
   */
  public List<String> getTags() {
    return tags;
  }

  /**
   * Get date ranges to schedule chat on from user's request.
   *
   * @return A list of DateRange objects representing each datetime range selected by the user.
   */
  public List<DateRange> getDateRanges() {
    return dateRanges;
  }

  /**
   * Get start dates for ranges of chat request for storage in datastore.
   *
   * @return A list of Dates representing the start of date ranges from user's request.
   */
  public List<Date> getDateRangeStarts() {
    List<Date> startDates = new ArrayList<>();

    for (DateRange range: dateRanges) {
      startDates.add(range.getStart());
    }

    return startDates;
  }

  /**
   * Get end dates for ranges of chat request for storage in datastore.
   *
   * @return A list of Dates representing the end of date ranges from user's request.
   */
  public List<Date> getDateRangeEnds() {
    List<Date> endDates = new ArrayList<>();

    for (DateRange range: dateRanges) {
      endDates.add(range.getEnd());
    }

    return endDates;
  }

  /**
   * Get minimum number of other participants the user wishes to chat with.
   *
   * @return An int of minimum value of 1, maximum value of 4.
   */
  public int getMinPeople() {
    return minPeople;
  }

  /**
   * Get maximum number of other participants the user wishes to chat with.
   *
   * @return An int of minimum value of 1, maximum value of 4.
   */
  public int getMaxPeople() {
    return maxPeople;
  }


  /**
   * Get maximum duration user wishes to chat for in minutes.
   *
   * @return A Duration object representing max chat duration, must be positive.
   */
  public Duration getDuration() {
    return duration;
  }

  /**
   * Get flag representing whether to match the user even if no matches could be made with the
   * given tags.
   *
   * @return A boolean representing the user's decision.
   */
  public boolean shouldMatchRandom() {
    return matchRandom;
  }

  /**
   * Get flag representing whether to match the user with users they have chatted with recently.
   *
   * @return A boolean representing the user's decision.
   */
  public boolean shouldMatchRecents() {
    return matchRecents;
  }

  public String getUserId() {
    return userId;
  }

  public boolean hasRequestId() {
    return hasRequestId;
  }

  public void setRequestId(long requestId) {
    this.hasRequestId = true;
    this.requestId = requestId;
  }

  public long getRequestId() {
    return requestId;
  }

  public Date getLastEndDate() {
    if (this.lastEndDate == null) {
      List<Date> endDates = getDateRangeEnds();
      Collections.sort(endDates);
      this.lastEndDate = endDates.get(endDates.size() - 1);
    }

    return lastEndDate;
  }
}
