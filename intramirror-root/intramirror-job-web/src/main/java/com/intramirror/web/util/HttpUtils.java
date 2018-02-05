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
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
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

    public static Map<String, Object> responseAndHeadersGet(String url) {
        Map<String, Object> result = requestHttps(url, HTTP_GET);
        Map<String, List<String>> headers = (Map<String, List<String>>) result.get("headers");
        List<String> currentList = headers.get("X-Count-Current");
        List<String> pagesList = headers.get("X-Count-Pages");
        List<String> totalList = headers.get("X-Count-Total");

        int current = Integer.parseInt(currentList.get(0));
        int pages = Integer.parseInt(pagesList.get(0));
        int total = Integer.parseInt(totalList.get(0));

        result.put("current", current);
        result.put("pages", pages);
        result.put("total", total);
        return result;
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

    public static Map<String, Object> requestHttps(String requestUrl, String requestType) {
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        StringBuffer buffer = null;
        try {
            // 创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = { new MyX509TrustManager() };
            // 初始化
            sslContext.init(null, tm, new java.security.SecureRandom());

            // 获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestType);
            // 设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();
            /*// 往服务器端写内容
            if (null != outputStr) {
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }*/
            Map<String, List<String>> headers = conn.getHeaderFields();
            mapUtils.putData("headers", headers);
            // 读取服务器端返回的内容
            int code = conn.getResponseCode();
            InputStream is = null;
            if (code != 200) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("HttpUtils,https,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        mapUtils.putData("resultMessage", buffer.toString());
        return mapUtils.getMap();
    }

}
