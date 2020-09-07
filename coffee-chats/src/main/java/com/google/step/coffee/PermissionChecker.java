package com.google.step.coffee;

import com.google.gson.annotations.Expose;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
import com.google.step.coffee.entity.User;

import javax.servlet.http.HttpServletResponse;

public class PermissionChecker {
  static GroupStore groupStore = new GroupStore();

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
    GroupMembership.Status status = groupStore.getMembershipStatus(group, UserManager.getCurrentUser());
    if (status != GroupMembership.Status.ADMINISTRATOR && status != GroupMembership.Status.OWNER) {
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }
  }

  public static void ensureCanUpdateMembershipStatus(Group group, User user, GroupMembership.Status status) throws HttpError {
    ensureLoggedIn();
    User activeUser = UserManager.getCurrentUser();
    GroupMembership.Status activeUserStatus = groupStore.getMembershipStatus(group, activeUser);
    GroupMembership.Status curStatus = groupStore.getMembershipStatus(group, user);
    boolean isThisUser = user.id().equals(activeUser.id());

    if (curStatus == GroupMembership.Status.OWNER) {
      // No one can kick/demote the owner
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Cannot change the owner's permissions");
    }

    if (curStatus == GroupMembership.Status.ADMINISTRATOR && activeUserStatus != GroupMembership.Status.OWNER) {
      // Only the owner can kick/demote admins
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Cannot change the user's permissions");
    }

    if (activeUserStatus == GroupMembership.Status.ADMINISTRATOR && status != GroupMembership.Status.NOT_A_MEMBER) {
      // An administrator can not promote others, only kick users from the group or leave
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Cannot change the user's permissions");
    }

    if (curStatus == GroupMembership.Status.NOT_A_MEMBER) {
      if (isThisUser) {
        if (status != GroupMembership.Status.REGULAR_MEMBER) {
          // A new user cannot join as anything other than regular member
          throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Cannot join a group with these permissions");
        } else {
          return;
        }
      }

      // A user cannot be added to a group against their will
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Cannot add a user into a group");
    }

    if (activeUserStatus == GroupMembership.Status.REGULAR_MEMBER) {
      if (isThisUser) {
        if (status == GroupMembership.Status.NOT_A_MEMBER) {
          // A regular user can only leave
          return;
        }
      }

      // A regular member cannot modify other user's permissions
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Cannot change the user's permissions");
    }
  }
}
