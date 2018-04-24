package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by caowei on 2018/4/24.
 */
@CrossOrigin
@Controller
@RequestMapping("/fileLoad")
public class DownloadFileController {

    @RequestMapping(value = "/awb", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage fileLoadAwb(String fileUrl,String fileName,HttpServletResponse res) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (StringUtils.isNotBlank(fileUrl)) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                //获取网络资源
                URL url = new URL(fileUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //超时响应时间为5秒
                conn.setConnectTimeout(5 * 1000);

                res.setHeader("Content-Type", "application/force-download");
                res.setHeader("Content-Type", "application/vnd.ms-excel");
                res.setHeader("Content-disposition",
                        "attachment; filename=" + new String(fileName.getBytes("utf-8"), "utf-8"));
                //构造读取流
                bis = new BufferedInputStream(conn.getInputStream());
                //构造输出流
                bos = new BufferedOutputStream(res.getOutputStream());
                byte[] buff = new byte[1024];
                int bytesRead;
                //每次读取缓存大小的流，写到输出流
                while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                    bos.write(buff, 0, bytesRead);
                }
                res.flushBuffer();//将所有的读取的流返回给客户端
                result.successStatus();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != bos) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

}
