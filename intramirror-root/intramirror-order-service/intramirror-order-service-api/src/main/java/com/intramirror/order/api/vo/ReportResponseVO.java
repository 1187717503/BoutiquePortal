package com.intramirror.order.api.vo;

import java.util.List;

public class ReportResponseVO {
    private Integer pageSize;
    private Integer pageNum;
    List<ReportVO> reportVOS;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public List<ReportVO> getReportVOS() {
        return reportVOS;
    }

    public void setReportVOS(List<ReportVO> reportVOS) {
        this.reportVOS = reportVOS;
    }
}
