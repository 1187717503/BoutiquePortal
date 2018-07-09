package pk.shoplus.common;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.DateUtil;
import com.intramirror.core.net.http.OkHttpUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import okhttp3.Response;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.shoplus.common.service.MediaStorageService;
import pk.shoplus.common.service.impl.OSSMediaStorageServiceImpl;
import pk.shoplus.common.utils.FileUtils;
import pk.shoplus.common.vo.MediaStorangeRespVo;
import spark.Request;

/**
 * 文件上传
 *
 * @author mingfly
 */
public class FileUploadHelper {

    private static Logger logger = LoggerFactory.getLogger(FileUploadHelper.class);

    public static final String OSS_CONFIG_FILE = "/oss.properties";

    public static final int maxSize = 204800;

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

            ossUrl = props.getProperty("oss.url");
            storeFolderName = props.getProperty("oss.upload");
            ossBucketname = props.getProperty("oss.bucketname");
            localUrl = props.getProperty("local.url");

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            //            clientConfiguration.setRequestTimeoutEnabled(true); //请求配置
            OSSClient ossClient = new OSSClient(props.getProperty("oss.end_point"), props.getProperty("oss.access_id"), props.getProperty("oss.access_key"),
                    clientConfiguration);
            OSSMediaStorageServiceImpl ossMediaStorageService = new OSSMediaStorageServiceImpl();
            ossMediaStorageService.setOssClient(ossClient);
            ossMediaStorageService.setBucketName(ossBucketname);
            ossMediaStorageService.setOssUrl(ossUrl);
            mediaStorageService = ossMediaStorageService;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * hepler for file/image upload
     *
     * @param req
     */
    public static void setFileStoreAttr(Request req) {
        MultipartConfigElement config = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.attribute("org.eclipse.jetty.multipartConfig", config);
    }

