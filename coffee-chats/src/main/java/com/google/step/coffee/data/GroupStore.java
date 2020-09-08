package com.google.step.coffee.data;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.UserManager;
import com.google.step.coffee.entity.Group;

public class GroupStore {
  private DatastoreService datastore;

  public GroupStore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public Group create(String name) {
    return put(Group.builder()
        .setName(name)
        .setDescription("")
        .setOwnerId(UserManager.getCurrentUserId())
        .build());
  }

  public void delete(Key key) {
    datastore.delete(key);
  }

  /**
   * Saves the group to the database. Returns a <code>Group</code>
   * that has its id set, which the group passed to the function might not.
   */
  public Group put(Group group) {
    Key key = group.key();
    Entity entity = key != null ? new Entity(key) : new Entity("group");
    entity.setProperty("name", group.name());
    entity.setProperty("description", new Text(group.description()));
    entity.setProperty("ownerId", group.ownerId());

    datastore.put(entity);

    return Group.fromEntity(entity);
  }
}
