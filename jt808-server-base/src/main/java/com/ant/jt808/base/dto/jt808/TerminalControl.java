package com.ant.jt808.base.dto.jt808;

import com.ant.jt808.base.annotation.Property;
import com.ant.jt808.base.annotation.Type;
import com.ant.jt808.base.enums.DataType;
import com.ant.jt808.base.message.AbstractBody;
import com.ant.jt808.base.common.MessageId;

@Type(MessageId.终端控制)
public class TerminalControl extends AbstractBody {

    private Integer command;
    private String parameter;

    @Property(index = 0, type = DataType.BYTE, desc = "命令字")
    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    @Property(index = 1, type = DataType.STRING, desc = "命令参数")
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}