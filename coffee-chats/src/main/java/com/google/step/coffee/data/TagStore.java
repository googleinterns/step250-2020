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

/**
 * Representation for the storage method for tags. Uses singleton design pattern.
 */
public class TagStore {
  private DatastoreService datastore;

  public TagStore() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /**
   * Returns a List of all currently stored tags.
   */
  public List<String> getTags() {
    Query query = new Query("Tag");
    PreparedQuery results = datastore.prepare(query);

    List<String> tags = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      tags.add((String) entity.getProperty("name"));
    }

    return tags;
  }

  /**
   * Adds given tags to the store, not creating duplicates.
   * @param tags List of tags used to be added to the store if new.
   */
  public void addTags(List<String> tags) {
    for (String tag : tags) {
      Key tagKey = KeyFactory.createKey("Tag", tag);
      Entity tagEntity = new Entity(tagKey);
      tagEntity.setProperty("name", tag);

      datastore.put(tagEntity);
    }
  }
}
