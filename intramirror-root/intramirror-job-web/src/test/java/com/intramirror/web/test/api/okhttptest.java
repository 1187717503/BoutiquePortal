package com.intramirror.web.test.api;

import com.intramirror.core.net.http.OkHttpUtils;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.util.HttpUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.shoplus.common.utils.StringUtil;

/**
 * Created on 2018/2/1.
 *
 * @author YouFeng.Zhu
 */
public class okhttptest {

    private final Logger LOGGER = LoggerFactory.getLogger(okhttptest.class);

    @Test
    public void testhttp() throws IOException {

        String appendUrl = "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a6c9eb33-0465-4674-aedc-0615cdf6282e\",\"FULL\":true}";
        okhttp3.Response httpResponse = OkHttpUtils.get().url(appendUrl).build().connTimeOut(60 * 1000).readTimeOut(60 * 1000).execute();
        LOGGER.info("{}",httpResponse.body().string());

    }

    private boolean unbindProductSpu(Long productId, Long spuId) {
        String baseUrl = "http://localhost:8103";
        if (StringUtil.isEmpty(HttpUtils.getToken())) {
            login(baseUrl);
        }
        Map<String, Object> response = sendUnbindRequest(baseUrl, productId, spuId);
        if (Integer.parseInt(response.get("status").toString()) == 401) {
            login(baseUrl);
            response = sendUnbindRequest(baseUrl, productId, spuId);
        }

        if (Integer.parseInt(response.get("status").toString()) != 200) {
            LOGGER.error("Fail to unbind product [{}] -- [{}]", productId, spuId);
            return false;
        }
        return true;
    }

    private Map<String, Object> sendUnbindRequest(String baseUrl, Long productId, Long spuId) {
        String url = baseUrl + "/product/spus/" + spuId + "/products/" + productId;
        Map<String, Object> response = HttpUtils.httpPut(url, HttpUtils.getToken(), "");
        return response;
    }

    private void login(String baseUrl) {
        String loginUrl = baseUrl + "/auth/login";
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", "job@intramirror.com");
        loginBody.put("password", "e10adc3949ba59abbe56e057f20f883e");
        Map<String, Object> loginResp = HttpUtils.httpPost2(loginUrl, JsonTransformUtil.toJson(loginBody));
        if (loginResp.get("resultMessage") != null) {
            Map<String, Object> resultMessage = JsonTransformUtil.readValue(loginResp.get("resultMessage").toString(), HashMap.class);
            String token = (String) resultMessage.get("token");

            if (token != null) {
                HttpUtils.setToken(token);
            }
        }
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
