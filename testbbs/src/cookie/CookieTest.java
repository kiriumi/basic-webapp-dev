package cookie;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CookieTest
 */
@WebServlet("/CookieTest")
public class CookieTest extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String counterStr = null;

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            // 初回アクセス時
            out.println("cookies == null");
        } else {

            // Cookieの全項目を出力
            out.println("cookies.length.." + cookies.length);
            for (int i = 0; i < cookies.length; i++) {
                out.println("cookies[" + i + "].."
                        + cookies[i].getName() + "/" + cookies[i].getValue());

                if (cookies[i].getName().equals("COUNTER")) {
                    // COUNTERの値を取得
                    counterStr = cookies[i].getValue();
                }
            }
        }

        // COUNTERの値のカウントアップ／初期値設定
        int counter;
        if (counterStr == null) {
            counter = 1;
        } else {
            counter = Integer.parseInt(counterStr) + 1;
        }

        Cookie newCookie = new Cookie("COUNTER", "" + counter);
        response.addCookie(newCookie);

        Cookie new2Cookie = new Cookie("HOGE2", "" + counter);
        new2Cookie.setPath("/hoge");
        response.addCookie(new2Cookie);

        Cookie new3Cookie = new Cookie("HOGE3", "" + counter);
        new3Cookie.setSecure(true);
        response.addCookie(new3Cookie);
    }

}
