package com.google.step.coffee.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;

public class TagStore {
  private static TagStore instance = new TagStore();

  private DatastoreService datastore;

  private TagStore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public static TagStore getInstance() {
    return instance;
  }

  public List<String> getTags() {
    Query query = new Query("Tag");
    PreparedQuery results = datastore.prepare(query);

    List<String> tags = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      tags.add((String) entity.getProperty("name"));
    }

    return tags;
  }

  public void addTags(List<String> tags) {
    for (String tag : tags) {
      Key tagKey = KeyFactory.createKey("Tag", tag);
      Entity tagEntity = new Entity(tagKey);
      tagEntity.setProperty("name", tag);

      datastore.put(tagEntity);
    }
  }
}
