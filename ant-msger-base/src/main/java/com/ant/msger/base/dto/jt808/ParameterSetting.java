package com.ant.msger.base.dto.jt808;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.annotation.Type;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.dto.jt808.basics.TerminalParameter;

import java.util.List;

@Type(MessageId.查询指定终端参数)
public class ParameterSetting extends AbstractBody {

    private Integer total;
    private List<TerminalParameter> parameters;

    @Property(index = 0, type = DataType.BYTE, desc = "参数总数")
    public Integer getTotal() {
        if (parameters == null || parameters.isEmpty())
            return 0;
        return parameters.size();
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Property(index = 1, type = DataType.LIST, desc = "参数项列表")
    public List<TerminalParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<TerminalParameter> parameters) {
        this.parameters = parameters;
    }
}