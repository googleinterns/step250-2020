package com.google.step.coffee.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.auto.value.AutoValue;
import com.google.step.coffee.Nullable;

@AutoValue
public abstract class Group {
  @Nullable
  public abstract String id();

  public abstract String name();

  public abstract String description();

  public abstract String ownerId();

  public static Builder builder() {
    return new AutoValue_Group.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public Builder setId(String value);
    abstract public Builder setName(String value);
    abstract public Builder setDescription(String value);
    abstract public Builder setOwnerId(String value);
    abstract public Group build();
  }

  public static Group fromEntity(Entity entity) {
    return Group.builder()
        .setId(KeyFactory.keyToString(entity.getKey()))
        .setName((String) entity.getProperty("name"))
        .setDescription((String) entity.getProperty("description"))
        .setOwnerId((String) entity.getProperty("ownerId"))
        .build();
  }

  public Key key() {
    if (id() == null) {
      return null;
    }

    return KeyFactory.stringToKey(id());
  }
}
