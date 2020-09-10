package com.google.step.coffee.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeCallbackServlet;
import com.google.step.coffee.OAuthService;
import com.google.step.coffee.data.UserStore;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handle callback from OAuth 2.0 request by user. */
@WebServlet("/api/oauth2callback")
public class OAuthCallbackServlet extends AbstractAppEngineAuthorizationCodeCallbackServlet {
  private UserStore userStore = new UserStore();

  @Override
  protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
      throws ServletException, IOException {
    userStore.updateCurrentUserInfo();
    resp.sendRedirect("/");
  }

  @Override
  protected void onError(HttpServletRequest req, HttpServletResponse resp,
      AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
    super.onError(req, resp, errorResponse);
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
    return OAuthService.getAuthFlow();
  }

  @Override
  protected String getRedirectUri(HttpServletRequest httpServletRequest)
      throws ServletException, IOException {
    return OAuthService.getRedirectUri(httpServletRequest);
  }
}
