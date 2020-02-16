package com.ant.msger.main.framework.session;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SessionManager {

    private static volatile SessionManager instance = null;
    // netty生成的sessionID和Session的对应关系
    private Map<String, Session> sessionIdMap;
    // 终端号（手机号/user_alias）和netty生成的sessionID的对应关系,在im业务里，一个user_alias有多个session
    private Map<String, Set<String>> terminalMap;

    public SessionManager() {
        this.sessionIdMap = new ConcurrentHashMap<>();
        this.terminalMap = new ConcurrentHashMap<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }
//
//    public boolean containsKey(String sessionId) {
//        return sessionIdMap.containsKey(sessionId);
//    }
//
//    public boolean containsSession(Session session) {
//        return sessionIdMap.containsValue(session);
//    }

    public Session getBySessionId(String sessionId) {
        return sessionIdMap.get(sessionId);
    }

    /**
     * 根据 手机号/userAlias查找session集合
     *
     * @param userAlias
     * @return
     */
    public Set<Session> getByUserAlias(String userAlias) {
        Set<String> sessionIds = this.terminalMap.get(userAlias);
        if (sessionIds == null || sessionIds.isEmpty())
            return null;

        Set<Session> setSessions = new HashSet<>();
        for (String sessionId : sessionIds) {
            setSessions.add(this.getBySessionId(sessionId));
        }

        return setSessions;
    }

    public synchronized Session put(String sessionId, Session session) {
        String userAlias = session.getUserAlias();
        if (StringUtils.isNotEmpty(userAlias)) {
            Set<String> setSessionIds = this.terminalMap.get(userAlias);
            if (null == setSessionIds) {
                setSessionIds = new CopyOnWriteArraySet<>();
                this.terminalMap.put(userAlias, setSessionIds);
            }
            setSessionIds.add(sessionId);
        }
        return sessionIdMap.put(sessionId, session);
    }

    public synchronized Session removeBySessionId(String sessionId) {
        if (sessionId == null)
            return null;
        Session session = sessionIdMap.remove(sessionId);
        if (session == null)
            return null;
        if (session.getUserAlias() != null) {
            Set<String> setSessionIds = this.terminalMap.get(session.getUserAlias());
            if (null != setSessionIds) {
                setSessionIds.remove(sessionId);
            }
        }
        return session;
    }

//    public Set<String> keySet() {
//        return sessionIdMap.keySet();
//    }
//
//    public void forEach(BiConsumer<? super String, ? super Session> action) {
//        sessionIdMap.forEach(action);
//    }
//
//    public Set<Entry<String, Session>> entrySet() {
//        return sessionIdMap.entrySet();
//    }
//
//    public List<Session> toList() {
//        return this.sessionIdMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
//    }

}