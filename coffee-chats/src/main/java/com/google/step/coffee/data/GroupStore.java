package com.google.step.coffee.data;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.UserManager;
import com.google.step.coffee.entity.Event;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
import com.google.step.coffee.entity.User;

import java.util.ArrayList;
import java.util.List;

public class GroupStore {
  private DatastoreService datastore;
  private UserStore userStore;

  public GroupStore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    userStore = new UserStore();
  }

  public Group create(String name) {
    Group group = put(Group.builder()
        .setName(name)
        .setDescription("")
        .setOwnerId(UserManager.getCurrentUserId())
        .build());

    updateMembershipStatus(group, UserManager.getCurrentUser(), GroupMembership.Status.OWNER);

    return group;
  }

  public void delete(Group group) {
    datastore.delete(group.key());

    Query query = new Query("groupMembership")
        .setFilter(new Query.FilterPredicate(
            "group", Query.FilterOperator.EQUAL, group.key()));

    for (Entity entity : datastore.prepare(query).asIterable()) {
      datastore.delete(entity.getKey());
    }

    query = new Query("event")
        .setFilter(new Query.FilterPredicate(
            "group", Query.FilterOperator.EQUAL, group.key()));

    for (Entity entity : datastore.prepare(query).asIterable()) {
      String calendarId = Event.fromEntity(entity).calendarId();

      if (calendarId != null) {
        CalendarUtils.removeEvent(group.ownerId(), calendarId);
      }

      datastore.delete(entity.getKey());
    }
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
    entity.setProperty("tags", group.tags());

    datastore.put(entity);

    return Group.fromEntity(entity);
  }

  public Group get(String groupId) {
    try {
      return Group.fromEntity(datastore.get(KeyFactory.stringToKey(groupId)));
    } catch (EntityNotFoundException exception) {
      return null;
    }
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
          .setUser(userStore.getUser((String) entity.getProperty("user")))
          .build());
    }

    return members;
  }

  public List<Group> getUserGroups(User user) {
    List<Group> groups = new ArrayList<>();

    Query query = new Query("groupMembership")
        .setFilter(new Query.FilterPredicate(
            "user", Query.FilterOperator.EQUAL, user.id()));

    for (Entity entity : datastore.prepare(query).asIterable()) {
      Key groupKey = (Key) entity.getProperty("group");
      try {
        groups.add(Group.fromEntity(datastore.get(groupKey)));
      } catch (EntityNotFoundException ignored) {}
    }

    return groups;
  }

  public List<Group> getAllGroups() {
    List<Group> groups = new ArrayList<>();

    Query query = new Query("group");

    for (Entity entity : datastore.prepare(query).asIterable()) {
      groups.add(Group.fromEntity(entity));
    }

    return groups;
  }

  /**
   * Returns a list of groups that match any of the given tags.
   */
  public List<Group> findGroupsByTags(List<String> tags) {
    if (tags.isEmpty()) {
      return new ArrayList<>();
    }

    List<Group> groups = new ArrayList<>();

    Query query = new Query("group")
        .setFilter(new Query.FilterPredicate(
            "tags", Query.FilterOperator.IN, tags));

    for (Entity entity : datastore.prepare(query).asIterable()) {
      groups.add(Group.fromEntity(entity));
    }

    return groups;
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
      return GroupMembership.Status.OWNER;
    }

    Entity entity = getMembershipEntity(group, user);
    return GroupMembership.Status.valueOf((String) entity.getProperty("status"));
  }

  public void updateMembershipStatus(Group group, User user, GroupMembership.Status status) {
    Entity entity = getMembershipEntity(group, user);
    if (status == GroupMembership.Status.NOT_A_MEMBER) {
      if (entity.getKey().isComplete()) {
        datastore.delete(entity.getKey());
      }
    } else {
      entity.setProperty("status", status.toString());
      datastore.put(entity);
    }
  }
}
