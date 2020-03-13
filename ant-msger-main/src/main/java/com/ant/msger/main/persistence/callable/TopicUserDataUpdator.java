package com.ant.msger.main.persistence.callable;

import com.alibaba.fastjson.JSONObject;
import com.ant.msger.base.enums.OperateType;
import com.ant.msger.main.framework.commons.SnowflakeIdWoker;
import com.ant.msger.main.framework.session.TopicManager;
import com.ant.msger.main.persistence.dao.TopicMapper;
import com.ant.msger.main.persistence.dao.TopicUserMapper;
import com.ant.msger.main.persistence.entity.Topic;
import com.ant.msger.main.persistence.entity.TopicUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 会话用户更新器
 */
public class TopicUserDataUpdator implements Callable<String> {

    private static final Logger LOG = LoggerFactory.getLogger("topicUserDataUpdator");

    private OperateType operateType; // 方法
    private JSONObject data;
    private TopicUserMapper topicUserMapper;
    private TopicMapper topicMapper;

    public TopicUserDataUpdator(OperateType operateType, JSONObject data, TopicUserMapper topicUserMapper, TopicMapper topicMapper) {
        this.operateType = operateType;
        this.data = data;
        this.topicUserMapper = topicUserMapper;
        this.topicMapper = topicMapper;
    }

    @Override
    public String call() throws Exception {
        LOG.info("### start update topicUser data. one batch...operateType={}", operateType);

        if (null == data
                || data.isEmpty()) {
            LOG.info("### no datas at this batch. stop all...");
            return "-2";
        }
//        // 类型转换
//        data.put("timeType", data.get("type"));

        try {
            switch (operateType) {
                case TOPIC_REGISTER:
                    return topicRegister();
                case TOPIC_RELEASE:
                    topicRelease();
                    break;
                case TOPIC_UPDATE_TYPE:
                    topicUpdateType();
                    break;
                case TOPIC_ADDUSER:
                    topicAddUser();
                    break;
                case TOPIC_REMOVEUSER:
                    topicRemoveUser();
                    break;
            }
        } catch (Exception e) {
            LOG.warn("#### exception occurred when update topicUser data... operateType={}, e={}", operateType, e);
        }
        return "0";
    }

    private void topicRemoveUser() {
        String topicId = data.getString("topicId");
        String[] userIds = data.getString("member").replaceAll("，", ",").split(",");
        List<TopicUser> list = new ArrayList<>();
        for (String userId : userIds) {
            TopicUser temp = new TopicUser();
            temp.setTopicId(topicId);
            temp.setUserId(userId);
            list.add(temp);
        }
        topicUserMapper.deleteTopicUserBatch(list);

        // 更新缓存
        TopicManager.getInstance().updateCacheDatas(operateType, null, list);
    }

    private void topicAddUser() {
        String topicId = data.getString("topicId");
        String[] userIds = data.getString("member").replaceAll("，", ",").split(",");
        List<TopicUser> list = new ArrayList<>();
        for (String userId : userIds) {
            TopicUser temp = new TopicUser();
            temp.setTopicId(topicId);
            temp.setUserId(userId);
            list.add(temp);
        }
        topicUserMapper.insertBatch(list);

        // 更新缓存
        TopicManager.getInstance().updateCacheDatas(operateType, null, list);
    }

    private void topicUpdateType() {
        Topic para = JSONObject.parseObject(data.toJSONString(), Topic.class);;
        topicMapper.updateByTopicId(para);
        // 更新缓存
        TopicManager.getInstance().updateCacheDatas(operateType, para, null);
    }

    private void topicRelease() {
        String topicId = data.getString("topicId");
        topicUserMapper.deleteByTopicId(topicId);
        topicMapper.deleteByTopicId(topicId);

        // 更新缓存
        TopicManager.getInstance().updateCacheDatas(operateType, new Topic(topicId), null);
    }

    private String topicRegister() {
        Topic topic = JSONObject.parseObject(data.toJSONString(), Topic.class);

        SnowflakeIdWoker idWorker = new SnowflakeIdWoker(1, 1);
        long topicId = idWorker.nextId();
        topic.setTopicId(String.valueOf(topicId));

        String[] userIds = data.getString("member").replaceAll("，", ",").split(",");
        List<TopicUser> list = new ArrayList<>();
        for (String userId : userIds) {
            TopicUser temp = new TopicUser();
            temp.setTopicId(topic.getTopicId());
            temp.setUserId(userId);
            list.add(temp);
        }
        topicMapper.insert(topic);
        topicUserMapper.insertBatch(list);

        // 更新缓存
        TopicManager.getInstance().updateCacheDatas(operateType, topic, list);
        return topic.getTopicId();
    }
}
