package tcp_server_client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    private static String serverDir = "C:/pleiadeses/pleiades-2020-06/workspace/tcp_server_client/file/server/";

    public static void main(String[] args) throws Exception {

        try (ServerSocket server = new ServerSocket(8001);
                FileOutputStream fos = new FileOutputStream(serverDir + "server_recv.txt");
                FileInputStream fis = new FileInputStream(serverDir + "server_send.txt")) {

            System.out.println("クライアントからの接続を待ちます。");
            Socket socket = server.accept();
            System.out.println("クライアント接続。");

            // クライアントから受け取った内容をserver_recv.txtに出力
            int reqChar;
            InputStream input = socket.getInputStream();
            while ((reqChar = input.read()) != 0) {
                // クライアントは、終了のマークとして0を送付してくる
                fos.write(reqChar);
            }

            // server_send.txt の内容をクライアントに送付
            int resChar;
            OutputStream output = socket.getOutputStream();
            while((resChar = fis.read()) != -1) {
                output.write(resChar);
            }

            socket.close();

            System.out.println("通信を終了しました");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
