
package pk.shoplus.data;


import java.util.Date;


public class Product {


    private String id;


    private String code;


    private ProductSnippet snippet;


    private ProductContentDetails contentDetails;


    private ProductSkuDetails skuDetails;


    private ProductStatus status;


    public ProductSkuDetails getSkuDetails() {
        return skuDetails;
    }


    public Product setSkuDetails(ProductSkuDetails skuDetails) {
        this.skuDetails = skuDetails;
        return this;
    }


    public ProductContentDetails getContentDetails() {
        return contentDetails;
    }


    public Product setContentDetails(ProductContentDetails contentDetails) {
        this.contentDetails = contentDetails;
        return this;
    }


    public ProductSnippet getSnippet() {
        return snippet;
    }


    public Product setSnippet(ProductSnippet snippet) {
        this.snippet = snippet;
        return this;
    }


    public ProductStatus getStatus() {
        return status;
    }


    public Product setStatus(ProductStatus status) {
        this.status = status;
        return this;
    }


    public String getId() {
        return id;
    }


    public Product setId(String id) {
        this.id = id;
        return this;
    }


    public String getCode() {
        return code;
    }


    public Product setCode(String code) {
        this.code = code;
        return this;
    }



}
