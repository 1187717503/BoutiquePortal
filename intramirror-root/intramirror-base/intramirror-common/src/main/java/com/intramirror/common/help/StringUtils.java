package com.intramirror.common.help;

import java.util.UUID;

/**
 * Created by dingyifan on 2017/7/17.
 */
public class StringUtils {

    public static boolean isNotBlank(String string){
        if(string == null || string.length() == 0) {
            return false;
        }
        return true;
    }

    public static boolean isBlank(String string){
        if(string == null || string.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotBlankStrings(String... strings){
        if(strings != null && strings.length > 0) {
            for(String str : strings) {
                if(StringUtils.isBlank(str)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isBlankStrings(String... strings){
        if(strings != null && strings.length > 0) {
            for(String str : strings) {
                if(StringUtils.isBlank(str)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /*生成order line num*/
    public static String getUUID(Integer length) {
        String uuid = String.valueOf(UUID.randomUUID().toString().hashCode()).replace("-", "");
        if (uuid.length() > length) {
            uuid = uuid.substring(0, length - 1);
        }
        if (null != length && length.intValue() > 0) {
            for (int i = uuid.length(); i < length; i++) {
                uuid += "0";
            }
        }

        return uuid;
    }
}
