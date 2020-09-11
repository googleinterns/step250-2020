package com.google.step.coffee.tasks;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.step.coffee.entity.ChatRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class RequestMatcherTest {

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

}
