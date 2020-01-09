package com.ant.msger.base.dto.jt808;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.annotation.Type;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.common.MessageId;

@Type(MessageId.信息点播_取消)
public class MessageSubOperate extends AbstractBody {

    private Integer type;
    private Integer action;

    @Property(index = 0, type = DataType.BYTE, desc = "消息类型")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Property(index = 1, type = DataType.BYTE, desc = "点播/取消标志 0：取消；1：点播")
    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }
}