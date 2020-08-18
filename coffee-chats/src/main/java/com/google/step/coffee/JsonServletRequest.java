package com.google.step.coffee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class JsonServletRequest extends HttpServletRequestWrapper {
  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request the {@link HttpServletRequest} to be wrapped.
   * @throws IllegalArgumentException if the request is null
   */
  public JsonServletRequest(HttpServletRequest request) {
    super(request);
  }

  public String getRequiredParameter(String name) throws HttpError {
    String value = getParameter(name);
    if (value == null) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "No value specified for parameter '" + name + "'");
    }
    return value;
  }
}
