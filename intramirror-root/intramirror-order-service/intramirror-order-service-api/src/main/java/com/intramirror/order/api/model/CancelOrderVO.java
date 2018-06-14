package com.intramirror.order.api.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by caowei on 2018/3/6.
 */
public class CancelOrderVO {

    private Long product_id;
    private String cover_img;
    private String brandID;
    private String colorCode;
    private String size;
    private String brandName;
    private String name;
    private Object price;
    private String supply_price_discount;
    private Object in_price;
    private String order_line_num;
    private Date created_at;
    private Date cancel_at;
    private BigDecimal tax_fee;

    public BigDecimal getTax_fee() {
        return tax_fee;
    }

    public void setTax_fee(BigDecimal tax_fee) {
        this.tax_fee = tax_fee;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupply_price_discount() {
        return supply_price_discount;
    }

    public void setSupply_price_discount(String supply_price_discount) {
        this.supply_price_discount = supply_price_discount;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public Object getIn_price() {
        return in_price;
    }

    public void setIn_price(Object in_price) {
        this.in_price = in_price;
    }

    public String getOrder_line_num() {
        return order_line_num;
    }

    public void setOrder_line_num(String order_line_num) {
        this.order_line_num = order_line_num;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getCancel_at() {
        return cancel_at;
    }

    public void setCancel_at(Date cancel_at) {
        this.cancel_at = cancel_at;
    }
}
