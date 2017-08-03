package pk.shoplus.common;
//
//package pk.shoplus.common;
//
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Properties;
//import java.util.UUID;
//
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.stream.ImageInputStream;
//import javax.imageio.stream.ImageOutputStream;
//import javax.servlet.MultipartConfigElement;
//import javax.servlet.http.Part;
//
//import org.apache.catalina.core.ApplicationPart;
//
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.regions.Region;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//
//import pk.shoplus.parameter.FileUploadConfiguration;
//import spark.Request;
//
///**
// * @author author : Jeff
// * @date create_at : 2016年11月3日 上午11:17:57
// * @version 1.0
// * @parameter
// * @since
// * @return
// */
//public class FileUploadHelper {
//
//	private static AmazonS3 s3;
//
//	private static String s3BucketName;
//
//	private static String s3Domain;
//
//	private static String storeFolderName;
//
//	static {
//		// AWS credentials
//		InputStream in = FileUploadHelper.class.getResourceAsStream(FileUploadConfiguration.S3_CONFIG_FILE);
//		Properties props = new Properties();
//		try {
//			props.load(in);
//			in.close();
//
//			BasicAWSCredentials credentials = new BasicAWSCredentials(props.getProperty("aws_access_key_id"),
//					props.getProperty("aws_secret_access_key"));
//			s3 = new AmazonS3Client(credentials);
//			s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
//			s3BucketName = props.getProperty("bucket_name");
//			s3Domain = props.getProperty("s3_domain");
//			storeFolderName = props.getProperty("folder");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * hepler for file/image upload
//	 *
//	 * @param req
//	 */
//	public static void setFileStoreAttr(Request req) {
//		MultipartConfigElement config = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
//		req.attribute("org.eclipse.jetty.multipartConfig", config);
//	}
//
//	/**
//	 * helper for file/image upload
//	 *
//	 * @param req
//	 * @return
//	 */
//	public static Collection<Part> getValidParts(Request req) {
//		Collection<Part> parts = new ArrayList<Part>();
//		try {
//			for (Part part : req.raw().getParts()) {
//				ApplicationPart appPart = (ApplicationPart) part;
//				String fileName = appPart.getSubmittedFileName();
//				if (fileName != null && !fileName.isEmpty()) {
//					parts.add(part);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return parts;
//	}
//
//	/**
//	 * Upload from request stream
//	 *
//	 */
//	public static List<String> uploadS3File(Request req) throws Exception {
//		List<String> fileUrls = new ArrayList<String>();
//		setFileStoreAttr(req);
//
//		Collection<Part> parts = getValidParts(req);
//		if (parts.size() > 0) {
//			for (Part part : parts) {
//				String path = storeFolderName + '/' + UUID.randomUUID();
//				String fileName = part.getName();
//				ObjectMetadata meta = new ObjectMetadata();
//				meta.setContentType(part.getContentType());
//				InputStream in = part.getInputStream();
//				PutObjectRequest request;
//				long size = part.getSize();
//				if (part.getContentType().contains("image")) {
//					if (size < 100000) {
//						request = new PutObjectRequest(s3BucketName, path, in, meta);
//					} else {
//						request = new PutObjectRequest(s3BucketName, path, compressImage(in, 0.5f), meta);
//					}
//				} else {
//					request = new PutObjectRequest(s3BucketName, path, in, meta);
//				}
//
//				request.withCannedAcl(CannedAccessControlList.PublicRead);
//				s3.putObject(request);
//				fileUrls.add(FileUploadHelper.getS3FilePath(path));
//				fileUrls.add(fileName);
//			}
//		}
//
//		return fileUrls;
//	}
//
//	/**
//	 * compress image
//	 *
//	 * @param input
//	 * @param quantity
//	 * @return
//	 */
//	private static InputStream compressImage(InputStream input, float quantity) {
//		quantity = quantity == 0f ? 0.5f : quantity;
//
//		try {
//			ImageInputStream imageInput = ImageIO.createImageInputStream(input);
//			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
//			ImageReader reader = readers.next();
//			BufferedImage image = ImageIO.read(imageInput);
//
//			ImageWriter writer = ImageIO.getImageWriter(reader);
//			ImageWriteParam param = writer.getDefaultWriteParam();
//			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//			param.setCompressionQuality(quantity);
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			ImageOutputStream imageOut = ImageIO.createImageOutputStream(out);
//			writer.setOutput(imageOut);
//			writer.write(null, new IIOImage(image, null, null), param);
//
//			ByteArrayInputStream result = new ByteArrayInputStream(out.toByteArray());
//
//			imageOut.close();
//			out.close();
//			writer.dispose();
//
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return input;
//	}
//
//	/**
//	 * Upload from request stream
//	 *
//	 */
//	public static String uploadS3ExcelFile(Part part) throws Exception {
//		String fileUrls = "";
//		// setFileStoreAttr(req);
//		try {
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//			String path = storeFolderName + '/' + "excel/" + simpleDateFormat.format(new Date()) + '/';
//			String uuid = UUID.randomUUID() + "";
//			ApplicationPart appPart = (ApplicationPart) part;
//			String fileName = appPart.getSubmittedFileName();
//			path = path + uuid.substring(0, 8) + '/' + fileName;
//			ObjectMetadata meta = new ObjectMetadata();
//			meta.setContentType(part.getContentType());
//			InputStream in = part.getInputStream();
//			PutObjectRequest request;
//			// long size = part.getSize();
//			request = new PutObjectRequest(s3BucketName, path, in, meta);
//
//			request.withCannedAcl(CannedAccessControlList.PublicRead);
//			s3.putObject(request);
//			fileUrls = FileUploadHelper.getS3FilePath(path);
//			// fileUrls.add(Helper.getS3FilePath(path));
//			// fileUrls.add(fileName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return fileUrls;
//	}
//
//	/**
//	 * Get file access url
//	 *
//	 * @param key
//	 * @return
//	 */
//	public static String getS3FilePath(String key) {
//		return s3Domain + '/' + key;
//	}
//
//	public static String getS3BucketName() {
//		return s3BucketName;
//	}
//
//	public static String getStoreFolderName() {
//		return storeFolderName;
//	}
//
//	public static void setStoreFolderName(String storeFolderName) {
//		FileUploadHelper.storeFolderName = storeFolderName;
//	}
//
//}
