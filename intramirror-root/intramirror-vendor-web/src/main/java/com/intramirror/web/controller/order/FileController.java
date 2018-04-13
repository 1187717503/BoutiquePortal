package com.intramirror.web.controller.order;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.core.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pk.shoplus.common.FileUploadHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 2018/4/13.
 */
@CrossOrigin
@Controller
@RequestMapping("/file")
public class FileController {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @RequestMapping(value = "/uploadCancelFile", method = RequestMethod.POST)
    @ResponseBody
    public Response uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new ValidateException(new ErrorResponse(StatusType.PARAM_EMPTY_OR_NULL, "File is empty."));
        }
        String fileName = file.getOriginalFilename();
        try {
            String url = FileUploadHelper.uploadFile(file.getInputStream(), file.getContentType(), file.getSize());
            LOGGER.info("Upload file successful. URL: [{}]", url);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", fileName);

            return Response.status(StatusType.SUCCESS).data(result);
        } catch (IOException e) {
            throw new ValidateException(new ErrorResponse(e.getMessage()));
        }
    }
}
