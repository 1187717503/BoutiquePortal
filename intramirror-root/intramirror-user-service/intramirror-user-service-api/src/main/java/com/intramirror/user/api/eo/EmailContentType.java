package com.intramirror.user.api.eo;

import java.util.*;

public enum EmailContentType {

    Order_No(1,"Order No"),Shipping_Address(2,"Shipping Address"),Order_Date(3,"Order Date"),Product_Id(4,"Product ID"),Color(5,"Color"),
    Size(6,"Size"),Brand(7,"Brand"),Boutique_Id(8,"Boutique ID"),Product_Name(9,"Product Name"),Retail_Price(10,"Retail Price"),
    PurChase_Price(11,"PurChase Price"),Discount(12,"Discount");

    private Integer code;

    private String explain;

    EmailContentType(Integer code, String explain) {
        this.code = code;
        this.explain = explain;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public static LinkedHashMap<String,Integer> toLinkedHashMap(){
        List<EmailContentType> statusList = new ArrayList<EmailContentType>();
        for (EmailContentType item : EmailContentType.values()) {
            statusList.add(item);
        }

        Collections.sort(statusList, new Comparator<EmailContentType>() {
            @Override
            public int compare(EmailContentType arg0, EmailContentType arg1) {
                return arg0.getCode().compareTo(arg1.getCode());
            }
        });


        LinkedHashMap<String,Integer> treeMap = new LinkedHashMap<String,Integer>();
        for(EmailContentType status : statusList){
            treeMap.put(status.getExplain(), status.getCode());
        }
        return treeMap;
    }
    public static EmailContentType getEmailContentTypeByCode(Integer code){
        for(EmailContentType emailContentType : EmailContentType.values()){
            if(code==emailContentType.getCode()){
                return emailContentType;
            }
        }
        return null;
    }
}
