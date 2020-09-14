package com.google.step.coffee.servlets;

import com.google.step.coffee.*;
import com.google.step.coffee.data.UserStore;
import com.google.step.coffee.entity.User;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/auth")
public class AuthServlet extends JsonServlet {
  private UserStore userStore = new UserStore();

  public static class Response {
    public String logoutUrl;
    public User user;
    public boolean oauthAuthorized; // Has the user granted the app access to their Google account?
    public String oauthLink;        // If not, they will need to follow this link.

    public Response(boolean oauthAuthorized, String oauthLink) {
      this.logoutUrl = UserManager.getLogoutUrl();
      this.user = UserManager.getCurrentUser();
      this.oauthAuthorized = oauthAuthorized;
      this.oauthLink = oauthLink;
    }
  }

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    if (!userStore.hasUserInfo(UserManager.getCurrentUserId())) {
      userStore.addNewUser(UserManager.getCurrentUser());
    }

    if (!OAuthService.userHasAuthorised(UserManager.getCurrentUserId())) {
      return new Response(false, OAuthService.getAuthURL(request));
    } else {
      return new Response(true, null);
    }
  }
}
