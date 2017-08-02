package com.intramirror.product.core.vo.product;

import com.intramirror.common.help.StringUtils;
import com.intramirror.product.core.vo.sku.SkuOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingyifan on 2017/8/2.
 * 这个是提供给接口用的商品VO,用于创建商品,修改商品
 */
public class ProductOptions {

    public String name = "";

    public String code = "";

    public String brandCode = "";

    public String seasonCode = "";

    public String carryOver = "";

    public String brandName = "";

    public String colorCode = "";

    public String colorDesc = "";

    public String categoryId = "";

    public String desc = "";

    public String composition = "";

    public String madeIn = "";

    public String sizeFit = "";

    public String coverImg = "";

    public String descImg = "";

    public String weight = "";

    public String length = "";

    public String width = "";

    public String heigit = "";

    public String retailPrice = "";

    public String salePrice = "";

    public String imgByFilippo;

    public List<SkuOptions> skus = new ArrayList<>();

    public String getName() {
        return name;
    }

    public ProductOptions setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return StringUtils.trim(code);
    }

    public ProductOptions setCode(String code) {
        this.code = code;
        return this;
    }

    public String getBrandCode() {
        return StringUtils.trim(brandCode);
    }

    public ProductOptions setBrandCode(String brandCode) {
        this.brandCode = brandCode;
        return this;
    }

    public String getSeasonCode() {
        return seasonCode;
    }

    public ProductOptions setSeasonCode(String seasonCode) {
        this.seasonCode = seasonCode;
        return this;
    }

    public String getCarryOver() {
        return carryOver;
    }

    public ProductOptions setCarryOver(String carryOver) {
        this.carryOver = carryOver;
        return this;
    }

    public String getBrandName() {
        return brandName;
    }

    public ProductOptions setBrandName(String brandName) {
        this.brandName = brandName;
        return this;
    }

    public String getColorCode() {
        return colorCode;
    }

    public ProductOptions setColorCode(String colorCode) {
        this.colorCode = colorCode;
        return this;
    }

    public String getColorDesc() {
        return colorDesc;
    }

    public ProductOptions setColorDesc(String colorDesc) {
        this.colorDesc = colorDesc;
        return this;
    }

    public String getCategoryId() {
        return StringUtils.trim(categoryId);
    }

    public ProductOptions setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ProductOptions setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getComposition() {
        return composition;
    }

    public ProductOptions setComposition(String composition) {
        this.composition = composition;
        return this;
    }

    public String getMadeIn() {
        return madeIn;
    }

    public ProductOptions setMadeIn(String madeIn) {
        this.madeIn = madeIn;
        return this;
    }

    public String getSizeFit() {
        return sizeFit;
    }

    public ProductOptions setSizeFit(String sizeFit) {
        this.sizeFit = sizeFit;
        return this;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public ProductOptions setCoverImg(String coverImg) {
        this.coverImg = coverImg;
        return this;
    }

    public String getDescImg() {
        return descImg;
    }

    public ProductOptions setDescImg(String descImg) {
        this.descImg = descImg;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public ProductOptions setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getLength() {
        return length;
    }

    public ProductOptions setLength(String length) {
        this.length = length;
        return this;
    }

    public String getWidth() {
        return width;
    }

    public ProductOptions setWidth(String width) {
        this.width = width;
        return this;
    }

    public String getHeigit() {
        return heigit;
    }

    public ProductOptions setHeigit(String heigit) {
        this.heigit = heigit;
        return this;
    }

    public String getRetailPrice() {
        return retailPrice;
    }

    public ProductOptions setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
        return this;
    }

    public String getSalePrice() {
        return StringUtils.trim(salePrice.replaceAll(",","."));
    }

    public ProductOptions setSalePrice(String salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public List<SkuOptions> getSkus() {
        return skus;
    }

    public ProductOptions setSkus(List<SkuOptions> skus) {
        this.skus = skus;
        return this;
    }

    public String getImgByFilippo() {
        return imgByFilippo;
    }

    public void setImgByFilippo(String imgByFilippo) {
        this.imgByFilippo = imgByFilippo;
    }
}
