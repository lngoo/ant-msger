package com.ant.msger.main.mq.puller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.enums.SubjectType;
import com.ant.msger.base.session.DialogUser;
import com.ant.msger.base.session.UserDevice;
import com.ant.msger.main.mq.ThreadPool;
import com.ant.msger.main.mq.callable.DialogUserUpdator;
import com.ant.msger.main.mq.callable.UserDeviceUpdator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ResponseDataPuller {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${redis.key.queue.response}")
    String redisKey;

    public void doJob() {

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
                        System.out.println("###[response] no response data. sleep 3 seconds." + System.currentTimeMillis());
                    } else {
                        System.out.println("###[response] got one data." + System.currentTimeMillis());
                        JSONObject jsonObject = JSON.parseObject(data);
                        NotifyType notifyType = Enum.valueOf(NotifyType.class, (String) jsonObject.get("notifyType"));
                        SubjectType subjectType = Enum.valueOf(SubjectType.class, (String) jsonObject.get("subjectType"));
                        switch (subjectType) {
                            case USER_DEVICE:
                                UserDevice userDevice = jsonObject.getJSONObject("object").toJavaObject(UserDevice.class);
                                ThreadPool.submit(new UserDeviceUpdator(notifyType, userDevice));
                                break;
                            case DIALOG_USER:
                                DialogUser dialogUser = jsonObject.getJSONObject("object").toJavaObject(DialogUser.class);
                                ThreadPool.submit(new DialogUserUpdator(notifyType, dialogUser));
                                break;
                        }
                    }
                }
            }
        }).start();
    }
}
