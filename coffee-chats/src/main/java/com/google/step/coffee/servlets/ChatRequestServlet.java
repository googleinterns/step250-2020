package com.google.step.coffee.servlets;

import com.google.step.coffee.*;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.entity.ChatRequest;
import com.google.step.coffee.entity.ChatRequestBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet to manage user requests for a chat. */
@WebServlet("/api/chat-request")
public class ChatRequestServlet extends JsonServlet {

  private RequestStore requestStore = new RequestStore();

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    if (OAuthService.userHasAuthorised(UserManager.getCurrentUserId())) {
      ChatRequest chatRequest = buildChatRequestFromHttpRequest(request);

      requestStore.addRequest(chatRequest);

      return "Success";
    } else {
      throw new HttpError(HttpServletResponse.SC_UNAUTHORIZED,
          "Please authorise app to schedule chat");
    }
  }

  private ChatRequest buildChatRequestFromHttpRequest(HttpServletRequest request) throws HttpError {
    List<String> tags = getParameterValues("tags", request);
    List<String> startDateStrings = getParameterValues("startDates", request);
    List<String> endDateStrings = getParameterValues("endDates", request);
    int minPeople = parseIntParam("minPeople", request);
    int maxPeople = parseIntParam("maxPeople", request);
    int durationMins = parseIntParam("durationMins", request);
    boolean matchRandom = parseBooleanParam("matchRandom", request, "false");
    boolean matchRecents = parseBooleanParam("matchRecents", request, "true");

    List<Date> startDates;
    List<Date> endDates;

    try {
       startDates = startDateStrings.stream()
          .map(Long::parseLong)
          .map(Date::new)
          .collect(Collectors.toList());

      endDates = endDateStrings.stream()
          .map(Long::parseLong)
          .map(Date::new)
          .collect(Collectors.toList());
    } catch (NumberFormatException e) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date timestamp.");
    }

    if (startDates.size() != endDates.size()) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Mismatched date ranges");
    }

    return new ChatRequestBuilder()
        .withTags(tags)
        .onDates(startDates, endDates)
        .withGroupSize(minPeople, maxPeople)
        .withMaxChatLength(durationMins)
        .willMatchRandomlyOnFail(matchRandom)
        .willMatchWithRecents(matchRecents)
        .forUser(UserManager.getCurrentUserId())
        .build();
  }

  private String getParameterOrDefault(String name, HttpServletRequest req, String defaultValue) {
    String paramString = req.getParameter(name);
    return (paramString != null) ? paramString : defaultValue;
  }

  private String getParameter(String name, HttpServletRequest req) throws HttpError {
    String paramString = req.getParameter(name);
    if (paramString != null) {
      return paramString;
    } else {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter: " + name);
    }
  }

  private List<String> getParameterValues(String name, HttpServletRequest req) {
    String paramString = req.getParameter(name);
    if (paramString == null || paramString.equals("")) {
      return new ArrayList<>();
    }

    return Arrays.asList(paramString.split(","));
  }

  private Integer parseIntParam(String name, HttpServletRequest req) throws HttpError {
    String paramString = getParameter(name, req);

    try {
      return Integer.parseInt(paramString);
    } catch (NumberFormatException e) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST,
          "Invalid integer parameter: " + name);
    }
  }

  private boolean parseBooleanParam(String name, HttpServletRequest req, String defaultValue) {
    String paramString = getParameterOrDefault(name, req, defaultValue);

    return Boolean.parseBoolean(paramString);
  }
}
