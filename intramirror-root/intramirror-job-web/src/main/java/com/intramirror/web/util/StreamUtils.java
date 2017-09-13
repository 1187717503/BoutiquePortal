package com.intramirror.web.util;

import pk.shoplus.util.ExceptionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dingyifan on 2017/9/3.
 */
public class StreamUtils {

    /**
     * InputStream 转 String
     * @param is
     * @return
     * @throws Exception
     */
    public static String convertStreamToString(InputStream is) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuffer stringBuffer = new StringBuffer();

        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line+"\n");
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuffer.toString();
    }




}
