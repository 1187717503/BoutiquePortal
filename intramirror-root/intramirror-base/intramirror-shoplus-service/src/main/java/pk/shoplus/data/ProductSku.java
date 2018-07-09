package pk.shoplus.data;

import java.math.BigDecimal;

/**
 * Created by chone on 2017/4/14.
 */
public class ProductSku {


    private String barcode;


    private String size;


    private BigDecimal stock;


    /**
     * Boutique sale price 
     */
    private BigDecimal price;


    /**
     * IntraMirror sale price
     */
    private BigDecimal salePrice;


    public BigDecimal getPrice() {
        return price;
    }

    
    public ProductSku setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }


    public BigDecimal getSalePrice() {
        return salePrice;
    }

    
    public ProductSku setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
        return this;
    }


    public BigDecimal getStock() {
        return stock;
    }


    public ProductSku setStock(BigDecimal stock) {
        this.stock = stock;
        return this;
    }


    public String getSize() {
        return size;
    }


    public ProductSku setSize(String size) {
        this.size = size;
        return this;
    }


    public String getBarcode() {
        return barcode;
    }


    public ProductSku setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }


}
