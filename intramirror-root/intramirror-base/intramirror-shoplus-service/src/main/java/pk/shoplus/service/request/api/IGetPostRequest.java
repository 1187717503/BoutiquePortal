package pk.shoplus.service.request.api;

import java.util.Map;

/**
 * Created by dingyifan on 2017/6/5.
 */
public interface IGetPostRequest {

    /**
     *
     * @param requestType
     *            请求类型
     * @param urlStr
     *            请求地址
     * @param body
     *            请求发送内容
     * @return 返回内容
     */
    public String requestMethod(String requestType, String urlStr, String body);
}
