package com.google.step.coffee.entity;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

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
    } catch (IllegalStateException e) {
      assertThat(e.getMessage(), is("At least one date must be set"));
    }
  }

  @Test
  public void shouldNotAcceptEmptyDates() {
    try {
      ChatRequest request = new ChatRequestBuilder().onDates(new ArrayList<>()).build();

      Assert.fail("Request built with invalid dates");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is("No dates selected"));
    }
  }

  @Test
  public void onlyUpToFourParticipantsAllowed() {
    try {
      ChatRequest request =
          new ChatRequestBuilder().withGroupSize(MIN_PARTICIPANTS, MAX_PARTICIPANTS + 1).build();

      Assert.fail("Request built with invalid max participant size");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is("Invalid participants range"));
    }
  }

  @Test
  public void mustHaveAtLeastOneOtherParticipant() {
    try {
      ChatRequest request =
          new ChatRequestBuilder().withGroupSize(MIN_PARTICIPANTS - 1, MAX_PARTICIPANTS).build();

      Assert.fail("Request built with invalid min participant size");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is("Invalid participants range"));
    }
  }

  @Test
  public void mustHaveValidChatDuration() {
    try {
      ChatRequest request = new ChatRequestBuilder().withMaxChatLength(0).build();

      Assert.fail("Request built with invalid chat duration");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is("Invalid chat duration"));
    }
  }

}
