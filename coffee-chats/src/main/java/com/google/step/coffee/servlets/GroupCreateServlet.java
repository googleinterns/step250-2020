package com.google.step.coffee.servlets;

import com.google.step.coffee.*;
import com.google.step.coffee.data.GroupStore;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/groupCreate")
public class GroupCreateServlet extends JsonServlet {
  private GroupStore groupStore = new GroupStore();

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();
    String name = request.getRequiredParameter("name");
    return groupStore.create(name);
  }
}
