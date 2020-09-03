package com.google.step.coffee.servlets;

import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.data.TagStore;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;

@WebServlet("/api/tags")
public class TagsServlet extends JsonServlet {
  private TagStore tagStore = new TagStore();

  @Override
  public Object get(JsonServletRequest request) throws IOException, HttpError {
    return tagStore.getTags();
  }
}
