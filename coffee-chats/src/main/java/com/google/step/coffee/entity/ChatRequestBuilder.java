package com.google.step.coffee.entity;

import com.google.step.coffee.InvalidEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRequestBuilder {
  /* Maximum number of total participants, including the user who requested the chat. */
  private static final int MAX_PARTICIPANTS = 5;

  private List<String> tags = new ArrayList<>();
  private List<Date> dates = new ArrayList<>();
  private int minPeople = 1;
  private int maxPeople = 4;
  private int durationMins = 30;
  private boolean matchRandom = false;
  private boolean matchRecents = true;
  private String userId = "";

  /**
   * Construct ChatRequest from the given state set by the builder methods. Must have at least one
   * date to be a valid ChatRequest.
   *
   * @return ChatRequest object with the given internal state set of builder object.
   */
  public ChatRequest build() throws InvalidEntityException {
    if (dates.isEmpty()) {
      throw new InvalidEntityException("At least one date must be set");
    }

    return new ChatRequest(tags, dates, minPeople, maxPeople, durationMins, matchRandom, matchRecents, userId);
  }

  /**
   * Set tags specified in user's request.
   *
   * @param tags List of Strings representing each tagged interest in request.
   * @return ChatRequestBuilder object with internal state set to given tags.
   */
  public ChatRequestBuilder withTags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  /**
   * Set dates to consider in scheduling of user's request.
   *
   * @param dates List of Date objects for each day given in user's request.
   * @return ChatRequestBuilder object with internal state set to given dates.
   */
  public ChatRequestBuilder onDates(List<Date> dates) throws InvalidEntityException {
    if (!dates.isEmpty()) {
      this.dates = dates;
      return this;
    } else {
      throw new InvalidEntityException("No dates selected");
    }
  }

  /**
   * Set minimum and maximum number of other users to consider matching with for this request, i.e.
   * excluding the user themselves.
   *
   * @param minPeople minimum number of other users in chat request.
   * @param maxPeople maximum number of other users in chat request.
   * @return ChatRequestBuilder object with internal state set with given minimum and maximum number
   * of users to match with.
   */
  public ChatRequestBuilder withGroupSize(int minPeople, int maxPeople) throws InvalidEntityException {
    if (minPeople > 0 && minPeople <= maxPeople && maxPeople < MAX_PARTICIPANTS) {
      this.minPeople = minPeople;
      this.maxPeople = maxPeople;
      return this;
    } else {
      throw new InvalidEntityException("Invalid participants range");
    }
  }

  /**
   * Set maximum duration of chat length for user's request.
   *
   * @param durationMins positive int representing maximum duration of chat in minutes.
   * @return ChatRequestBuilder object with internal state set to given maximum chat duration.
   */
  public ChatRequestBuilder withMaxChatLength(int durationMins) throws InvalidEntityException {
    if (durationMins > 0) {
      this.durationMins = durationMins;
      return this;
    } else {
      throw new InvalidEntityException("Invalid chat duration");
    }
  }

  /**
   * Set whether to still consider matching to a user if no matches are found on the given tags.
   *
   * @param randomMatch boolean representing user's decision to match randomly or not after
   *     attempted matching.
   * @return ChatRequestBuilder object with internal state set to given choice on random matching.
   */
  public ChatRequestBuilder willMatchRandomlyOnFail(boolean randomMatch) {
    this.matchRandom = randomMatch;
    return this;
  }

  /**
   * Set whether to consider users who the user has recently chatted/scheduled to chat with.
   *
   * @param matchRecent boolean representing user's decision to match with users they have recently
   *     chatted with.
   * @return ChatRequestBuilder object with internal state set to given choice on matching to
   *     recently-met users.
   */
  public ChatRequestBuilder willMatchWithRecents(boolean matchRecent) {
    this.matchRecents = matchRecent;
    return this;
  }

  public ChatRequestBuilder forUser(String userId) {
    this.userId = userId;
    return this;
  }
}
