package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.HttpRedirect;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.UserManager;
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

@WebServlet("/api/chat-request")
public class ChatRequestServlet extends JsonServlet {

  private RequestStore requestStore = new RequestStore();

  @Override
  public Object post(HttpServletRequest request) throws IOException, HttpError, HttpRedirect {
    UserManager.enforceUserLogin(request);

    ChatRequest chatRequest = buildChatRequestFromHttpRequest(request);

    requestStore.addRequest(chatRequest);

    return "Success";
  }

  private ChatRequest buildChatRequestFromHttpRequest(HttpServletRequest request) throws HttpError {
    List<String> tags = getParameterValues("tags", request);
    List<String> dateStrings = getParameterValues("dates", request);
    int minPeople = Integer.parseInt(getParameterOrDefault("minPeople", request, "1"));
    int maxPeople = Integer.parseInt(getParameterOrDefault("maxPeople", request, "1"));
    int duration = Integer.parseInt(getParameterOrDefault("duration", request, "30"));
    boolean matchRandom = Boolean.parseBoolean(getParameterOrDefault("matchRandom", request, "false"));
    boolean matchRecents = Boolean.parseBoolean(getParameterOrDefault("matchRecents", request, "true"));

    List<Date> dates = dateStrings.stream()
        .map(Long::parseLong)
        .map(Date::new)
        .collect(Collectors.toList());

    return new ChatRequestBuilder()
        .withTags(tags)
        .onDates(dates)
        .withGroupSize(minPeople, maxPeople)
        .withMaxChatLength(duration)
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
