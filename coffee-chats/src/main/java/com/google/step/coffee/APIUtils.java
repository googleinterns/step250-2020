package com.google.step.coffee;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/** Utility class holding common tools for Google API usage and authorisation. */
public class APIUtils {

  public static final String APPLICATION_NAME = "Coffee Chats";
  public static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
  public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
}
