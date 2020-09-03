package com.google.step.coffee;

import com.google.gson.annotations.Expose;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;

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

  public static void ensureCanManageGroup(Group group) throws HttpError {
    ensureLoggedIn();
    GroupMembership.Status status = new GroupStore().getMembershipStatus(group, UserManager.getCurrentUser());
    if (status != GroupMembership.Status.ADMINISTRATOR) {
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }
  }
}
