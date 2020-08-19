package com.google.step.coffee.servlets;

import com.google.step.coffee.JsonServlet;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet("/api/tags")
public class TagsServlet extends JsonServlet {

  @Override
  public Object get(HttpServletRequest request) throws IOException, HttpError {
    return Arrays.asList(
        "Football",
        "Rugby",
        "Cricket",
        "Baseball",
        "Netball",
        "American Football",
        "Hockey",
        "Ice Hockey",
        "Running",
        "Hiking",
        "Skiing",
        "Photography",
        "Art",
        "Classical Art",
        "Instruments",
        "Jazz",
        "Classical Music",
        "Orchestra",
        "R&B",
        "Hip Hop",
        "Pop Music",
        "Electronic Music",
        "Python",
        "C++",
        "Java",
        "Javascript",
        "Typescript",
        "Go",
        "Assembly",
        "Video Games",
        "Movies",
        "Comics",
        "Books"
    );
  }
}
