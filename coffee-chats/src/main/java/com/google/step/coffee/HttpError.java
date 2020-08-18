package com.google.step.coffee;

import com.google.gson.annotations.Expose;

public class HttpError extends Exception {
  @Expose
  private int errorCode;
  @Expose private String message;

  public HttpError(int errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }
}
