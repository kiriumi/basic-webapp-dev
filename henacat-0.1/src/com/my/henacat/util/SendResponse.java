package com.my.henacat.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * HTTPレスポンスの出力
 *
 * 200だけでなく、301や404に対応
 *
 * @author kengo
 *
 */
public class SendResponse {

    public static void sendOkResponseHeader(OutputStream output, String contentType) throws Exception {

        Util.writeLine(output, "HTTP/1.1 200 OK");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Henacat/0.1");
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "Content-type: " + contentType);
        Util.writeLine(output, "");
    }

    public static void sendOkResponse(OutputStream output, InputStream fis, String extension) throws Exception {

        Util.writeLine(output, "HTTP/1.1 200 OK");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Henacat/0.1");
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "Content-type: " + Util.getContentType(extension));
        Util.writeLine(output, "");

        // レスポンスボディを返す
        int intChar;
        while ((intChar = fis.read()) != -1) {
            output.write(intChar);
        }
    }

    public static void sendMovedPermanentlyResponse(OutputStream output, String location) throws Exception {

        Util.writeLine(output, "HTTP/1.1 301 Moved Permanently");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Henacat/0.1");
        Util.writeLine(output, "Location: " + location);
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "");
    }

    public static void sendFoundResponse(OutputStream output, String location) throws Exception {

        Util.writeLine(output, "HTTP/1.1 302 Found");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Henacat/0.1");
        Util.writeLine(output, "Location: " + location);
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "");
    }

    public static void sendNotFoundResponse(OutputStream output, String errorDocumentRoot) throws Exception {

        Util.writeLine(output, "HTTP/1.1 404 Not Found");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Henacat/0.1");
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "Content-type: text/html");
        Util.writeLine(output, "");

        // 404用のHTMLを返す
        try (InputStream fis = new BufferedInputStream(
                new FileInputStream(errorDocumentRoot + "/404.html"))) {

            int intChar;
            while ((intChar = fis.read()) != -1) {
                output.write(intChar);
            }
        }
    }

}
