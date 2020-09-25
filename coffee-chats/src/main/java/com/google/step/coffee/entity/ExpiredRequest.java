package com.google.step.coffee.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.auto.value.AutoValue;
import com.google.step.coffee.InvalidEntityException;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@AutoValue
abstract public class ExpiredRequest implements CompletedRequest {

  public abstract boolean isMatched();

  public abstract DateRange getFirstDateRange();

  public abstract List<DateRange> getDateRanges();

  public abstract Duration getDuration();

  public abstract String getUserId();

  public abstract List<String> getTags();

  public abstract int getMinPeople();

  public abstract int getMaxPeople();

  public abstract boolean getMatchRandom();

  public abstract boolean getMatchRecents();

  public static Builder builder() {
    return new AutoValue_ExpiredRequest.Builder().setMatched(false);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setMatched(boolean value);
    public abstract Builder setFirstDateRange(DateRange value);
    public abstract Builder setDateRanges(List<DateRange> value);
    public abstract Builder setDuration(Duration value);
    public abstract Builder setUserId(String value);
    public abstract Builder setTags(List<String> value);
    public abstract Builder setMinPeople(int value);
    public abstract Builder setMaxPeople(int value);
    public abstract Builder setMatchRandom(boolean value);
    public abstract Builder setMatchRecents(boolean value);

    public abstract ExpiredRequest build();
  }

  public static ExpiredRequest fromEntity(Entity entity) throws InvalidEntityException {
    List<Date> startDates = (List<Date>) entity.getProperty("startDates");
    List<Date> endDates = (List<Date>) entity.getProperty("endDates");

    return builder()
        .setUserId((String) entity.getProperty("userId"))
        .setFirstDateRange(new DateRange(startDates.get(0), endDates.get(0)))
        .setDateRanges(DateRange.combineLists(startDates, endDates))
        .setDuration(Duration.ofMinutes((Long) entity.getProperty("durationMins")))
        .setTags((entity.getProperty("tags") != null) ?
            (List<String>) entity.getProperty("tags") :
            Collections.emptyList())
        .setMinPeople(((Long) entity.getProperty("minPeople")).intValue())
        .setMaxPeople(((Long) entity.getProperty("maxPeople")).intValue())
        .setMatchRandom((boolean) entity.getProperty("matchRandom"))
        .setMatchRecents((boolean) entity.getProperty("matchRecents"))
        .build();
  }
}
