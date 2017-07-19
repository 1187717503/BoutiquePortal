package com.intramirror.common.help;

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
}
