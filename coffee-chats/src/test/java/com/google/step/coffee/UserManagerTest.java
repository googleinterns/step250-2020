package com.google.step.coffee;

import com.google.step.coffee.entity.User;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class UserManagerTest extends TestHelper {
  @Test
  public void testGetCurrentUserIdLoggedIn() {
    assertThat(UserManager.getCurrentUserId(), equalTo("test_user"));
  }

  @Test
  public void testGetCurrentUserIdNotLoggedIn() {
    helper.setEnvIsLoggedIn(false);
    assertThat(UserManager.getCurrentUserId(), nullValue());
  }

  @Test
  public void testGetCurrentUserLoggedIn() {
    assertThat(UserManager.getCurrentUser(), equalTo(User.builder()
        .setId("test_user")
        .setEmail("test-user@example.com")
        .build()));
  }

  @Test
  public void testGetCurrentUserNotLoggedIn() {
    helper.setEnvIsLoggedIn(false);
    assertThat(UserManager.getCurrentUser(), nullValue());
  }

  @Test
  public void testIsUserLoggedInTrue() {
    assertThat(UserManager.isUserLoggedIn(), equalTo(true));
  }

  @Test
  public void testIsUserLoggedInFalse() {
    helper.setEnvIsLoggedIn(false);
    assertThat(UserManager.isUserLoggedIn(), equalTo(false));
  }
}
