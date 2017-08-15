package com.intramirror.common.help;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/15.
 */
public class PageUtils implements Serializable {

    private List<Map<String,Object>> result; // 结果
    private Page page;
    private int total; // 总数量
    private int sumPage; // 总页数
    private IPageService iPageService;

    public PageUtils(Page page, IPageService iPageService, Map<String,Object> params) {
        this.page = page;
        this.iPageService = iPageService;

        int startPage = 0;
        int endPage = 0;

        startPage = page.getCurrentPage() * page.getPageSize() - page.getPageSize();
        endPage = page.getCurrentPage() * page.getPageSize();

        params.put("startPage",startPage);
        params.put("endPage",endPage);

        result = iPageService.getResult(params);
        total = iPageService.getCount(params);
        sumPage = (total + page.getPageSize() - 1) / page.getPageSize();
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public IPageService getiPageService() {
        return iPageService;
    }

    public void setiPageService(IPageService iPageService) {
        this.iPageService = iPageService;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSumPage() {
        return sumPage;
    }

    public void setSumPage(int sumPage) {
        this.sumPage = sumPage;
    }

    class Page {
        private int currentPage; // 当前第几页
        private int pageSize; // 每页数量

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }
}
