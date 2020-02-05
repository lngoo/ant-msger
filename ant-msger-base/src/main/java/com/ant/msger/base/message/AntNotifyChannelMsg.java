package com.ant.msger.base.message;

import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.enums.SubjectType;
import com.ant.msger.base.session.SessionObject;
import com.ant.msger.base.session.UserDevice;

/**
 * 响应通道消息类型
 */
public class AntNotifyChannelMsg {
    private NotifyType notifyType;
    private SubjectType subjectType;
    private SessionObject object;

    public AntNotifyChannelMsg(NotifyType notifyType, SubjectType subjectType, SessionObject object) {
        this.notifyType = notifyType;
        this.subjectType = subjectType;
        this.object = object;
    }

    public NotifyType getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(NotifyType notifyType) {
        this.notifyType = notifyType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public SessionObject getObject() {
        return object;
    }

    public void setObject(SessionObject object) {
        this.object = object;
    }
}