    /**
     * helper for file/image upload
     *
     * @param req
     * @return
     */
    public static Collection<Part> getValidParts(Request req) {
        logger.info("getValidParts start");
        Collection<Part> parts = new ArrayList<Part>();
        try {
            for (Part part : req.raw().getParts()) {
                ApplicationPart appPart = (ApplicationPart) part;
                String fileName = appPart.getSubmittedFileName();
                logger.info("fileName" + fileName);
                if (fileName != null && !fileName.isEmpty()) {
                    parts.add(part);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parts;
    }

    /**
     * Upload from request stream
     */
    public static List<String> uploadS3File(Request req) throws Exception {
        logger.info("start uploadS3File ");
        List<String> fileUrls = new ArrayList<String>();
        setFileStoreAttr(req);
        InputStream inputStream;
        Collection<Part> parts = getValidParts(req);
        logger.info("parts size " + parts.size());
        MediaStorangeRespVo mediaStorangeRespVo;
        if (parts.size() > 0) {
            for (Part part : parts) {
                String path = storeFolderName + '/' + UUID.randomUUID();
                String fileName = part.getName();
                logger.info("part:" + part.getContentType());
                inputStream = part.getInputStream();
                logger.info("part size : " + part.getSize());
                if (part.getContentType().contains("image") && part.getSize() > maxSize) {
                    //                    inputStream=compressImage(part.getInputStream(), 0.5f);
                    inputStream = ImageHelper.compressImage(part.getInputStream(), 0.5f);
                }
                logger.info("path:" + path);
                mediaStorangeRespVo = mediaStorageService.storageMediaWithPath(inputStream, path);
                fileUrls.add(mediaStorangeRespVo.getHttpUrl());
                fileUrls.add(fileName);
            }
        }
        return fileUrls;
    }

    public static String uploadFile(InputStream in, String contentType, Long contentLength) {
        String path = storeFolderName + '/' + UUID.randomUUID();
        InputStream inputStream = in;
        if (contentType.contains("image") && contentLength > maxSize) {
            inputStream = ImageHelper.compressImage(inputStream, 0.5f);
        }
        MediaStorangeRespVo mediaStorangeRespVo = mediaStorageService.storageMediaWithPath(inputStream, path);
        if (mediaStorangeRespVo == null) {
            return null;
        }
        return mediaStorangeRespVo.getHttpUrl();
    }

    public static List<String> uploadFileByImgUrl(String imgUrl) throws Exception {
        HttpURLConnection httpUrl = null;
        InputStream inputStream = null;
        URL url = null;
        List<String> fileUrls = new ArrayList<String>();
        MediaStorangeRespVo mediaStorangeRespVo;

        try {
            imgUrl = imgUrl.replace("https:", "http:");
            url = new URL(imgUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            inputStream = httpUrl.getInputStream();
            if (null != inputStream) {
                String path = storeFolderName + '/' + UUID.randomUUID();
                String fileName = DateUtil.formatAlternativeIso8601Date(new Date());
                mediaStorangeRespVo = mediaStorageService.storageMediaWithPath(inputStream, path);
                fileUrls.add(mediaStorangeRespVo.getHttpUrl());
                fileUrls.add(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpUrl.disconnect();
            inputStream.close();
        }

        return fileUrls;
    }

    public static List<String> uploadFileByImgUrl2(String imgUrl) throws Exception {
        long start = System.currentTimeMillis();
        HttpURLConnection httpUrl = null;
        InputStream inputStream = null;
        URL url = null;
        List<String> fileUrls = new ArrayList<String>();
        MediaStorangeRespVo mediaStorangeRespVo;

        try {

            if (imgUrl.startsWith("https:")) {
                Response response = OkHttpUtils.get().url(imgUrl).build().execute();
                inputStream = response.body().byteStream();
            } else {
                url = new URL(imgUrl);
                httpUrl = (HttpURLConnection) url.openConnection();
                httpUrl.setConnectTimeout(5 * 1000);
                httpUrl.setReadTimeout(5 * 1000);
                httpUrl.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:37.0) Gecko/20100101 Firefox/37.0");
                inputStream = httpUrl.getInputStream();
            }
            if (null != inputStream) {
                String path = storeFolderName + '/' + UUID.randomUUID();
                String fileName = DateUtil.formatAlternativeIso8601Date(new Date());
                mediaStorangeRespVo = mediaStorageService.storageMediaWithPath(inputStream, path);
                fileUrls.add(mediaStorangeRespVo.getHttpUrl());
                fileUrls.add(fileName);
            }
        } catch (IOException e) {
            fileUrls = new ArrayList<>();
            e.printStackTrace();
            logger.info("uploadFileByImgUrl2,e:" + e + ",imageUrl:" + imgUrl);
        } finally {
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,uploadFileByImgUrl2,start:" + start + ",end:" + end + ",time:" + (end - start));
        return fileUrls;
    }

    /**
     * compress image
     *
     * @param input
     * @param quantity
     * @return
     */
    private static InputStream compressImage(InputStream input, float quantity) {
        quantity = quantity == 0f ? 0.5f : quantity;
        logger.info("compressImage start");
        try {
            logger.info("----------------oss---------------");
            logger.info("ossUrl:" + ossUrl);
            logger.info("ossBucketname:" + ossBucketname);
            logger.info("storeFolderName:" + storeFolderName);
            logger.info("localUrl:" + localUrl);

            ImageInputStream imageInput = ImageIO.createImageInputStream(input);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            ImageReader reader = readers.next();
            BufferedImage image = ImageIO.read(imageInput);

            ImageWriter writer = ImageIO.getImageWriter(reader);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param == null) {
                logger.info("param is null");
            } else {
                logger.info(param.toString());
            }
            logger.info("ImageWriteParam.MODE_EXPLICIT" + ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quantity);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageOutputStream imageOut = ImageIO.createImageOutputStream(out);
            writer.setOutput(imageOut);
            writer.write(null, new IIOImage(image, null, null), param);

            ByteArrayInputStream result = new ByteArrayInputStream(out.toByteArray());

            imageOut.close();
            out.close();
            writer.dispose();
            logger.info("compressImage end");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return input;
    }

    /**
     * Upload from request stream
     */
    public static String uploadS3ExcelFile(Part part) throws Exception {
        String fileUrls = "";
        // setFileStoreAttr(req);
        try {
            ApplicationPart appPart = (ApplicationPart) part;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String path = storeFolderName + '/' + "excel/" + simpleDateFormat.format(new Date()) + '/';
            String uuid = UUID.randomUUID() + "";
            String fileName = appPart.getSubmittedFileName();
            path = path + uuid.substring(0, 8) + '/' + fileName;

            MediaStorangeRespVo mediaStorangeRespVo = mediaStorageService.storageMediaWithPath(part.getInputStream(), path);
            fileUrls = mediaStorangeRespVo.getHttpUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileUrls;
    }

    /**
     * Upload from request stream to local
     */
    public static String uploadS3ExcelFile(Part part, boolean isLocal) throws Exception {
        String fileUrls = "";
        // setFileStoreAttr(req);
        try {
            ApplicationPart appPart = (ApplicationPart) part;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String path = storeFolderName + '/' + "excel/" + simpleDateFormat.format(new Date()) + '/';
            String uuid = UUID.randomUUID() + "";
            String fileName = appPart.getSubmittedFileName();
            path = path + uuid.substring(0, 8) + '/' + fileName;

            if (isLocal) {
                path = localUrl + path;
                FileUtils.createFile(new File(path));
                FileUtils.writeByte(path, FileUtils.toBytes(part.getInputStream()));
                fileUrls = path;
            } else {
                MediaStorangeRespVo mediaStorangeRespVo = mediaStorageService.storageMediaWithPath(part.getInputStream(), path);
                fileUrls = mediaStorangeRespVo.getHttpUrl();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileUrls;
    }

    /**
     * Get file access url
     *
     * @param key
     * @return
     */
    public static String getS3FilePath(String key) {
        return MessageFormat.format(ossUrl, key);
    }

    public static String getOssBucketname() {
        return ossBucketname;
    }

    public static String getStoreFolderName() {
        return storeFolderName;
    }

    public static void setStoreFolderName(String storeFolderName) {
        FileUploadHelper.storeFolderName = storeFolderName;
    }

}
