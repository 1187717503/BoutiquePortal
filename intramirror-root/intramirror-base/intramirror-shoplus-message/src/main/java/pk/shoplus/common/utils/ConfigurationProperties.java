package pk.shoplus.common.utils;

import org.apache.log4j.Logger;
import pk.shoplus.service.RedisService;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by dingyifan on 2017/6/21.
 */
public class ConfigurationProperties {
    private static Logger logger = Logger.getLogger(ConfigurationProperties.class);

    private static final String common_file_path = "/mqcommon.properties";

    private String mq_env;

    private static ConfigurationProperties configurationProperties;

    public static ConfigurationProperties getInstance(){
        if(configurationProperties==null)
            configurationProperties = new ConfigurationProperties();
        return configurationProperties;
    }

    public String getMq_env() {
        logger.info("ConfigurationProperties.getMq_env() : " + mq_env);
        return this.mq_env;
    }

    public ConfigurationProperties() {
        System.out.println(System.getProperty("java.class.path"));
        InputStream in = ConfigurationProperties.class.getResourceAsStream(common_file_path);
        Properties props = new Properties();
        try {
            props.load(in);

            mq_env = props.getProperty("message.mq.env");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : shoplus-message project 读取配置文件失败 !!!" + e.getMessage());
        }
    }

}
