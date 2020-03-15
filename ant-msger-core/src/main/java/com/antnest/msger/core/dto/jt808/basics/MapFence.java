package com.antnest.msger.core.dto.jt808.basics;

import com.antnest.msger.core.annotation.Property;
import com.antnest.msger.core.enums.DataType;
import com.antnest.msger.core.message.AbstractBody;

public class MapFence extends AbstractBody {

    private Long id;

    public MapFence() {
    }

    public MapFence(Long id) {
        this.id = id;
    }

    @Property(index = 0, type = DataType.DWORD, desc = "区域ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}