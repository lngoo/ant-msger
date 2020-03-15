package com.antnest.msger.main.mq.subscriber;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antnest.msger.core.enums.OperateType;
import com.antnest.msger.core.enums.SubjectType;
import com.antnest.msger.main.mq.ThreadPool;
import com.antnest.msger.main.persistence.callable.TopicUserDataUpdator;
import com.antnest.msger.main.persistence.dao.TopicMapper;
import com.antnest.msger.main.persistence.dao.TopicUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 任务通道数据处理器
 */
public class TaskMsgSubscriber implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger("taskChannel");

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    TopicUserMapper topicUserMapper;

    @Autowired
    TopicMapper topicMapper;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        LOG.info("### receive one data.");
        try {
            byte[] body = message.getBody();
            String json = new String(body, "utf-8");
            LOG.info("### data json = {}.", json);
            JSONObject jsonObject = JSON.parseObject(json);
            OperateType operateType = Enum.valueOf(OperateType.class, (String) jsonObject.get("operateType"));
            SubjectType subjectType = Enum.valueOf(SubjectType.class, (String) jsonObject.get("subjectType"));
            switch (subjectType) {
//                case USER_DEVICE:
//                break;
                case TOPIC_USER:
                    LOG.info("### it`s TopicUser data..");
//                    List<TopicUser> list = JSONArray.parseArray(jsonObject.getJSONArray("list").toJSONString(), TopicUser.class);
                    JSONObject data = jsonObject.getJSONObject("data");
                    ThreadPool.submit(new TopicUserDataUpdator(operateType, data, topicUserMapper, topicMapper));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("#################### end ... ");
    }
}
