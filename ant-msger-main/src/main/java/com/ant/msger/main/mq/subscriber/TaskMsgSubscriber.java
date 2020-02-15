package com.ant.msger.main.mq.subscriber;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ant.msger.base.dto.persistence.TopicUser;
import com.ant.msger.base.enums.OperateType;
import com.ant.msger.base.enums.SubjectType;
import com.ant.msger.main.mq.ThreadPool;
import com.ant.msger.main.persistence.callable.TopicUserUpdator;
import com.ant.msger.main.persistence.dao.TopicUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 任务通道数据处理器
 */
public class TaskMsgSubscriber implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger("taskChannel");

    @Autowired
    RedisTemplate redisTemplate;

//    @Autowired
//    UserDeviceMapper userDeviceMapper;

    @Autowired
    TopicUserMapper topicUserMapper;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        LOG.info("### receive one data.");
        try {
            byte[] body = message.getBody();
            String json = new String(body,"utf-8");
            LOG.info("### data json = {}.", json);
            JSONObject jsonObject = JSON.parseObject(json);
            OperateType operateType = Enum.valueOf(OperateType.class, (String) jsonObject.get("operateType"));
            SubjectType subjectType = Enum.valueOf(SubjectType.class, (String) jsonObject.get("subjectType"));
            switch (subjectType) {
//                case USER_DEVICE:
//                    LOG.info("### it`s UserDevice data..");
//                    List<UserDevice> listUserDevices = jsonObject.getJSONArray("list").toJavaList(UserDevice.class);
//                    ThreadPool.submit(new UserDeviceUpdator(operateType, listUserDevices, userDeviceMapper));
//                    break;
                case TOPIC_USER:
                    LOG.info("### it`s TopicUser data..");
                    List<TopicUser> list= JSONArray.parseArray(jsonObject.getJSONArray("list").toJSONString(), TopicUser.class);
                    ThreadPool.submit(new TopicUserUpdator(operateType, list, topicUserMapper));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("#################### end ... ");
    }
}
