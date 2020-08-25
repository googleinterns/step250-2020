package com.google.step.coffee.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRequestBuilder {
  private static final int MAX_PARTICIPANTS = 5;

  private List<String> tags = new ArrayList<>();
  private List<Date> dates = new ArrayList<>();
  private int minPeople = 1;
  private int maxPeople = 4;
  private int duration = 30;
  private boolean matchRandom = false;
  private boolean matchRecents = true;

  public ChatRequest build() {
    if (dates.isEmpty()) {
      throw new IllegalStateException("At least one date must be set");
    }
    return new ChatRequest(tags, dates, minPeople, maxPeople, duration, matchRandom, matchRecents);
  }

  public ChatRequestBuilder withTags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public ChatRequestBuilder onDates(List<Date> dates) {
    if (!dates.isEmpty()) {
      this.dates = dates;
      return this;
    } else {
      throw new IllegalArgumentException("No dates selected");
    }
  }

  public ChatRequestBuilder withGroupSize(int minPeople, int maxPeople) {
    if (minPeople > 0 && minPeople <= maxPeople && maxPeople < MAX_PARTICIPANTS) {
      this.minPeople = minPeople;
      this.maxPeople = maxPeople;
      return this;
    } else {
      throw new IllegalArgumentException("Invalid participants range");
    }
  }

  public ChatRequestBuilder withMaxChatLength(int duration) {
    if (duration > 0) {
      this.duration = duration;
      return this;
    } else {
      throw new IllegalArgumentException("Invalid chat duration");
    }
  }

  public ChatRequestBuilder willMatchRandomlyOnFail(boolean randomMatch) {
    this.matchRandom = randomMatch;
    return this;
  }

  public ChatRequestBuilder willMatchWithRecents(boolean matchRecent) {
    this.matchRecents = matchRecent;
    return this;
  }
}
