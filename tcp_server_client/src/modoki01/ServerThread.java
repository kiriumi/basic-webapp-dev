package modoki01;

import static java.util.Map.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ServerThread implements Runnable {

    private static final String DOCUMENT_ROOT = "C:\\Apache24\\htdocs";

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try(InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream()) {

            String line;
            String path = null;
            String extension = null;

            while ((line = readLine(input)) != null) {

                if (line.isEmpty()) {
                    break;
                }

                if (line.startsWith("GET")) {
                    path = line.split(" ")[1];
                    if (path.equals("/")) {
                        path = "/index.html";
                    }

                    // pathから拡張子を取得する（後にCotentTypeを取得するため）
                    String[] tmp = path.split("\\.");
                    extension = tmp[tmp.length - 1];
                }
            }

            // レスポンスヘッダを返す
            writeLine(output, "HTTP/1.1 200 OK");
            writeLine(output, "Date: " + getDateStringUtc());
            writeLine(output, "Server: Modoki/0.1");
            writeLine(output, "Connection: close");
            writeLine(output, "Content-type: " + getContentType(extension));
            writeLine(output, "");

            // レスポンスボディを返す
            try (FileInputStream fis = new FileInputStream(DOCUMENT_ROOT + path)) {
                int intChar;
                while ((intChar = fis.read()) != -1) {
                    output.write(intChar);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * InputStreamからのバイト列を、行単位で読み込むユーティリティメソッド
     * @param input
     * @return
     * @throws Exception
     */
    private static String readLine(InputStream input) throws Exception {

        int intChar;
        String ret = "";

        while ((intChar = input.read()) != -1) {
            if (intChar == '\r') {
                // 何もしない
            } else if (intChar == '\n') {
                break;
            } else {
                ret += (char) intChar;
            }
        }

        if (intChar == -1) {
            return null;
        } else {
            return ret;
        }
    }

    /**
     * 1行の文字列を、バイト列としてOutputStreamに書き込むユーティリティメソッド
     * @param output
     * @throws Exception
     */
    private static void writeLine(OutputStream output, String str) throws Exception {

        for (char ch : str.toCharArray()) {
            output.write((int) ch);
        }
        output.write((int) '\r');
        output.write((int) '\n');
    }

    /**
     * 現在時刻から、HTTP標準に合わせてフォーマットした文字列を返す
     * @return
     */
    private static String getDateStringUtc() {
        return ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    private static final Map<String, String> contentTypeMap = Map.ofEntries(
            entry("html", "text/html"),
            entry("htm", "text/html"),
            entry("txt", "text/plain"),
            entry("css", "text/css"),
            entry("png", "image/png"),
            entry("jpg", "image/jpeg"),
            entry("jpeg", "image/jpeg"),
            entry("gif", "image/gif"));

    private static String getContentType(String ext) {
        String contentType = contentTypeMap.get(ext.toLowerCase());
        return (contentType == null) ? "application/octet-stream" : contentType;
    }

}
