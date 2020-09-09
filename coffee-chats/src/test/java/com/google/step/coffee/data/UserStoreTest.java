package com.google.step.coffee.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.step.coffee.TestHelper;
import org.junit.Assert;
import org.junit.Test;

public class UserStoreTest extends TestHelper {

  final String testUserId = "id1234test";
  final String testEmail = "test-user@example.com";

  UserStore userStore = new UserStore();

  @Test
  public void addingNewUserHasCorrectEmail() {
    User user = mock(User.class);
    when(user.getUserId()).thenReturn(testUserId);
    when(user.getEmail()).thenReturn(testEmail);

    userStore.addNewUser(user);

    assertThat(userStore.getEmail(testUserId), equalTo(testEmail));
  }

  @Test
  public void noUserInfoOnGetEmailThrowsException() {
    try {
      userStore.getEmail(testUserId);
      Assert.fail("Should not be able to retrieve email from no stored users.");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), equalTo("No UserInfo stored for userId " + testUserId));
    }
  }

  @Test
  public void addingUserCreatesCheckableEntry() {
    User user = mock(User.class);
    when(user.getUserId()).thenReturn(testUserId);
    when(user.getEmail()).thenReturn(testEmail);

    userStore.addNewUser(user);

    assert userStore.hasUserInfo(testUserId);
  }

  @Test
  public void noUserEntryCheckedReturnsFalse() {
    assertThat(userStore.hasUserInfo(testUserId), equalTo(false));
  }
}
