package com.google.step.coffee.servlets;

import com.google.step.coffee.data.TagStore;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/chat-request")
public class ChatRequestServlet extends HttpServlet {

  private static final int MAX_PARTICIPANTS = 4;

  private TagStore tagStore = TagStore.getInstance();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    List<String> tags = getParameterValues("tags", req);
    List<String> dateStrings = getParameterValues("dates", req);
    int minPeople = Integer.parseInt(getParameterOrDefault("minPeople", req, "1"));
    int maxPeople = Integer.parseInt(getParameterOrDefault("maxPeople", req, "1"));
    int duration = Integer.parseInt(getParameterOrDefault("duration", req, "30"));
    boolean randomMatch = Boolean.parseBoolean(getParameterOrDefault("randomMatch", req, "false"));
    boolean pastMatched = Boolean.parseBoolean(getParameterOrDefault("pastMatched", req, "true"));

    if (dateStrings.isEmpty()) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<LocalDateTime> dates = dateStrings.stream()
        .map(Long::parseLong)
        .map(epochSecond -> LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC))
        .collect(Collectors.toList());

    tagStore.addTags(tags);

    resp.setStatus(HttpServletResponse.SC_OK);
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
