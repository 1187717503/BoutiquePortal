package com.intramirror.web.config;

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
import pk.shoplus.common.utils.StringUtil;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

/**
 * Created by dingyifan on 2017/11/6.
 */
public class HttpUtils {

    private static final Logger logger = Logger.getLogger(HttpUtils.class);

    private static final String HTTP_POST = "POST";

    private static final String HTTP_GET = "GET";

    private static final String HTTP_PUT = "PUT";

    private static final String CONTENT_FROM_TYPE = "application/x-www-form-urlencoded";

    private static final int SEND_REQUEST_TIME_OUT = 1800000;

    private static final int READ_TIME_OUT = 1800000;

    private static final String CHARSET_UTF_8 = "UTF-8";

    public static String getToken() {
        return token;
    }

    public static synchronized void setToken(String token) {
        HttpUtils.token = token;
    }

    private static String token = "";

    public static String httpGet(String url) {
        Map<String, Object> result = requestHttp(HTTP_GET, url, CONTENT_FROM_TYPE, null, null);
        return result.get("resultMessage").toString();
    }

    public static String httpPost(String url, String body) {
        Map<String, Object> result = requestHttp(HTTP_POST, url, CONTENT_FROM_TYPE, body, null);
        return result.get("resultMessage").toString();
    }

    public static Map<String, Object> httpPost2(String url, String body) {
        return requestHttp(HTTP_POST, url, "application/json", body, null);
    }

    public static Map<String, Object> httpPut(String url, String token, String body) {
        return requestHttp(HTTP_PUT, url, "application/json", body, token);
    }

    private static Map<String, Object> requestHttp(String requestType, String url, String contentType, String body, String token) {
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
            if (requestType.equals(HTTP_POST)) {
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

            if (StringUtil.isNotEmpty(token)) {
                httpURLConnection.setRequestProperty("token", token);
            }
            httpURLConnection.connect();
            if (requestType.equals(HTTP_POST)) {
                outputStream = httpURLConnection.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(body);
                outputStreamWriter.flush();
            }
            Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
            mapUtils.putData("status", httpURLConnection.getResponseCode());
            mapUtils.putData("headers", headers);

            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
                resultBuffer.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("HttpUtils,requestHttp,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        } finally {
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
        mapUtils.putData("resultMessage", resultBuffer.toString());
        return mapUtils.getMap();
    }

}
