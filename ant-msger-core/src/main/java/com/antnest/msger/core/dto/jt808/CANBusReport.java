package com.antnest.msger.core.dto.jt808;

import com.antnest.msger.core.annotation.Property;
import com.antnest.msger.core.annotation.Type;
import com.antnest.msger.core.enums.DataType;
import com.antnest.msger.core.message.AbstractBody;
import com.antnest.msger.core.common.MessageId;

import java.util.List;

@Type(MessageId.CAN总线数据上传)
public class CANBusReport extends AbstractBody {

    private Integer total;
    private String dateTime;
    private List<Item> list;

    @Property(index = 0, type = DataType.WORD, desc = "数据项个数")
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Property(index = 2, type = DataType.BCD8421, length = 5, desc = "CAN 总线数据接收时间")
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Property(index = 7, type = DataType.BCD8421, length = 5, desc = "CAN 总线数据项")
    public List<Item> getList() {
        return list;
    }

    public void setList(List<Item> list) {
        this.list = list;
    }

    public static class Item {
        private byte[] id;
        private byte[] data;

        @Property(index = 0, type = DataType.BYTES, length = 4, desc = "CAN ID")
        public byte[] getId() {
            return id;
        }

        public void setId(byte[] id) {
            this.id = id;
        }

        @Property(index = 4, type = DataType.BYTES, length = 8, desc = "CAN DATA")
        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
}