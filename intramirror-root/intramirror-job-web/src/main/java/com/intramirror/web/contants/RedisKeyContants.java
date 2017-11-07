package com.intramirror.web.contants;

/**
 * Created by dingyifan on 2017/9/15.
 */
public class RedisKeyContants {

    /** nugnes增量更新商品记录的offset */
    public static final String nugnes_product_offset = "nugnes_product_offset";

    /** nugnes增量更新当日商品记录的offset */
    public static final String nugnes_product_day_offset = "nugnes_product_day_offset";

    /** baseblu增量更新商品记录的offset */
    public static final String baseblu_product_offset = "baseblu_product_offset";

    /** baseblu增量更新当日商品记录的offset */
    public static final String baseblu_product_day_offset = "baseblu_product_day_offset";

    /** apartment 增量更新当日商品记录的startIndex */
    public static final String apartment_start_index_bydate = "apartment_start_index_bydate";

    /** apartment 增量更新当日商品记录的endIndex */
    public static final String apartment_end_index_bydate = "apartment_end_index_bydate";

    /** atelier全量更新商品库存置零的表识 */
    public static final String atelier_all_product_zero = "atelier_all_product_zero";

    /** Coltori Token */
    public static final String ct_token_url = "ct_token_url";
}
