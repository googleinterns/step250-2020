package com.google.step.coffee.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.step.coffee.entity.ChatRequest;

public class RequestStore {
  private DatastoreService datastore;

  public RequestStore() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public void addRequest(ChatRequest request, String userId) {
    Entity reqEntity = new Entity("ChatRequest");
    reqEntity.setProperty("tags", request.getTags());
    reqEntity.setProperty("dates", request.getDates());
    reqEntity.setProperty("minPeople", request.getMinPeople());
    reqEntity.setProperty("maxPeople", request.getMaxPeople());
    reqEntity.setProperty("randomMatch", request.shouldMatchRandom());
    reqEntity.setProperty("matchRecent", request.shouldMatchRecents());
    reqEntity.setProperty("userId", userId);

    datastore.put(reqEntity);
  }
}
