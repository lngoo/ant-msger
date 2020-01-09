package com.ant.msger.base.dto.jt808;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.annotation.Type;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.common.MessageId;

@Type(MessageId.摄像头立即拍摄命令应答)
public class CameraShotReply extends AbstractBody {

    private Integer serialNumber;
    private Integer result;
    private Integer total;
    private byte[] idList;

    public CameraShotReply() {
    }

    @Property(index = 0, type = DataType.WORD, desc = "应答流水号")
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Property(index = 2, type = DataType.BYTE, desc = "结果")
    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    @Property(index = 2, type = DataType.WORD, desc = "多媒体ID个数")
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Property(index = 4, type = DataType.BYTES, desc = "多媒体ID列表")
    public byte[] getIdList() {
        return idList;
    }

    public void setIdList(byte[] idList) {
        this.idList = idList;
    }
}