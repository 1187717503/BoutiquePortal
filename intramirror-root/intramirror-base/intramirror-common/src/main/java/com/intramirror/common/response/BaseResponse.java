package com.intramirror.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 *
 * 响应信息
 *
 * @auth:mingfly
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = -684746356686681213L;
    private static final String SUCCESS = "0";//成功
    private static final String ERROR = "1";//失败
    private String resultCode;
    private Object result;
    private String errorMsg;//错误信息

    private BaseResponse(String statusCode) {
        this.resultCode = statusCode;
    }

    public static BaseResponse success() {
        return new BaseResponse(SUCCESS);
    }

    public static BaseResponse error() {
        return new BaseResponse(ERROR);
    }

    public static BaseResponse error(String errorCode) {
        return new BaseResponse(errorCode);
    }

    public BaseResponse setResultCode(String resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public BaseResponse setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public String getResultCode() {
        return resultCode;
    }

    public <T> T getResult() {
        return (T) result;
    }

    public BaseResponse setResult(Object result) {
        this.result = result;
        return this;
    }

    @JsonIgnore
    //此注解用于属性或者方法上（最好是属性上），作用是json序列化时将java bean中的一些属性忽略掉，序列化和反序列化都受影响。
    public boolean isSuccess(){
        return SUCCESS.equals(getResultCode());
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
