package com.intramirror.web.controller.order;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.core.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pk.shoplus.common.FileUploadHelper;

import java.io.IOException;

/**
 * Created by 123 on 2018/4/13.
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @PostMapping(value = "/uploadCancelFile")
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
}
