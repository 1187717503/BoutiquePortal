/*
package com.intramirror.web.controller.price;

import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

*/
/**
 * Created by dingyifan on 2017/7/17.
 * price change rule job
 *//*

@Controller
@RequestMapping("/job/price")
public class PriceChangeRuleController {

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleController.class);

    @Resource(name = "productPriceChangeRule")
    private IPriceChangeRule iPriceChangeRule;

    @CrossOrigin
    @RequestMapping("/updatePriceByVendor")
    @ResponseBody
    public ResultMessage updatePriceByVendor() {

        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            iPriceChangeRule.updateVendorPrice();
            resultMessage.successStatus().addMsg("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PriceChangeRuleController,updatePriceByVendor,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
    }

    */
/*@RequestMapping(value = "/updatePriceByShop",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage updatePriceByShop(){

        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            iPriceChangeRule.updateShopPrice();
            resultMessage.successStatus().addMsg("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
    }*//*


    @CrossOrigin
    @RequestMapping("/updatePriceByAdmin")
    @ResponseBody
    public ResultMessage updatePriceByAdmin() {

        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            iPriceChangeRule.updateVendorPrice();

            iPriceChangeRule.updateAdminPrice();

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
            resultMessage.successStatus().addMsg("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PriceChangeRuleController,updatePriceByAdmin,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
    }
}
*/
