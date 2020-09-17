package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.*;
import com.google.step.coffee.tasks.AvailabilityScheduler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/scheduleEvent")
public class EventScheduleServlet extends JsonServlet {
  static final Duration MIN_DURATION = Duration.ofMinutes(15);
  static final Duration MAX_DURATION = Duration.ofMinutes(180);

  private GroupStore groupStore = new GroupStore();

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
    List<Availability> requests = groupStore.getMembers(group).stream()
        .map(member -> EventRequest.builder()
            .setUserId(member.user().id())
            .setDuration(duration)
            .setDateRanges(ranges)
        .build()).collect(Collectors.toList());

    AvailabilityScheduler scheduler = new AvailabilityScheduler(requests);
    List<DateRange> suggestions = scheduler.findAvailableRangesBestEffort(duration);

    if (suggestions.isEmpty()) {
      return null;
    }

    return suggestions.get(0).getStart();
  }
}
