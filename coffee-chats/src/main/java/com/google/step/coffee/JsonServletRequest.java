package com.google.step.coffee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JsonServletRequest extends HttpServletRequest {
  default String getRequiredParameter(String name) throws HttpError {
    String value = getParameter(name);
    if (value == null) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "No value specified for parameter '" + name + "'");
    }
    return value;
  }
}
