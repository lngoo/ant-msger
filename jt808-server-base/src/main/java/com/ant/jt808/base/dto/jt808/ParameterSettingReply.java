package com.ant.jt808.base.dto.jt808;

import com.ant.jt808.base.annotation.Property;
import com.ant.jt808.base.annotation.Type;
import com.ant.jt808.base.enums.DataType;
import com.ant.jt808.base.message.AbstractBody;
import com.ant.jt808.base.common.MessageId;
import com.ant.jt808.base.dto.jt808.basics.TerminalParameter;

import java.util.List;

@Type(MessageId.查询终端参数应答)
public class ParameterSettingReply extends AbstractBody {

    private Integer serialNumber;
    private Integer total;

    private List<TerminalParameter> terminalParameters;

    @Property(index = 0, type = DataType.WORD, desc = "应答流水号")
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Property(index = 2, type = DataType.BYTE, desc = "应答参数个数")
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Property(index = 3, type = DataType.LIST, desc = "参数项列表")
    public List<TerminalParameter> getTerminalParameters() {
        return terminalParameters;
    }

    public void setTerminalParameters(List<TerminalParameter> terminalParameters) {
        this.terminalParameters = terminalParameters;
    }
}