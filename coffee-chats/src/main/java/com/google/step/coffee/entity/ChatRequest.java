package com.google.step.coffee.entity;

import java.util.Date;
import java.util.List;

public class ChatRequest {

  private List<String> tags;
  private List<Date> dates;
  private int minPeople;
  private int maxPeople;
  private int duration;
  private boolean matchRandom;
  private boolean matchRecents;

  public ChatRequest(List<String> tags, List<Date> dates, int minPeople, int maxPeople,
      int duration, boolean matchRandom, boolean matchRecents) {
    this.tags = tags;
    this.dates = dates;
    this.minPeople = minPeople;
    this.maxPeople = maxPeople;
    this.duration = duration;
    this.matchRandom = matchRandom;
    this.matchRecents = matchRecents;
  }

  public List<String> getTags() {
    return tags;
  }

  public List<Date> getDates() {
    return dates;
  }

  public int getMinPeople() {
    return minPeople;
  }

  public int getMaxPeople() {
    return maxPeople;
  }

  public int getDuration() {
    return duration;
  }

  public boolean shouldMatchRandom() {
    return matchRandom;
  }

  public boolean shouldMatchRecents() {
    return matchRecents;
  }
}
