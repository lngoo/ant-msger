package com.ant.msger.base.message;

import com.ant.msger.base.enums.OperateType;
import com.ant.msger.base.enums.SubjectType;
import com.ant.msger.base.dto.persistence.PersistenceObject;

import java.util.List;

/**
 * 任务通道消息类型
 */
public class MsgerTaskMsg {
    private OperateType operateType;
    private SubjectType subjectType;
    private List<PersistenceObject> list;

    public MsgerTaskMsg(OperateType operateType, SubjectType subjectType, List<PersistenceObject> list) {
        this.operateType = operateType;
        this.subjectType = subjectType;
        this.list = list;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public List<PersistenceObject> getList() {
        return list;
    }

    public void setList(List<PersistenceObject> list) {
        this.list = list;
    }
}
