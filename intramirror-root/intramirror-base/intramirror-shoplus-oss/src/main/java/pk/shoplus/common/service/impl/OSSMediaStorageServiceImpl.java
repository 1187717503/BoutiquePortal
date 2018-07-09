package pk.shoplus.common.service.impl;

import pk.shoplus.common.utils.FileUtils;
import com.aliyun.oss.*;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import pk.shoplus.common.exception.MediaStorageFailException;
import pk.shoplus.common.service.MediaStorageService;
import pk.shoplus.common.utils.FileStrategyUtils;
import pk.shoplus.common.vo.MediaStorangeRespVo;
import pk.shoplus.common.vo.StrategyVo;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * Created by zhuheng on 2014/12/3.
 */
public class OSSMediaStorageServiceImpl implements MediaStorageService {

    private static Logger logger = LoggerFactory.getLogger(OSSMediaStorageServiceImpl.class);

    private String bucketName;

    private OSSClient ossClient;

    private String ossUrl;

    static {
        //注册类型
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }


    @Override
    public MediaStorangeRespVo storageMedia(InputStream mediaInputStream, String mediaPath) throws MediaStorageFailException {
        String filePath = uploadFile(ossClient, bucketName, FileUtils.toBytes(mediaInputStream), mediaPath, null,null);
        String url = MessageFormat.format(ossUrl, filePath);
        return new MediaStorangeRespVo(filePath, url);
    }


    @Override
    public MediaStorangeRespVo storageMedia(InputStream mediaInputStream, String mediaPath, String strategy) throws MediaStorageFailException {
        StrategyVo strategyVo = FileStrategyUtils.parse(strategy);
        if (null == strategyVo) {
            //服务
            return storageMedia(mediaInputStream, mediaPath);
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //对图片进行裁剪
            Thumbnails.of(mediaInputStream).size(strategyVo.getWidth(), strategyVo.getHeight()).outputQuality(strategyVo.getQuality()).toOutputStream(byteArrayOutputStream);

            String filePath = uploadFile(ossClient, bucketName, byteArrayOutputStream.toByteArray(), mediaPath, strategy,null);
            String url = MessageFormat.format(ossUrl, filePath);
            return new MediaStorangeRespVo(filePath, url);
        } catch (IOException e) {
            logger.error("上传图片失败");
            throw new MediaStorageFailException(e);
        }

    }

    @Override
    public MediaStorangeRespVo storageMediaWithPath(InputStream mediaInputStream, String mediaPath) throws MediaStorageFailException {
        String filePath = uploadFile(ossClient, bucketName, FileUtils.toBytes(mediaInputStream), null, null,mediaPath);
        String url = MessageFormat.format(ossUrl, filePath);
        return new MediaStorangeRespVo(filePath, url);
    }

    // 创建Bucket
    private static void ensureBucket(OSSClient client, String bucketName) throws MediaStorageFailException {
        try {
            // 创建bucket
            client.createBucket(bucketName);
        } catch (ServiceException e) {
            if (!OSSErrorCode.BUCKET_ALREADY_EXISTS.equals(e.getErrorCode())) {
                // 如果Bucket已经存在，则忽略
                throw new MediaStorageFailException(e);
            }
        }
    }

    // 把Bucket设置为所有人可读
    private static void setBucketPublicReadable(OSSClient client, String bucketName) throws MediaStorageFailException {
        try {
            //创建bucket
            ensureBucket(client,bucketName);
            //设置bucket的访问权限，public-read-write权限
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        } catch (OSSException | ClientException e) {
//            throw new MediaStorageFailException(e);
            logger.warn("创建Bucket失败！");
        }

    }

    // 上传文件
    private static String uploadFile(OSSClient ossClient, String bucketName, byte[] fileBytes, String mediaOwnerType, String strategy,String fileName) throws MediaStorageFailException {
        setBucketPublicReadable(ossClient, bucketName);

        MimeType mimeType = (MimeType) MimeUtil.getMimeTypes(fileBytes).iterator().next();
        //文件元素信息
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(fileBytes.length);
        // 可以在metadata中标记文件类型
        objectMeta.setContentType(mimeType.getMediaType());
        if (StringUtils.isBlank(fileName)){
            fileName = FileUtils.randomFileName(mediaOwnerType, strategy, mimeType);
        }
        ossClient.putObject(bucketName, fileName, new ByteArrayInputStream(fileBytes), objectMeta);
        return fileName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setOssClient(OSSClient ossClient) {
        this.ossClient = ossClient;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }
}
