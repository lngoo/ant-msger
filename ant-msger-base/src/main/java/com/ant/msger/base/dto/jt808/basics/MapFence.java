package com.ant.msger.base.dto.jt808.basics;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;

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