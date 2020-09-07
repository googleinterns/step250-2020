package com.google.step.coffee;

import javax.servlet.http.HttpServletResponse;

public class InvalidEntityException extends HttpError {

  public InvalidEntityException(String message) {
    super(HttpServletResponse.SC_BAD_REQUEST, message);
  }
}
