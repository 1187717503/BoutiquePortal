package com.intramirror.web.util;

import com.intramirror.common.utils.DateUtils;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.common.utils.FileUtils;
import pk.shoplus.service.RedisService;

/**
 * Created by dingyifan on 2017/12/19.
 */
@Service
public class AtelierFileUtils {

    // logger
    private static final Logger logger = Logger.getLogger(AtelierFileUtils.class);

    // redis util
    private static RedisService redisService = RedisService.getInstance();

    // 根目录
    private static final String basePath = "/Users/dingyifan/Documents/fileTest/product";

    // 写入文件的时候存储的key
    private static final String atelier_write_index = "atelier_write_index";

    // 读取文件的时候存储的key
    private static final String atelier_read_index = "atelier_read_index";

    /**
     *  写入文件
     */
    public String write(String content) throws Exception {
        // 获取write_index
        String date = DateUtils.getStrDate(new Date(),"yyyyMMdd");
        String key = atelier_write_index+date;
        String writeValue = redisService.getKey(key);
        int writeIndex ;
        if(StringUtils.isBlank(writeValue)) {
            writeValue = "0";
        }
        writeIndex = Integer.parseInt(writeValue);
        writeIndex++;

        // 创建文件夹
        String folderPath = basePath + "/" +date;
        File fileFolder = new File(folderPath);
        if(!fileFolder.exists()) {
            fileFolder.mkdirs();
        }

        // 创建文件
        String filePath = folderPath + "/" + writeIndex + ".txt";
        String url = this.writeFileContent(filePath,content);
        redisService.put(key,writeIndex+"");
        return url;
    }

    /**
     * 读取文件
     */
    public List<String> read(int number) throws Exception {
        List<String> messages = new ArrayList<>();

        // 获取read_index
        String date = DateUtils.getStrDate(new Date(),"yyyyMMdd");
        String key = atelier_read_index+date;
        String readValue = redisService.getKey(key);
        int readIndex;
        if(StringUtils.isBlank(readValue)) {
            readValue = "1";
        }
        readIndex = Integer.parseInt(readValue);

        // 循环文件
        String folderPath = basePath + "/" + date + "/";
        for(int index = 0;index < number;index++) {
            String filePath = folderPath + readIndex + ".txt";
            File file = new File(filePath);
            if(file.exists()) {
                String body = FileUtils.file2String(file,"UTF-8");
                messages.add(body);
                readIndex ++;
            } else {
                break;
            }
        }
        redisService.put(key,readIndex+"");
        return messages;
    }

    /**
     * 写入文件内容
     */
    private String writeFileContent(String fileUrl,String content) throws Exception {
        // 创建文件
        File file = new File(fileUrl);
        if(!file.exists()) {
            file.createNewFile();
        }

        // 写入文件
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content,0,content.length());
        fileWriter.flush();
        if(fileWriter != null) {fileWriter.close();}
        return fileUrl;
    }
}
