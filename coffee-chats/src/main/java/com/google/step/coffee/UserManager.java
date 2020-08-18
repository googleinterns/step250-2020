package com.google.step.coffee;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.coffee.entity.User;

public class UserManager {
  private static UserService getUserService() {
    return UserServiceFactory.getUserService();
  }

  public static String getLoginUrl() {
    return getUserService().createLoginURL("/");
  }

  public static String getLogoutUrl() {
    return getUserService().createLogoutURL("/");
  }

  public static String getCurrentUserId() {
    if (!getUserService().isUserLoggedIn()) {
      return null;
    }

    return getUserService().getCurrentUser().getUserId();
  }

  public static User getCurrentUser() {
    String userId = getCurrentUserId();

    if (userId == null) {
      return null;
    }

    return User.builder().setId(userId).build();
  }

  public static boolean isUserLoggedIn() {
    return getUserService().isUserLoggedIn();
  }
}
