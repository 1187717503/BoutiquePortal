package com.intramirror.order.api.vo;

import java.util.List;

/**
 * Created by caowei on 2018/3/6.
 */
public class PageListVO {
    private Integer total; //总记录数
    private Integer pageTotal; //总页数
    private List data; //数据
    private Integer currPageNo = 1; //当前页
    private Integer pageSize = 10; //页面大小

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public Integer getCurrPageNo() {
        return currPageNo;
    }

    public void setCurrPageNo(Integer currPageNo) {
        this.currPageNo = currPageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public PageListVO(List data){
        total = data.size();
    }

    public void calPageTotal(){
        if(total % pageSize != 0){
            pageTotal = total / pageSize + 1;
        }else {
            pageTotal = total / pageSize;
        }

    }

}
