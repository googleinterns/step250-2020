package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.EventStore;
import com.google.step.coffee.entity.Event;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@WebServlet("/api/eventCreate")
public class EventCreateServlet extends JsonServlet {
  private EventStore eventStore = new EventStore();

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    Group group = Group.fromEntity(request.getEntityFromParameter("id", "group"));
    PermissionChecker.ensureCanManageGroup(group);

    Instant start = Instant.ofEpochSecond(request.getRequiredLongParameter("start"));
    Duration duration = Duration.ofMinutes(request.getRequiredLongParameter("duration"));
    String description = request.getRequiredParameter("description");

    return eventStore.put(Event.builder()
        .setGroupId(group.id())
        .setStart(start)
        .setDuration(duration)
        .setDescription(description)
        .build()
    );
  }
}
