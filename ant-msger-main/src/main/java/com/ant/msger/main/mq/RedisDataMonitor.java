package com.ant.msger.main.mq;

import com.ant.msger.main.mq.puller.ResponseDataPuller;
import com.ant.msger.main.mq.puller.SendDataPuller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RedisDataMonitor implements ApplicationRunner {

    @Value("${threadpool.thread.nums:4}")
    Integer threadNumbs;

    @Autowired
    SendDataPuller sendDataPuller;

    @Autowired
    ResponseDataPuller responseDataPuller;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (threadNumbs < 4) {
            threadNumbs = 4;
        }
        ThreadPool.init(threadNumbs);

        // 监听发送队列
        sendDataPuller.doJob();
        responseDataPuller.doJob();

    }
}
