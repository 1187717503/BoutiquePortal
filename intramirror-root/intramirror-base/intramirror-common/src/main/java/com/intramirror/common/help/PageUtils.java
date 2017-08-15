package com.intramirror.common.help;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/15.
 */
public class PageUtils {

    private List<Map<String,Object>> result; // 结果
    private Page page;
    private int total; // 总数量
    private int sumPage; // 总页数

    public PageUtils(Page page, IPageService iPageService, Map<String,Object> params) {
        this.page = page;

        int startPage = 0;
//        int endPage = 0;

        startPage = page.getCurrentPage() * page.getPageSize() - page.getPageSize();
//        endPage = page.getCurrentPage() * page.getPageSize();

        params.put("startPage",startPage);
        params.put("endPage",page.getPageSize());

        result = iPageService.getResult(params);
        params.put("count","1");
        List<Map<String,Object>> list = iPageService.getResult(params);
        if(list != null && list.size() > 0) {
            total = Integer.parseInt(list.get(0).get("count").toString());
        }
        sumPage = (total + page.getPageSize() - 1) / page.getPageSize();
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
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

}
