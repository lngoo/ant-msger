package com.antnest.msger.main.mq;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {

    private static ExecutorService executorService;

    public static void init(int threadNumbs){
        executorService = Executors.newScheduledThreadPool(threadNumbs);
    }

    public static Future submit(Callable callable){
        return executorService.submit(callable);
    }
}
