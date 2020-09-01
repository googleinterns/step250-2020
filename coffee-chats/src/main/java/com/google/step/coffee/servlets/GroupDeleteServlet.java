package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/groupDelete")
public class GroupDeleteServlet extends JsonServlet {
  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    // TODO(tsarn): also check that the user has write access to the group
    PermissionChecker.ensureLoggedIn();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(request.getKeyFromParameter("id", "group"));

    return null;
  }
}
