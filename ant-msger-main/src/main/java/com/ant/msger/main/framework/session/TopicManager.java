package com.ant.msger.main.framework.session;

import com.ant.msger.base.enums.OperateType;
import com.ant.msger.main.persistence.dao.TopicUserMapper;
import com.ant.msger.main.persistence.entity.TopicUser;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * topic-user 缓存
 */
public class TopicManager {
    private static volatile TopicManager instance = null;

    /* topicId, 用户集合*/
    private Map<String, Set<TopicUser>> map;

    public static TopicManager getInstance() {
        if (instance == null) {
            synchronized (TopicManager.class) {
                if (instance == null) {
                    instance = new TopicManager();
                }
            }
        }
        return instance;
    }

    public TopicManager() {
        this.map = new ConcurrentHashMap<>();
    }

    public boolean loadDBDatas(TopicUserMapper mapper) {
        try {
            List<TopicUser> list = mapper.loadAllEffective();
            if (null == list || list.isEmpty()) {
                return true;
            }
            synchronized (TopicManager.class) {

                map.clear();
                for (TopicUser temp : list) {
                    String topicId = temp.getTopicId();
                    Set<TopicUser> set = map.get(topicId);
                    if (null == set) {
                        set = new CopyOnWriteArraySet<>();
                        map.put(topicId, set);
                    }
                    set.add(temp);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateCacheDatas(OperateType operateType, List<TopicUser> list) {
        if (null == list || list.isEmpty()) {
            return true;
        }
        synchronized (TopicManager.class) {
            try {
                switch (operateType) {
                    case ADD:
                        update4Insert(list);
                        break;
                    case UPDATE:
                        update4Update(list);
                        break;
                    case DELETE:
                        update4Delete(list);
                        break;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private void update4Delete(List<TopicUser> list) {
        for (TopicUser temp : list) {
            String topicId = temp.getTopicId();
            String userId = temp.getUserId();
            if (StringUtils.isEmpty(userId)) {
                map.remove(topicId);
            } else {
                Set<TopicUser> set = map.get(topicId);
                set.removeIf(topicUser -> {
                    if (StringUtils.equals(topicId, topicUser.getTopicId())
                            && StringUtils.equals(temp.getUserId(), topicUser.getUserId())) {
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    private void update4Update(List<TopicUser> list) {
        for (TopicUser temp : list) {
            String topicId = temp.getTopicId();
            Set<TopicUser> set = map.get(topicId);
            set.removeIf(topicUser -> {
                if (StringUtils.equals(topicId, topicUser.getTopicId())
                        && StringUtils.equals(temp.getUserId(), topicUser.getUserId())) {
                    return true;
                }
                return false;
            });
            set.add(temp);
        }
    }

    private void update4Insert(List<TopicUser> list) {
        for (TopicUser temp : list) {
            Set<TopicUser> set = map.get(temp.getTopicId());
            if (null == set) {
                set = new CopyOnWriteArraySet<>();
                map.put(temp.getTopicId(), set);
            }

            set.add(temp);
        }
    }
}
