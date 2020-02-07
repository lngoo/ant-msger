package com.ant.msger.main.mq.puller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.enums.SubjectType;
import com.ant.msger.base.session.TopicUser;
import com.ant.msger.base.session.UserDevice;
import com.ant.msger.main.mq.ThreadPool;
import com.ant.msger.main.mq.callable.DialogUserUpdator;
import com.ant.msger.main.mq.callable.UserDeviceUpdator;
import com.ant.msger.main.persistence.dao.UserDeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class ResponseDataPuller {
    private static final Logger LOG = LoggerFactory.getLogger("responseChannel");

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${redis.key.queue.response}")
    String redisKey;

    @Autowired
    UserDeviceMapper userDeviceMapper;

    public void doJob() {
        UserDevice userDevice = new UserDevice("1","1",null);
        if (StringUtils.isEmpty(redisKey)) {
            System.out.println("###[response] no response redis key config. stop all...");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String data = stringRedisTemplate.opsForList().rightPop(redisKey);
                    if (null == data) {
                        // Sleep 3秒
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LOG.info("###[response] no response data. sleep 3 seconds." + System.currentTimeMillis());
                    } else {
                        LOG.info("###[response] got one data." + System.currentTimeMillis());
                        try {
                            JSONObject jsonObject = JSON.parseObject(data);
                            NotifyType notifyType = Enum.valueOf(NotifyType.class, (String) jsonObject.get("notifyType"));
                            SubjectType subjectType = Enum.valueOf(SubjectType.class, (String) jsonObject.get("subjectType"));
                            switch (subjectType) {
                                case USER_DEVICE:
//                                    UserDevice userDevice = jsonObject.getJSONObject("object").toJavaObject(UserDevice.class);
                                    List<UserDevice> listUserDevices = jsonObject.getJSONArray("objects").toJavaList(UserDevice.class);
                                    ThreadPool.submit(new UserDeviceUpdator(notifyType, listUserDevices, userDeviceMapper));
                                    break;
                                case DIALOG_USER:
//                                    TopicUser topicUser = jsonObject.getJSONObject("object").toJavaObject(TopicUser.class);
                                    List<TopicUser> listTopicUsers = jsonObject.getJSONArray("objects").toJavaList(TopicUser.class);
                                    // TODO
//                                    ThreadPool.submit(new DialogUserUpdator(notifyType, listTopicUsers, ));
                                    break;
                            }
                        } catch (Exception e) {
                            LOG.warn("###%%%[response] Exception Happened.");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}
