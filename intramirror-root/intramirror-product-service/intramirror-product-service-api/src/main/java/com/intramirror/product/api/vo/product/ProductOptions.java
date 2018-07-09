package com.intramirror.product.api.vo.product;

import java.util.ArrayList;
import java.util.List;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.enums.ApiErrorTypeEnum;
import com.intramirror.product.api.vo.sku.SkuOptions;
import com.intramirror.product.api.vo.vendor.VendorOptions;

/**
 * Created by dingyifan on 2017/8/2.
 * 这个是提供给接口用的商品VO,用于创建商品,修改商品
 */
public class ProductOptions {

    private String name = "";

    private String code = "";

    private String brandCode = "";

    private String seasonCode = "";

    private String carryOver = "";

    private String brandName = "";

    private String colorCode = "";

    private String colorDesc = "";

    private String categoryId = "";

    private String desc = "";

    private String composition = "";

    private String madeIn = "";

    private String sizeFit = "";

    private String coverImg = "";

    private String descImg = "";

    private String weight = "";

    private String length = "";

    private String width = "";

    private String heigit = "";

    private String retailPrice = "";

    private String salePrice = "";

    private String imgByFilippo;

    private List<SkuOptions> skus = new ArrayList<>();

    private VendorOptions vendorOptions;

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

    public VendorOptions getVendorOptions() {
        return vendorOptions;
    }

    public void setVendorOptions(VendorOptions vendorOptions) {
        this.vendorOptions = vendorOptions;
    }

    public ResultMessage checkBasicsParams(){
        ResultMessage resultMessage = ResultMessage.getInstance();
        resultMessage.errorStatus();

        // check product
        if(StringUtils.isBlank(this.name)) {
            resultMessage.setData(ApiErrorTypeEnum.BOUTIQUE_NAME_NOT_EXIST);
        } else if(StringUtils.isBlank(this.code)) {
            resultMessage.setData(ApiErrorTypeEnum.BOUTIQUE_ID_NOT_EXIST);
        } else if(StringUtils.isBlank(this.brandCode)) {
            resultMessage.setData(ApiErrorTypeEnum.BRANDID_NOT_EXIST);
        } else if(StringUtils.isBlank(this.brandName)) {
            resultMessage.setData(ApiErrorTypeEnum.BRANDNAME_NOT_EXIST);
        } else if(StringUtils.isBlank(this.colorCode)) {
            resultMessage.setData(ApiErrorTypeEnum.COLORCODE_NOT_EXIST);
        } else if(StringUtils.isBlank(this.categoryId)) {
            resultMessage.setData(ApiErrorTypeEnum.CATEGORY_NOT_EXIST);
        } else if(StringUtils.isBlank(this.coverImg) || StringUtils.isBlank(this.descImg)) {
            resultMessage.setData(ApiErrorTypeEnum.IMG_NOT_EXIST);
        } else if(StringUtils.isBlank(this.salePrice) || Integer.parseInt(this.salePrice) == 0) {
            resultMessage.setData(ApiErrorTypeEnum.RETAIL_PRICE_IS_ZERO);
        } else if(StringUtils.isBlank(this.weight)) {
            resultMessage.setData(ApiErrorTypeEnum.WEIGHT_NOT_EXIST);
        } else {
            resultMessage.successStatus();
        }

        if(resultMessage.isERROR())
            return resultMessage;

        // check sku
        resultMessage.successStatus();
        if(this.skus == null || this.skus.size() == 0) {
            resultMessage.errorStatus().setData(ApiErrorTypeEnum.SKU_NOT_EXIST);
        } else {
            for(SkuOptions skuOptions : skus) {
                if(StringUtils.isBlank(skuOptions.getBarcodes())) {
                    resultMessage.errorStatus().setData(ApiErrorTypeEnum.SKU_SIZT_NOT_EXIST);
                    break;
                }
            }
        }
        return resultMessage;
    }
}
