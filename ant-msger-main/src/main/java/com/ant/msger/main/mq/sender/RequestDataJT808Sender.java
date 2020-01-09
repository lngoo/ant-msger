package com.ant.msger.main.mq.sender;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.main.framework.commons.transform.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RequestDataJT808Sender {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${redis.key.queue.request.jt808}")
    String redisKey;

    /**
     * 左边放右边取
     * @param message
     */
    public void send(Message message){
        if (null == message
            || StringUtils.isEmpty(message.getMobileNumber())
            || null == message.getType()
            || null == message.getBody()) {
            System.out.println("### some data loss, can`t send to redis...");
        }

        System.out.println("### sender send data to redis.." + System.currentTimeMillis());
        stringRedisTemplate.opsForList().leftPush(redisKey, JsonUtils.toJson(message));
    }
}
