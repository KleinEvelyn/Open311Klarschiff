package com.iu.open311_klarschiff;

import android.os.Process;
import android.util.Log;

public class ThreadFactory implements java.util.concurrent.ThreadFactory {

    private final int threadPriority;

    public ThreadFactory(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        return new Thread(() -> {
            try {
                Process.setThreadPriority(threadPriority);
            } catch (Throwable t) {
                Log.e(this.getClass().getSimpleName(), "Problems while running thread", t);
            }
            runnable.run();
        });
    }

}
