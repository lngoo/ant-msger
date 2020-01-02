package org.yzh.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.yzh.mq.puller.ResponseDataPuller;

@Component
public class RedisDataMonitor implements ApplicationRunner {

    @Value("${threadpool.thread.nums:4}")
    Integer threadNumbs;

    @Autowired
    ResponseDataPuller responseDataPuller;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (threadNumbs < 4) {
            threadNumbs = 4;
        }
        ThreadPool.init(threadNumbs);

        // 监听响应队列
        responseDataPuller.doJob();

    }
}