//package com.intramirror.web.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//import org.springframework.session.web.http.HeaderHttpSessionStrategy;
//import org.springframework.session.web.http.HttpSessionStrategy;
//
///**
// * Created on 2018/1/30.
// *
// * @author YouFeng.Zhu
// */
//@Configuration
//@EnableRedisHttpSession(redisNamespace = "micro-service")
//public class HttpSessionConfig {
//    @Bean
//    public JedisConnectionFactory connectionFactory() {
//        JedisConnectionFactory connection = new JedisConnectionFactory();
//        connection.setHostName("192.168.99.100");
//        connection.setPort(6379);
//        return connection;
//    }
//
//    @Bean
//    public HttpSessionStrategy httpSessionStrategy() {
//        HeaderHttpSessionStrategy strategy = new HeaderHttpSessionStrategy();
//        strategy.setHeaderName("token");
//        return strategy;
//    }
//}
