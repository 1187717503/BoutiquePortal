package com.intramirror.common.help;

/**
 * Created by dingyifan on 2017/8/8.
 */
public class ExceptionUtils {

    public static String getExceptionDetail(Exception e) {
        StringBuffer stringBuffer = new StringBuffer(e.toString() + "\n");
        StackTraceElement[] messages = e.getStackTrace();
        int length = messages.length;
        for (int i = 0; i < length; i++) {
            stringBuffer.append("\t"+messages[i].toString()+"\n");
        }
        return stringBuffer.toString();
    }

}
