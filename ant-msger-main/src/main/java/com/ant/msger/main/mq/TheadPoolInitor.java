package com.ant.msger.main.mq;

import com.ant.msger.main.mq.subscriber.ResponseDataPuller;
import com.ant.msger.main.mq.subscriber.SendDataPuller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TheadPoolInitor implements ApplicationRunner {

    @Value("${threadpool.thread.nums:10}")
    Integer threadNumbs;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (threadNumbs < 10) {
            threadNumbs = 10;
        }
        ThreadPool.init(threadNumbs);

    }
}
