package com.my.henacat.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.my.henacat.servletimpl.WebApplication;

/**
 * リクエストを受け付ける窓口
 *
 * @author kengo
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {

        // サーブレットの登録
        // 本来ならweb.xmlで外部から定義する
        WebApplication app = WebApplication.createInstance("test");
        app.addServlet("/ShowBbs", "bbs.ShowBbs");
        app.addServlet("/PostBbs", "bbs.PostBbs");
        app.addServlet("/CookieTest", "cookie.CookieTest");
        app.addServlet("/SessionTest", "session.SessionTest");

        try (ServerSocket server = new ServerSocket(8001)) {

            for (;;) {
                Socket socket = server.accept();

                ServerThread serverThread = new ServerThread(socket);
                Thread thread = new Thread(serverThread);
                thread.start();
            }
        }
    }

}
