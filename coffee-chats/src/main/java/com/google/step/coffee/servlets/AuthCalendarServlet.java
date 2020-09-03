package com.google.step.coffee.servlets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.OAuthService;
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
  private static class CalAuthResponse {
    private boolean authorised;
    private String authLink;

    CalAuthResponse(boolean authorised) {
      this.authorised = authorised;
    }

    CalAuthResponse(boolean authorised, String authLink) {
      this(authorised);
      this.authLink = authLink;
    }
  }

  @Override
  public Object get(JsonServletRequest request) throws IOException {
    CalAuthResponse responseData;
    Credential credentials = OAuthService.getCredentials(UserManager.getCurrentUserId());

    if (credentials != null) {
      responseData = new CalAuthResponse(true);
    } else {
      responseData = new CalAuthResponse(false, OAuthService.getAuthURL(request));
    }

    return responseData;
  }
}
