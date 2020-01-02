package org.yzh.mq.puller;

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
            System.out.println("### no response redis key config. stop all...");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    Register register = new Register();
//                    register.setCityId(23);
//                    register.setLicensePlate("xdssd我们");
//                    stringRedisTemplate.opsForList().rightPush(redisKey, JsonUtils.toJson(register));
//                }
                try {
                    while (true) {
                        String data = stringRedisTemplate.opsForList().leftPop(redisKey);
                        if (null == data) {
                            // Sleep 3秒
                            Thread.sleep(3000);
                            System.out.println("### no response data. sleep 3 seconds." + System.currentTimeMillis());
                        } else {
                            System.out.println("### got one data." + System.currentTimeMillis());

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
