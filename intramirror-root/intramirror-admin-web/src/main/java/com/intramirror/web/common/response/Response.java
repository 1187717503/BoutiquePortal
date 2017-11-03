package com.intramirror.web.common.response;

import com.intramirror.web.common.StatusCode;

/**
 * Created on 2017/10/26.
 *
 * @author YouFeng.Zhu
 */
public class Response {
    private int status;

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Response(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public Response(int status, Object data) {
        this.status = status;
        this.data = data;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static Response.ResponseBuilder status(int status) {
        return new Response.ResponseBuilder(status);
    }

    public static Response success() {
        return new Response.ResponseBuilder(StatusCode.SUCCESS).build();
    }

    public static class ResponseBuilder {

        private final int status;

        public ResponseBuilder(int status) {
            this.status = status;
        }

        public Response build() {
            return data(null);
        }

        public Response data(Object data) {
            return new Response(this.status, data);
        }
    }

    @Override
    public String toString() {
        return "Response{" + "status=" + status + ", data=" + data + '}';
    }

}
