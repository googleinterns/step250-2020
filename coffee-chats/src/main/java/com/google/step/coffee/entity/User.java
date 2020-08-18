package com.google.step.coffee.entity;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class User {
  public abstract String id();

  public static User.Builder builder() {
    return new AutoValue_User.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public User.Builder setId(String value);
    abstract public User build();
  }
}
