package com.my.henacat.webserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.my.henacat.servletimpl.ServletInfo;
import com.my.henacat.servletimpl.ServletService;
import com.my.henacat.servletimpl.WebApplication;
import com.my.henacat.util.MyUrlDecorder;
import com.my.henacat.util.SendResponse;
import com.my.henacat.util.Util;

/**
 * Webサーバの心臓部
 *
 * @author kengo
 *
 */
public class ServerThread implements Runnable {

    private static final String DOCUMENT_ROOT = "C:\\Apache24\\htdocs";
    private static final String ERROR_DOCUMENT = "C:\\webserver\\error_document";
    private static final String SERVER_NAME = "localhost:8001";

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (InputStream input = socket.getInputStream();
                OutputStream output = new BufferedOutputStream(socket.getOutputStream())) {

            // HTTPリクエストの解析
            String line;
            String requestLine = null;
            String method = null;
            Map<String, String> requestHeader = new HashMap<>();

            while ((line = Util.readLine(input)) != null) {

                if (line.isEmpty()) {
                    break;
                }

                if (line.startsWith("GET")) {
                    method = "GET";
                    requestLine = line;

                } else if (line.startsWith("POST")) {
                    method = "POST";
                    requestLine = line;

                } else {
                    addRequestHeader(requestHeader, line);
                }
            }

            if (requestLine == null) {
                return;
            }

            // URLをパスとクエリパラメータに分割
            String reqUri = MyUrlDecorder.decode(requestLine.split(" ")[1], "UTF-8");
            String[] pathAndQuery = reqUri.split("\\?");
            String path = pathAndQuery[0];
            String query = null;
            if (pathAndQuery.length > 1) {
                query = pathAndQuery[1];
            }

            // HTTPレスポンスの作成

            // サーブレットの場合、サーブレットを起動する
            String appDir = path.substring(1).split("/")[0]; // コンテキストルートの取得
            WebApplication webApp = WebApplication.searchWebApplication(appDir);

            if (webApp != null) {
                ServletInfo servletInfo = webApp.searchServlet(path.substring(appDir.length() + 1));

                if (servletInfo != null) {
                    ServletService.doService(method, query, servletInfo, requestHeader, input, output);
                    return;
                }
            }

            // HTTPレスポンスの作成

            // リクエストのURLからファイルの拡張子を取得する（後でCotentTypeを取得するため）
            String extension = null;
            if (reqUri.equals("/")) {
                // {ホスト名}/index.html の場合、「/index.html」が「/」に変換されてくるため
                reqUri = "/index.html";
            }
            String[] tmp = reqUri.split("\\.");
            extension = tmp[tmp.length - 1];

            if (path.endsWith("/")) {
                // URLのパスが/で終わっている場合は、そのディレクトリ内のindex.htmlをレスポンス対象とする
                path += "index.html";
                extension = "html";
            }
            FileSystem fs = FileSystems.getDefault();
            Path pathObj = fs.getPath(DOCUMENT_ROOT, path);
            Path realPath;

            try {
                realPath = pathObj.toRealPath();

            } catch (NoSuchFileException e) {
                // リクエストで指定されたファイルがない場合
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
                return;
            }

            if (!realPath.startsWith(DOCUMENT_ROOT)) {
                // ディレクトリ・トラバーサル対策
                // realPathがDOCUMENT_ROOTより上の階層になっていたら、攻撃されていると判断する
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
                return;
            }

            if (Files.isDirectory(realPath)) {
                // URLの末尾にスラッシュ（/）がない場合の対策
                // リダイレクト先を設定（末尾にスラッシュを付ける）
                String host = requestHeader.get("HOST");
                String location = "http://" + ((host != null) ? host : SERVER_NAME) + path + "/";
                SendResponse.sendMovedPermanentlyResponse(output, location);
                return;
            }

            try (InputStream fis = new BufferedInputStream(Files.newInputStream(realPath))) {
                SendResponse.sendOkResponse(output, fis, extension);

            } catch (FileNotFoundException e) {
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
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
     * リクエストヘッダーをマップに登録する
     *
     * @param requestHeader
     * @param line
     */
    private static void addRequestHeader(Map<String, String> requestHeader, String line) {

        int colonPos = line.indexOf(":");
        if (colonPos == -1) {
            // コロンがない場合、リクエストヘッダーではないので、何もしない
            return;
        }

        String headerName = line.substring(0, colonPos).toUpperCase();
        String headerValue = line.substring(colonPos + 1).trim();
        requestHeader.put(headerName, headerValue);
    }

}
