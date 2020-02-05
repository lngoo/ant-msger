package com.ant.msger.main.framework.session;

import com.ant.msger.base.session.UserDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户设备缓存
 */
public class UserManager {
    private Map<String, Map<String, UserDevice>> map = new HashMap<>();

    public Map<String, UserDevice> getUserAllDevices(String userId) {
        return map.get(userId);
    }

    public UserDevice getUserOneDevice(String userId, String deviceId) {
        Map<String, UserDevice> mapDevices = map.get(userId);
        if (null == mapDevices) {
            return null;
        } else {
            return mapDevices.get(deviceId);
        }
    }

    public void setMap(Map<String, Map<String, UserDevice>> map) {
        this.map = map;
    }
}
