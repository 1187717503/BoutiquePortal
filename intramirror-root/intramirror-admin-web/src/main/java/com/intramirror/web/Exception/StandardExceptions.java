package com.intramirror.web.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2017/11/1.
 *
 * @author YouFeng.Zhu
 */
public class StandardExceptions {

    public static final UnauthorizedException UNAUTHORIZED = new UnauthorizedException();

    public static final NotFoundException NOT_FOUND = new NotFoundException();

    public static final AlreadyExistException ALREADYEXIST = new AlreadyExistException();

    static class HttpCommonException extends RuntimeException {
        private HttpStatus httpStatus;

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }

        public void setHttpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        HttpCommonException(String message) {
            super(message);
        }

    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Resource already exist")
    static class AlreadyExistException extends HttpCommonException {// 422

        AlreadyExistException() {
            super("Resource already exist");
            super.setHttpStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Not login or session expired")
    static class UnauthorizedException extends HttpCommonException {// 422

        UnauthorizedException() {
            super("Not login or session expired");
            super.setHttpStatus(HttpStatus.UNAUTHORIZED);

        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    static class NotFoundException extends HttpCommonException {// 404

        NotFoundException() {
            super("Resource not found");
            super.setHttpStatus(HttpStatus.NOT_FOUND);
        }
    }
}
