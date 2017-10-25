package com.intramirror.web.Exception;

/**
 * Created on 3/24/2017.
 *
 * @author YouFeng.Zhu
 */
public class ErrorResponse {

    private int code;
    private String message;
    private String detail;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    private static class ErrorResponseBuilder {

    }

}
