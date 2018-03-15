package com.intramirror.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/8/28.
 */
public class GetPostRequestUtil {

    private static Logger logger = Logger.getLogger(GetPostRequestUtil.class);

    // post请求
    public static final String HTTP_POST = "POST";

    // get请求
    public static final String HTTP_GET = "GET";

    // utf-8字符编码
    public static final String CHARSET_UTF_8 = "utf-8";

    // HTTP内容类型。如果未指定ContentType，默认为TEXT/HTML
    public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";

    // HTTP内容类型。相当于form表单的形式，提交暑假
    public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

    // 请求超时时间
    public static final int SEND_REQUEST_TIME_OUT = 500000;

    // 将读超时时间
    public static final int READ_TIME_OUT = 500000;

    /**
     * @param requestType
     *         请求类型
     * @param urlStr
     *         请求地址
     * @param body
     *         请求发送内容
     * @return 返回内容
     */
    public String requestMethod(String requestType, String urlStr, String body) throws Exception {

        // 是否有http正文提交
        boolean isDoInput = false;
        if (body != null && body.length() > 0)
            isDoInput = true;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        try {
            // 统一资源
            URL url = new URL(urlStr);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true, 默认情况下是false;
            if (isDoInput) {
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(body.length()));
            }
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setDoInput(true);
            // 设置一个指定的超时值（以毫秒为单位）
            httpURLConnection.setConnectTimeout(SEND_REQUEST_TIME_OUT);
            // 将读超时设置为指定的超时，以毫秒为单位。
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            // 设置字符编码
            httpURLConnection.setRequestProperty("Accept-Charset", CHARSET_UTF_8);
            // 设置内容类型
            httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE_FORM_URL);
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod(requestType);

            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            // 如果在已打开连接（此时 connected 字段的值为 true）的情况下调用 connect 方法，则忽略该调用。
            httpURLConnection.connect();

            if (isDoInput) {
                outputStream = httpURLConnection.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(body);
                outputStreamWriter.flush();// 刷新
            }
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);

                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                    resultBuffer.append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("GetPostRequestUtilRequestMethod,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
            throw e;
        } finally {// 关闭流

            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return resultBuffer.toString();
    }

    /**
     * @param requestType
     *         请求类型
     * @param urlStr
     *         请求地址
     * @param body
     *         请求发送内容
     * @return 返回内容
     */
    public String requestMethodType(String requestType, String urlStr, String body) {

        // 是否有http正文提交
        boolean isDoInput = false;
        if (body != null && body.length() > 0)
            isDoInput = true;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        try {
            // 统一资源
            URL url = new URL(urlStr);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true, 默认情况下是false;
            if (isDoInput) {
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(body.length()));
            }
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setDoInput(true);
            // 设置一个指定的超时值（以毫秒为单位）
            httpURLConnection.setConnectTimeout(SEND_REQUEST_TIME_OUT);
            // 将读超时设置为指定的超时，以毫秒为单位。
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            // 设置字符编码
            httpURLConnection.setRequestProperty("Accept-Charset", CHARSET_UTF_8);
            // 设置内容类型
            httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE_FORM_URL);
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod(requestType);

            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            // 如果在已打开连接（此时 connected 字段的值为 true）的情况下调用 connect 方法，则忽略该调用。
            httpURLConnection.connect();

            if (isDoInput) {
                outputStream = httpURLConnection.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(body);
                outputStreamWriter.flush();// 刷新
            }
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream, "windows-1252");
                reader = new BufferedReader(inputStreamReader);

                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                    resultBuffer.append("\n");
                }
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {// 关闭流

            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return resultBuffer.toString();
    }

    /**
     * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
     *
     * @param parameterMap
     *         需要转化的键值对集合
     * @return 字符串
     */
    public static String convertStringParamter(Map parameterMap) {
        StringBuffer parameterBuffer = new StringBuffer();
        if (parameterMap != null) {
            Iterator iterator = parameterMap.keySet().iterator();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (parameterMap.get(key) != null) {
                    value = (String) parameterMap.get(key);
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        return parameterBuffer.toString();
    }

    public String postAuth(String url, String username, String password) throws Exception {

        DefaultHttpClient Client = new DefaultHttpClient();

        HttpPost httpGet = new HttpPost(url);
        String encoding = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes("UTF-8"));
        httpGet.setHeader("Authorization", "Basic " + encoding);

        HttpResponse response = Client.execute(httpGet);

        System.out.println("response = " + response);

        BufferedReader breader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseString = new StringBuilder();
        String line = "";
        while ((line = breader.readLine()) != null) {
            responseString.append(line);
        }
        breader.close();
        String repsonseStr = responseString.toString();

        System.out.println("repsonseStr = " + repsonseStr);
        return repsonseStr;
    }

    public static String httpsRequest(String requestUrl, String username, String password) {
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
            conn.setRequestMethod("POST");
            String encoding = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes("UTF-8"));
            conn.setRequestProperty("Authorization", "Basic " + encoding);
            // 设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();
            /*// 往服务器端写内容
            if (null != outputStr) {
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }*/

            // 读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String httpsGetRequest(String requestUrl, String token) {
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
            conn.setRequestMethod("GET");
            conn.setSSLSocketFactory(ssf);
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5 * 1000);
            conn.setRequestProperty("token", token);
            conn.connect();

            // 读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
