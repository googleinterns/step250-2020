package com.google.step.coffee.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/chat-request")
public class ChatRequestServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String[] tags = req.getParameter("tags").split(",");
    String datesString = req.getParameter("dates");
    int numPeople = Integer.parseInt(req.getParameter("numPeople"));
    int duration = Integer.parseInt(req.getParameter("duration"));
    boolean randomMatch = Boolean.parseBoolean(req.getParameter("randomMatch"));
    boolean pastMatched = Boolean.parseBoolean(req.getParameter("pastMatched"));

    if (datesString.equals("")) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    long[] unixDates = Arrays.stream(datesString.split(",")).mapToLong(Long::parseLong).toArray();

    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
