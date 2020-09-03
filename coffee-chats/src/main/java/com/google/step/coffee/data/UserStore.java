package com.google.step.coffee.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;

public class UserStore {
  private DatastoreService datastore;

  public UserStore() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public void addNewUser(User user) {
    Key key = KeyFactory.createKey("UserInfo", user.getUserId());

    Entity entity = new Entity(key);
    entity.setProperty("email", user.getEmail());

    datastore.put(entity);
  }

  public String getEmail(String userId) {
    Key key = KeyFactory.createKey("UserInfo", userId);
    Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key);
    Query query = new Query("UserInfo").setFilter(filter);

    Entity entity = datastore.prepare(query).asSingleEntity();

    if (entity == null) {
      throw new IllegalArgumentException("No UserInfo stored for userId " + userId);
    }

    return (String) entity.getProperty("email");
  }

  public boolean hasUserInfo(String userId) {
    Key key = KeyFactory.createKey("UserInfo", userId);
    Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key);
    Query query = new Query("UserInfo").setFilter(filter).setKeysOnly();

    return datastore.prepare(query).countEntities(Builder.withDefaults()) > 0;
  }
}
