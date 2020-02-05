package com.ant.msger.main.framework.session;

import com.ant.msger.base.session.DialogUser;
import com.ant.msger.base.session.UserDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * 会话用户缓存
 */
public class DialogManager {
    private Map<String, Map<String, DialogUser>> map = new HashMap<>();

    public Map<String, Map<String, DialogUser>> getMap() {
        return map;
    }

    public void setMap(Map<String, Map<String, DialogUser>> map) {
        this.map = map;
    }
}
