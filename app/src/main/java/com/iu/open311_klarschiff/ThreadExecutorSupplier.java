package com.iu.open311_klarschiff;


import android.os.Process;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @see "https://blog.mindorks.com/threadpoolexecutor-in-android-8e9d22330ee3"
 */
public class ThreadExecutorSupplier {
    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static ThreadExecutorSupplier instance;
    private final ThreadPoolExecutor majorBackgroundTasks;
    private final ThreadPoolExecutor minorBackgroundTasks;

    private ThreadExecutorSupplier() {

        ThreadFactory backgroundThreadFactory =
                new ThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);

        majorBackgroundTasks = new ThreadPoolExecutor(NUMBER_OF_CORES * 2, NUMBER_OF_CORES * 2, 60L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), backgroundThreadFactory
        );

        minorBackgroundTasks = new ThreadPoolExecutor(NUMBER_OF_CORES * 2, NUMBER_OF_CORES * 2, 60L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), backgroundThreadFactory
        );
    }

    public static ThreadExecutorSupplier getInstance() {
        if (instance == null) {
            synchronized (ThreadExecutorSupplier.class) {
                instance = new ThreadExecutorSupplier();
            }
        }

        return instance;
    }

    public ThreadPoolExecutor getMajorBackgroundTasks() {
        return majorBackgroundTasks;
    }

    public ThreadPoolExecutor getMinorBackgroundTasks() {
        return minorBackgroundTasks;
    }
}
