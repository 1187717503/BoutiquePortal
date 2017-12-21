package com.intramirror.core.common.exception;

import com.intramirror.core.common.response.ErrorResponse;

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
