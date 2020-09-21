package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.EventStore;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/eventList")
public class EventListServlet extends JsonServlet {
  private final EventStore eventStore = new EventStore();

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();
    List<String> groupIds = Arrays.asList(request.getRequiredJsonParameter("groups", String[].class));

    if (groupIds.isEmpty()) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Invalid value for parameter 'groupIds'");
    }

    return eventStore.getUpcomingEventsForGroups(groupIds);
  }
}
