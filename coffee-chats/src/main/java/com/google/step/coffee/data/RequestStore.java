package com.google.step.coffee.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.ChatRequestBuilder;
import com.google.step.coffee.entity.TimeSlot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestStore {
  private DatastoreService datastore;

  public RequestStore() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public void addRequest(ChatRequest request) {
    Entity reqEntity = new Entity("ChatRequest");
    reqEntity.setProperty("tags", request.getTags());
    reqEntity.setProperty("dates", request.getDates());
    reqEntity.setProperty("minPeople", request.getMinPeople());
    reqEntity.setProperty("maxPeople", request.getMaxPeople());
    reqEntity.setProperty("duration", request.getDuration());
    reqEntity.setProperty("matchRandom", request.shouldMatchRandom());
    reqEntity.setProperty("matchRecents", request.shouldMatchRecents());
    reqEntity.setProperty("userId", request.getUserId());

    datastore.put(reqEntity);
  }

  public List<ChatRequest> getUnmatchedRequests() {
    Query query = new Query("ChatRequest");
    PreparedQuery results = datastore.prepare(query);

    List<ChatRequest> unmatchedRequests = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      ChatRequest request = getRequestFromEntity(entity);


      unmatchedRequests.add(request);
    }

    return unmatchedRequests;
  }

  private ChatRequest getRequestFromEntity(Entity entity) {
    ChatRequest request = new ChatRequestBuilder()
        .withTags((List<String>) entity.getProperty("tags"))
        .onDates((List<Date>) entity.getProperty("dates"))
        .withGroupSize(((Long) entity.getProperty("minPeople")).intValue(),
            ((Long) entity.getProperty("maxPeople")).intValue())
        .withMaxChatLength(((Long) entity.getProperty("duration")).intValue())
        .willMatchRandomlyOnFail((boolean) entity.getProperty("matchRandom"))
        .willMatchWithRecents((boolean) entity.getProperty("matchRecents"))
        .forUser((String) entity.getProperty("userId"))
        .build();

    request.setRequestId(entity.getKey().getId());

    return request;
  }

  public void removeRequests(Key ...requestKeys) {
    datastore.delete(requestKeys);
  }

  public void addMatchedRequest(ChatRequest resolvedRequest, TimeSlot slot,
      List<String> participantIds, List<String> commonTags) {
    Entity entity = new Entity("MatchedRequest");
    entity.setProperty("userId", resolvedRequest.getUserId());
    entity.setProperty("tags", resolvedRequest.getTags());
    entity.setProperty("datetime", Date.from(slot.getDatetimeStart().toInstant()));
    entity.setProperty("duration", slot.getDuration());
    entity.setProperty("participants", participantIds);
    entity.setProperty("commonTags", commonTags);

    datastore.put(entity);
  }
}
