package com.google.step.coffee.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.data.TagStore;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.ChatRequestBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/chat-request")
public class ChatRequestServlet extends HttpServlet {

  private TagStore tagStore = new TagStore();
  private RequestStore requestStore = new RequestStore();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      String loginURL = userService.createLoginURL(req.getHeader("referer"));
      resp.sendRedirect(loginURL);
    }

    List<String> tags = getParameterValues("tags", req);
    List<String> dateStrings = getParameterValues("dates", req);
    int minPeople = Integer.parseInt(getParameterOrDefault("minPeople", req, "1"));
    int maxPeople = Integer.parseInt(getParameterOrDefault("maxPeople", req, "1"));
    int duration = Integer.parseInt(getParameterOrDefault("duration", req, "30"));
    boolean matchRandom = Boolean.parseBoolean(getParameterOrDefault("matchRandom", req, "false"));
    boolean matchRecents = Boolean.parseBoolean(getParameterOrDefault("matchRecents", req, "true"));

    List<Date> dates = dateStrings.stream()
        .map(Long::parseLong)
        .map(Date::new)
        .collect(Collectors.toList());

    tagStore.addTags(tags);

    try {
      ChatRequest chatRequest = new ChatRequestBuilder()
          .withTags(tags)
          .onDates(dates)
          .withGroupSize(minPeople, maxPeople)
          .withMaxChatLength(duration)
          .willMatchRandomlyOnFail(matchRandom)
          .willMatchWithRecents(matchRecents)
          .build();

      requestStore.addRequest(chatRequest, userService.getCurrentUser());

      resp.setStatus(HttpServletResponse.SC_OK);
    } catch (IllegalArgumentException | IllegalStateException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }

  private String getParameterOrDefault(String name, HttpServletRequest req, String defaultValue) {
    String paramString = req.getParameter(name);
    return (paramString != null) ? paramString : defaultValue;
  }

  private List<String> getParameterValues(String name, HttpServletRequest req) {
    String paramString = req.getParameter(name);
    if (paramString == null || paramString.equals("")) {
      return new ArrayList<>();
    }

    return Arrays.asList(paramString.split(","));
  }
}
