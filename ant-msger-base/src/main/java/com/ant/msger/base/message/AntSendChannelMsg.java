package com.ant.msger.base.message;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.enums.TopicType;

/**
 * 蚂蚁信使  发送通道消息体
 */
public class AntSendChannelMsg {
    // topic类型
    private TopicType topicType;
    // 类型下的ID，toUser,toDialog时需要
    private String dataIdentifier;
    // 发送消息的消息体，遵从JT808消息格式
    private Message message;

    public AntSendChannelMsg(TopicType topicType, String dataIdentifier, Message message) {
        this.topicType = topicType;
        this.dataIdentifier = dataIdentifier;
        this.message = message;
    }

    public TopicType getTopicType() {
        return topicType;
    }

    public void setTopicType(TopicType topicType) {
        this.topicType = topicType;
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
