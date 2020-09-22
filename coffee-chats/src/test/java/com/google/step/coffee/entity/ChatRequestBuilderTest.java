package com.google.step.coffee.entity;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.InvalidEntityException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

import static com.google.step.coffee.tasks.RequestMatcher.MATCH_RANDOM_TAG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ChatRequestBuilderTest {

  private static final int MIN_PARTICIPANTS = 1;
  private static final int MAX_PARTICIPANTS = 4;

  @Test
  public void canNotBuildRequestWithoutDates() {
    try {
      ChatRequest request = new ChatRequestBuilder().build();

      Assert.fail("Request built with no dates being set");
    } catch (HttpError e) {
      assertThat(e.getMessage(), is("At least one date range must be set"));
    }
  }

  @Test
  public void shouldNotAcceptEmptyDates() {
    try {
      ChatRequest request = new ChatRequestBuilder().onDates(new ArrayList<>()).build();

      Assert.fail("Request built with invalid dates");
    } catch (HttpError e) {
      assertThat(e.getMessage(), is("No date ranges selected"));
    }
  }

  @Test
  public void onlyUpToFourParticipantsAllowed() {
    try {
      ChatRequest request =
          new ChatRequestBuilder().withGroupSize(MIN_PARTICIPANTS, MAX_PARTICIPANTS + 1).build();

      Assert.fail("Request built with invalid max participant size");
    } catch (HttpError e) {
      assertThat(e.getMessage(), is("Invalid participants range"));
    }
  }

  @Test
  public void mustHaveAtLeastOneOtherParticipant() {
    try {
      ChatRequest request =
          new ChatRequestBuilder().withGroupSize(MIN_PARTICIPANTS - 1, MAX_PARTICIPANTS).build();

      Assert.fail("Request built with invalid min participant size");
    } catch (HttpError e) {
      assertThat(e.getMessage(), is("Invalid participants range"));
    }
  }

  @Test
  public void mustHaveValidChatDuration() {
    try {
      ChatRequest request = new ChatRequestBuilder().withMaxChatLength(0).build();

      Assert.fail("Request built with invalid chat duration");
    } catch (HttpError e) {
      assertThat(e.getMessage(), is("Invalid chat duration"));
    }
  }

  @Test
  public void emptyTagsDefaultToRandom() throws InvalidEntityException {
    DateRange dateRange = new DateRange(Date.from(Instant.EPOCH), Date.from(Instant.now()));
    ChatRequest request = new ChatRequestBuilder().withTags(Collections.emptyList())
        .onDates(Collections.singletonList(dateRange))
        .build();

    assertThat(request.getTags(), contains(MATCH_RANDOM_TAG));
  }
}
