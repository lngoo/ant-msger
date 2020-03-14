package com.ant.msger.base.dto.jt808;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.annotation.Type;
import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;

@Type(MessageId.IM消息)
public class IMMsg extends AbstractBody {
    /**
     * 类型，1-topic，0-user
     */
    private Integer sendType;

    /**
     * 发送给
     */
    protected String sendTo;

    /**
     * 发送方标识
     */
    protected String sendUserAlias;

    /**
     * 发送时间
     */
    protected String sendTime;

    /**
     * IM老接口用的字段
     */
    protected Integer msgType = -1;

    /**
     * IM老接口用的字段
     */
    protected String msgId = "null";

    /**
     * 消息内容
     */
    private String msg;

    @Property(index = 0, type = DataType.BYTE, desc = "发送类型：1-Topic,2-用户")
    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    @Property(index = 1, type = DataType.BCD8421, length = 10, pad = 48, desc = "发送给")
    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    @Property(index = 11, type = DataType.BCD8421, length = 10, pad = 48, desc = "发送方用户ID")
    public String getSendUserAlias() {
        return sendUserAlias;
    }

    public void setSendUserAlias(String sendUserAlias) {
        this.sendUserAlias = sendUserAlias;
    }

    @Property(index = 21, type = DataType.BCD8421, length = 7, pad = 48, desc = "发送时间")
    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    @Property(index = 28, type = DataType.DWORD, desc = "MsgType")
    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    @Property(index = 32, type = DataType.STRING, length = 20, pad = 0, desc = "MsgId")
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Property(index = 52, type = DataType.STRING, desc = "消息内容")
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}