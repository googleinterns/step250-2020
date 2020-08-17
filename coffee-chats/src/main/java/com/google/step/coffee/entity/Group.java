package com.google.step.coffee.entity;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Group {
  public abstract String id();

  public abstract String name();

  public abstract String description();

  public static Builder builder() {
    return new AutoValue_Group.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public Builder setId(String value);
    abstract public Builder setName(String value);
    abstract public Builder setDescription(String value);
    abstract public Group build();
  }
}
