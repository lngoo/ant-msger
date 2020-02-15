package com.ant.msger.main.framework.session;

import com.ant.msger.base.dto.persistence.TopicUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话用户缓存
 */
public class TopicManager {
    private Map<String, Map<String, TopicUser>> map = new ConcurrentHashMap<>();

    public Map<String, Map<String, TopicUser>> getMap() {
        return map;
    }

    public void setMap(Map<String, Map<String, TopicUser>> map) {
        this.map = map;
    }
}
