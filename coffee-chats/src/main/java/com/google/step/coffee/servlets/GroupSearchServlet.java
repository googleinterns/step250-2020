package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.data.GroupStore;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/groupSearch")
public class GroupSearchServlet extends JsonServlet {
  private GroupStore groupStore = new GroupStore();

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    List<String> tags = Arrays.asList(request.getRequiredJsonParameter("tags", String[].class));

    return groupStore.findGroupsByTags(tags);
  }
}
