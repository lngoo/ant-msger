package com.ant.jt808.base.dto.jt808;

import com.ant.jt808.base.annotation.Property;
import com.ant.jt808.base.annotation.Type;
import com.ant.jt808.base.enums.DataType;
import com.ant.jt808.base.message.AbstractBody;
import com.ant.jt808.base.common.MessageId;

@Type({MessageId.平台RSA公钥, MessageId.终端RSA公钥})
public class RSAPack extends AbstractBody {

    private Long e;
    private byte[] n;

    public RSAPack() {
    }

    public RSAPack(Long e, byte[] n) {
        this.e = e;
        this.n = n;
    }

    @Property(index = 0, type = DataType.DWORD, desc = "RSA公钥{e,n}中的e")
    public Long getE() {
        return e;
    }

    public void setE(Long e) {
        this.e = e;
    }

    @Property(index = 4, type = DataType.BYTES, length = 128, desc = "RSA公钥{e,n}中的n")
    public byte[] getN() {
        return n;
    }

    public void setN(byte[] n) {
        this.n = n;
    }
}