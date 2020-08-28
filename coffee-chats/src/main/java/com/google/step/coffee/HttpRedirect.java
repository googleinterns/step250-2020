package com.google.step.coffee;

/** An exception for within JsonServlets that can be handled to send redirects as a response. */
public class HttpRedirect extends Exception {
  private final String redirectURL;

  public HttpRedirect(String redirectURL) {
    this.redirectURL = redirectURL;
  }

  public String getRedirectURL() {
    return redirectURL;
  }
}
