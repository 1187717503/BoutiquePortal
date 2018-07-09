package pk.shoplus.vo;

import pk.shoplus.model.Sku;

import java.util.List;

/**
 * Created by dingyifan on 2017/6/15.
 */
public class UpdateSkuPriceVO {

    private String brand;

    private String brandId;

    private String skuPrice;

    private List<Sku> list ;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }

    public List<Sku> getList() {
        return list;
    }

    public void setList(List<Sku> list) {
        this.list = list;
    }
}
