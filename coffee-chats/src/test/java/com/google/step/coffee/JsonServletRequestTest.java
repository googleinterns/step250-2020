package com.google.step.coffee;

import com.google.appengine.api.datastore.*;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    try {
      jsonServletRequest.getRequiredParameter("goodbye");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
      assertThat(err.getMessage(), equalTo("No value specified for parameter 'goodbye'"));
      throw err;
    }
  }

  @Test
  public void testGetKeyFromParameterExists() throws Exception {
    Key key = KeyFactory.createKey("foo", 12345);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    assertThat(
        jsonServletRequest.getKeyFromParameter("id", "foo"),
        equalTo(key));
  }

  @Test(expected = HttpError.class)
  public void testGetKeyFromParameterDoesNotExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(null);

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    try {
      jsonServletRequest.getKeyFromParameter("id", "foo");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
      assertThat(err.getMessage(), equalTo("No value specified for parameter 'id'"));
      throw err;
    }
  }

  @Test(expected = HttpError.class)
  public void testGetKeyFromParameterWrongKind() throws Exception {
    Key key = KeyFactory.createKey("foo", 12345);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    try {
      jsonServletRequest.getKeyFromParameter("id", "bar");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
      assertThat(err.getMessage(), equalTo("Invalid value for parameter 'id'"));
      throw err;
    }
  }

  @Test(expected = HttpError.class)
  public void testGetKeyFromParameterInvalidKey() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn("this is not a valid key");

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    try {
      jsonServletRequest.getKeyFromParameter("id", "bar");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
      assertThat(err.getMessage(), equalTo("Invalid value for parameter 'id'"));
      throw err;
    }
  }

  @Test
  public void testGetEntityFromParameterExists() throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("foo");
    datastore.put(entity);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(entity.getKey()));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    assertThat(
        jsonServletRequest.getEntityFromParameter("id", "foo"),
        equalTo(entity));
  }

  @Test(expected = HttpError.class)
  public void testGetEntityFromParameterParameterDoesNotExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(null);

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    try {
      jsonServletRequest.getEntityFromParameter("id", "foo");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
      assertThat(err.getMessage(), equalTo("No value specified for parameter 'id'"));
      throw err;
    }
  }

  @Test(expected = HttpError.class)
  public void testGetEntityFromParameterDoesNotExist() throws Exception {
    Key key = KeyFactory.createKey("foo", 12345);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    try {
      jsonServletRequest.getEntityFromParameter("id", "foo");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_NOT_FOUND));
      assertThat(err.getMessage(), equalTo("Requested foo doesn't exist"));
      throw err;
    }
  }

  @Test(expected = HttpError.class)
  public void testGetEntityFromParameterInvalidKey() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn("this is not a valid key");

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    try {
      jsonServletRequest.getEntityFromParameter("id", "foo");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
      assertThat(err.getMessage(), equalTo("Invalid value for parameter 'id'"));
      throw err;
    }
  }

  @Test(expected = HttpError.class)
  public void testGetEntityFromParameterWrongKind() throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("foo");
    datastore.put(entity);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(entity.getKey()));

    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    try {
      jsonServletRequest.getEntityFromParameter("id", "bar");
    } catch (HttpError err) {
      assertThat(err.getErrorCode(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
      assertThat(err.getMessage(), equalTo("Invalid value for parameter 'id'"));
      throw err;
    }
  }
}
