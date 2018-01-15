package com.intramirror.product.api.exception;

/**
 * Created on 2018/1/12.
 * @author 123
 */
public class BusinessException extends Exception {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

}
