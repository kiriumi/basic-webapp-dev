package bbs;

import java.io.IOException;

import com.my.henacat.servlet.ServletException;
import com.my.henacat.servlet.http.HttpServlet;
import com.my.henacat.servlet.http.HttpServletRequest;
import com.my.henacat.servlet.http.HttpServletResponse;

/**
 * 掲示板の投稿を受け付けるサーブレット
 *
 * @author kengo
 *
 */
public class PostBbs extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Message newMessage = new Message(
                request.getParameter("title"),
                request.getParameter("handle"),
                request.getParameter("message"));

        Message.messageList.add(0, newMessage);
        response.sendRedirect("/testbbs/ShowBbs");
    }

}
