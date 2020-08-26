package com.google.step.coffee.entity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.Date;
import java.util.List;

/** Represents a request made by a user for a chat on given topics. */
public class ChatRequest {

  private long requestId;
  private List<String> tags;
  private List<Date> dates;
  private int minPeople;
  private int maxPeople;
  private int duration;
  private boolean matchRandom;
  private boolean matchRecents;
  private String userId;

  public ChatRequest(List<String> tags, List<Date> dates, int minPeople, int maxPeople,
      int duration, boolean matchRandom, boolean matchRecents, String userId) {
    this.tags = tags;
    this.dates = dates;
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
   * Get days to schedule chat on from user's request.
   *
   * @return A list of Date objects representing each day selected by the user.
   */
  public List<Date> getDates() {
    return dates;
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
   * @return A positive integer, maximum value of 60.
   */
  public int getDuration() {
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

  public void setRequestId(long requestId) {
    this.requestId = requestId;
  }

  public Key getRequestKey() {
    return KeyFactory.createKey("ChatRequest", requestId);
  }
}
