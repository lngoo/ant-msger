package com.antnest.msger.core.dto.jt808;

import com.antnest.msger.core.annotation.Property;
import com.antnest.msger.core.annotation.Type;
import com.antnest.msger.core.enums.DataType;
import com.antnest.msger.core.message.AbstractBody;
import com.antnest.msger.core.common.MessageId;

@Type(MessageId.临时位置跟踪控制)
public class TemporaryMonitor extends AbstractBody {

    private Integer interval;
    private Integer validityPeriod;

    @Property(index = 0, type = DataType.WORD, desc = "时间间隔（秒）")
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    @Property(index = 2, type = DataType.DWORD, desc = "有效期（秒）")
    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
}