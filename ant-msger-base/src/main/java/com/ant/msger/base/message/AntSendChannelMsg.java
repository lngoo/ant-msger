package com.ant.msger.base.message;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.enums.SendType;

/**
 * 蚂蚁信使  发送通道消息体
 */
public class AntSendChannelMsg {
    // 发送类型
    private SendType sendType;
    // 类型下的ID，toUser,toDialog时需要
    private String dataIdentifier;
    // 发送消息的消息体，遵从JT808消息格式
    private Message message;

    public AntSendChannelMsg(SendType sendType, String dataIdentifier, Message message) {
        this.sendType = sendType;
        this.dataIdentifier = dataIdentifier;
        this.message = message;
    }

    public SendType getSendType() {
        return sendType;
    }

    public void setSendType(SendType sendType) {
        this.sendType = sendType;
    }

    public String getDataIdentifier() {
        return dataIdentifier;
    }

    public void setDataIdentifier(String dataIdentifier) {
        this.dataIdentifier = dataIdentifier;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
