package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.UserStore;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;

/**
 * Handles requests to fetch user information for several users by id.
 */
@WebServlet("/api/users-info")
public class UserInfoServlet extends JsonServlet {

  private UserStore userStore = new UserStore();

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    String[] userIds = request.getRequiredJsonParameter("userIds", String[].class);

    return Arrays.stream(userIds).map(userStore::getUser).collect(Collectors.toList());
  }
}
