package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
import com.google.step.coffee.entity.User;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/api/groupMembers")
public class GroupMembersServlet extends JsonServlet {
  private GroupStore groupStore = new GroupStore();

  private static class GroupMember {
    public final User user;
    public final GroupMembership.Status status;

    public GroupMember(User user, GroupMembership.Status status) {
      this.user = user;
      this.status = status;
    }
  }

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();
    Group group = Group.fromEntity(request.getEntityFromParameter("id", "group"));
    return groupStore.getMembers(group).stream()
        .map(x -> new GroupMember(x.user(), x.status())).collect(Collectors.toList());
  }
}
