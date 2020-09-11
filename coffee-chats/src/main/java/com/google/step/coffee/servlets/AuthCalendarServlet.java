package com.google.step.coffee.servlets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.OAuthService;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.UserManager;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;

/**
 * Manage and check user authorisation for scopes for Google Calendar API in
 * <code>OAuthService</code>
 */
@WebServlet("/api/auth/calendar")
public class AuthCalendarServlet extends JsonServlet {

  /** Response object for fetching authorisation status of user. */
  static class CalAuthResponse {
    private boolean authorised;
    private String authLink;

    CalAuthResponse(boolean authorised) {
      this.authorised = authorised;
    }

    CalAuthResponse(boolean authorised, String authLink) {
      this(authorised);
      this.authLink = authLink;
    }

    public boolean isAuthorised() {
      return authorised;
    }

    public String getAuthLink() {
      return authLink;
    }
  }

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    CalAuthResponse responseData;

    if (!OAuthService.userHasAuthorised(UserManager.getCurrentUserId())) {
      responseData = new CalAuthResponse(false, OAuthService.getAuthURL(request));
    } else {
      responseData = new CalAuthResponse(true);
    }

    return responseData;
  }
}
