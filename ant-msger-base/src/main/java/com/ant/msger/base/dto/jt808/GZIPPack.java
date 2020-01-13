package com.ant.msger.base.dto.jt808;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.annotation.Type;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.common.MessageId;

@Type(MessageId.数据压缩上报)
public class GZIPPack extends AbstractBody {

    private Long length;
    private byte[] body;

    public GZIPPack() {
    }

    @Property(index = 0, type = DataType.DWORD, desc = "压缩消息长度")
    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    @Property(index = 4, type = DataType.BYTES, desc = "压缩消息体")
    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}