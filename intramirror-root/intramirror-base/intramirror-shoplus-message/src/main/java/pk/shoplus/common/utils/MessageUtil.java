package pk.shoplus.common.utils;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import pk.shoplus.common.exception.MessageFailException;

import java.io.InputStream;
import java.util.Properties;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class MessageUtil {
    public static final String OSS_CONFIG_FILE = "/message.properties";

    public static final Logger LOGGER = Logger.getLogger(MessageUtil.class);

    public static String accessId;

    public static String accessKey;

    public static String mnsEndPoint;

    private static  CloudAccount account;

    private static MNSClient client;

    static {
        // OSS
        InputStream in = MessageUtil.class.getResourceAsStream(OSS_CONFIG_FILE);
        Properties props = new Properties();
        try {
            props.load(in);

            accessId=props.getProperty("mns.accesskeyid");
            accessKey=props.getProperty("mns.accesskeysecret");
            mnsEndPoint = props.getProperty("mns.accountendpoint");

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.toString());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 获取MNSClient实例
     * @return
     * @throws MessageFailException
     */
    public synchronized static MNSClient getMNSClient () throws MessageFailException {
        account = new CloudAccount(accessId, accessKey, mnsEndPoint);
        client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全

        return client;
    }


}
