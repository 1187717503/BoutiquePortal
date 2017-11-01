package com.intramirror.web.Exception;

import com.intramirror.web.common.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.intramirror.web.Exception.StandardExceptions.HttpCommonException;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
@ControllerAdvice
public class ExceptionController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    //    @ExceptionHandler(DuplicateKeyException.class)
    //    public ResponseEntity<Void> handleDuplicateKeyException(DuplicateKeyException e) {
    //        LOGGER.debug("Fail to create , DuplicateKeyException : {}", e.getMessage());
    //        return ResponseEntity.unprocessableEntity().build();
    //    }

    //    @ExceptionHandler(HttpSessionRequiredException.class)
    //    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    //    public void handleHttpSessionRequiredException(HttpSessionRequiredException e) {
    //        LOGGER.debug("Session expired , HttpSessionRequiredException : {}", e.getMessage());
    //    }
    @ExceptionHandler(ValidateException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(ValidateException e) {
        LOGGER.warn("Validate Exception: {}", e.getErrorResponse().getMessage());
        return e.getErrorResponse();
    }

    @ExceptionHandler(HttpCommonException.class)
    public ResponseEntity handleStandardException(HttpCommonException e) {
        LOGGER.warn("Validate Exception: {}", e);
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        LOGGER.error("Unexcepted exception: \n", e);
        ErrorResponse errorResponse = new ErrorResponse(StatusCode.FAILURE, "Unexcepted exception");
        errorResponse.setDetail(e.getMessage());
        return errorResponse;
    }
}
