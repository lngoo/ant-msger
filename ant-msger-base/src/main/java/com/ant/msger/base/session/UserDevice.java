package com.ant.msger.base.session;

import java.util.Date;

public class UserDevice extends SessionObject {
    private String userId;
    private String deviceId;
    private Date expireTime;

    public UserDevice() {
    }

    public UserDevice(String userId, String deviceId, Date expireTime) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.expireTime = expireTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
