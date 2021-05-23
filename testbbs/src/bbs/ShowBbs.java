package bbs;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 掲示板の表示を行うサーブレット
 *
 * @author kengo
 *
 */
public class ShowBbs extends HttpServlet {

    /**
     * HTMLで意味をもつ文字をエスケープ
     *
     * @param src
     * @return
     */
    private String escapeHtml(String src) {

        String escaped = src.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");

        return escaped;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>テスト掲示板</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>テスト掲示板</ht>");

        out.println("<form action='/testbbs/PostBbs' method='post'>");
        out.println("タイトル：<input type='text' name='title' size='60'> <br/>");
        out.println("ハンドル名：<input type='text' name='handle'> <br/>");
        out.println("<textarea name='message' rows='4' cols='60'></textarea> <br/>");
        out.println("<input type='submit' />");
        out.println("</form>");

        out.println("<hr/>");

        for (Message message : Message.messageList) {
            out.println("<p>『" + escapeHtml(message.title) + "』&nbsp;&nbsp;"
                    + escapeHtml(message.handle) + "さん&nbsp;&nbsp;"
                    + escapeHtml(message.date.toString() + "</p>"));

            out.println("<p>");
            out.println(escapeHtml(message.message).replace("\r\n", "<br/>"));
            out.println("</p>");

            out.println("<hr/>");
        }

        out.println("</body>");
        out.println("</html>");
    }

}
