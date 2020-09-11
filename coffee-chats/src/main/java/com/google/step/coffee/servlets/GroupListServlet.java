package com.google.step.coffee.servlets;

import com.google.step.coffee.*;
import com.google.step.coffee.data.GroupStore;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/groupList")
public class GroupListServlet extends JsonServlet {
  GroupStore groupStore = new GroupStore();

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    return groupStore.getUserGroups(UserManager.getCurrentUser());
  }
}
