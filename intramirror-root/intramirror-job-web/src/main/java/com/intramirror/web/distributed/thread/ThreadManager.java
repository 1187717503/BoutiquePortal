package com.intramirror.web.distributed.thread;

import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadManager {

    private static final ExecutorService productPool = Executors.newFixedThreadPool(50);
    private static final ExecutorService productImagePool = Executors.newFixedThreadPool(50);
    private static final ExecutorService stockPool = Executors.newFixedThreadPool(50);

    public static Future<?> submit(Runnable runnable) {
        if (runnable instanceof UpdateProductImageThread) {
            return productImagePool.submit(runnable);
        } else if (runnable instanceof UpdateProductThread) {
            return productPool.submit(runnable);
        } else if (runnable instanceof UpdateStockThread) {
            return stockPool.submit(runnable);
        }
        return null;
    }
}
