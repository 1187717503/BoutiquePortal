package com.intramirror.web.common;

import com.intramirror.web.util.RedissonUtils;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import spark.Request;

public class Cache {

    private static Cache instance = null;

    private RMapCache<String, Object> map;

    private RMapCache<String, Object> sizeMap;

    private RedissonClient client;

    private final static Object syncLock = new Object();

    public static Cache getInstance() {
        if (instance == null) {
            synchronized (syncLock) {
                if (instance == null) {
                    instance = new Cache();
                    try {
                        instance.client = RedissonUtils.getClient();
                        instance.map = instance.client.getMapCache("api:cache");
                        instance.sizeMap = instance.client.getMapCache("product_size:cache");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    private Cache() {

    }

    public Object get(Request request) {
        return map.get(createKey(request));
    }

    public Object get(String key) {
        return map.get(key);
    }

    private String createKey(Request request) {
        String params = "";
        boolean first = true;
        for (String key : request.queryParams()) {
            if (first) {
                first = false;
            } else {
                params += "&";
            }
            params += key + "=" + request.queryParams(key);
        }
        String key = request.uri() + "?" + params;
        return key;
    }

    /* 根据指定key获取指定map中的值 */
    public Object getValueByMapKey(RMapCache<String, Object> map, String key) throws Exception {
        try {
            Object obj = map.get(key);
            return obj;
        } catch (Exception e) {
            throw e;
        }
    }

    /* 设置map的key,value */
    public void setMapValue(RMapCache<String, Object> map, String key, Object value) throws Exception {
        try {
            map.put(key, value, 15, TimeUnit.DAYS);
        } catch (Exception e) {
            throw e;
        }
    }

    public void setExpire(String key, Object value, Long time, TimeUnit timeUnit) {
        RBucket<Object> bucket = client.getBucket(key);
        bucket.set(value, time, timeUnit);
    }

    public RAtomicLong getIncr(String key) {
        RAtomicLong atomicLong = client.getAtomicLong("orderKey:" + key);
        boolean falg = atomicLong.expire(1, TimeUnit.DAYS);
        System.out.print(key + " 生成" + falg);
        return atomicLong;
    }
}
