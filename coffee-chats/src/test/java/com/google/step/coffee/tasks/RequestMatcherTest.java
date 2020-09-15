package com.google.step.coffee.tasks;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.calendar.model.Event;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.ChatRequestBuilder;
import com.google.step.coffee.entity.TimeSlot;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.MockedStatic;

public class RequestMatcherTest {

  Date MORNING = new Date(1601888400000L);
  RequestMatcher matcher =  new RequestMatcher();

  @Test
  public void taggedRequestsMappedCorrectly() {
    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getTags()).thenReturn(Arrays.asList("Football", "Photography", "C++"));
    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getTags()).thenReturn(Arrays.asList("Football", "C++"));
    ChatRequest req3 = mock(ChatRequest.class);
    when(req3.getTags()).thenReturn(Arrays.asList("C++", "Random"));

    Map<String, List<ChatRequest>> tagMap = matcher.buildTagMap(Arrays.asList(req1, req2, req3));

    assertThat(tagMap.keySet(),
        containsInAnyOrder("Football", "Photography", "C++", "Random"));
    assertThat(tagMap.get("Football"), contains(req1, req2));
    assertThat(tagMap.get("Photography"), contains(req1));
    assertThat(tagMap.get("C++"), contains(req1, req2, req3));
    assertThat(tagMap.get("Random"), contains(req3));
  }

  @Test
  public void emptyTagRequestsAndRandomReqsMappedTogether() {
    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getTags()).thenReturn(Arrays.asList("Football", "Random"));
    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getTags()).thenReturn(Collections.emptyList());
    ChatRequest req3 = mock(ChatRequest.class);
    when(req3.getTags()).thenReturn(Collections.emptyList());

    Map<String, List<ChatRequest>> tagMap = matcher.buildTagMap(Arrays.asList(req1, req2, req3));

    assertThat(tagMap.keySet(), containsInAnyOrder("Football", "Random"));
    assertThat(tagMap.get("Football"), contains(req1));
    assertThat(tagMap.get("Random"), contains(req1, req2, req3));
  }

  @Test
  public void createEventCalledOncePerUser() {
    RequestStore store = mock(RequestStore.class);

    List<String> participantIds = Arrays.asList("id1", "id2", "id3");
    List<String> commonTags = Arrays.asList("Football", "Photography");
    TimeSlot slot = new TimeSlot(MORNING, Duration.ofMinutes(30));

    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getUserId()).thenReturn("user1");
    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getUserId()).thenReturn("user2");
    ChatRequest req3 = mock(ChatRequest.class);
    when(req3.getUserId()).thenReturn("user3");

    Event event = mock(Event.class);

    try (MockedStatic<CalendarUtils> utils = mockStatic(CalendarUtils.class)) {
      utils.when(() -> CalendarUtils.createEvent(slot, participantIds, commonTags))
          .thenReturn(event);

      matcher.addMatchingRequests(store, participantIds, slot, commonTags,
          Arrays.asList(req1, req2, req3));

      utils.verify(times(1), () -> CalendarUtils.addEvent("user1", event));
      utils.verify(times(1), () -> CalendarUtils.addEvent("user2", event));
      utils.verify(times(1), () -> CalendarUtils.addEvent("user3", event));

      verify(store, times(1)).addMatchedRequest(req1, slot, participantIds, commonTags);
      verify(store, times(1)).addMatchedRequest(req2, slot, participantIds, commonTags);
      verify(store, times(1)).addMatchedRequest(req3, slot, participantIds, commonTags);
    }
  }

  @Test
  public void groupSizesRightForBoundaryRanges() {
    ChatRequest req = mock(ChatRequest.class);

    when(req.getMinPeople()).thenReturn(2);
    when(req.getMaxPeople()).thenReturn(4);

    assertThat(matcher.groupSizeInRange(3, req), is(true));
    assertThat(matcher.groupSizeInRange(5, req), is(true));
  }

  @Test
  public void groupSizesWrongOffByOne() {
    ChatRequest req = mock(ChatRequest.class);

    when(req.getMinPeople()).thenReturn(2);
    when(req.getMaxPeople()).thenReturn(3);

    assertThat(matcher.groupSizeInRange(2, req), is(false));
    assertThat(matcher.groupSizeInRange(5, req), is(false));
  }

  @Test
  public void sameTagsInSameTagMapLists() {
    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getTags()).thenReturn(Arrays.asList("Football", "Photography"));

    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getTags()).thenReturn(Arrays.asList("Football", "Photography"));

    Map<String, List<ChatRequest>> tagMap = matcher.buildTagMap(Arrays.asList(req1, req2));

    assertThat(tagMap.keySet(), containsInAnyOrder("Football", "Photography"));
    assertThat(tagMap.get("Football"), contains(req1, req2));
    assertThat(tagMap.get("Photography"), contains(req1, req2));
  }

  @Test
  public void differentTagsInDifferentTagMapLists() {
    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getTags()).thenReturn(Arrays.asList("Football", "Photography"));

    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getTags()).thenReturn(Arrays.asList("Basketball", "Gardening"));

    Map<String, List<ChatRequest>> tagMap = matcher.buildTagMap(Arrays.asList(req1, req2));

    assertThat(tagMap.keySet(),
        containsInAnyOrder("Football", "Photography", "Basketball", "Gardening"));
    assertThat(tagMap.get("Football"), contains(req1));
    assertThat(tagMap.get("Photography"), contains(req1));
    assertThat(tagMap.get("Basketball"), contains(req2));
    assertThat(tagMap.get("Gardening"), contains(req2));
  }

  @Test
  public void emptyAndRandomRequestsInSameTagMapList() {
    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getTags()).thenReturn(Arrays.asList("Football", "Random"));

    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getTags()).thenReturn(Collections.emptyList());

    Map<String, List<ChatRequest>> tagMap = matcher.buildTagMap(Arrays.asList(req1, req2));

    assertThat(tagMap.keySet(), containsInAnyOrder("Football", "Random"));
    assertThat(tagMap.get("Football"), contains(req1));
    assertThat(tagMap.get("Random"), contains(req1, req2));
  }

  @Test
  public void removingFromTagMapRemovesAllInstances() {
    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getTags()).thenReturn(Arrays.asList("Football", "Random"));

    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getTags()).thenReturn(Arrays.asList("Football", "Photography"));

    Map<String, List<ChatRequest>> tagMap = matcher.buildTagMap(Arrays.asList(req1, req2));

    assertThat(tagMap.get("Football"), contains(req1, req2));
    assertThat(tagMap.get("Random"), contains(req1));
    assertThat(tagMap.get("Photography"), contains(req2));

    matcher.removeFromTagMap(tagMap, req1);

    assertThat(tagMap.get("Football"), contains(req2));
    assertThat(tagMap.get("Random").isEmpty(), is(true));
  }

  @Test
  public void removeEmptyTagRequestRemovesFromTagMap() {
    ChatRequest req1 = mock(ChatRequest.class);
    when(req1.getTags()).thenReturn(Arrays.asList("Football", "Random"));

    ChatRequest req2 = mock(ChatRequest.class);
    when(req2.getTags()).thenReturn(Collections.emptyList());

    Map<String, List<ChatRequest>> tagMap = matcher.buildTagMap(Arrays.asList(req1, req2));

    assertThat(tagMap.get("Football"), contains(req1));
    assertThat(tagMap.get("Random"), contains(req1, req2));

    matcher.removeFromTagMap(tagMap, req2);

    assertThat(tagMap.get("Football"), contains(req1));
    assertThat(tagMap.get("Random").size(), is(1));
    assertThat(tagMap.get("Random"), contains(req1));
  }
}
