package download;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GenerateCSV")
public class GenerateCSV extends HttpServlet {

    private static final String zodiacSigns[] = {
            "おひつじ座", "おうし座", "ふたご座", "かに座",
            "しし座", "おとめ座", "てんびん座", "さそり座",
            "いて座", "やぎ座", "みずがめ座", "うお座",
    };
    private static final String fortunes[] = {
            "ラッキー", "ふつう", "最悪"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/csv;charset=Shift_JIS");
        response.setHeader("Content-Disposition", "attachment; filename=\"horoscope.csv\"");
        PrintWriter out = response.getWriter();

        for (int i = 0; i < zodiacSigns.length; i++) {
            out.print("\"" + zodiacSigns[i] + "\",");
            out.print("\"" + fortunes[(int) (Math.random() * fortunes.length)]
                    + "\"\r\n");
        }

    }
}
