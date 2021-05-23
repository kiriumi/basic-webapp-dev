package com.my.henacat.servletimpl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import com.my.henacat.servlet.http.Cookie;
import com.my.henacat.servlet.http.HttpServletRequest;
import com.my.henacat.servlet.http.HttpSession;
import com.my.henacat.util.MyUrlDecorder;

public class HttpServletRequestImpl implements HttpServletRequest {

    private String method;

    private Map<String, String[]> paramaterMap;

    private String characterEncoding = "ISO-8859-1";

    private Cookie[] cookies;

    private HttpSessionImpl session;

    private HttpServletResponseImpl response;

    private WebApplication webApp;

    private final String SESSION_COOKIE_ID = "JSESSIONID";

    public HttpServletRequestImpl(
            String method, Map<String, String> requestHeader, Map<String, String[]> paramaterMap,
            HttpServletResponseImpl resp, WebApplication webApp) {

        this.method = method;
        this.paramaterMap = paramaterMap;
        this.cookies = parseCookies(requestHeader.get("COOKIE"));
        this.response = resp;
        this.webApp = webApp;
        this.session = getSessionInternal();

        if (session != null) {
            addSessionCookie();
        }
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

    @Override
    public Cookie[] getCookies() {
        return this.cookies;
    }

    /**
     * リクエストヘッダーからクッキー一覧を作成
     *
     * @param cookieString
     * @return
     */
    private static Cookie[] parseCookies(String cookieString) {

        if (cookieString == null) {
            return null;
        }

        String[] cookiePairArray = cookieString.split(";"); // 項目ごとに分割
        Cookie[] ret = new Cookie[cookiePairArray.length];
        int cookieCount = 0;

        for (String cookiePair : cookiePairArray) {
            // 項目名と項目値を分割し、Cookieインスタンスを生成
            String[] pair = cookiePair.split("=", 2);
            ret[cookieCount] = new Cookie(pair[0], pair[1]);
            cookieCount++;
        }

        return ret;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {

        if (!create) {
            return session;
        }

        if (session == null) {
            SessionManager manager = this.webApp.getSessionManager();
            this.session = manager.createSession();
            addSessionCookie();
        }

        return session;
    }

    /**
     * CookieからセッションIDを取り出し、そのIDに紐づくセッションを取得する
     *
     * @return
     */
    private HttpSessionImpl getSessionInternal() {

        if (cookies == null) {
            return null;
        }

        Cookie sessionIdCookie = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(SESSION_COOKIE_ID)) {
                sessionIdCookie = cookie;
            }
        }

        SessionManager manager = webApp.getSessionManager();
        HttpSessionImpl ret = null;
        if (sessionIdCookie != null) {
            ret = manager.getSession(sessionIdCookie.getValue());
        }
        return ret;
    }

    private void addSessionCookie() {

        Cookie cookie = new Cookie(SESSION_COOKIE_ID, session.getId());
        cookie.setPath("/" + webApp.directory + "/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

    }

}
