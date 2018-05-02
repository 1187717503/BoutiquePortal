package com.intramirror.web.service.price;

import com.intramirror.product.api.service.price.IPriceChangeRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceRuleSynService {

    private static final Logger logger = LoggerFactory.getLogger(PriceRuleSynService.class);

    @Autowired
    private IPriceChangeRule iPriceChangeRule;

    // 页面点击preview
    public void syncPreview(Long vendorId, Long previewStatus, int categoryType, Long priceChangeRuleId, String flag) throws Exception {
        synchronized (this) {
            logger.info("SyncPreviewStart,vendorId:{},previewStatus:{},categoryType:{},priceChangeRuleId:{},flag:{}", vendorId, previewStatus, categoryType,
                    priceChangeRuleId, flag);
            iPriceChangeRule.updatePreviewPrice(vendorId, previewStatus, categoryType, priceChangeRuleId, flag);
            logger.info("SyncPreviewEnd,vendorId:{},previewStatus:{},categoryType:{},priceChangeRuleId:{},flag:{}", vendorId, previewStatus, categoryType,
                    priceChangeRuleId, flag);
        }
    }

    // 页面点击Active
    public void syncBoutique(Long vendorId, int categoryType, Long priceChangeRuleId) throws Exception {
        synchronized (this) {
            logger.info("syncBoutiqueStart,vendorId:{},categoryType:{},priceChangeRuleId:{}", vendorId, categoryType, priceChangeRuleId);
            iPriceChangeRule.updateVendorPrice(vendorId, categoryType, priceChangeRuleId);
            this.syncAllPriceByTable();
            logger.info("syncBoutiqueEnd,vendorId:{},categoryType:{},priceChangeRuleId:{}", vendorId, categoryType, priceChangeRuleId);
        }
    }

    // 页面点击Active
    public void syncIm(Long vendorId, int categoryType, Long priceChangeRuleId) throws Exception {
        synchronized (this) {
            logger.info("syncImStart,vendorId:{},categoryType:{},priceChangeRuleId:{}", vendorId, categoryType, priceChangeRuleId);
            iPriceChangeRule.updateAdminPrice(vendorId, categoryType, priceChangeRuleId);
            this.syncAllPriceByTable();
            logger.info("syncImEnd,vendorId:{},categoryType:{},priceChangeRuleId:{}", vendorId, categoryType, priceChangeRuleId);
        }
    }

    // 根据日期运行Boutique
    public void syncBoutique() throws Exception {
        synchronized (this) {
            logger.info("syncBoutiqueStart");
            iPriceChangeRule.updateVendorPrice(1);
            iPriceChangeRule.updateVendorPrice(2);
            logger.info("syncBoutiqueEnd");
        }
    }

    // 根据日期运行IM
    public void syncIm() throws Exception {
        synchronized (this) {
            logger.info("syncImStart");
            iPriceChangeRule.updateAdminPrice(1);
            iPriceChangeRule.updateAdminPrice(2);
            logger.info("syncImEnd");
        }
    }

    // 同步sku到其它表
    public void syncAllPriceByTable() throws Exception {
        synchronized (this) {
            logger.info("syncAllPriceByTableStart");
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
            logger.info("syncAllPriceByTableEnd");
        }
    }

    // 活动前一分钟同步preview_im_price到其它表
    public void syncActivePreview() throws Exception {
        synchronized (this) {
            logger.info("syncActivePreviewStart");

            // product.preview_im_price -> sku.im_price
            iPriceChangeRule.updateSkuImPrice();

            // sku.im_price -> product.min_im_price,product.max_im_price
            iPriceChangeRule.updateProductImPrice();

            // sku.im_price -> shop_product_sku.sale_price
            iPriceChangeRule.updateShopPrice();

            // shop_product_sku.sale_price -> shop_product.min_sale_price,shop_product.max_sale_price
            iPriceChangeRule.updateShopProductSalePrice();

            logger.info("syncActivePreviewEnd");
        }
    }

}
