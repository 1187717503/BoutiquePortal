package pk.shoplus.util;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/6/9.
 */
public class MapUtils {
    private Map<String,Object> map;

    private String method;

    public Map<String, Object> getMap() {
        return map;
    }

    public MapUtils(Map<String, Object> map,String method) {
        this.method = method;
        this.map = map;
    }

    public MapUtils(Map<String, Object> map) {
        this.map = map;
    }

    public MapUtils putData(String key,Object object){
        this.map.put(key,object);
        return this;
    }

    public MapUtils putLog(String key,Object object){
        if(StringUtils.isNotBlank(method))
            this.map.put(key," Method : " + method + " ; " + object);
        else
            this.map.put(key,object);
        return this;
    }

    public String toJson(){
        return new Gson().toJson(map);
    }
}
