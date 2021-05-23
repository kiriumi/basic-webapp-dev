package modoki02;

import static modoki02.Util.*;

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

    static void sendOkResponse(OutputStream output, InputStream fis, String extension) throws Exception {

        writeLine(output, "HTTP/1.1 200 OK");
        writeLine(output, "Date: " + getDateStringUtc());
        writeLine(output, "Server: Modoki/0.2");
        writeLine(output, "Connection: close");
        writeLine(output, "Content-type: " + getContentType(extension));
        writeLine(output, "");

        // レスポンスボディを返す
        int intChar;
        while ((intChar = fis.read()) != -1) {
            output.write(intChar);
        }
    }

    static void sendMovedPermanentlyResponse(OutputStream output, String location) throws Exception {

        writeLine(output, "HTTP/1.1 301 Moved Permanently");
        writeLine(output, "Date: " + getDateStringUtc());
        writeLine(output, "Server: Modoki/0.2");
        writeLine(output, "Location: " + location);
        writeLine(output, "Connection: close");
        writeLine(output, "");
    }

    static void sendNotFoundResponse(OutputStream output, String errorDocumentRoot) throws Exception {

        writeLine(output, "HTTP/1.1 404 Not Found");
        writeLine(output, "Date: " + getDateStringUtc());
        writeLine(output, "Server: Modoki/0.2");
        writeLine(output, "Connection: close");
        writeLine(output, "Content-type: text/html");
        writeLine(output, "");

        try (InputStream fis = new BufferedInputStream(
                new FileInputStream(errorDocumentRoot + "/404.html"))) {

            int intChar;
            while ((intChar = fis.read()) != -1) {
                output.write(intChar);
            }
        }
    }

}
