package com.google.step.coffee.tasks;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.entity.ChatRequest;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet triggered as cron job to match current chat requests together. Triggered periodically by
 * fetch.
 */
@WebServlet("/api/tasks/request-matching")
public class RequestMatchingTask extends HttpServlet {

  private RequestMatcher matcher = new RequestMatcher();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String cronHeader = req.getHeader("X-Appengine-Cron");
    UserService userService = UserServiceFactory.getUserService();

    if (cronHeader == null || !Boolean.parseBoolean(cronHeader)) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden action.");
      return;
    }

    RequestStore requestStore = new RequestStore();
    List<ChatRequest> requestList = requestStore.getUnmatchedRequests();

    matcher.matchRequests(requestList, requestStore);

    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
