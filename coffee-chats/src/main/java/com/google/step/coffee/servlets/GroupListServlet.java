package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/groupList")
public class GroupListServlet extends JsonServlet {
    @Override
    public Object get(JsonServletRequest request) throws IOException, HttpError {
        PermissionChecker.ensureLoggedIn();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        List<Group> groups = new ArrayList<>();
        Query query = new Query("group");
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {
            groups.add(Group.builder()
                .setId(KeyFactory.keyToString(entity.getKey()))
                .setName((String)entity.getProperty("name"))
                .setDescription((String)entity.getProperty("description"))
                .build());
        }

        return groups;
    }
}
