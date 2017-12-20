package com.intramirror.web.util;

import com.intramirror.common.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import pk.shoplus.common.utils.FileUtils;
import pk.shoplus.util.ExceptionUtils;

import java.io.*;
import java.util.Date;

/**
 * Created by dingyifan on 2017/9/15.
 */
public class ApiDataFileUtils {

    private static final Logger logger = Logger.getLogger(ApiDataFileUtils.class);

//    public static final String baseUrl = "/mnt2";

    public static final String baseUrl = "/Users/dingyifan/Documents/fileTest";

    public static final String bak_file = "backup";

    public static final String error_file = "error";

    private String vendorName; // 不同vendor名称

    private String eventName; // 处理事件名称

    private String fileName ;

    public ApiDataFileUtils(String vendorName,String eventName) {
        this.vendorName = vendorName;
        this.eventName = eventName;
    }

    public String bakPendingFile(String fileName, String content){
        long start = System.currentTimeMillis();
        // 获取文件夹路径
        String strDate = DateUtils.getStrDate(new Date(),"yyyyMMdd");
        String bakUrl = baseUrl+"/"+vendorName+"/"+bak_file+"/"+eventName+"/"+strDate;
        String url = this.writeFile(bakUrl,fileName,content);
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,ApiDataFileUtils_bakPendingFile,start:"+start+",end:"+end+",time:"+(end-start));
        return url;
    }

    public String bakErrorFile(String fileName, String content){
        long start = System.currentTimeMillis();
        // 获取文件夹路径
        String strDate = DateUtils.getStrDate(new Date(),"yyyyMMdd");
        String bakUrl = baseUrl+"/"+vendorName+"/"+error_file+"/"+eventName+"/"+strDate;
        String url = this.writeFile(bakUrl,fileName,content);
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,ApiDataFileUtils_bakErrorFile,start:"+start+",end:"+end+",time:"+(end-start));
        return url;
    }

    private String writeFile(String bakUrl,String fileName, String content) {
        try {
            // 校验参数
            if(StringUtils.isBlank(fileName) || StringUtils.isBlank(content)) {
                logger.info("ApiDataFileUtilsBakFile,checkParams,fileName:"+fileName+",content:"+content);
                return "fileName_or_content_error";
            }

            // 检查文件夹是否创建
            File fileFolder = new File(bakUrl);
            if(!fileFolder.exists()) {
                fileFolder.mkdirs();
                logger.info("ApiDataFileUtilsBakFile,createNewFile,bakUrl:"+bakUrl);
            }

            // 创建文件
//            String formatDate = DateUtils.getStrDate(new Date(),"yyyyMMddHHmmss");
            String formatDate = RandomUtils.getDateRandom();
            fileName = fileName + "" + formatDate;
            this.setFileName(fileName);
            String fileUrl = bakUrl+"/"+fileName+".txt";
            FileUtils.serialize(content,fileUrl);
             this.writeFileContent(fileUrl,content);
             return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiDataFileUtilsBakFile,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",fileName:"+fileName+",content:"+content);
        }
        return "writeFile_error";
    }

    private String writeFileContent(String fileUrl,String content) throws IOException {
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
        return fileUrl;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ApiDataFileUtils() {
    }

    public static void main(String[] args) {

    }
}
