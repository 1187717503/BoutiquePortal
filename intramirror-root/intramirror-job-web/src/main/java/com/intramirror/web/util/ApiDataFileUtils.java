package com.intramirror.web.util;

import com.intramirror.common.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import pk.shoplus.util.ExceptionUtils;

import java.io.*;
import java.util.Date;

/**
 * Created by dingyifan on 2017/9/15.
 */
public class ApiDataFileUtils {

    private static final Logger logger = Logger.getLogger(ApiDataFileUtils.class);

//    private static final String baseUrl = "/mnt";

    private static final String baseUrl = "/Users/dingyifan/Documents/fileTest";

    private static final String bak_file = "backup";

    private static final String error_file = "error";

    private String vendorName; // 不同vendor名称

    private String eventName; // 处理事件名称

    public ApiDataFileUtils(String vendorName,String eventName) {
        this.vendorName = vendorName;
        this.eventName = eventName;
    }

    public boolean bakPendingFile(String fileName, String content){
        // 获取文件夹路径
        String strDate = DateUtils.getStrDate(new Date());
        String bakUrl = baseUrl+"/"+vendorName+"/"+eventName+"/"+strDate+"/"+bak_file;
        return this.writeFile(bakUrl,fileName,content);
    }

    public boolean bakErrorFile(String fileName, String content){
        // 获取文件夹路径
        String strDate = DateUtils.getStrDate(new Date());
        String bakUrl = baseUrl+"/"+vendorName+"/"+eventName+"/"+strDate+"/"+error_file;
        return this.writeFile(bakUrl,fileName,content);
    }

    private boolean writeFile(String bakUrl,String fileName, String content) {
        try {
            // 校验参数
            if(StringUtils.isBlank(fileName) || StringUtils.isBlank(content)) {
                logger.info("ApiDataFileUtilsBakFile,checkParams,fileName:"+fileName+",content:"+content);
                return false;
            }

            // 检查文件夹是否创建
            File fileFolder = new File(bakUrl);
            if(!fileFolder.exists()) {
                fileFolder.mkdirs();
                logger.info("ApiDataFileUtilsBakFile,createNewFile,bakUrl:"+bakUrl);
            }

            // 创建文件
            String formatDate = DateUtils.getStrDate(new Date(),"yyyyMMddHHmmss");
            fileName = fileName + "" + formatDate;
            String fileUrl = bakUrl+"/"+fileName+".txt";
            this.writeFileContent(fileUrl,content);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiDataFileUtilsBakFile,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",fileName:"+fileName+",content:"+content);
        }
        return true;
    }

    private void writeFileContent(String fileUrl,String content) throws IOException {
        FileWriter fileWriter = null;
        try {
            // 创建文件
            File file = new File(fileUrl);
            if(!file.exists()) {
                file.createNewFile();
            }

            // 写入文件
            fileWriter = new FileWriter(file);
            fileWriter.write(content,0,content.length());
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiDataFileUtilsWriteFileContent,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        } finally {
            if(fileWriter != null) {fileWriter.close();}
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getBak_file() {
        return bak_file;
    }

    public static String getError_file() {
        return error_file;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public static void main(String[] args) {
        new ApiDataFileUtils("ding","testo").bakErrorFile("123","000");
    }
}
