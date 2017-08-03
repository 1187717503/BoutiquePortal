package pk.shoplus.model;


import java.util.List;

/**
 * product 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
public class Result {

    public String reqCode;

    public String count;

    public List<EDSProduct> items;

    public String getReqCode() {
        return reqCode;
    }

    public String getCount() {
        return count;
    }

    public List<EDSProduct> getItems() {
        return items;
    }
}
