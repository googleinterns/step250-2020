package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.entity.Event;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@WebServlet("/api/events")
public class EventsServlet extends JsonServlet {
  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    return Event.builder()
        .setId("test_event")
        .setGroupId("test_group")
        .setDescription("Description of the example event")
        .setDuration(Duration.ofMinutes(30))
        .setStart(Instant.now());
  }
}
