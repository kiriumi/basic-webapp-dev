package com.my.henacat.servletimpl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import com.my.henacat.servlet.http.HttpServletRequest;
import com.my.henacat.util.MyUrlDecorder;

public class HttpServletRequestImpl implements HttpServletRequest {

    private String method;
    private Map<String, String[]> paramaterMap;
    private String characterEncoding = "ISO-8859-1";

    public HttpServletRequestImpl(String method, Map<String, String[]> paramaterMap) {
        this.method = method;
        this.paramaterMap = paramaterMap;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getParameter(String name) {

        String[] values = getParameterValues(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {

        String[] values = this.paramaterMap.get(name);
        if (values == null) {
            return null;
        }

        String[] decoded = new String[values.length];

        try {
            // パラメータの値をデコードする
            for (int i = 0; i < values.length; i++) {
                decoded[i] = MyUrlDecorder.decode(values[i], this.characterEncoding);
            }

        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }

        return decoded;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        if (!Charset.isSupported(env)) {
            throw new UnsupportedEncodingException("encoding.." + env);
        }
        this.characterEncoding = env;
    }

}
