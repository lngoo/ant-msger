package com.antnest.msger.core.message;

/**
 * 发送通道、请求通道消息体
 * 对外的MESSAGE bean
 */
public class MessageExternal<T extends AbstractBody> {
    private String protocolType; // 协议类型，如Jt808、AntIM等
    private Integer cmd; // 消息ID
    private String userAlias; // 用户别名/终端手机号
    private Integer serialNumber; // 消息流水号
    private String msgerId; // antMsger标识符
    private String sessionId; // sessionId
    private String sendTo;  // 发送通道时，指明发送给谁

    private T msgBody; // 消息体

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getMsgerId() {
        return msgerId;
    }

    public void setMsgerId(String msgerId) {
        this.msgerId = msgerId;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public T getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(T msgBody) {
        this.msgBody = msgBody;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
}
