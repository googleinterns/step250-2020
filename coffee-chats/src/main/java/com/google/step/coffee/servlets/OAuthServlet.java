package com.google.step.coffee.servlets;

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
@WebServlet("/api/oauth")
public class OAuthServlet extends JsonServlet {

  /** Response object for fetching authorisation status of user. */
  private static class OAuthResponse {
    private boolean authorised;
    private String authLink;

    OAuthResponse(boolean authorised) {
      this.authorised = authorised;
    }

    OAuthResponse(boolean authorised, String authLink) {
      this(authorised);
      this.authLink = authLink;
    }
  }

  @Override
  public Object get(JsonServletRequest request) throws IOException {
    OAuthResponse responseData;

    if (!OAuthService.userHasAuthorised(UserManager.getCurrentUserId())) {
      responseData = new OAuthResponse(false, OAuthService.getAuthURL(request));
    } else {
      responseData = new OAuthResponse(true);
    }

    return responseData;
  }
}
