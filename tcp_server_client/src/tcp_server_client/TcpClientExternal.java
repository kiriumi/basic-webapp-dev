package tcp_server_client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClientExternal {

    private static String clientDir = "C:/pleiadeses/pleiades-2020-06/workspace/tcp_server_client/file/client/";

    private static String clientSendFilename = "client_send_kmaebashi.txt";

    public static void main(String[] args) throws Exception {

        try (Socket socket = new Socket("kmaebashi.com", 80);
                FileInputStream fis = new FileInputStream(clientDir + clientSendFilename);
                FileOutputStream fos = new FileOutputStream(clientDir + "client_recv.txt")) {

            // 読み込んだファイルの内容をサーバに送信
            int reqChar;
            OutputStream output = socket.getOutputStream();
            while ((reqChar = fis.read()) != -1) {
                output.write(reqChar);
            }

            //            // 終了を示すため、ゼロを送信
            //            output.write(0);

            // サーバからの返信をcliet_recv.txtに出力
            int resChar;
            InputStream input = socket.getInputStream();
            while ((resChar = input.read()) != -1) {
                fos.write(resChar);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
