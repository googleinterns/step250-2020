package com.google.step.coffee.servlets;

import com.google.step.coffee.*;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
import com.google.step.coffee.entity.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/groupJoin")
public class GroupJoinServlet extends JsonServlet {
  private GroupStore groupStore = new GroupStore();

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    Group group = Group.fromEntity(request.getEntityFromParameter("id", "group"));
    User user = UserManager.getCurrentUser();

    if (groupStore.getMembershipStatus(group, user) != GroupMembership.Status.NOT_A_MEMBER) {
      throw new HttpError(HttpServletResponse.SC_FORBIDDEN, "Already a member");
    }

    groupStore.updateMembershipStatus(group, user, GroupMembership.Status.REGULAR_MEMBER);
    return null;
  }
}
