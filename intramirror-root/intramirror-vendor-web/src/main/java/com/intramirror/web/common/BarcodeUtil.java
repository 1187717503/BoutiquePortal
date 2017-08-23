package com.intramirror.web.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.google.gson.Gson;

import pk.shoplus.common.FileUploadHelper;
import pk.shoplus.common.exception.MediaStorageFailException;
import pk.shoplus.common.service.MediaStorageService;
import pk.shoplus.common.service.impl.OSSMediaStorageServiceImpl;
import pk.shoplus.common.vo.MediaStorangeRespVo;

public class BarcodeUtil {
	
    public static final String OSS_CONFIG_FILE = "/oss.properties";

    private static MediaStorageService mediaStorageService;

    private static String ossUrl;

    private static String ossBucketname;

    private static String storeFolderName;

    private static String localUrl;

    static {
        // OSS
        InputStream in = FileUploadHelper.class.getResourceAsStream(OSS_CONFIG_FILE);
        Properties props = new Properties();
        try {
            props.load(in);

            ossUrl=props.getProperty("oss.url");
            storeFolderName=props.getProperty("oss.upload");
            ossBucketname=props.getProperty("oss.bucketname");
            localUrl = props.getProperty("local.url");

            ClientConfiguration clientConfiguration = new ClientConfiguration();
//            clientConfiguration.setRequestTimeoutEnabled(true); //请求配置
            OSSClient ossClient = new OSSClient(props.getProperty("oss.end_point"), props.getProperty("oss.access_id"), props.getProperty("oss.access_key"), clientConfiguration);
            OSSMediaStorageServiceImpl ossMediaStorageService = new OSSMediaStorageServiceImpl();
            ossMediaStorageService.setOssClient(ossClient);
            ossMediaStorageService.setBucketName(ossBucketname);
            ossMediaStorageService.setOssUrl(ossUrl);
            mediaStorageService=ossMediaStorageService;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
	
    
    /**
     * 生成文件
     *
     * @param msg
     * @param path
     * @return
     */
    public static String generateFile(String msg, String path,Boolean show) {
    	path = storeFolderName +"/"+path;
//        File file = new File(path);
        MediaStorangeRespVo mediaStorangeRespVo;
		try {
			mediaStorangeRespVo=mediaStorageService.storageMediaWithPath(new ByteArrayInputStream(generate(msg,show)) ,path);
			System.out.println(new Gson().toJson(mediaStorangeRespVo));
			return mediaStorangeRespVo.getHttpUrl();
		} catch (MediaStorageFailException e1) {
			e1.printStackTrace();
		} 
//        File file = new File(path);
//        try {
//            generate(msg, new FileOutputStream(file),show);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
		return null;
       
    }
 
    /**
     * 生成字节
     *
     * @param msg
     * @return
     */
    public static byte[] generate(String msg,Boolean show) {
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        generate(msg, ous,show);
        return ous.toByteArray();
    }
 
    /**
     * 生成到流
     *
     * @param msg
     * @param ous
     */
    public static void generate(String msg, OutputStream ous,Boolean show) {
        if (StringUtils.isBlank(msg) || ous == null) {
            return;
        }
 
//        Code39Bean bean = new Code39Bean();
        Code128Bean bean = new Code128Bean();
 
        // 精细度
//        final int dpi = 200;
        final int dpi = 150;
        // module宽度
        final double moduleWidth = UnitConv.in2mm(1.0f / dpi);
 
        // 配置对象
        bean.setModuleWidth(moduleWidth);
//        bean.setWideFactor(3);
        bean.doQuietZone(true);
        if(!show){
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        
 
        String format = "image/png";
        try {
 
            // 输出到流
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);
 
            // 生成条形码
            bean.generateBarcode(canvas, msg);
 
            // 结束绘制
            canvas.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 
    public static void main(String[] args) {
        String msg = "2017081231456875";
        String path = "D:/barcode3.png";
        generateFile(msg, path,false);
    }

}
