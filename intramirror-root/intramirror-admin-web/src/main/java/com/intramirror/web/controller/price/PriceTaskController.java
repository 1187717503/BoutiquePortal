package com.intramirror.web.controller.price;

import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/price/task")
public class PriceTaskController {

    private static final Logger logger = LoggerFactory.getLogger(PriceTaskController.class);

    @Autowired
    private IPriceChangeRule iPriceChangeRule;

    @GetMapping("/run/{type}")
    public Response run(@PathVariable(name = "type") String type) {
        long start = System.currentTimeMillis();
        logger.info("start run price rule by type:{}", type);
        try {

            if (type.equals("im")) {
                this.syncIm();
            }

            if (type.equals("boutique")) {
                this.syncBoutique();
            }

            if (type.equals("imAndBoutique")) {
                this.syncBoutique();
                this.syncIm();
            }

            this.syncAllRedundantTable();

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Execution of discounts,e:{}", e);
        }
        long end = System.currentTimeMillis();
        logger.info("end run price rule by type:{},execution time : {}", type, (start - end) / 1000);
        return Response.success();
    }

    @PostMapping("/sync/preview")
    public Response synPreview() {
        long start = System.currentTimeMillis();
        logger.info("Start synchronous activity price.");
        try {
            this.syncPreviewRedundantTable();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Execution of discounts,e:{}", e);
        }
        long end = System.currentTimeMillis();
        logger.info("end synchronous activity price. execution time : {}", (start - end) / 1000);
        return Response.success();
    }

    private void syncBoutique() throws Exception {
        iPriceChangeRule.updateVendorPrice(1);
        iPriceChangeRule.updateVendorPrice(2);
    }

    private void syncIm() throws Exception {
        iPriceChangeRule.updateAdminPrice(1);
        iPriceChangeRule.updateAdminPrice(2);
    }

    /**
     * 同步价格到 shop_product, product
     *
     * @throws Exception
     */
    public synchronized void syncAllRedundantTable() throws Exception {
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

    public synchronized void syncPreviewRedundantTable() throws Exception {
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
