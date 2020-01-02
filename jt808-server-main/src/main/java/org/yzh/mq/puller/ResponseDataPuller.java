package org.yzh.mq.puller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ResponseDataPuller {

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${redis.key.queue.response}")
    String redisKey;

    public void doJob() {

        if (StringUtils.isEmpty(redisKey)) {
            System.out.println("### no response redis key config. stop all...");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Object data = redisTemplate.opsForList().leftPop(redisKey);
                        if (null == data) {
                            // Sleep 3秒
                            Thread.sleep(3000);
                            System.out.println("### no response data. sleep 3 seconds.");
                        } else {
                            System.out.println("### got one data.");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
