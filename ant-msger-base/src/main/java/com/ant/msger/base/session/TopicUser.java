package com.ant.msger.base.session;

import java.util.Date;

public class TopicUser extends SessionObject{
    private String dialogId;
    private String userId;
    private Date expireTime;

    public TopicUser(String dialogId, String userId, Date expireTime) {
        this.dialogId = dialogId;
        this.userId = userId;
        this.expireTime = expireTime;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
