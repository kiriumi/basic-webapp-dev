package modoki02;

import static java.util.Map.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Util {

    Util() {
    }

    /**
     * InputStreamからのバイト列を、行単位で読み込むユーティリティメソッド
     * @param input
     * @return
     * @throws Exception
     */
    static String readLine(InputStream input) throws Exception {

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
    static void writeLine(OutputStream output, String str) throws Exception {

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
    static String getDateStringUtc() {
        return ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    /**
     * 拡張子とContent-Typeの対応表
     */
    private static final Map<String, String> contentTypeMap = Map.ofEntries(
            entry("html", "text/html"),
            entry("htm", "text/html"),
            entry("txt", "text/plain"),
            entry("css", "text/css"),
            entry("png", "image/png"),
            entry("jpg", "image/jpeg"),
            entry("jpeg", "image/jpeg"),
            entry("gif", "image/gif"));

    /**
     * 拡張子に対応するContent-Typeを返す
     * @param ext
     * @return
     */
    static String getContentType(String ext) {
        String contentType = contentTypeMap.get(ext.toLowerCase());
        return (contentType == null) ? "application/octet-stream" : contentType;
    }

}
