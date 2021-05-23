package com.my.henacat.servletimpl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.my.henacat.servlet.http.HttpSession;

public class HttpSessionImpl implements HttpSession {

    private String id;

    /*
     * ConcurrentHashMap：スレッドセーフなハッシュマップ
     * ※HashMapはスレッドアンセーフ
     */
    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    /*
     * volatile：スレッドにフィールド値のキャッシュを抑制する
     * ※マルチスレッドの際、スレッドによってキャッシュしているフィールド値が異なる、という状況を防ぐ
     */
    private volatile long lastAccessedTime;

    public HttpSessionImpl(String id) {
        this.id = id;
        this.access();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {

        // なぜこんなことをしているのか？
        Set<String> names = new HashSet<String>();
        names.addAll(attributes.keySet());

        return Collections.enumeration(names);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        // TODO 自動生成されたメソッド・スタブ

        if (value == null) {
            removeAttribute(name);
            return;
        }
        attributes.put(name, value);
    }

    /**
     * セッションに最期にアクセスした時刻を更新する
     * ※複数のスレッドが並列に時刻を更新させないようにするためsyncronizedにしている
     */
    synchronized void access() {
        lastAccessedTime = System.currentTimeMillis();
    }

    long getLastAccessdTime() {
        return lastAccessedTime;
    }

}
