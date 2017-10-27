package com.intramirror.web.Exception;

/**
 * Created on 3/24/2017.
 *
 * @author YouFeng.Zhu
 */
public class ErrorResponse {

    private int status;
    private String message;
    private String detail;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(String message) {
        this.status = -1;
        this.message = message;
    }

    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
