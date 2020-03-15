package com.antnest.msger.core.dto.jt808;

import com.antnest.msger.core.annotation.Property;
import com.antnest.msger.core.annotation.Type;
import com.antnest.msger.core.enums.DataType;
import com.antnest.msger.core.message.AbstractBody;
import com.antnest.msger.core.common.MessageId;

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