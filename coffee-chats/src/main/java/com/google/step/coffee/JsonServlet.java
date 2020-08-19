package com.google.step.coffee;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Provides an abstract class for JSON endpoints.
 * Users of this class are expected to override <code>get</code> and <code>post</code>
 * methods to either return a Gson-serializable <code>Object</code> or to throw an exception.
 *
 * If a method throws <code>HttpError</code> an appropriate HTTP error code will be
 * set, and a JSON response in format of <code>{"errorCode": ERROR_CODE, "message": MESSAGE}</code>
 * will be returned.
*/
abstract public class JsonServlet extends HttpServlet {
  protected interface JsonHttpHandler {
    Object handle(HttpServletRequest request) throws IOException, HttpError;
  }

  private static String stringify(Object object, boolean onlyExposed) {
    GsonBuilder builder = new GsonBuilder();
    builder.disableHtmlEscaping();
    if (onlyExposed) {
      builder.excludeFieldsWithoutExposeAnnotation();
    }
    Gson gson = builder.create();

    return gson.toJson(object);
  }

  public Object get(HttpServletRequest request) throws IOException, HttpError {
    throw new HttpError(
        HttpServletResponse.SC_METHOD_NOT_ALLOWED,
        "GET method is not allowed for this endpoint"
    );
  }

  public Object post(HttpServletRequest request) throws IOException, HttpError {
    throw new HttpError(
        HttpServletResponse.SC_METHOD_NOT_ALLOWED,
        "POST method is not allowed for this endpoint"
    );
  }

  private void handle(HttpServletRequest request, HttpServletResponse response, JsonHttpHandler handler) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/json");

    try {
      Object jsonResponse = handler.handle(request);
      response.getWriter().write(stringify(jsonResponse, false));
    } catch (HttpError httpError) {
      response.setStatus(httpError.getErrorCode());
      response.getWriter().write(stringify(httpError, true));
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    handle(request, response, this::get);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    handle(request, response, this::post);
  }
}
