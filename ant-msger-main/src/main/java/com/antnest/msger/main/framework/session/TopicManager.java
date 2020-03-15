package com.antnest.msger.main.framework.session;

import com.antnest.msger.core.enums.OperateType;
import com.antnest.msger.core.constant.Constants;
import com.antnest.msger.main.persistence.dao.TopicMapper;
import com.antnest.msger.main.persistence.dao.TopicUserMapper;
import com.antnest.msger.main.persistence.entity.Topic;
import com.antnest.msger.main.persistence.entity.TopicUser;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * topic-user 缓存
 */
public class TopicManager {
    private static volatile TopicManager instance = null;

    /* topicId, 用户集合*/
    private Map<String, Set<TopicUser>> mapTopicUser;
    private Map<String, Topic> mapTopic;

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
        this.mapTopic = new ConcurrentHashMap<>();
        this.mapTopicUser = new ConcurrentHashMap<>();
    }

    public Set<TopicUser> getByTopicId(String topicId) {
        if (StringUtils.isEmpty(topicId)) {
            return null;
        }

        Topic topic = mapTopic.get(topicId);
        // topic是否过期
        if (topic.getTimeType() == Constants.TopicType.TEMPORARY
            && topic.getExpireTime().before(new Date())) {
            return new HashSet<>();
        }

        return mapTopicUser.get(topicId);
    }

    public boolean loadDBDatas(TopicMapper topicMapper, TopicUserMapper mapper) {
        try {
            List<Topic> listTopic = topicMapper.loadAllEffective();
            if (null == listTopic || listTopic.isEmpty()) {
                return true;
            }

            List<TopicUser> list = mapper.loadAllEffective();
            if (null == list || list.isEmpty()) {
                return true;
            }

            synchronized (TopicManager.class) {
                mapTopic.clear();
                mapTopicUser.clear();

                for (Topic topic : listTopic) {
                    mapTopic.put(topic.getTopicId(), topic);
                    mapTopicUser.put(topic.getTopicId(), new CopyOnWriteArraySet<>());
                }

                for (TopicUser topicUser : list) {
                    String topicId = topicUser.getTopicId();
                    Set<TopicUser> set = mapTopicUser.get(topicId);
                    if (null == set) {
                        set = new CopyOnWriteArraySet<>();
                        mapTopicUser.put(topicId, set);
                    }
                    set.add(topicUser);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateCacheDatas(OperateType operateType, Topic topic, List<TopicUser> list) {
        if (null == list || list.isEmpty()) {
            return true;
        }
        synchronized (TopicManager.class) {
            try {
                switch (operateType) {
                    case TOPIC_REGISTER:
                        update4TopicRegister(topic, list);
                        break;
                    case TOPIC_RELEASE:
                        update4TopicRelease(topic.getTopicId());
                        break;
                    case TOPIC_UPDATE_TYPE:
                        update4TopicUpdateType(topic);
                        break;
                    case TOPIC_ADDUSER:
                        update4TopicAddUser(list);
                        break;
                    case TOPIC_REMOVEUSER:
                        update4TopicRemoveUser(list);
                        break;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private void update4TopicAddUser(List<TopicUser> list) {
        for (TopicUser temp : list) {
            Set<TopicUser> set = mapTopicUser.get(temp.getTopicId());
            if (null == set) {
                set = new CopyOnWriteArraySet<>();
                mapTopicUser.put(temp.getTopicId(), set);
            }
            set.add(temp);
        }
    }

    private void update4TopicUpdateType(Topic topic) {
        Topic topicCache = mapTopic.get(topic.getTopicId());
        topicCache.setTimeType(topic.getTimeType());
        topicCache.setExpireTime(topic.getExpireTime());
        topicCache.setTopicName(topic.getTopicName());
    }

    private void update4TopicRelease(String topicId) {
        mapTopic.remove(topicId);
        mapTopicUser.get(topicId).clear();
        mapTopicUser.remove(topicId);
    }

    private void update4TopicRemoveUser(List<TopicUser> list) {
        for (TopicUser temp : list) {
            String topicId = temp.getTopicId();
            Set<TopicUser> set = mapTopicUser.get(topicId);
            set.removeIf(topicUser -> {
                if (StringUtils.equals(topicId, topicUser.getTopicId())
                        && StringUtils.equals(temp.getUserId(), topicUser.getUserId())) {
                    return true;
                }
                return false;
            });

            if (mapTopicUser.get(topicId).isEmpty()) {
                mapTopicUser.remove(topicId);
                mapTopic.remove(topicId);
            }
        }
    }

    private void update4TopicRegister(Topic topic, List<TopicUser> list) {
        mapTopic.put(topic.getTopicId(), topic);

        for (TopicUser temp : list) {
            Set<TopicUser> set = mapTopicUser.get(temp.getTopicId());
            if (null == set) {
                set = new CopyOnWriteArraySet<>();
                mapTopicUser.put(temp.getTopicId(), set);
            }
            set.add(temp);
        }
    }
}
