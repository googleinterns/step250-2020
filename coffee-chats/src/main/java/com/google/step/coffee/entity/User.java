package com.google.step.coffee.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class User {
  public abstract String id();

  public abstract String email();

  public abstract String name();

  public abstract String avatarUrl();

  public static User.Builder builder() {
    return new AutoValue_User.Builder()
        .setEmail("")
        .setName("")
        .setAvatarUrl("");
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public User.Builder setId(String value);
    abstract public User.Builder setEmail(String value);
    abstract public User.Builder setName(String value);
    abstract public User.Builder setAvatarUrl(String value);
    abstract public User build();
  }

  public static User fromEntity(Entity entity) {
    String email = (String) entity.getProperty("email");
    if (email == null) {
      email = "";
    }

    String name = (String) entity.getProperty("name");
    if (name == null) {
      name = "";
    }

    String avatarUrl = (String) entity.getProperty("avatarUrl");
    if (avatarUrl == null) {
      avatarUrl = "";
    }

    return User.builder()
        .setId(entity.getKey().getName())
        .setEmail(email)
        .setName(name)
        .setAvatarUrl(avatarUrl)
        .build();
  }
}
