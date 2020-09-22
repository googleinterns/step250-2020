package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.EventStore;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Event;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/api/groupSuggest")
public class GroupSuggestServlet extends JsonServlet {
  GroupStore groupStore = new GroupStore();
  EventStore eventStore = new EventStore();

  static class Recommendation {
    public Group group;
    public long nearestEvent = 0;

    Recommendation(Group group) {
      this.group = group;
    }
  }

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    List<String> tags = Arrays.asList(request.getRequiredJsonParameter("tags", String[].class));

    List<Group> groups = groupStore.findGroupsByTags(tags);
    List<Event> events = eventStore.getUpcomingEventsForGroups(
        groups.stream().map(Group::id).collect(Collectors.toList()));

    Map<String, Recommendation> recommendationMap = new HashMap<>();

    for (Group group : groups) {
      recommendationMap.put(group.id(), new Recommendation(group));
    }

    for (Event event : events) {
      Recommendation rec = recommendationMap.get(event.groupId());

      if (rec.nearestEvent == 0 || rec.nearestEvent > event.start().getEpochSecond()) {
        rec.nearestEvent = event.start().getEpochSecond();
      }
    }

    return recommendationMap.values();
  }
}
