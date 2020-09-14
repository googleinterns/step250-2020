package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.data.TagStore;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/groupInfo")
public class GroupInfoServlet extends JsonServlet {
  private GroupStore groupStore = new GroupStore();
  private TagStore tagStore = new TagStore();

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();
    return Group.fromEntity(request.getEntityFromParameter("id", "group"));
  }

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    Group prevGroup = Group.fromEntity(request.getEntityFromParameter("id", "group"));
    PermissionChecker.ensureCanManageGroup(prevGroup);

    List<String> tags = Arrays.asList(request.getRequiredJsonParameter("tags", String[].class));

    tagStore.addTags(tags);

    Group group = Group.builder()
        .setId(prevGroup.id())
        .setName(request.getRequiredParameter("name"))
        .setDescription(request.getRequiredParameter("description"))
        .setOwnerId(prevGroup.ownerId())
        .setTags(tags)
        .build();

    groupStore.put(group);

    return null;
  }
}
