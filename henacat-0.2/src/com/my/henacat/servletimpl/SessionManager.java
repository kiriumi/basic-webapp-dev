package com.my.henacat.servletimpl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    private final ScheduledExecutorService scheduler;

    private final ScheduledFuture<?> cleanerHandle;

    private final int CLEAN_INTERVAL = 60; // seconds;
    private final int SESSION_TIMEOUT = 10; // minutes

    private Map<String, HttpSessionImpl> sessions = new ConcurrentHashMap<String, HttpSessionImpl>();

    private SessionIdGenerator sessionIdGenerator;

    SessionManager() {

        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable cleaner = () -> {
            cleanSessions();
        };

        this.cleanerHandle = scheduler.scheduleWithFixedDelay(
                cleaner, CLEAN_INTERVAL, CLEAN_INTERVAL, TimeUnit.SECONDS);
        this.sessionIdGenerator = new SessionIdGenerator();
    }

    synchronized HttpSessionImpl getSession(String id) {

        HttpSessionImpl ret = sessions.get(id);
        if (ret != null) {
            ret.access();
        }
        return ret;
    }

    HttpSessionImpl createSession() {

        String id = sessionIdGenerator.generateSessionId();
        HttpSessionImpl session = new HttpSessionImpl(id);
        sessions.put(id, session);

        return session;
    }

    private synchronized void cleanSessions() {

        for (Iterator<String> iterator = sessions.keySet().iterator(); iterator.hasNext();) {

            String id = iterator.next();
            HttpSessionImpl session = sessions.get(id);

            if (session.getLastAccessdTime() < (System.currentTimeMillis() - (SESSION_TIMEOUT * 60 * 1000))) {
                iterator.remove();
            }
        }
    }

}
