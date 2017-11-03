package com.intramirror.web.common.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/11/1.
 *
 * @author YouFeng.Zhu
 */
public class BatchResponseMessage {
    private List<BatchResponseItem> success = new ArrayList<>();
    private List<BatchResponseItem> failed = new ArrayList<>();

    public List<BatchResponseItem> getSuccess() {
        return success;
    }

    public void setSuccess(List<BatchResponseItem> success) {
        this.success = success;
    }

    public List<BatchResponseItem> getFailed() {
        return failed;
    }

    public void setFailed(List<BatchResponseItem> failed) {
        this.failed = failed;
    }

}
