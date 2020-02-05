package com.ant.msger.main.mq.callable;

import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.session.UserDevice;
import org.apache.commons.lang3.StringUtils;

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
        // 参数检验
        if (!checkParas()) {
            return false;
        }

        switch (notifyType) {
            case ADD:
            case UPDATE:
                break;
            case DELETE:
                break;
        }

        return true;
    }

    private boolean checkParas() {
        if (null == userDevice
                || StringUtils.isEmpty(userDevice.getDeviceId())
                || StringUtils.isEmpty(userDevice.getUserId())) {
            return false;
        } else {
            return true;
        }
    }
}
