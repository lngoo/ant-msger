package com.ant.msger.main.framework.session;

import com.ant.msger.base.session.TopicUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 会话用户缓存
 */
public class DialogManager {
    private Map<String, Map<String, TopicUser>> map = new HashMap<>();

    public Map<String, Map<String, TopicUser>> getMap() {
        return map;
    }

    public void setMap(Map<String, Map<String, TopicUser>> map) {
        this.map = map;
    }
}
