package com.intramirror.web.util;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by caowei on 2018/4/23.
 */
public class DHLHttpClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(DHLHttpClient.class);

    public static String createAWBUrl;

    public static String queryAWBUrl;

    public static String deleteAWBUrl;

    public static String httpPost(JSONObject jsonObj, String url){
        HttpPost post = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();

            // 设置超时时间
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);

            post = new HttpPost(url);
            // 构造消息头
            post.setHeader("Content-type", "application/json; charset=utf-8");

            // 构建消息实体
            StringEntity entity = new StringEntity(jsonObj.toString(), Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            // 发送Json格式的数据请求
            entity.setContentType("application/json");
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);

            // 检验返回码
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != HttpStatus.SC_OK){
                LOGGER.error("request fail,url={},param={}",url,jsonObj.toString());
            }else{
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                JSONObject object = JSONObject.fromObject(jsonString);
                if(object.optInt("status")!=1){
                    LOGGER.error("mag={}",object.optString("msg"));
                }else {
                    JSONObject data = object.optJSONObject("data");
                    return data!=null?data.toString():"request success";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(post != null){
                try {
                    post.releaseConnection();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static String httpGet(String url){
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        try {

            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            LOGGER.info("result={}",result);
            JSONObject object = JSONObject.fromObject(result);
            if(object.optInt("status")!=1){
                LOGGER.error("mag={}",object.optString("msg"));
            }else {
                JSONObject data = object.optJSONObject("data");
                return data.toString();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCreateAWBUrl() {
        return createAWBUrl;
    }

    public static void setCreateAWBUrl(String createAWBUrl) {
        DHLHttpClient.createAWBUrl = createAWBUrl;
    }

    public static String getQueryAWBUrl() {
        return queryAWBUrl;
    }

    public static void setQueryAWBUrl(String queryAWBUrl) {
        DHLHttpClient.queryAWBUrl = queryAWBUrl;
    }

    public static String getDeleteAWBUrl() {
        return deleteAWBUrl;
    }

    public static void setDeleteAWBUrl(String deleteAWBUrl) {
        DHLHttpClient.deleteAWBUrl = deleteAWBUrl;
    }
}
