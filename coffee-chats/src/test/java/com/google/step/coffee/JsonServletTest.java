package com.google.step.coffee;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class JsonServletTest {
  private static class GetTestServlet extends JsonServlet {
    @Override
    public Object get(HttpServletRequest request) throws IOException, HttpError {
      List<String> response = new ArrayList<>();
      response.add("hello");
      response.add("world");
      return response;
    }
  }

  private static class ErrorThrowingServlet extends JsonServlet {
    @Override
    public Object get(HttpServletRequest request) throws IOException, HttpError {
      throw new HttpError(403, "No access for you!");
    }
  }

  @Test
  public void testJsonWorks() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    (new GetTestServlet()).doGet(request, response);

    verify(response).setCharacterEncoding("UTF-8");
    verify(response).setContentType("text/json");
    JSONAssert.assertEquals(
        "[\"hello\", \"world\"]",
        stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testBadMethod() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    (new GetTestServlet()).doPost(request, response);

    verify(response).setCharacterEncoding("UTF-8");
    verify(response).setContentType("text/json");
    verify(response).setStatus(405);
    JSONAssert.assertEquals(
        "{\"errorCode\": 405, \"message\": \"POST method is not allowed for this endpoint\"}",
        stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testCustomErrorCode() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    (new ErrorThrowingServlet()).doGet(request, response);

    verify(response).setCharacterEncoding("UTF-8");
    verify(response).setContentType("text/json");
    verify(response).setStatus(403);
    JSONAssert.assertEquals(
        "{\"errorCode\": 403, \"message\": \"No access for you!\"}",
        stringWriter.toString(), JSONCompareMode.STRICT);
  }
}
