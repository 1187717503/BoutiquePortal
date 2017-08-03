package pk.shoplus.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.shoplus.common.vo.MessageResponseVo;
import pk.shoplus.service.RedisService;
import pk.shoplus.service.excel.api.IExcelService;
import pk.shoplus.service.excel.impl.ExcelServiceImpl;

import java.io.IOException;
import java.util.*;

/**
 * Created by dingyifan on 2017/8/2.
 *
 */
public class MessageExport {
    private static Logger logger = LoggerFactory.getLogger(MessageExport.class);

    public static void exportByMqName(String name) throws IOException {
        Map<String,Object> maps = RedisService.getInstance().getMap(name);
        logger.info(" maps size and name " + maps.size() + "---" + name);
        List<MessageResponseVo> resultList = new ArrayList<MessageResponseVo>();
        IExcelService iExcelService = new ExcelServiceImpl();

        // 遍历redis对象，转换成mq对象
        if(maps != null){
            List<String> sets = new ArrayList<>();
            int j = 0;
            for(Map.Entry<String, Object> entry : maps.entrySet()){
                JSONObject data = JSONObject.parseObject(entry.getValue().toString());
                JSONObject responseBody = data.getJSONObject("data").getJSONObject("body").getJSONObject("responseBody");
                Iterator iterator = responseBody.keySet().iterator();
                while(iterator.hasNext()){
                    String key = (String) iterator.next();
                    if(key.contains("cat_ids")) {
                        if(!sets.contains("cat_ids-1")){
                            sets.add("cat_ids-1");
                            sets.add("cat_ids-2");
                            sets.add("cat_ids-3");
                        }
                    } else {
                        if(!sets.contains(key))
                            sets.add(key);
                    }


                }
            }

            List<List<String>> lists = new ArrayList<>();
            List<String> head = new ArrayList<>();
            for (Object obj : sets) {
                head.add(obj.toString());
            }
            head.add("error info");
            lists.add(head);

            for(Map.Entry<String, Object> entry : maps.entrySet()){
                JSONObject data = JSONObject.parseObject(entry.getValue().toString());
                JSONObject responseBody = data.getJSONObject("data").getJSONObject("body").getJSONObject("responseBody");
                List<String> list = new ArrayList<>();
                for (Object obj : sets) {
                    if(obj.toString().contains("cat_ids-1")) {
                        JSONArray jsonArray = responseBody.getJSONArray("cat_ids");
                        for(int i = 0;i<3;i++){
                            try {
                                String id = jsonArray.getJSONObject(i).getString("$id");
                                list.add(id);
                            } catch (Exception e) {
                                list.add("null");
                            }
                        }
                    } else if(!obj.toString().contains("cat_ids")){
                        String value = responseBody.getString(obj.toString());
                        if(org.apache.commons.lang.StringUtils.isBlank(value)) {
                            value = "null";
                        }
                        list.add(value);
                    }

                }
                list.add(data.getString("info"));
                lists.add(list);
            }
            iExcelService.genExcel(lists);
        }
    }
}
