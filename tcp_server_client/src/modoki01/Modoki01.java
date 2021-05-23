package modoki01;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * Webサーバもどき
 * 以下レスポンスヘッダを返す
 * ・Data
 * ・Server
 * ・Connection
 * ・Content-TYpe
 *
 * @author kengo
 *
 */

public class Modoki01 {

    private static final String DOCUMENT_ROOT = "C:\\Apache24\\htdocs";

    public static void main(String[] args) throws Exception {

        try (ServerSocket server = new ServerSocket(8001)) {

            // リクエストを受け付ける状態にする
            Socket socket = server.accept();

            // 受け付けたリクエストの情報を読み取る
            InputStream input = socket.getInputStream();
            String line;
            String path = null;

            while ((line = readLine(input)) != null) {
                if (line == "") {
                    // リクエストボディに到達したら終了
                    break;
                }

                if (line.startsWith("GET")) {
                    // HTTPメソッドで指定されたパスを読取る
                    path = line.split(" ")[1];
                }
            }

            // ---- ここまでがリクエストの取得 ----
            // ---- ここからレスポンスの生成 ----

            // レスポンスヘッダの生成
            OutputStream output = socket.getOutputStream();
            writeLine(output, "HTTP/1.1 200 OK");
            writeLine(output, "Date: " + getDateStringUtc());
            writeLine(output, "Server: Modoki/0.1");
            writeLine(output, "Connection: close");
            writeLine(output, "Content-type: text/html");
            writeLine(output, "");

            // レスポンスボディを返す
            try (FileInputStream fis = new FileInputStream(DOCUMENT_ROOT + path)) {
                int ch;
                while ((ch = fis.read()) != -1) {
                    output.write(ch);
                }
            }

            socket.close();
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
        //        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss");
        return ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

}
