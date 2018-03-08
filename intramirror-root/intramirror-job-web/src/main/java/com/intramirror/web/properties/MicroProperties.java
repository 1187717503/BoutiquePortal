package com.intramirror.web.properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created on 2018/2/5.
 *
 * @author YouFeng.Zhu
 */

public class MicroProperties implements InitializingBean{
    @Value("${micro.baseUrl}")
    private String baseUrl;

    @Value("${micro.job.username}")
    private String username;

    @Value("${micro.job.password}")
    private String password;

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("bootstrap.servers", this.bootstrapServers);
    }
}
