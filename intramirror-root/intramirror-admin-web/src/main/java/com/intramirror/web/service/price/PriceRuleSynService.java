package com.intramirror.web.service.price;

import com.intramirror.product.api.service.price.IPriceChangeRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceRuleSynService {

    @Autowired
    private IPriceChangeRule iPriceChangeRule;

    // sync preview_im_price by vendor or season
    public void syncPreview(Long vendorId, Long previewStatus, int categoryType, Long priceChangeRuleId, String flag) throws Exception {
        synchronized (this) {
            iPriceChangeRule.updatePreviewPrice(vendorId, previewStatus, categoryType, priceChangeRuleId, flag);
        }
    }

    public void syncBoutique(Long vendorId, int categoryType, Long priceChangeRuleId) throws Exception {
        iPriceChangeRule.updateVendorPrice(vendorId, categoryType, priceChangeRuleId);
    }

    public void synC(Long vendorId, int categoryType, Long priceChangeRuleId) throws Exception {
        iPriceChangeRule.updateAdminPrice(vendorId, categoryType, priceChangeRuleId);
    }



    /**
     * 定时执行Boutique折扣
     *
     * @throws Exception
     */
    public void syncBoutique() throws Exception {
        iPriceChangeRule.updateVendorPrice(1);
        iPriceChangeRule.updateVendorPrice(2);
    }

    /**
     * 定时执行IM折扣
     *
     * @throws Exception
     */
    public void syncIm() throws Exception {
        iPriceChangeRule.updateAdminPrice(1);
        iPriceChangeRule.updateAdminPrice(2);
    }

    public void syncAllPriceByTable() throws Exception {
        // sku.im_price -> shop_product_sku.sale_price
        iPriceChangeRule.updateShopPrice();

        // shop_product_sku.sale_price -> shop_product.min_sale_price,shop_product.max_sale_price
        iPriceChangeRule.updateShopProductSalePrice();

        // sku.price -> product.min_retail_price,product.max_retail_price
        iPriceChangeRule.updateProductRetailPrice();

        // sku.in_price -> product.min_boutique_price,product.max_boutique_price
        iPriceChangeRule.updateProductBoutiquePrice();

        // sku.im_price -> product.min_im_price,product.max_im_price
        iPriceChangeRule.updateProductImPrice();
    }

    public void sync() throws Exception {
        // product.preview_im_price -> sku.im_price
        iPriceChangeRule.updateSkuImPrice();

        // sku.im_price -> product.min_im_price,product.max_im_price
        iPriceChangeRule.updateProductImPrice();

        // sku.im_price -> shop_product_sku.sale_price
        iPriceChangeRule.updateShopPrice();

        // shop_product_sku.sale_price -> shop_product.min_sale_price,shop_product.max_sale_price
        iPriceChangeRule.updateShopProductSalePrice();
    }

}
