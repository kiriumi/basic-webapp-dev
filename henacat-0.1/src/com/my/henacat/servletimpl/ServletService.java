package com.my.henacat.servletimpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.my.henacat.servlet.http.HttpServlet;
import com.my.henacat.servlet.http.HttpServletRequest;
import com.my.henacat.servlet.http.HttpServletResponse;
import com.my.henacat.util.Constants;
import com.my.henacat.util.SendResponse;

/**
 * APサーバ
 *
 * ・サーブレットを生成
 * ・HTTPリクエストに応じたHttpServletRequestを生成
 * ・サーブレットの処理を実施
 * ・HTTPレスポンスを返す
 *
 * @author kengo
 *
 */
public class ServletService {

    public static void doService(String method, String query, ServletInfo servletInfo,
            Map<String, String> requestHeader, InputStream input, OutputStream output) throws Exception {

        if (servletInfo.servlet == null) {
            // 初めて使用するサーブレットの場合、そのインスタンスを生成する
            servletInfo.servlet = createServlet(servletInfo);
        }

        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream(); // サーブレットが出力するレスポンス（ボディ）を、いったん全てバッファリングする（ヘッダ→ボディの順で出力する必要があるため）
        HttpServletResponseImpl resp = new HttpServletResponseImpl(outputBuffer);

        // HTTPリクエスト（GET／POST）に応じたHttpServletRequestのインスタンスを生成
        HttpServletRequest req;
        if (method.equals("GET")) {
            Map<String, String[]> reqParamsMap;
            reqParamsMap = stringToMapOfRequestParamaters(query);
            req = new HttpServletRequestImpl("GET", reqParamsMap);

        } else if (method.equals("POST")) {

            int contentLength = Integer.parseInt(requestHeader.get("CONTENT-LENGTH"));
            Map<String, String[]> reqParamsMap;
            String line = readToSize(input, contentLength);
            reqParamsMap = stringToMapOfRequestParamaters(line);
            req = new HttpServletRequestImpl("POST", reqParamsMap);

        } else {
            throw new AssertionError("BAD METHOD:" + method);
        }

        // サーブレットの処理を実施（レスポンスボディとHTTPステータスなどを設定）
        servletInfo.servlet.service(req, resp);

        // HTTPレスポンスを返す
        if (resp.status == HttpServletResponse.SC_OK) {
            SendResponse.sendOkResponseHeader(output, resp.contentType); // レスポンスヘッダをまずクライアントに返す

            // サーブレットの処理結果（主にレスポンスボディ）をクライアントに返す
            resp.printWriter.flush(); // サーブレットの処理結果（主にレスポンスボディ）を確定する（メモリ上に出力する）
            byte[] outputBytes = outputBuffer.toByteArray();
            for (byte b : outputBytes) {
                output.write(b);
            }

        } else if (resp.status == HttpServletResponse.SC_FOUND) {

            // TODO これなんだろね？
            String redirectLocation;

            if (resp.redirectLocation.startsWith("/")) {
                // スラッシュ（/）始まりの場合、自Webアプリケーションなので、リダイレクト先に自ホストの情報を付け足す
                String host = requestHeader.get("HOST");
                redirectLocation = "http://"
                        + ((host != null) ? host : Constants.SERVER_NAME)
                        + resp.redirectLocation;

            } else {
                redirectLocation = resp.redirectLocation;
            }

            SendResponse.sendFoundResponse(output, redirectLocation);
        }
    }

    /**
     * サーブレットのインスタンスを生成する
     *
     * @param servletInfo
     * @return
     * @throws Exception
     */
    private static HttpServlet createServlet(ServletInfo servletInfo)
            throws Exception {

        Class<?> clazz = servletInfo.webApp.classLoader.loadClass(servletInfo.servletClassName);
        return (HttpServlet) clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * リクエストパラメータをマップに変換する
     */
    private static Map<String, String[]> stringToMapOfRequestParamaters(String str) {

        Map<String, String[]> paramaterMap = new HashMap<>();

        if (str != null) {

            String[] paramArray = str.split("&");
            for (String param : paramArray) {

                String[] keyValue = param.split("=");
                if (paramaterMap.containsKey(keyValue[0])) {
                    // チェックボックスのように、同一キーで複数の値が来たときの対応
                    String[] array = paramaterMap.get(keyValue[0]);
                    String[] newArray = new String[array.length + 1];
                    System.arraycopy(array, 0, newArray, 0, array.length);
                    newArray[array.length] = keyValue[1];
                    paramaterMap.put(keyValue[0], newArray);

                } else {

                    if (keyValue.length < 2) {
                        paramaterMap.put(keyValue[0], new String[] { "" });
                    } else {
                        paramaterMap.put(keyValue[0], new String[] { keyValue[1] });
                    }
                }
            }
        }
        return paramaterMap;
    }

    /**
     * POSTのメッセージボディにあるリクエストパラメータを取得する
     *
     * @param input
     * @param size
     * @return
     * @throws IOException
     */
    private static String readToSize(InputStream input, int size) throws IOException {

        int intChar;
        StringBuilder sb = new StringBuilder();
        int readSize = 0;

        while (readSize < size && (intChar = input.read()) != -1) {
            sb.append((char) intChar);
            readSize++;
        }
        return sb.toString();
    }

}
