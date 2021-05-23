package com.my.henacat.servletimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * サーブレットコンテナ
 * ・コンテキストルートに紐づくサーブレットを保持
 *
 * @author kengo
 *
 */
public class WebApplication {

    private static final String WEBAPPS_DIR = "C:\\Henacat_0_1\\webpps";

    private static Map<String, WebApplication> webAppCollection = new HashMap<>();

    String directory;

    ClassLoader classLoader;

    private Map<String, ServletInfo> servletCollection = new HashMap<>();

    private SessionManager sessionManager;

    private WebApplication(String dir) throws MalformedURLException {

        this.directory = dir;
        FileSystem fs = FileSystems.getDefault();
        Path pathObj = fs.getPath(WEBAPPS_DIR, dir);

        this.classLoader = URLClassLoader.newInstance(new URL[] { pathObj.toUri().toURL() });
    }

    /**
     * 指定のディレクトリ（コンテキストルート）のWebApplicationインスタンスを生成・登録する
     *
     * @param dir ディレクトリ（コンテキストルート）
     * @return
     * @throws MalformedURLException
     */
    public static WebApplication createInstance(String dir) throws MalformedURLException {

        WebApplication newApp = new WebApplication(dir);
        webAppCollection.put(dir, newApp);
        return newApp;
    }

    /**
     * 指定のディレクトリ（コンテキストルート）のWebApplicationを取得する
     *
     * @param dir
     * @return
     */
    public static WebApplication searchWebApplication(String dir) {
        return webAppCollection.get(dir);
    }

    /**
     * 指定のURLパターンのサーブレットを追加する
     *
     * @param urlPattern
     * @param servletClassName
     */
    public void addServlet(String urlPattern, String servletClassName) {

        ServletInfo servletInfo = new ServletInfo(this, urlPattern, servletClassName);
        this.servletCollection.put(urlPattern, servletInfo);
    }

    public ServletInfo searchServlet(String path) {
        return servletCollection.get(path);
    }

    SessionManager getSessionManager() {
        if (sessionManager == null) {
            this.sessionManager = new SessionManager();
        }
        return sessionManager;
    }
}
