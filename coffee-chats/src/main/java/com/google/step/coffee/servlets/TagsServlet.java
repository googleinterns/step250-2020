package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.data.TagStore;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet("/api/tags")
public class TagsServlet extends JsonServlet {
  private TagStore tagStore = TagStore.getInstance();

  @Override
  public Object get(HttpServletRequest request) throws IOException, HttpError {
    return tagStore.getTags();
  }
}
