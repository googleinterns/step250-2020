package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.data.CalendarUtils;
import com.google.step.coffee.data.EventStore;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Event;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/eventDelete")
public class EventDeleteServlet extends JsonServlet {
  private final GroupStore groupStore = new GroupStore();
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    Event event = Event.fromEntity(request.getEntityFromParameter("id", "event"));

    Group group = groupStore.get(event.groupId());

    if (group == null) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Orphan event");
    }

    PermissionChecker.ensureCanManageGroup(group);

    CalendarUtils.removeEvent(group.ownerId(), event.calendarId());
    datastore.delete(event.key());
    
    return null;
  }
}
