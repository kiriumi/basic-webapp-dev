package session;

import java.io.IOException;
import java.io.PrintWriter;

import com.my.henacat.servlet.ServletException;
import com.my.henacat.servlet.http.HttpServlet;
import com.my.henacat.servlet.http.HttpServletRequest;
import com.my.henacat.servlet.http.HttpServletResponse;
import com.my.henacat.servlet.http.HttpSession;



public class SessionTest extends HttpServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);
        Integer counter = (Integer) session.getAttribute("Counter");

        if (counter == null) {
            out.println("No session");
            session.setAttribute("Counter", 1);
        } else {
            out.println("Counter.." + counter);
            session.setAttribute("Counter", counter + 1);
        }
    }

}
