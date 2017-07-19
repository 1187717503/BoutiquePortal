package com.intramirror.common.help;

import com.intramirror.common.parameter.StatusType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/17.
 */
public class ResultMessage {
    private int status;
    private Object data;
    private String msg;
    private Map<String,Object> infoMap;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultMessage addMsg(String msg){
        this.msg = (this.msg == null ? "" : this.msg) + msg + ";";
        return this;
    }

    public ResultMessage putMsg(String key,String value){
        if(this.infoMap == null) {
            this.infoMap = new HashMap<>();
        }
        this.infoMap.put(key,value);
        return this;
    }

    public static ResultMessage getInstance(){
        return new ResultMessage();
    }

    public ResultMessage errorStatus(){
        this.status = StatusType.FAILURE;
        return this;
    }

    public ResultMessage successStatus(){
        this.status = StatusType.SUCCESS;
        return this;
    }
}
