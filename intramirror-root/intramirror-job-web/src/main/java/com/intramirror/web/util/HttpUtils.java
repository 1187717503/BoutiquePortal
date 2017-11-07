package com.intramirror.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by dingyifan on 2017/11/6.
 */
public class HttpUtils {

    public static String httpGet(String url){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet method = new HttpGet(url);

            HttpResponse result = httpClient.execute(method);

            InputStream is = result.getEntity().getContent();
            String resultMeesage = convertStreamToString(is);

            return resultMeesage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpPost(String url,String body) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost method = new HttpPost(url);

            if(StringUtils.isNotBlank(body)) {
                StringEntity entity = new StringEntity(body, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/x-www-form-urlencoded");
                method.setEntity(entity);
            }

            HttpResponse result = httpClient.execute(method);

            InputStream is = result.getEntity().getContent();
            String resultMeesage = convertStreamToString(is);

            return resultMeesage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
