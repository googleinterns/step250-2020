package com.google.step.coffee.data;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.UserManager;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
import com.google.step.coffee.entity.User;

import java.util.ArrayList;
import java.util.List;

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

  public Group put(Group group) {
    Key key = group.key();
    Entity entity = key != null ? new Entity(key) : new Entity("group");
    entity.setProperty("name", group.name());
    entity.setProperty("description", new Text(group.description()));
    entity.setProperty("ownerId", group.ownerId());

    datastore.put(entity);

    return Group.fromEntity(entity);
  }

  public static List<GroupMembership> getMembers(Group group) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<GroupMembership> members = new ArrayList<>();

    Query query = new Query("groupMembership")
        .setFilter(new Query.FilterPredicate(
            "group", Query.FilterOperator.EQUAL, KeyFactory.stringToKey(group.id())));

    for (Entity entity : datastore.prepare(query).asIterable()) {
      members.add(GroupMembership.builder()
          .setKind(GroupMembership.Kind.valueOf((String) entity.getProperty("kind")))
          .setGroup(group)
          .setUser(User.builder()
              .setId((String) entity.getProperty("userId"))
              .build())
          .build());
    }

    return members;
  }
}
