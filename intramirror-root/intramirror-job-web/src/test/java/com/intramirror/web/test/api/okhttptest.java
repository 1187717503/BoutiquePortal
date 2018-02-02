package com.intramirror.web.test.api;

import com.intramirror.utils.transform.JsonTransformUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2018/2/1.
 *
 * @author YouFeng.Zhu
 */
public class okhttptest {

    private final Logger LOGGER = LoggerFactory.getLogger(okhttptest.class);

    @Test
    public void testSortimage() {

        String img = "[\"http://185.58.119.172/dolcitrame/ObjImg//08012018/080120180_0027.jpg\",\"http://185.58.119.172/dolcitrame/ObjImg//08012018/08012018_00026.jpg\",\"http://185.58.119.172/dolcitrame/ObjImg//08012018/08012018_00025.jpg\"]";

        LOGGER.info("{}", sortImageString(img));
    }

    private String sortImageString(String imageJson) {
        List<String> imageList = JsonTransformUtil.readValue(imageJson, ArrayList.class);
        if (imageList == null) {
            return imageJson;
        }
        Map<String, String> sortedImages = new TreeMap<>();

        for (String image : imageList) {
            String[] ret = image.split("_");
            if (ret.length <= 1) {
                return imageJson;
            }
            sortedImages.put(ret[ret.length - 11], image);
        }

        List<String> sortedImagesList = new ArrayList<>();
        Iterator it = sortedImages.keySet().iterator();
        while (it.hasNext()) {
            sortedImagesList.add(sortedImages.get(it.next()));
        }
        return JsonTransformUtil.toJson(sortedImagesList);
    }

    @Test
    public void testMultiThread() throws Exception {
        final long startTime = System.currentTimeMillis();
        final int numThreads = 256;// Connection number
        final int rounds = 100;
        final int timeout = numThreads * rounds;// Connection timeout
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        // build 256 threads
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);

        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Thread  test start.");
            }
            for (int i = 0; i < numThreads; i++) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        try {
                            allExecutorThreadsReady.countDown();
                            afterInitBlocker.await();
                            //test
                        } catch (final Throwable e) {
                            exceptions.add(e);
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            // start all test runners
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent",
                    allExecutorThreadsReady.await(numThreads, TimeUnit.SECONDS));
            afterInitBlocker.countDown();
            assertTrue(" timeout! More than" + timeout + "seconds", allDone.await(timeout, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        final long duration = System.currentTimeMillis() - startTime;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Find Exception " + exceptions.size());
            LOGGER.debug("Total use time: " + duration + ".....");
        }
        for (Throwable ex : exceptions) {
            ex.printStackTrace();
        }
        assertTrue("failed with exception(s)" + exceptions, exceptions.isEmpty());
    }

}
