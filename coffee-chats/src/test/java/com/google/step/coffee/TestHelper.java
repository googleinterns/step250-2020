package com.google.step.coffee;

import java.util.Collections;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import org.junit.After;
import org.junit.Before;

public class TestHelper {
  protected final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig(),
          new LocalUserServiceTestConfig())
          .setEnvIsLoggedIn(true)
          .setEnvEmail("test-user@example.com")
          .setEnvAuthDomain("example.com")
          .setEnvAttributes(
              Collections.singletonMap(
                  "com.google.appengine.api.users.UserService.user_id_key",
                  "test_user"
              )
          );

  @Before
  public void setUp() throws Exception {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
