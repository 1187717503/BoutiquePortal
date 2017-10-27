package com.intramirror.product.api.model;

/**
 * Created on 2017/10/20.
 *
 * @author YouFeng.Zhu
 */
public class SearchCondition {
    private int status;
    private int shopStatus;
    private String boutique;
    private String boutiqueid;
    private String brand;
    private String category;
    private String season;
    private String designerid;
    private String colorcode;
    private String image;
    private String modelimage;
    private String streetimage;
    private String stock;
    private int start;
    private int count;

    @Override
    public String toString() {
        return "SearchCondition{" + "status=" + status + ", shopStatus=" + shopStatus + ", boutique='" + boutique + '\'' + ", boutiqueid='" + boutiqueid + '\''
                + ", brand='" + brand + '\'' + ", category='" + category + '\'' + ", season='" + season + '\'' + ", designerid='" + designerid + '\''
                + ", colorcode='" + colorcode + '\'' + ", image='" + image + '\'' + ", modelimage='" + modelimage + '\'' + ", streetimage='" + streetimage
                + '\'' + ", stock='" + stock + '\'' + ", start=" + start + ", count=" + count + '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setShopStatus(int shopStatus) {
        this.shopStatus = shopStatus;
    }

    public int getShopStatus() {
        return shopStatus;
    }

    public String getBoutique() {
        return boutique;
    }

    public void setBoutique(String boutique) {
        this.boutique = boutique;
    }

    public String getBoutiqueid() {
        return boutiqueid;
    }

    public void setBoutiqueid(String boutiqueid) {
        this.boutiqueid = boutiqueid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDesignerid() {
        return designerid;
    }

    public void setDesignerid(String designerid) {
        this.designerid = designerid;
    }

    public String getColorcode() {
        return colorcode;
    }

    public void setColorcode(String colorcode) {
        this.colorcode = colorcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getModelimage() {
        return modelimage;
    }

    public void setModelimage(String modelimage) {
        this.modelimage = modelimage;
    }

    public String getStreetimage() {
        return streetimage;
    }

    public void setStreetimage(String streetimage) {
        this.streetimage = streetimage;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
