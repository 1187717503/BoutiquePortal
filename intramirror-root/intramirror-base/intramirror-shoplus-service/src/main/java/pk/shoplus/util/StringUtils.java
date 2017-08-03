package pk.shoplus.util;

import com.google.gson.Gson;

/**
 * Created by dingyifan on 2017/6/15.
 */
public class StringUtils {

    /**
     * 处理数据库特殊字符
     * @param str
     * @return
     */
    public static String escapeStr(String str){
        if(org.apache.commons.lang.StringUtils.isNotBlank(str)) {
            str = str
            .replace("\\","\\\\")
            .replace("\"","\\\"");
//            .replace("'","\\'")
        }
        return str;
    }

    public static String escapeStrToMysql(String str){
        if(org.apache.commons.lang.StringUtils.isNotBlank(str)) {
            str = str
                    .replace("'","\\'");
//            .replace("'","\\'")
        }
        return str;
    }


    public static void main(String[] args) {
        String str = "Chia\\ra F%er\"ragni'";
        System.out.println(new Gson().toJson(str));
        System.out.println(str);
       ;
        System.out.println( StringUtils.escapeStr(str));
    }
}
