package com.google.step.coffee.servlets;

import com.google.step.coffee.*;
import com.google.step.coffee.data.UserStore;
import com.google.step.coffee.entity.User;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/auth")
public class AuthServlet extends JsonServlet {

  private UserStore userStore = new UserStore();

  private static class Response {

    private String logoutUrl;
    private User user;

    public Response() {
      this.logoutUrl = UserManager.getLogoutUrl();
      this.user = UserManager.getCurrentUser();
    }

    public String getLogoutUrl() {
      return logoutUrl;
    }

    public User getUser() {
      return user;
    }
  }

  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    if (!userStore.hasUserInfo(UserManager.getCurrentUserId())) {
      userStore.addNewUser(UserManager.getCurrentGAEUser());
    }

    return new Response();
  }
}
