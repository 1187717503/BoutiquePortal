package com.intramirror.order.api.util;

import com.google.gson.Gson;
import com.intramirror.common.help.StringUtils;
import com.intramirror.core.net.http.OkHttpUtils;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by caowei on 2018/4/23.
 */
public class HttpClientUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
    public static String createAWBUrl;
    public static String queryAWBUrl;
    public static String deleteAWBUrl;
    public static String confirmedOrder;
    public static String shippedOrder;
    public static String confirmStoreUrl;
    public static String tmsProviderRouteUrl;
    public static String appMemberPointsUrl;
    public static String order_capture_confirm;
    public static String tableuaTokenUrl;

    public static String getOrder_capture_confirm() {
        return order_capture_confirm;
    }

    public static void setOrder_capture_confirm(String order_capture_confirm) {
        HttpClientUtil.order_capture_confirm = order_capture_confirm;
    }

    public static String httpPost(String jsonObj, String url){
        HttpPost post = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();

            post = new HttpPost(url);
            // 构造消息头
            post.setHeader("Content-type", "application/json; charset=utf-8");
            //设置超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(2000).build();//设置请求和传输超时时间
            post.setConfig(requestConfig);

            // 构建消息实体
            if(StringUtils.isNotBlank(jsonObj)){
                StringEntity entity = new StringEntity(jsonObj, Charset.forName("UTF-8"));
                entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                // 发送Json格式的数据请求
                entity.setContentType("application/json");
                post.setEntity(entity);
            }

            HttpResponse response = httpClient.execute(post);

            // 检验返回码
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String jsonString = EntityUtils.toString(responseEntity);
            if(statusCode != HttpStatus.SC_OK){
                LOGGER.error("request fail,url={},param={}",url,jsonObj);
                return jsonString;
            }else{
                JSONObject object = JSONObject.fromObject(jsonString);
                if(object.optInt("status")!=1){
                    LOGGER.error("msg={}",object.optString("msg"));
                    return object.toString();
                }else {
                    JSONObject data = object.optJSONObject("data");
                    if (data==null){
                        data = new JSONObject();
                        data.put("data",object.optString("data"));
                    }
                    data.put("status",1);
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

    public static String httpPostNoHandle(String jsonObj, String url){
        HttpPost post = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();

            post = new HttpPost(url);
            // 构造消息头
            post.setHeader("Content-type", "application/json; charset=utf-8");
            //设置超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(100000).setConnectTimeout(2000).build();//设置请求和传输超时时间
            post.setConfig(requestConfig);

            // 构建消息实体
            StringEntity entity = new StringEntity(jsonObj, Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            // 发送Json格式的数据请求
            entity.setContentType("application/json");
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);

            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
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

    public static String httpPostTableuaToken(){
        try {
            LOGGER.info("开始获取tableua token，{}",tableuaTokenUrl);
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "username=ceci.chen%40intramirror.com");
            Request request = new Request.Builder()
                    .url(tableuaTokenUrl)
                    .post(body)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .build();

            Response response = client.newCall(request).execute();
            String ret = response.body().string();
            LOGGER.info("获取 token返回:{}",ret);
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String httpPut(String jsonObj, String url) throws IOException {
        Response response = OkHttpUtils.put().url(url).requestBody(jsonObj).build().readTimeOut(1000L).connTimeOut(1000L).writeTimeOut(1000L).execute();
        if (response!=null){
            return response.body().string();
        }
        return "";
    }

    public static String doPost(String url, String jsonObj, String charset) {

        String result = null;
        try {
            HttpClient httpClient = new SSLClient();
            HttpPost httpPost = new HttpPost(url);

            LOGGER.info("Post url =>" + url);
            // 设置参数
            /*while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
                LOGGER.info("Post param =>" + elem.getKey() + ":" + elem.getValue());
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }*/

            StringEntity entity = new StringEntity(jsonObj, Charset.forName("UTF-8"));
            entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            // 发送Json格式的数据请求
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 1500);
            httpPost.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
            HttpResponse response = httpClient.execute(httpPost);

            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Connect time out.", ex);

        }
        return result;
    }

    public static String httpGet(String url){
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        //设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(2000).build();//设置请求和传输超时时间
        get.setConfig(requestConfig);
        try {

            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            LOGGER.info("result={}",result);
            JSONObject object = JSONObject.fromObject(result);
            if(object.optInt("status")!=1){
                LOGGER.error("msg={}",object.optString("msg"));
                return object.toString();
            }else {
                return object.getString("data");
//                return data.toString();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String getConfirmStoreUrl() {
        return confirmStoreUrl;
    }

    public static void setConfirmStoreUrl(String confirmStoreUrl) {
        HttpClientUtil.confirmStoreUrl = confirmStoreUrl;
    }

    public static String getCreateAWBUrl() {
        return createAWBUrl;
    }

    public static void setCreateAWBUrl(String createAWBUrl) {
        HttpClientUtil.createAWBUrl = createAWBUrl;
    }

    public static String getQueryAWBUrl() {
        return queryAWBUrl;
    }

    public static void setQueryAWBUrl(String queryAWBUrl) {
        HttpClientUtil.queryAWBUrl = queryAWBUrl;
    }

    public static String getDeleteAWBUrl() {
        return deleteAWBUrl;
    }

    public static void setDeleteAWBUrl(String deleteAWBUrl) {
        HttpClientUtil.deleteAWBUrl = deleteAWBUrl;
    }

    public static String getConfirmedOrder() {
        return confirmedOrder;
    }

    public static void setConfirmedOrder(String confirmedOrder) {
        HttpClientUtil.confirmedOrder = confirmedOrder;
    }

    public static String getShippedOrder() {
        return shippedOrder;
    }

    public static String getTmsProviderRouteUrl() {
        return tmsProviderRouteUrl;
    }

    public static void setTmsProviderRouteUrl(String tmsProviderRouteUrl) {
        HttpClientUtil.tmsProviderRouteUrl = tmsProviderRouteUrl;
    }

    public static void setShippedOrder(String shippedOrder) {
        HttpClientUtil.shippedOrder = shippedOrder;
    }

    public static String getAppMemberPointsUrl() {
        return appMemberPointsUrl;
    }

    public static void setAppMemberPointsUrl(String appMemberPointsUrl) {
        HttpClientUtil.appMemberPointsUrl = appMemberPointsUrl;
    }

    public static String getTableuaTokenUrl() {
        return tableuaTokenUrl;
    }

    public static void setTableuaTokenUrl(String tableuaTokenUrl) {
        HttpClientUtil.tableuaTokenUrl = tableuaTokenUrl;
    }

}
