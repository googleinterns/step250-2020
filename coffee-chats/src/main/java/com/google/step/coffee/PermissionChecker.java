package com.google.step.coffee;

import com.google.gson.annotations.Expose;

import javax.servlet.http.HttpServletResponse;

public class PermissionChecker {
  private static class AuthenticationRequiredError extends HttpError {
    @Expose
    private String loginUrl;

    public AuthenticationRequiredError() {
      super(HttpServletResponse.SC_FORBIDDEN, "Authentication required");
      this.loginUrl = UserManager.getLoginUrl();
    }

    public String getLoginUrl() {
      return loginUrl;
    }
  }

  public static void ensureLoggedIn() throws HttpError {
    if (!UserManager.isUserLoggedIn()) {
      throw new AuthenticationRequiredError();
    }
  }
}
