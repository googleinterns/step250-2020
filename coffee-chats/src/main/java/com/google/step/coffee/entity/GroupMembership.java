package com.google.step.coffee.entity;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GroupMembership {
  public enum Kind {
    NOT_A_MEMBER,
    REGULAR_MEMBER,
    ADMINISTRATOR
  }

  public abstract Kind kind();

  public abstract User user();

  public abstract Group group();

  public static Builder builder() {
    return new AutoValue_GroupMembership.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public Builder setKind(Kind value);
    abstract public Builder setUser(User value);
    abstract public Builder setGroup(Group value);
    abstract public GroupMembership build();
  }
}
