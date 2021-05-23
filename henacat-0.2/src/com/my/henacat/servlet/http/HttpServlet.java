package com.my.henacat.servlet.http;

import java.io.IOException;

import com.my.henacat.servlet.ServletException;

public class HttpServlet {

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("GET")) {
            doGet(req, resp);
        } else if (req.getMethod().equals("POST")) {
            doPost(req, resp);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

}
