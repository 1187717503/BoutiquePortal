package com.intramirror.web.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.parameter.StatusType;

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
    public ErrorResponse handleValidateException(ValidateException e) {
        LOGGER.error("Validate Exception: {}", e.getErrorResponse().getMessage());
        return e.getErrorResponse();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorResponse handleException(Exception e) {
        LOGGER.error("Unexcepted exception: \n", e);
        return new ErrorResponse(StatusType.FAILURE, e.getMessage());
    }
}
