package com.my.henacat.servlet.http;

import java.io.IOException;
import java.io.PrintWriter;

public interface HttpServletResponse {

    static final int SC_OK = 200;
    static final int SC_FOUND = 302;

    /**
     * レスポンスヘッダのContent-Typeヘッダを指定
     *
     * @param contentType
     */
    void setContentType(String contentType);

    void setCharacterEncoding(String charset);

    PrintWriter getWriter() throws IOException;

    void sendRedirect(String location);

    void setStatus(int statusCode);

}
