package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/groupCreate")
public class GroupCreateServlet extends JsonServlet {
  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();
    String name = request.getRequiredParameter("name");
    return null;
  }
}
