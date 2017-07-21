package com.intramirror.common.enums;

public class SystemPropertyEnum {
    public static enum propertyName {

    	BOUTIQUE_DISCOUNT_DEFAULT("Boutique_Discount_Default","Boutique_Discount_Default"),
    	IM_DISCOUNT_DEFAULT("IM_Discount_Default","IM_Discount_Default");

        private String code;
        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        propertyName(String code, String value) {
            this.code = code;
            this.value = value;
        }
    }
}
