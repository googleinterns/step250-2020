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

  public List<GroupMembership> getMembers(Group group) {
    List<GroupMembership> members = new ArrayList<>();

    Query query = new Query("groupMembership")
        .setFilter(new Query.FilterPredicate(
            "group", Query.FilterOperator.EQUAL, KeyFactory.stringToKey(group.id())));

    for (Entity entity : datastore.prepare(query).asIterable()) {
      members.add(GroupMembership.builder()
          .setStatus(GroupMembership.Status.valueOf((String) entity.getProperty("status")))
          .setGroup(group)
          .setUser(User.builder()
              .setId((String) entity.getProperty("user"))
              .build())
          .build());
    }

    return members;
  }

  private Entity getMembershipEntity(Group group, User user) {
    Query query = new Query("groupMembership")
        .setFilter(Query.CompositeFilterOperator.and(
            new Query.FilterPredicate(
                "group", Query.FilterOperator.EQUAL, group.key()
            ),
            new Query.FilterPredicate(
                "user", Query.FilterOperator.EQUAL, user.id()
            )
        ));

    Entity entity = datastore.prepare(query).asSingleEntity();

    if (entity == null) {
      entity = new Entity("groupMembership");
      entity.setProperty("group", group.key());
      entity.setProperty("user", user.id());
      entity.setProperty("status", GroupMembership.Status.NOT_A_MEMBER.toString());
    }

    return entity;
  }

  public GroupMembership.Status getMembershipStatus(Group group, User user) {
    if (group.ownerId().equals(user.id())) {
      return GroupMembership.Status.ADMINISTRATOR;
    }

    Entity entity = getMembershipEntity(group, user);
    return GroupMembership.Status.valueOf((String) entity.getProperty("status"));
  }

  public void updateMembershipStatus(Group group, User user, GroupMembership.Status status) {
    Entity entity = getMembershipEntity(group, user);
    entity.setProperty("status", status.toString());
  }
}
