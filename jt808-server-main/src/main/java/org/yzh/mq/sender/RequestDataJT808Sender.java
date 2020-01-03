package org.yzh.mq.sender;

import com.ant.jt808.base.dto.jt808.basics.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.yzh.framework.commons.transform.JsonUtils;

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
        stringRedisTemplate.opsForList().leftPush(redisKey, JsonUtils.toJson(message));
    }
}
