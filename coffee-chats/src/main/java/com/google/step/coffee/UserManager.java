package com.google.step.coffee;

import com.google.api.services.people.v1.PeopleService;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.coffee.data.PeopleUtils;
import com.google.step.coffee.entity.User;

import java.io.IOException;

public class UserManager {
  private static UserService getUserService() {
    return UserServiceFactory.getUserService();
  }

  /**
   * Returns the URL to which the user will be redirected if not logged in
   */
  public static String getLoginUrl() {
    return getLoginUrl("/");
  }

  /**
   * Returns the URL to which the user will be redirected if not logged in, returning to
   * <code>originURL</code> when log in is completed.
   */
  public static String getLoginUrl(String originURL) {
    return getUserService().createLoginURL(originURL);
  }

  /**
   * Returns the URL that the user can follow to log out
   */
  public static String getLogoutUrl() {
    return getUserService().createLogoutURL("/");
  }

  /**
   * Returns current user's unique identifier or <code>null</code> if not logged in
   */
  public static String getCurrentUserId() {
    if (!getUserService().isUserLoggedIn()) {
      return null;
    }

    return getUserService().getCurrentUser().getUserId();
  }

  /**
   * Returns info about current user or <code>null</code> if not logged in
   */
  public static User getCurrentUser() {
    String userId = getCurrentUserId();

    if (userId == null) {
      return null;
    }

    return User.builder()
        .setId(userId)
        .setEmail(getUserService().getCurrentUser().getEmail())
        .build();
  }

  /** Returns an AppEngine User object for the current user. */
  public static com.google.appengine.api.users.User getCurrentGAEUser() {
    return getUserService().getCurrentUser();
  }

  /**
   * Returns <code>true</code> iff the user is logged in
   */
  public static boolean isUserLoggedIn() {
    return getUserService().isUserLoggedIn();
  }
}
