package com.ant.msger.base.dto.jt808;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.annotation.Type;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.common.MessageId;

@Type(MessageId.多媒体数据上传应答)
public class MediaDataReportReply extends AbstractBody {

    private Integer mediaId;
    private Integer packageTotal;
    private byte[] idList;

    public MediaDataReportReply() {
    }

    /** >0，如收到全部数据包则没有后续字段 */
    @Property(index = 0, type = DataType.DWORD, desc = "多媒体ID")
    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    @Property(index = 4, type = DataType.BYTE, desc = "重传包总数")
    public Integer getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(Integer packageTotal) {
        this.packageTotal = packageTotal;
    }

    @Property(index = 5, type = DataType.BYTES, desc = "重传包ID列表")
    public byte[] getIdList() {
        return idList;
    }

    public void setIdList(byte[] idList) {
        this.idList = idList;
    }
}