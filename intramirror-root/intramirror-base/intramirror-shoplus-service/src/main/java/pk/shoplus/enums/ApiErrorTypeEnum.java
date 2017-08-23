package pk.shoplus.enums;

/**
 * Created by dingyifan on 2017/8/7.
 */
public class ApiErrorTypeEnum {

    public static enum errorType{
        brand_name_is_null("brand_name_is_null","接口传递过来的brand_name为null。"),

        colorCode_is_null("colorCode_is_null","接口传递过来的colorCode为null。"),
        brandID_is_null("brandID_is_null","接口传递过来的BrandID为null。"),
        coverImage_is_null("coverImage_is_null","接口传递过来的coverImage为null。"),
        descImage_is_null("descImage_is_null","接口传递过来的descImage为null。"),
        weight_is_null("weight_is_null","接口传递过来的weight为null。"),

        boutique_id_already_exist("boutique_id_already_exist","查询到这个boutique_id。"),
        boutique_name_is_null("boutique_name_is_null","接口传递过来的boutique_name为null。"),
        boutique_id_is_null("boutique_id_is_null","接口传递过来的boutique_id为null。"),
        boutique_create_error("boutique_create_error","创建product表记录失败。"),
//        productInfo_create_error("productInfo_create_error","创建product_info表记录失败。"),

        skuSize_is_null("skuSize_is_null","接口传递过来的skuSize为null。"),
        skuCode_is_null("skuCode_is_null","接口传递过来的skuCode为null。"),
        skuStock_is_null("skuStock_is_null","接口传递过来的skuStock为null。"),

        vendor_is_null("vendor_is_null","接口传递过来的vendor_id查不到。"),


        stock_is_null("stock_is_null","接口传递过来的stock为null。"),
        updateStock_params_is_error("updateStock_params_is_error","修改库存时入参传递有误。"),

// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
        warning_price_out_of_range("warning_price_out_of_range","价格不能超过正负5%"),
        Data_can_not_be_null("Data_can_not_be_null","数据不能为null。"),

        Data_is_duplicated("Data_is_duplicated","数据发生重复。"),
        retail_price_is_zero("retail_price_is_zero","接口传递过来的retail_price为null或者为0。"),
        warning_retail_price_is_zero("warning_retail_price_is_zero","警告，接口传递过来的retail_price为null或者为0。"),

        warning_data_can_not_find_mapping("warning_data_can_not_find_mapping","警告，数据不能映射。"),
        Data_can_not_find_mapping("Data_can_not_find_mapping","数据不能映射。"),
            category_is_null("category_is_null","接口传递过来的category_id为null。"),
            category_not_exist("category_not_exist","通过映射找不到对应目录。"),
            category_is_not_three("category_is_not_three","category_id不是三级目录。"),
            brand_not_exist("brand_not_exist","通过映射找不到对应品牌。"),
            season_not_exist("season_not_exist","通过映射找不到对应season_code。"),

        Data_is_negative("Data_is_negative","数据为负数。"),
        warning_data_is_negative("warning_data_is_negative","警告，数据为负数。"),
        Data_is_not_number("Data_is_not_number","数据不是数字。"),
        warning_ColorCode_change("warning_ColorCode_change","警告，ColorCode不允许改变。"),
        warning_BrandID_change("warning_BrandID_change","警告，BrandID不允许改变。"),

        Data_not_exist("Data_not_exist","数据不能找到。"),
            boutique_id_not_exist("boutique_id_not_exist","查询不到这个boutique_id。"),

        Data_create_error("Data_create_error","数据创建错误。"),
            handle_stock_rule_error("handle_stock_rule_error","stock处理规则有误。"),

        Data_update_Error("Data_update_Error","数据修改错误。"),
        Runtime_exception("Runtime_exception","异常。"),
        Change_status_error("Change_status_error","状态机变化失败。"),
        ;
        private String code;
        private String desc;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        errorType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
