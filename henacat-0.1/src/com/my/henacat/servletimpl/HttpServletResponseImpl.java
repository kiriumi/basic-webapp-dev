package com.my.henacat.servletimpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.my.henacat.servlet.http.HttpServletResponse;

public class HttpServletResponseImpl implements HttpServletResponse {

    public String contentType = "application/octet-stream";
    public PrintWriter printWriter;
    public String redirectLocation;

    int status;

    private String characterEncoding = "ISO-8859-1";
    private OutputStream outputStream;

    public HttpServletResponseImpl(OutputStream output) {
        this.outputStream = output;
        this.status = SC_OK;
    }

    @Override
    public void setContentType(String contentType) {

        this.contentType = contentType;

        // 「text/html;charset=UTF-8」という指定をされた場合、それを解析してエンコーディングを取り出す
        String[] temp = contentType.split(" *; *");
        if (temp.length > 1) {
            String[] keyValue = temp[1].split("=");
            if (keyValue.length == 2 && keyValue[0].equals("charset")) {
                setCharacterEncoding(keyValue[1]);
            }
        }
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        this.printWriter = new PrintWriter(new OutputStreamWriter(outputStream, this.characterEncoding));
        return printWriter;
    }

    @Override
    public void sendRedirect(String location) {
        this.redirectLocation = location;
        setStatus(SC_FOUND);
    }

    @Override
    public void setStatus(int statusCode) {
        this.status = statusCode;
    }

}
