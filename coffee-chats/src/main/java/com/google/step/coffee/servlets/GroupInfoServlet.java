package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/groupInfo")
public class GroupInfoServlet extends JsonServlet {
  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();
    return Group.fromEntity(request.getEntityFromParameter("id", "group"));
  }

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    // TODO(tsarn): also check that the user has write access to the group
    PermissionChecker.ensureLoggedIn();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity entity = request.getEntityFromParameter("id", "group");
    entity.setProperty("name", request.getRequiredParameter("name"));
    entity.setProperty("description", request.getRequiredParameter("description"));
    datastore.put(entity);

    return null;
  }
}
