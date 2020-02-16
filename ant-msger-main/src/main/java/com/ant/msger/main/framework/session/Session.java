package com.ant.msger.main.framework.session;

import com.ant.msger.main.framework.commons.enumeration.ProtocolCommunication;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Session {

    private ProtocolCommunication protocolCommunication;
    private String id;
    private String userAlias;  // 手机号(JT808)或用户名(IM)
    private InetSocketAddress socketAddress = null;
    private Channel channel = null;
    private boolean isAuthenticated = false;
    // 消息流水号 word(16) 按发送顺序从 0 开始循环累加
    private int currentFlowId = 0;
    // 客户端上次的连接时间，该值改变的情况:
    // 1. terminal --> server 心跳包
    // 2. terminal --> server 数据包
    private long lastCommunicateTimeStamp = 0l;

    public Session() {
    }

    public static String buildId(Channel channel) {
        return channel.id().asLongText();
    }

    public static String buildId(InetSocketAddress socketAddress) {
        return socketAddress.getAddress() + ":" + socketAddress.getPort();
    }

    public static Session buildSession(Channel channel) {
        return buildSession(channel, null);
    }

    public static Session buildSession(Channel channel, String terminalId) {
        Session session = new Session();
        session.setChannel(channel);
        session.setId(buildId(channel));
        session.setUserAlias(terminalId);
        session.setLastCommunicateTimeStamp(System.currentTimeMillis());
        return session;
    }

    public ProtocolCommunication getProtocolCommunication() {
        return protocolCommunication;
    }

    public void setProtocolCommunication(ProtocolCommunication protocolCommunication) {
        this.protocolCommunication = protocolCommunication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public long getLastCommunicateTimeStamp() {
        return lastCommunicateTimeStamp;
    }

    public void setLastCommunicateTimeStamp(long lastCommunicateTimeStamp) {
        this.lastCommunicateTimeStamp = lastCommunicateTimeStamp;
    }

    public SocketAddress getRemoteAddr() {
        return this.channel.remoteAddress();
    }

    public synchronized int currentFlowId() {
        if (currentFlowId >= 0xffff)
            currentFlowId = 0;
        return currentFlowId++;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (getClass() != that.getClass())
            return false;
        Session other = (Session) that;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Session [identity=" + id + ", userAlias=" + userAlias + ", channel=" + channel + "]";
    }
}