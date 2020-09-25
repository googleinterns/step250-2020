package com.google.step.coffee.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.auto.value.AutoValue;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@AutoValue
abstract public class MatchedRequest implements CompletedRequest {
  public abstract boolean isMatched();

  public abstract DateRange getFirstDateRange();

  public abstract Duration getDuration();

  public abstract String getUserId();

  public abstract List<String> getParticipants();

  public abstract List<String> getTags();

  public abstract List<String> getCommonTags();

  public static Builder builder() {
    return new AutoValue_MatchedRequest.Builder().setMatched(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setMatched(boolean value);
    public abstract Builder setFirstDateRange(DateRange value);
    public abstract Builder setDuration(Duration value);
    public abstract Builder setUserId(String value);
    public abstract Builder setParticipants(List<String> value);
    public abstract Builder setTags(List<String> value);
    public abstract Builder setCommonTags(List<String> value);

    public abstract MatchedRequest build();
  }

  public static MatchedRequest fromEntity(Entity entity) {
    Duration duration = Duration.ofMinutes((Long) entity.getProperty("duration"));

    return builder()
        .setUserId((String) entity.getProperty("userId"))
        .setParticipants((List<String>) entity.getProperty("participants"))
        .setTags((entity.getProperty("tags") != null) ?
            (List<String>) entity.getProperty("tags") :
            Collections.emptyList())
        .setCommonTags((entity.getProperty("commonTags") != null) ?
            (List<String>) entity.getProperty("commonTags") :
            Collections.emptyList())
        .setFirstDateRange(new DateRange((Date) entity.getProperty("datetime"), duration))
        .setDuration(duration)
        .build();
  }
}
