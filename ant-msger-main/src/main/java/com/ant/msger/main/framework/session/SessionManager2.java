//package com.ant.msger.main.framework.session;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.BiConsumer;
//import java.util.stream.Collectors;
//
//public class SessionManager2 {
//
//    private static volatile SessionManager2 instance = null;
////    // netty生成的sessionID和Session的对应关系
////    private Map<String, Session> sessionIdMap;
//    // 终端号和netty生成的sessionID的对应关系
//    private Map<String, Session> terminalMap;
//
//    public SessionManager2() {
////        this.sessionIdMap = new ConcurrentHashMap<>();
//        this.terminalMap = new ConcurrentHashMap<>();
//    }
//
//    public static SessionManager2 getInstance() {
//        if (instance == null) {
//            synchronized (SessionManager2.class) {
//                if (instance == null) {
//                    instance = new SessionManager2();
//                }
//            }
//        }
//        return instance;
//    }
//
//    public boolean containsKey(String sessionId) {
//        return terminalMap.containsKey(sessionId);
//    }
//
//    public boolean containsSession(Session session) {
//        return terminalMap.containsValue(session);
//    }
//
//    public Session getBySessionId(String sessionId) {
//        return terminalMap.get(sessionId);
//    }
//
//    public Session getByMobileNumber(String mobileNumber) {
////        String sessionId = this.terminalMap.get(mobileNumber);
////        if (sessionId == null)
////            return null;
//        return this.getBySessionId(mobileNumber);
//    }
//
//    public synchronized Session put(String key, Session value) {
////        if (value.getTerminalId() != null && !"".equals(value.getTerminalId().trim())) {
////            this.terminalMap.put(value.getTerminalId(), value.getId());
////        }
//        return terminalMap.put(key, value);
//    }
//
//    public synchronized Session removeBySessionId(String sessionId) {
////        if (sessionId == null)
////            return null;
////        Session session = sessionIdMap.remove(sessionId);
////        if (session == null)
////            return null;
////        if (session.getTerminalId() != null)
//        Session session = terminalMap.remove(sessionId);
////            this.terminalMap.remove(sessionId);
//        return session;
//    }
//
//    // public synchronized void remove(String sessionId) {
//    // if (sessionId == null)
//    // return;
//    // Session session = sessionIdMap.remove(sessionId);
//    // if (session == null)
//    // return;
//    // if (session.getTerminalId() != null)
//    // this.terminalMap.remove(session.getTerminalId());
//    // try {
//    // if (session.getChannel() != null) {
//    // if (session.getChannel().isActive() || session.getChannel().isOpen()) {
//    // session.getChannel().close();
//    // }
//    // session = null;
//    // }
//    // } catch (Exception e) {
//    // e.printStackTrace();
//    // }
//    // }
//
//    public Set<String> keySet() {
//        return terminalMap.keySet();
//    }
//
//    public void forEach(BiConsumer<? super String, ? super Session> action) {
//        terminalMap.forEach(action);
//    }
//
//    public Set<Entry<String, Session>> entrySet() {
//        return terminalMap.entrySet();
//    }
//
//    public List<Session> toList() {
//        return this.terminalMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
//    }
//
//}