package com.google.step.coffee.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.coffee.*;
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet("/api/chat-request")
public class ChatRequestServlet extends JsonServlet {

  private TagStore tagStore = new TagStore();
  private RequestStore requestStore = new RequestStore();

  @Override
  public Object post(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

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

    tagStore.addTags(tags);

    ChatRequest chatRequest = new ChatRequestBuilder()
        .withTags(tags)
        .onDates(dates)
        .withGroupSize(minPeople, maxPeople)
        .withMaxChatLength(duration)
        .willMatchRandomlyOnFail(matchRandom)
        .willMatchWithRecents(matchRecents)
        .build();

    requestStore.addRequest(chatRequest, UserManager.getCurrentUserId());

    return "Success";
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
