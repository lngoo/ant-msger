package com.ant.msger.main.mq.callable;

import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.session.UserDevice;

import java.util.concurrent.Callable;

/**
 * 用户设备更新器
 */
public class UserDeviceUpdator implements Callable<Boolean> {

    private NotifyType notifyType;
    private UserDevice userDevice;

    public UserDeviceUpdator(NotifyType notifyType, UserDevice userDevice) {
        this.notifyType = notifyType;
        this.userDevice = userDevice;
    }

    @Override
    public Boolean call() throws Exception {
        // TODO
        return null;
    }
}
