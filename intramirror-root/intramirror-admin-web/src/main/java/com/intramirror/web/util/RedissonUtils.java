package com.intramirror.web.util;

import com.intramirror.web.common.Cache;
import java.io.InputStream;
import java.util.Properties;
import org.apache.poi.util.IOUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 *
 */
public class RedissonUtils {
    private static RedissonClient client;
    private static int promotionTime;
    private static int activeRefreshCacheTime;

    private final static Object syncLock = new Object();

    public static RedissonClient createClient() {

        InputStream in = null;
        try {
            in = Cache.class.getResourceAsStream("/redis.properties");
            Properties props = new Properties();
            props.load(in);

            String localUrl = props.getProperty("spring.redis.host");
            String port = props.getProperty("spring.redis.port");
            String cacheTime = props.getProperty("cache.second.promotion");
            String refreshCacheTime = props.getProperty("cache.second.promotion.activeRefreshCacheTime");

            if (cacheTime != null) {
                promotionTime = Integer.parseInt(cacheTime);
            } else {
                promotionTime = 15;
            }

            if (refreshCacheTime != null) {
                activeRefreshCacheTime = Integer.parseInt(refreshCacheTime);
            } else {
                activeRefreshCacheTime = 300;
            }

            Config config = new Config();
            config.useSingleServer().setAddress(localUrl + ":" + port);
            client = Redisson.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
        }
        return client;
    }

    public static RedissonClient getClient() {
        if (client == null) {
            synchronized (syncLock) {
                if (client == null) {
                    client = createClient();
                }
            }
        }
        return client;
    }

    public static int getPromotionTime() {
        return promotionTime;
    }

    public static int getActiveRefreshCacheTime() {
        return activeRefreshCacheTime;
    }

}
