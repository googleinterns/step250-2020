package com.google.step.coffee;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonServletRequestTest extends TestHelper {
  @Test
  public void testGetRequiredParameterExists() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("hello")).thenReturn("world");

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    assertThat(jsonServletRequest.getRequiredParameter("hello"), equalTo("world"));
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredParameterDoesNotExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("goodbye")).thenReturn(null);

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredParameter("goodbye");
  }

  @Test
  public void testGetRequiredKeyExists() throws Exception {
    Key key = KeyFactory.createKey("foo", 12345);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    assertThat(
        jsonServletRequest.getRequiredKey("id", "foo"),
        equalTo(key));
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredKeyDoesNotExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(null);

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredKey("id", "foo");
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredKeyWrongKind() throws Exception {
    Key key = KeyFactory.createKey("foo", 12345);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredKey("id", "bar");
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredKeyInvalidKey() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn("this is not a valid key");

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredKey("id", "bar");
  }

  @Test
  public void testGetRequiredEntityExists() throws Exception {
    Entity entity = new Entity("foo");
    datastore.put(entity);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(entity.getKey()));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    assertThat(
        jsonServletRequest.getRequiredEntity("id", "foo"),
        equalTo(entity));
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredEntityParameterDoesNotExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(null);

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredEntity("id", "foo");
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredEntityDoesNotExist() throws Exception {
    Key key = KeyFactory.createKey("foo", 12345);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredEntity("id", "foo");
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredEntityInvalidKey() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn("this is not a valid key");

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredEntity("id", "foo");
  }

  @Test(expected = HttpError.class)
  public void testGetRequiredEntityWrongKind() throws Exception {
    Entity entity = new Entity("foo");
    datastore.put(entity);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(entity.getKey()));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    jsonServletRequest.getRequiredEntity("id", "bar");
  }
}
