package com.intramirror.web.controller.price;

import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.service.price.PriceRuleSynService;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
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

    @Autowired
    private PriceRuleSynService priceRuleSynService;

    @GetMapping("/run/t1")
    public Response t1() {
        synchronized (this) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("t1 - thread:{},index:{}", Thread.currentThread().getName(), i);
            }
        }
        return Response.success();
    }

    @GetMapping("/run/t2")
    public Response t2() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("t2 - thread:{},index:{}", Thread.currentThread().getName(), i);
        }
        return Response.success();
    }

    @GetMapping("/run/{type}")
    public Response run(@PathVariable(name = "type") String type) {
        long start = System.currentTimeMillis();
        logger.info("start run price rule by type:{}", type);
        try {

            synchronized (this) {
                Calendar startCalendar = Calendar.getInstance();

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.add(Calendar.MINUTE, 1);

                Map<String, Object> params = new HashMap<>();
                params.put("startTime", DateFormatUtils.format(startCalendar.getTime(), "yyyy-MM-dd HH:mm:00"));
                params.put("endTime", DateFormatUtils.format(endCalendar.getTime(), "yyyy-MM-dd HH:mm:00"));

                logger.info("start selectNowActiveRule,{},params:{}", type, JsonTransformUtil.toJson(params));
                List<Map<String, Object>> activeRules = iPriceChangeRule.selectNowActiveRule(params);

                if (CollectionUtils.isNotEmpty(activeRules)) {
                    logger.info("end selectNowActiveRule,{},params:{},activeRules:{}", type, JsonTransformUtil.toJson(params),
                            JsonTransformUtil.toJson(activeRules));
                    priceRuleSynService.syncBoutique(params);
                    priceRuleSynService.syncIm(params);
                }

                priceRuleSynService.syncAllPriceByTable();
            }

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
            //            this.syncPreviewRedundantTable();
            priceRuleSynService.syncActivePreview();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Execution of discounts,e:{}", e);
        }
        long end = System.currentTimeMillis();
        logger.info("end synchronous activity price. execution time : {}", (start - end) / 1000);
        return Response.success();
    }

    /*private void syncBoutique() throws Exception {
        iPriceChangeRule.updateVendorPrice(1);
        iPriceChangeRule.updateVendorPrice(2);
    }*/

    /*private void syncIm() throws Exception {
        iPriceChangeRule.updateAdminPrice(1);
        iPriceChangeRule.updateAdminPrice(2);
    }*/

    /**
     * 同步价格到 shop_product, product
     * @throws Exception
     */
    /*public synchronized void syncAllRedundantTable() throws Exception {
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
    }*/

    /*public synchronized void syncPreviewRedundantTable() throws Exception {
        // product.preview_im_price -> sku.im_price
        iPriceChangeRule.updateSkuImPrice();

        // sku.im_price -> product.min_im_price,product.max_im_price
        iPriceChangeRule.updateProductImPrice();

        // sku.im_price -> shop_product_sku.sale_price
        iPriceChangeRule.updateShopPrice();

        // shop_product_sku.sale_price -> shop_product.min_sale_price,shop_product.max_sale_price
        iPriceChangeRule.updateShopProductSalePrice();
    }*/

}
