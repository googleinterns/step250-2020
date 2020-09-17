package com.google.step.coffee.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AutoValue
public abstract class Group {
  @Nullable
  public abstract String id();

  public abstract String name();

  public abstract String description();

  public abstract String ownerId();

  public abstract List<String> tags();

  public static Builder builder() {
    return new AutoValue_Group.Builder().setTags(new ArrayList<>());
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract public Builder setId(String value);
    abstract public Builder setName(String value);
    abstract public Builder setDescription(String value);
    abstract public Builder setOwnerId(String value);
    abstract public Builder setTags(List<String> value);
    abstract public Group build();
  }

  public static Group fromEntity(Entity entity) {
    List<String> tags = (List<String>) entity.getProperty("tags");

    if (tags == null) {
      tags = new ArrayList<>();
    }

    return Group.builder()
        .setId(KeyFactory.keyToString(entity.getKey()))
        .setName((String) entity.getProperty("name"))
        .setDescription(((Text) entity.getProperty("description")).getValue())
        .setOwnerId((String) entity.getProperty("ownerId"))
        .setTags(tags)
        .build();
  }

  public Key key() {
    if (id() == null) {
      return null;
    }

    return KeyFactory.stringToKey(id());
  }
}
