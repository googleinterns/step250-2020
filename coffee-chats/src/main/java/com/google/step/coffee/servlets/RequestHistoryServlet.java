package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.PermissionChecker;
import com.google.step.coffee.UserManager;
import com.google.step.coffee.data.RequestStore;
import com.google.step.coffee.entity.CompletedRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.annotation.WebServlet;

@WebServlet("/api/requests")
public class RequestHistoryServlet extends JsonServlet {

  private enum RequestType {
    PENDING,
    COMPLETED
  }

  private RequestStore requestStore = new RequestStore();

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    PermissionChecker.ensureLoggedIn();

    String paramType = request.getRequiredParameter("type");
    RequestType type = RequestType.valueOf(paramType.toUpperCase());

    if (type == RequestType.PENDING) {
      return requestStore.getUnmatchedRequests(UserManager.getCurrentUserId());
    } else {
      List<CompletedRequest> completedRequests = new ArrayList<>();

      completedRequests.addAll(requestStore.getExpiredRequests(UserManager.getCurrentUserId()));
      completedRequests.addAll(requestStore.getMatchedRequests(UserManager.getCurrentUserId()));

      completedRequests.sort(Collections.reverseOrder(
          Comparator.comparing(CompletedRequest::getFirstDateRange)));

      return completedRequests;
    }
  }
}
