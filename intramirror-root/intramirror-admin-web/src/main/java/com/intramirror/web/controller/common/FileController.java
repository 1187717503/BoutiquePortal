package com.intramirror.web.controller.common;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.Exception.ErrorResponse;
import com.intramirror.web.Exception.ValidateException;
import com.intramirror.web.common.response.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pk.shoplus.common.FileUploadHelper;

/**
 * Created on 2017/11/23.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/file")
public class FileController {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @PostMapping(value = "/upload")
    public Response uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new ValidateException(new ErrorResponse(StatusType.PARAM_EMPTY_OR_NULL, "File is empty."));
        }
        try {
            String url = FileUploadHelper.uploadFile(file.getInputStream(), file.getContentType(), file.getSize());
            LOGGER.info("Upload file successful. URL: [{}]", url);
            return Response.status(StatusType.SUCCESS).data(url);
        } catch (IOException e) {
            throw new ValidateException(new ErrorResponse(e.getMessage()));
        }
    }

    private static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }
}
