package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.step.coffee.*;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/api/groupCreate")
public class GroupCreateServlet extends JsonServlet {
  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();
    String name = request.getRequiredParameter("name");

    Entity group = new Entity("group");
    group.setProperty("name", name);
    group.setProperty("description", "");
    group.setProperty("ownerId", UserManager.getCurrentUserId());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(group);

    return Group.builder()
        .setId(KeyFactory.keyToString(group.getKey()))
        .setName((String)group.getProperty("name"))
        .setDescription((String)group.getProperty("description"))
        .build();
  }
}
