package org.yzh.mq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private static ExecutorService executorService;

    public static void init(int threadNumbs){
        executorService = Executors.newScheduledThreadPool(threadNumbs);
    }


}
