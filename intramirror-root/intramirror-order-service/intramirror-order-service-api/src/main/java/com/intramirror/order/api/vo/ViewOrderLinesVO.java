package com.intramirror.order.api.vo;

import java.math.BigDecimal;

/**
 * Created by 123 on 2018/5/22.
 */
public class ViewOrderLinesVO {
    private String order_line_num;
    private String order_num;
    private BigDecimal current_rate;
    private BigDecimal shipping_fee;
    private String created_at_datetime;
    private String confirmed_at_datetime;
    private String packed_at_datetime;
    private String shipped_at_datetime;
    private String qty;
    private String vendor_name;
    private String brand;
    private String l1_category;
    private String l2_category;
    private String l3_category;
    private String designer_id;
    private String color_code;
    private String product_name;
    private String size;
    private BigDecimal boutique_price;
    private BigDecimal retail_price;
    private String stock_location;
    private String buyer_name;
    private String buyer_contact;
    private String consignee;
    private String consignee_mobile;
    private String ship_to_country;
    private String ship_to_province;
    private String ship_to_area;
    private String ship_to_city;
    private String ship_to_address;
    private String zip_code;
    private String container_nr;
    private String height;
    private String length;
    private String width;
    private String weight;
    private String shipment_nr;
    private String shipment_status;
    private String awb_nbr;
    private String channel;
    private BigDecimal salePrice;

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private Integer sortingType;

    public Integer getSortingType() {
        return sortingType;
    }

    public void setSortingType(Integer sortingType) {
        this.sortingType = sortingType;
    }

    public String getOrder_line_num() {
        return order_line_num;
    }

    public void setOrder_line_num(String order_line_num) {
        this.order_line_num = order_line_num;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public BigDecimal getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(BigDecimal shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public BigDecimal getCurrent_rate() {
        return current_rate;
    }

    public void setCurrent_rate(BigDecimal current_rate) {
        this.current_rate = current_rate;
    }

    public String getCreated_at_datetime() {
        return created_at_datetime;
    }

    public void setCreated_at_datetime(String created_at_datetime) {
        this.created_at_datetime = created_at_datetime;
    }

    public String getConfirmed_at_datetime() {
        return confirmed_at_datetime;
    }

    public void setConfirmed_at_datetime(String confirmed_at_datetime) {
        this.confirmed_at_datetime = confirmed_at_datetime;
    }

    public String getPacked_at_datetime() {
        return packed_at_datetime;
    }

    public void setPacked_at_datetime(String packed_at_datetime) {
        this.packed_at_datetime = packed_at_datetime;
    }

    public String getShipped_at_datetime() {
        return shipped_at_datetime;
    }

    public void setShipped_at_datetime(String shipped_at_datetime) {
        this.shipped_at_datetime = shipped_at_datetime;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getL1_category() {
        return l1_category;
    }

    public void setL1_category(String l1_category) {
        this.l1_category = l1_category;
    }

    public String getL2_category() {
        return l2_category;
    }

    public void setL2_category(String l2_category) {
        this.l2_category = l2_category;
    }

    public String getL3_category() {
        return l3_category;
    }

    public void setL3_category(String l3_category) {
        this.l3_category = l3_category;
    }

    public String getDesigner_id() {
        return designer_id;
    }

    public void setDesigner_id(String designer_id) {
        this.designer_id = designer_id;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getBoutique_price() {
        return boutique_price;
    }

    public void setBoutique_price(BigDecimal boutique_price) {
        this.boutique_price = boutique_price;
    }

    public BigDecimal getRetail_price() {
        return retail_price;
    }

    public void setRetail_price(BigDecimal retail_price) {
        this.retail_price = retail_price;
    }

    public String getStock_location() {
        return stock_location;
    }

    public void setStock_location(String stock_location) {
        this.stock_location = stock_location;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public String getBuyer_contact() {
        return buyer_contact;
    }

    public void setBuyer_contact(String buyer_contact) {
        this.buyer_contact = buyer_contact;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsignee_mobile() {
        return consignee_mobile;
    }

    public void setConsignee_mobile(String consignee_mobile) {
        this.consignee_mobile = consignee_mobile;
    }

    public String getShip_to_country() {
        return ship_to_country;
    }

    public void setShip_to_country(String ship_to_country) {
        this.ship_to_country = ship_to_country;
    }

    public String getShip_to_province() {
        return ship_to_province;
    }

    public void setShip_to_province(String ship_to_province) {
        this.ship_to_province = ship_to_province;
    }

    public String getShip_to_area() {
        return ship_to_area;
    }

    public void setShip_to_area(String ship_to_area) {
        this.ship_to_area = ship_to_area;
    }

    public String getShip_to_city() {
        return ship_to_city;
    }

    public void setShip_to_city(String ship_to_city) {
        this.ship_to_city = ship_to_city;
    }

    public String getShip_to_address() {
        return ship_to_address;
    }

    public void setShip_to_address(String ship_to_address) {
        this.ship_to_address = ship_to_address;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getContainer_nr() {
        return container_nr;
    }

    public void setContainer_nr(String container_nr) {
        this.container_nr = container_nr;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getShipment_nr() {
        return shipment_nr;
    }

    public void setShipment_nr(String shipment_nr) {
        this.shipment_nr = shipment_nr;
    }

    public String getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(String shipment_status) {
        this.shipment_status = shipment_status;
    }

    public String getAwb_nbr() {
        return awb_nbr;
    }

    public void setAwb_nbr(String awb_nbr) {
        this.awb_nbr = awb_nbr;
    }
}
