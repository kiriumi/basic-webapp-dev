package tcp_server_client;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServerPost {

    private static String serverDir = "C:/pleiadeses/pleiades-2020-06/workspace/tcp_server_client/file/server/";

    public static void main(String[] args) throws Exception {

        try (ServerSocket server = new ServerSocket(8001);
                FileOutputStream fos = new FileOutputStream(serverDir + "server_recv.txt")) {

            System.out.println("クライアントからの接続を待ちます。");
            Socket socket = server.accept();
            System.out.println("クライアント接続。");

            // クライアントから受け取った内容をserver_recv.txtに出力
            int reqChar;
            InputStream input = socket.getInputStream();
            while ((reqChar = input.read()) != -1) {
                fos.write(reqChar);
            }
            socket.close();

            System.out.println("通信を終了しました");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
