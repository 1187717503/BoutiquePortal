package com.intramirror.web.Exception;

/**
 * Created on 3/24/2017.
 *
 * @author eyoufzh
 */
public final class ValidateException extends RuntimeException {
    private ErrorResponse errorResponse;

    public ValidateException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

}
