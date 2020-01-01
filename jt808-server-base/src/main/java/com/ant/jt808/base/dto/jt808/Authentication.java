package com.ant.jt808.base.dto.jt808;

import com.ant.jt808.base.annotation.Property;
import com.ant.jt808.base.annotation.Type;
import com.ant.jt808.base.enums.DataType;
import com.ant.jt808.base.message.AbstractBody;
import com.ant.jt808.base.common.MessageId;

@Type(MessageId.终端鉴权)
public class Authentication extends AbstractBody {

    /** 终端重连后上报鉴权码 */
    private String token;

    @Property(index = 0, type = DataType.STRING, desc = "鉴权码")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}