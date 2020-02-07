package com.ant.msger.main.framework.session;

import com.ant.msger.base.session.UserDevice;
import com.ant.msger.main.framework.commons.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户设备缓存
 */
public class UserManager {
    private static Map<String, Map<String, UserDevice>> map = new ConcurrentHashMap<>();

    public static Map<String, UserDevice> getUserAllDevices(String userId) {
        return map.get(userId);
    }

    public static UserDevice getUserOneDevice(String userId, String deviceId) {
        Map<String, UserDevice> mapDevices = map.get(userId);
        if (null == mapDevices) {
            return null;
        } else {
            return mapDevices.get(deviceId);
        }
    }

    public static void addOne(UserDevice userDevice) {
        String userId = userDevice.getUserId();
        Map<String, UserDevice> mapChild = map.get(userId);
        if (null == mapChild) {
            mapChild = new ConcurrentHashMap<>();
            map.put(userId, mapChild);
        }
        mapChild.put(userDevice.getDeviceId(), userDevice);
    }
}
