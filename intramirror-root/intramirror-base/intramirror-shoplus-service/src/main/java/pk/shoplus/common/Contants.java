package pk.shoplus.common;

/**
 * 基本常量定义累
 *
 * @author yfding
 */
public class Contants {
    /**
     * 退款相关(payment表process_status)
     */
    public static final String PROCESS_INIT = "INIT"; // 初始化
    public static final String PROCESS_CANCEL = "CANCEL"; // 取消
    public static final String PROCESS_SUCCESS = "SUCCESS"; // 成功
    public static final String PROCESS_FAILED = "FAILED"; // 失败

    /**
     * 订单相关(order表pay_way)
     */
    public static final String PAY_WAY_WECHAT = "1";
    public static final String PAY_WAY_BANK = "2";
    public static final String PAY_OFFLINE = "3";

    /**
     * logistics_product.status
     */
    public static final Integer LOGISTICSPRODUCT_PENDING_STATUS = 1;
    public static final Integer LOGISTICSPRODUCT_COMFIRMED_STATUS = 2;
    public static final Integer LOGISTICSPRODUCT_ORDERED_STATUS = 3;
    public static final Integer LOGISTICSPRODUCT_PAYED_STATUS = 4;
    public static final Integer LOGISTICSPRODUCT_FINISHED_STATUS = 5;
    public static final Integer LOGISTICSPRODUCT_CANCELED_STATUS = 6;
    public static final Integer LOGISTICSPRODUCT_SETTLEMENT_STATUS = 7; //
    public static final Integer LOGISTICSPRODUCT_REFUNDING_STATUS = -4; // 退款中

    /**
     * 退款查询
     */
    public static final String REFUND_QUERY_STATUS_INIT = "INIT"; // 初始状态
    public static final String REFUND_QUERY_STATUS_SUCCESS = "SUCCESS"; // 成功
    public static final String REFUND_QUERY_STATUS_FAILED = "FAILED"; // 失败

    /**
     * 是否有库存
     */
    public static final String STOCK_ALL = "0";
    public static final String STOCK_IN_STOCK = "1";
    public static final String STOCK_OUT_OF_STOCK = "2";

    /**
     * 是否上下架
     */
    public static final String SHOP_ALL = "0";
    public static final String SHOP_IN_SHOP = "1";
    public static final String NOT_IN_SHOP = "2";

    /**
     * 商品状态
     */
    public static final String ON_SALE = "0"; // 在售
    public static final String SOLD_OUT = "1"; // 售罄
    public static final String STOP_SELLING = "2"; // 停售

    /**
     * 判断Product是否包含图片
     */
    public static final String hasImage = "0";
    public static final String notHasImage = "1";

    /**
     * 判断Product是否做过修改
     */
    public static final String isModified = "0";
    public static final String notIsModified = "1";

    /**
     * 判断Product是否是实拍图
     */
    public static final String notHasImageModify = "0";
    public static final String hasImageModify = "1";

    /**
     * cloudstore的getEvents接口返回type类型
     */
    public static final int EVENTS_TYPE_0 = 0;
    public static final int EVENTS_TYPE_1 = 1;
    public static final int EVENTS_TYPE_2 = 2;
    public static final int EVENTS_TYPE_3 = 3;
    public static final int EVENTS_TYPE_4 = 4;
    public static final int EVENTS_TYPE_5 = 5;

    /**
     * stock_qty 库存绝对值, stock_qty_diff 库存差异值
     */
    public static final int STOCK_QTY = 0;
    public static final int STOCK_QTY_DIFF = 1;

    /**
     * filippo 接口
     */
    public static final String file_filippo_path = "/opt/data/filippo/";
    public static final String all_filippo = "all_filippo";
    public static final String origin_filippo = "getall_origin_filippo";
    public static final String revised_filippo = "getall_revised_filippo";
    public static final String change_filippo = "getall_change_filippo";
    public static final String origin_filippo_product = "getall_origin_filippo_product";
    public static final String revised_filippo_product = "getall_revised_filippo_product";
    public static final String file_filippo_type = ".txt";

    public static final String qty_origin_filippo = "getqty_origin_filippo";
    public static final String qty_revised_filippo = "getqty_revised_filippo";
    public static final String qty_change_filippo = "getqty_change_filippo";
    public static final String qty_origin_filippo_product = "getqty_origin_filippo_product";
    public static final String qty_revised_filippo_product = "getqty_revised_filippo_product";
    
    
    
    /**
     * Quadra 接口
     */
    public static final String quadra_file_path = "/mnt/quadra/compare/";
    public static final String quadra_file_type = ".txt";
    public static final String quadra_origin_product_day = "quadra_origin_product_day";
    public static final String quadra_revised_product_day = "quadra_revised_product_day";
    public static final String quadra_change_product_day = "quadra_change_product_day";

    


    //	public static final String download_excel_path = "D:/";
    public static final String download_excel_path = "/opt/data/shop_excel/";

    public static final String excel_suffix = ".xls";
}
