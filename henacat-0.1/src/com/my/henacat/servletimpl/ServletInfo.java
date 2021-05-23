package com.my.henacat.servletimpl;

import com.my.henacat.servlet.http.HttpServlet;

/**
 * サーブレットおよびWebアプリケーションの情報保持
 *
 * @author kengo
 *
 */
public class ServletInfo {

    WebApplication webApp;
    String urlPattern;
    String servletClassName;
    HttpServlet servlet;

    public ServletInfo(WebApplication webApp, String urlPattern, String servletClassName) {

        this.webApp = webApp;
        this.urlPattern = urlPattern;
        this.servletClassName = servletClassName;
    }

}
