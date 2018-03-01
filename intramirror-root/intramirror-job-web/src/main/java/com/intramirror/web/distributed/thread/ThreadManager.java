package com.intramirror.web.distributed.thread;

import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    private static final ExecutorService productPool = Executors.newFixedThreadPool(100);
    private static final ExecutorService productImagePool = Executors.newFixedThreadPool(100);
    private static final ExecutorService stockPool = Executors.newFixedThreadPool(100);

    public static void submit(Runnable runnable) {
        if (runnable instanceof UpdateProductImageThread) {
            productImagePool.submit(runnable);
        } else if (runnable instanceof UpdateProductThread) {
            productPool.submit(runnable);
        } else if (runnable instanceof UpdateStockThread) {
            stockPool.submit(runnable);
        }
    }
}
