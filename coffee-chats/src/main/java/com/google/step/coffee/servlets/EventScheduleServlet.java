package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.entity.DateRange;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@WebServlet("/api/scheduleEvent")
public class EventScheduleServlet extends JsonServlet {
  static final Duration MIN_DURATION = Duration.ofMinutes(15);
  static final Duration MAX_DURATION = Duration.ofMinutes(180);

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    Group group = Group.fromEntity(request.getEntityFromParameter("id", "group"));
    PermissionChecker.ensureCanManageGroup(group);

    Duration duration = Duration.ofMinutes(request.getRequiredLongParameter("duration"));
    if (duration.compareTo(MIN_DURATION) < 0 ||
        duration.compareTo(MAX_DURATION) > 0) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Invalid duration");
    }

    List<DateRange> ranges = Arrays.asList(request.getRequiredJsonParameter("ranges", DateRange[].class));

    return new Date();
  }
}
