package com.antnest.msger.core.dto.jt808;

import com.antnest.msger.core.annotation.Property;
import com.antnest.msger.core.annotation.Type;
import com.antnest.msger.core.enums.DataType;
import com.antnest.msger.core.message.AbstractBody;
import com.antnest.msger.core.common.MessageId;

@Type(MessageId.电话回拨)
public class CallPhone extends AbstractBody {

    /** 普通通话 */
    public static final int Normal = 0;
    /** 监听 */
    public static final int Listen = 1;

    private Integer type;
    private String content;

    @Property(index = 0, type = DataType.BYTE, desc = "标志")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Property(index = 1, type = DataType.STRING, length = 20, desc = "文本信息")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}