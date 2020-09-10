package com.google.step.coffee.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.step.coffee.TestHelper;
import com.google.step.coffee.entity.User;
import org.junit.Assert;
import org.junit.Test;

public class UserStoreTest extends TestHelper {

  final String testUserId = "id1234test";
  final String testEmail = "test-user@example.com";

  UserStore userStore = new UserStore();

  @Test
  public void addingNewUserHasCorrectEmail() {
    User user = User.builder()
        .setId(testUserId)
        .setEmail(testEmail)
        .build();

    userStore.addNewUser(user);

    assertThat(userStore.getUser(testUserId).email(), equalTo(testEmail));
  }

  @Test
  public void noUserInfoOnGetEmailThrowsException() {
    try {
      userStore.getUser(testUserId);
      Assert.fail("Should not be able to retrieve email from no stored users.");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), equalTo("No UserInfo stored for userId " + testUserId));
    }
  }

  @Test
  public void addingUserCreatesCheckableEntry() {
    User user = User.builder()
        .setId(testUserId)
        .setEmail(testEmail)
        .build();

    userStore.addNewUser(user);

    assert userStore.hasUserInfo(testUserId);
  }

  @Test
  public void noUserEntryCheckedReturnsFalse() {
    assertThat(userStore.hasUserInfo(testUserId), equalTo(false));
  }
}
