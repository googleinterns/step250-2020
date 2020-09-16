package com.google.step.coffee.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.step.coffee.entity.User;

public class UserStore {
  private DatastoreService datastore;

  public UserStore() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /**
   * Creates new entry in datastore for given user with information stored attached to user's id.
   */
  public void addNewUser(User user) {
    Key key = KeyFactory.createKey("UserInfo", user.id());

    Entity entity = new Entity(key);
    entity.setProperty("email", user.email());
    entity.setProperty("name", user.name());
    entity.setProperty("avatarUrl", user.avatarUrl());

    datastore.put(entity);
  }

  /**
   * Retrieves the info of given user from datastore, throws exception if user is not found.
   *
   * @throws IllegalArgumentException when userId given is not present in datastore.
   */
  public User getUser(String userId) {
    Key key = KeyFactory.createKey("UserInfo", userId);

    try {
      Entity entity = datastore.get(key);

      return User.fromEntity(entity);
    } catch (EntityNotFoundException e) {
      throw new IllegalArgumentException("No UserInfo stored for userId " + userId);
    }
  }

  /**
   * Checks whether given user has their basic info stored within datastore.
   *
   * This function uses a keys-only query for speed purposes as it is used when loading initial
   * landing page for a user during login check.
   *
   * @param userId Id of user to check for if information is stored.
   */
  public boolean hasUserInfo(String userId) {
    Key key = KeyFactory.createKey("UserInfo", userId);
    Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key);
    Query query = new Query("UserInfo").setFilter(filter).setKeysOnly();

    return datastore.prepare(query).countEntities(Builder.withDefaults()) > 0;
  }

  /**
   * Ensures that the current user has all the info about them stored in the database.
   */
  public void updateCurrentUserInfo() {
    addNewUser(PeopleUtils.getCurrentUser());
  }
}
