package pk.shoplus.service.queue.api;

import com.alibaba.fastjson.JSONObject;
import pk.shoplus.vo.ResultMessage;

/**
 * 
 * @author yfding
 *
 */
public interface IMqQueueService {

    /**
     * 调用CloudStore的getEvents接口
     * type = 0 -> update stock
     * type = 1 -> update stock (store = 0)
     * type = 2 -> update product
     * type = 3 -> update product
     * type = 4 -> update stock
     * @param jsonObject
     * @return
     * @throws Exception
     */
    ResultMessage putGetEventsData(JSONObject jsonObject) throws Exception;
}
