package modoki02;

import static modoki02.Util.*;

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

/**
 * Webサーバの心臓部
 *
 * 以下を追加
 * ・NotFoundの処理
 * ・リダイレクト処理
 * ・ディレクトリ・トラバーサル対応
 * ・URLエンコード
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
            String path = null;
            String extension = null;
            String host = null;

            while ((line = readLine(input)) != null) {

                if (line.isEmpty()) {
                    break;
                }

                if (line.startsWith("GET")) {
                    // URLエンコードされた文字をデコードする
                    path = MyUrlDecorder.decode(line.split(" ")[1], "UTF-8");
                    if (path.equals("/")) {
                        // {ホスト名}/index.html の場合、「/index.html」が「/」に変換されてくるため
                        path = "/index.html";
                    }

                    String[] tmp = path.split("\\."); // pathから拡張子を取得する（後にCotentTypeを取得するため）
                    extension = tmp[tmp.length - 1];
                }

                if (line.startsWith("Host:")) {
                    host = line.substring("Host: ".length());
                }
            }

            if (path == null || path.isBlank()) {
                return;
            }

            if (path.endsWith("/")) {
                path += "index.html";
                extension = "html";
            }

            // HTTPレスポンスの作成
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

}
