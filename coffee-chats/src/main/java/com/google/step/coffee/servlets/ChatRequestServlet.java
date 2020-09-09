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
    int minPeople = Integer.parseInt(getParameterOrDefault("minPeople", request, "1"));
    int maxPeople = Integer.parseInt(getParameterOrDefault("maxPeople", request, "1"));
    int durationMins = Integer.parseInt(getParameterOrDefault("durationMins", request, "30"));
    boolean matchRandom =
        Boolean.parseBoolean(getParameterOrDefault("matchRandom", request, "false"));
    boolean matchRecents =
        Boolean.parseBoolean(getParameterOrDefault("matchRecents", request, "true"));

    List<Date> startDates = startDateStrings.stream()
        .map(Long::parseLong)
        .map(Date::new)
        .collect(Collectors.toList());

    List<Date> endDates = endDateStrings.stream()
        .map(Long::parseLong)
        .map(Date::new)
        .collect(Collectors.toList());

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

  private List<String> getParameterValues(String name, HttpServletRequest req) {
    String paramString = req.getParameter(name);
    if (paramString == null || paramString.equals("")) {
      return new ArrayList<>();
    }

    return Arrays.asList(paramString.split(","));
  }
}
