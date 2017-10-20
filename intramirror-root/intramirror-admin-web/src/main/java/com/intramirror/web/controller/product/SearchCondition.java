package com.intramirror.web.controller.product;

/**
 * Created on 2017/10/20.
 * @author YouFeng.Zhu
 */
public class SearchCondition {
    private String status;
    private String boutique;
    private String boutiqueid;
    private String brand;
    private String category;
    private String season;
    private String designerid_colorcode;
    private String image;
    private String modelimage;
    private String streetimage;
    private String stock;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SearchCondition {" + "status='" + status + '\'' + ", boutique='" + boutique + '\'' + ", boutiqueid='" + boutiqueid + '\'' + ", brand='" + brand
                + '\'' + ", category='" + category + '\'' + ", season='" + season + '\'' + ", designerid_colorcode='" + designerid_colorcode + '\''
                + ", image='" + image + '\'' + ", modelimage='" + modelimage + '\'' + ", streetimage='" + streetimage + '\'' + ", stock='" + stock + '\'' + '}';
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

    public String getDesignerid_colorcode() {
        return designerid_colorcode;
    }

    public void setDesignerid_colorcode(String designerid_colorcode) {
        this.designerid_colorcode = designerid_colorcode;
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

}
