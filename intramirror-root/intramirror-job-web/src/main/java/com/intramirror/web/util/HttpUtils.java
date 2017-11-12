package com.intramirror.web.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

/**
 * Created by dingyifan on 2017/11/6.
 */
public class HttpUtils {

    private static final Logger logger = Logger.getLogger(HttpUtils.class);

    private static final String HTTP_POST = "POST";

    private static final String HTTP_GET = "GET";

    private static final String CONTENT_FROM_TYPE = "application/x-www-form-urlencoded";

    private static final int SEND_REQUEST_TIME_OUT = 1800000;

    private static final int READ_TIME_OUT = 1800000;

    private static final String CHARSET_UTF_8 = "UTF-8";

    public static String httpGet(String url){
        Map<String,Object> result = request(HTTP_GET,url,CONTENT_FROM_TYPE,null);
        return result.get("resultMessage").toString();
    }

    public static String httpPost(String url,String body){
        Map<String,Object> result = request(HTTP_POST,url,CONTENT_FROM_TYPE,body);
        return result.get("resultMessage").toString();
    }

    public static Map<String,Object> responseAndHeadersGet(String url){
        Map<String,Object> result = request(HTTP_GET,url,CONTENT_FROM_TYPE,null);
        Map<String,List<String>> headers = (Map<String, List<String>>) result.get("headers");
        List<String> currentList = headers.get("X-Count-Current");
        List<String> pagesList = headers.get("X-Count-Pages");
        List<String> totalList = headers.get("X-Count-Total");

        int current = Integer.parseInt(currentList.get(0));
        int pages = Integer.parseInt(pagesList.get(0));
        int total = Integer.parseInt(totalList.get(0));

        result.put("current",current);
        result.put("pages",pages);
        result.put("total",total);
        return result;
    }

    private static Map<String,Object> request(String requestType,String url,String contentType,String body){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            URL urlObj = new URL(url);
            URLConnection urlConnection = urlObj.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            if(requestType.equals(HTTP_POST)) {
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(body.length()));
            }
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(SEND_REQUEST_TIME_OUT); // 链接超时时间
            httpURLConnection.setReadTimeout(READ_TIME_OUT); // 响应超时时间
            httpURLConnection.setUseCaches(false); // 请求时不使用Cache
            httpURLConnection.setRequestProperty("Accept-Charset", CHARSET_UTF_8);
            httpURLConnection.setRequestProperty("Content-Type", contentType);
            httpURLConnection.setRequestMethod(requestType);
            httpURLConnection.connect();

            Map<String,List<String>> headers = httpURLConnection.getHeaderFields();
            mapUtils.putData("headers",headers);
            if(requestType.equals(HTTP_POST)) {
                outputStream = httpURLConnection.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(body);
                outputStreamWriter.flush();
            }

            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
                resultBuffer.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("HttpUtils,request,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mapUtils.putData("resultMessage",resultBuffer.toString());
        return mapUtils.getMap();
    }

}
