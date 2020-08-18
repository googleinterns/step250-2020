package com.google.step.coffee;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserManager {
  private static UserService getUserService() {
    return UserServiceFactory.getUserService();
  }

  public static String getLoginUrl() {
    return getUserService().createLoginURL("/");
  }

  public static String getCurrentUserId() {
    if (!getUserService().isUserLoggedIn()) {
      return null;
    }

    return getUserService().getCurrentUser().getUserId();
  }

  public static boolean isUserLoggedIn() {
    return getUserService().isUserLoggedIn();
  }
}
