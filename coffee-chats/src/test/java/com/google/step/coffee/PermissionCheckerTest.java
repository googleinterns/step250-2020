package com.google.step.coffee;

import org.junit.Test;

public class PermissionCheckerTest extends TestHelper {
  @Test
  public void testEnsureLoggedInWhenLoggedIn() throws Exception {
    PermissionChecker.ensureLoggedIn();
  }

  @Test(expected = HttpError.class)
  public void testEnsureLoggedInWhenLoggedOut() throws Exception {
    helper.setEnvIsLoggedIn(false);
    PermissionChecker.ensureLoggedIn();
  }
}
