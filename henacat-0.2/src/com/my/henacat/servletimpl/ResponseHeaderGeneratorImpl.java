package com.my.henacat.servletimpl;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.my.henacat.servlet.http.Cookie;
import com.my.henacat.util.ResponseHeaderGenerator;
import com.my.henacat.util.Util;

public class ResponseHeaderGeneratorImpl implements ResponseHeaderGenerator {

    private List<Cookie> cookies;

    public ResponseHeaderGeneratorImpl(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    @Override
    public void generate(OutputStream output) throws IOException {

        for (Cookie cookie : cookies) {

            String header;
            header = "Set-Cookie: " + cookie.getName() + "=" + cookie.getValue();

            if (cookie.getDomain() != null) {
                header += "; Domain=" + cookie.getDomain();
            }

            if (cookie.getMaxAge() > 0) {
                // クッキーの期限が設定されている場合
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.add(Calendar.SECOND, cookie.getMaxAge());
                header += "; Expires=" + getCookieDataString(cal);

            } else if (cookie.getMaxAge() == 0) {
                // 期限なしの場合は、過去の日付を設定し、クッキーを保持しないようにする
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.set(1970, 0, 1, 0, 0, 10);
                header += "; Expires=" + getCookieDataString(cal);
            }

            if (cookie.getPath() != null) {
                header += "; Path=" + cookie.getPath();
            }

            if (cookie.getSecure()) {
                header += "; Secure";
            }

            if (cookie.isHttpOnly()) {
                header += "; HttpOnly";
            }

            Util.writeLine(output, header);
        }

    }

    private static String getCookieDataString(Calendar cal) {

        DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss", Locale.US);
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime()) + "GMT";
    }

}
