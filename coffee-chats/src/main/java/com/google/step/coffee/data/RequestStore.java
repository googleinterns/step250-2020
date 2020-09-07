package com.google.step.coffee.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.coffee.InvalidEntityException;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.ChatRequestBuilder;
import com.google.step.coffee.entity.TimeSlot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstraction to access chat requests from DataStore instance.
 */
public class RequestStore {

  private DatastoreService datastore;
  private TagStore tagStore;


  public RequestStore() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.tagStore = new TagStore();
  }

  /**
   * Adds ChatRequest object into datastore, adding any new tags into datastore too.
   */
  public void addRequest(ChatRequest request) {
    Entity reqEntity = new Entity("ChatRequest");
    reqEntity.setProperty("tags", request.getTags());
    reqEntity.setProperty("dates", request.getDates());
    reqEntity.setProperty("minPeople", request.getMinPeople());
    reqEntity.setProperty("maxPeople", request.getMaxPeople());
    reqEntity.setProperty("durationMins", request.getDuration().toMinutes());
    reqEntity.setProperty("matchRandom", request.shouldMatchRandom());
    reqEntity.setProperty("matchRecents", request.shouldMatchRecents());
    reqEntity.setProperty("userId", request.getUserId());

    datastore.put(reqEntity);
    tagStore.addTags(request.getTags());
  }

  /**
   * Retrieves unmatched ChatRequest objects stored within datastore.
   *
   * @return List of ChatRequest objects that have not been matched.
   */
  public List<ChatRequest> getUnmatchedRequests() {
    Query query = new Query("ChatRequest");
    PreparedQuery results = datastore.prepare(query);

    List<ChatRequest> unmatchedRequests = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      try {
        ChatRequest request = getRequestFromEntity(entity);

        unmatchedRequests.add(request);
      } catch (InvalidEntityException e) {
        System.out.println("Can not construct chat request from entity: " + e.getMessage());

        removeRequests(entity.getKey());
      }
    }

    return unmatchedRequests;
  }

  /**
   * Removes unmatched chat requests for the given keys.
   *
   * @param requestKeys keys of ChatRequest entities to remove.
   */
  public void removeRequests(Key... requestKeys) {
    datastore.delete(requestKeys);
  }

  /**
   * Creates new MatchedRequest entity within datastore.
   *
   * @param resolvedRequest ChatRequest object of user's original request.
   * @param slot TimeSlot in which the request's event has been scheduled to.
   * @param participantIds List of user Ids of all users to be invited to this event.
   * @param commonTags List of tags that all participants matched on.
   */
  public void addMatchedRequest(ChatRequest resolvedRequest, TimeSlot slot,
      List<String> participantIds, List<String> commonTags) {
    Entity entity = new Entity("MatchedRequest");
    entity.setProperty("userId", resolvedRequest.getUserId());
    entity.setProperty("tags", resolvedRequest.getTags());
    entity.setProperty("datetime", Date.from(slot.getZonedDatetimeStart().toInstant()));
    entity.setProperty("duration", slot.getDuration().toMinutes());
    entity.setProperty("participants", participantIds);
    entity.setProperty("commonTags", commonTags);

    datastore.put(entity);
  }

  private ChatRequest getRequestFromEntity(Entity entity) throws InvalidEntityException {
    ChatRequest request = new ChatRequestBuilder()
        .withTags((List<String>) entity.getProperty("tags"))
        .onDates((List<Date>) entity.getProperty("dates"))
        .withGroupSize(((Long) entity.getProperty("minPeople")).intValue(),
            ((Long) entity.getProperty("maxPeople")).intValue())
        .withMaxChatLength(((Long) entity.getProperty("durationMins")).intValue())
        .willMatchRandomlyOnFail((boolean) entity.getProperty("matchRandom"))
        .willMatchWithRecents((boolean) entity.getProperty("matchRecents"))
        .forUser((String) entity.getProperty("userId"))
        .build();

    request.setRequestId(entity.getKey().getId());

    return request;
  }
}
