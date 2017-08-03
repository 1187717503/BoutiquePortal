package pk.shoplus.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chone on 2017/4/11.
 */
public class ResultHelper {

    public static Map<String, Object> createErrorResult(
            String errorCode, String errorMsg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ResponseStatus", "2000");
        Map<String, String> error = new HashMap<String, String>();
        error.put("ErrorCode", errorCode);
        error.put("ErrorMsg", errorMsg);
        error.put("TimeStamp", new Date().toString());
        result.put("Error", error);
        return result;
    }

}
