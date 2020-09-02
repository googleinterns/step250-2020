package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/groupInfo")
public class GroupInfoServlet extends JsonServlet {
  private GroupStore groupStore = new GroupStore();

  private Group getGroup(JsonServletRequest request) throws HttpError {
    // TODO(tsarn): also check that the user has write access to the group
    PermissionChecker.ensureLoggedIn();

    return Group.fromEntity(request.getEntityFromParameter("id", "group"));
  }

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    return getGroup(request);
  }

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    Group prevGroup = getGroup(request);
    Group group = Group.builder()
        .setId(prevGroup.id())
        .setName(request.getRequiredParameter("name"))
        .setDescription(request.getRequiredParameter("description"))
        .setOwnerId(prevGroup.ownerId())
        .build();

    groupStore.put(group);

    return null;
  }
}
