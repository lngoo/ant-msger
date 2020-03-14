package com.ant.msger.base.dto.jt808;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.annotation.Type;
import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import org.apache.commons.lang3.StringUtils;

@Type(MessageId.IM消息)
public class IMMsg extends AbstractBody {
    /** 类型，1-topic，0-user  */
    private Integer sendType;

    /** 发送给  */
    protected String sendTo;

    /** 发送方标识  */
    protected String sendUserAlias;

    /** 发送方名字  */
    protected String sendUserName;

    /** 消息内容  */
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

    @Property(index = 21, type = DataType.STRING, length = 30, desc = "发送方用户ID")
    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    @Property(index = 51, type = DataType.STRING, desc = "消息内容")
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}