package com.intramirror.common.enums;

/**
 * Created by dingyifan on 2017/7/17.
 * price_change_rule 表相关枚举
 */
public class PriceChangeRuleEnum {

    public static enum PriceType {
        SUPPLY_PRICE(1,"supply price"),
        SALE_PRICE(2,"sale price"),
        IM_PRICE(3,"im price");
        private Integer code;
        private String value;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        PriceType(Integer code, String value) {
            this.code = code;
            this.value = value;
        }
    }

    public static enum Status {
        PENDING(1,"Pending (once the rule is created, it is Pending), user is able to Edit this rule"),
        ACTIVE(2,"Active (once the rule take effect, it is then Active), user is NOT able to Edit this rule"),
        IN_ACTIVE(3,"Inactive (once there is another Active rule, this rule is Inactive), user is NOT able to Edit this rule");

        private int code;
        private String value;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        Status(int code, String value) {
            this.code = code;
            this.value = value;
        }
    }
}
