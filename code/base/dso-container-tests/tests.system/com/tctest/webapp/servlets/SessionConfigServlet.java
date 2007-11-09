/*
 * All content copyright (c) 2003-2007 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tctest.webapp.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class SessionConfigServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(true);
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    
    String testCase = request.getParameter("testcase");
    if ("testCookieDisabled".equals(testCase)) {
      // cookie enable set to false so we should get no cookies
      // on second hit (hit=1)
      String hit = request.getParameter("hit");
      if ("1".equals(hit) && request.getCookies() != null) {
        out.println("Still receive cookie when CookiesEnabled=false");
      }
    } else if ("testUrlRewritingDisabled".equals(testCase)) {
      String originalUrl = request.getRequestURI() + "/index.html";
      String encodedUrl = response.encodeURL(originalUrl);
      if (!originalUrl.equals(encodedUrl)) {
        out.println("encodeUrl succeeded: " + encodedUrl);
      }
    } else if ("testTrackingDisabled".equals(testCase)) {
      String hit = request.getParameter("hit");
      if ("1".equals(hit) && request.getCookies() != null) {
        out.println("Still receive cookie when TrackingDisabled=false");
      }
      String originalUrl = request.getRequestURI() + "/index.html";
      String encodedUrl = response.encodeURL(originalUrl);
      if (!originalUrl.equals(encodedUrl)) {
        out.println("encodeUrl succeeded: " + encodedUrl);
      }
    } else if ("testSessionTimeOut".equals(testCase)) {
      out.println(session.getMaxInactiveInterval());
    }
    out.println("OK");
  }
}