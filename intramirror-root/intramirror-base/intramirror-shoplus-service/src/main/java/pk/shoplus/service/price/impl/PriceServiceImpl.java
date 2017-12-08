package pk.shoplus.service.price.impl;

import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.model.Product;
import pk.shoplus.model.Sku;
import pk.shoplus.service.PriceChangeRuleService;
import pk.shoplus.service.SkuService;
import pk.shoplus.service.price.api.IPriceService;

public class PriceServiceImpl implements IPriceService{

    private static Logger logger = Logger.getLogger(PriceServiceImpl.class);

    @Override
    public Sku getPriceByRule(Product product, Long vendorId, BigDecimal price,Connection connection) throws Exception{
        logger.info("PriceServiceImplGetPriceByRule,inputParams,product:"+new Gson().toJson(product)+",vendorId:"+vendorId+",price:"+price);
        PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(connection);

        // query 折扣率
        Integer vendorDiscount = 100;
        Integer adminDiscount = 100;
        Map<String,Object> priceRuleList = new HashMap<>();

        if ((null != product.getCategory_id())
                && (null != product.getBrand_id())
                && (null != product.getProduct_code())
                && !"".equals(product.getProduct_code())) {
            priceRuleList = priceChangeRuleService.getCurrentChangeRuleByProduct(product);
        }

        if (null != priceRuleList) {
            vendorDiscount = Integer.parseInt(priceRuleList.get("price1").toString());
            adminDiscount = Integer.parseInt(priceRuleList.get("price3").toString());
        }

        Sku sku = new Sku();
        sku.price = price;
        sku.in_price = new BigDecimal(sku.price.doubleValue() * vendorDiscount / ((1 + 0.22) * 100));
        sku.im_price = new BigDecimal(sku.price.doubleValue() * adminDiscount / ((1) * 100));
        logger.info("PriceServiceImplGetPriceByRule,outputParams,sku:"+new Gson().toJson(sku));
        return sku;
    }

    @Override
    public boolean synProductPriceRule(Product product,BigDecimal newPrice,Connection conn) throws Exception{
        Integer vendorDiscount = 100;
        Integer adminDiscount = 100;

        PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);

        Map<String,Object> map = priceChangeRuleService.getCurrentChangeRuleByProduct(product);
        if(map != null) {
            vendorDiscount = Integer.parseInt(map.get("price1").toString());
            adminDiscount = Integer.parseInt(map.get("price3").toString());
        }

        SkuService skuService = new SkuService(conn);

        BigDecimal im_price = new BigDecimal(newPrice.doubleValue() * adminDiscount / ((1) * 100));
        BigDecimal in_price = new BigDecimal(newPrice.doubleValue() * vendorDiscount / ((1 + 0.22) * 100));
        Long product_id = product.getProduct_id();

        if(im_price.intValue() != 0 && newPrice.intValue() != 0 && in_price.intValue() != 0) {
            String updateSkuPrice = "update `sku`  set `in_price`  ="+in_price+" ,`im_price` ="+im_price+" , `price`  = "+newPrice+" where `product_id`  ="+product_id;
            String updateShopProductSalePrice = "update shop_product set max_sale_price="+im_price+",min_sale_price="+im_price+",updated_at=now() where product_id="+product_id;
            String updateShopProductSkuImPrice =  "update `shop_product_sku`  sps \n"
                    + "inner join `shop_product`  sp on(sps.`shop_product_id` = sp.`shop_product_id`)\n"
                    + "set sps.`sale_price`  ="+im_price+",sps.updated_at=now()\n"
                    + "where sp.`product_id`  ="+product_id;
            String updateProductRetailPrice = "update `product` set `max_retail_price` ="+newPrice+",`min_retail_price` = "+newPrice+",last_check=now()  where enabled = 1 and product_id="+product_id;
            String updateProductBoutiquePrice = "update `product` set `min_boutique_price` ="+in_price+",`max_boutique_price` = "+in_price+",last_check=now()  where enabled = 1 and product_id="+product_id;
            String updateProductImPrice = "update `product` set `min_im_price` ="+im_price+",`max_im_price` = "+im_price+",last_check=now()  where enabled = 1 and product_id="+product_id;

            skuService.updateBySQL(updateSkuPrice);
            skuService.updateBySQL(updateShopProductSalePrice);
            skuService.updateBySQL(updateShopProductSkuImPrice);
            skuService.updateBySQL(updateProductRetailPrice);
            skuService.updateBySQL(updateProductBoutiquePrice);
            skuService.updateBySQL(updateProductImPrice);


            logger.info("IPriceService,synProductPriceRule,SQL:"+
                    "updateSkuPrice:"+updateSkuPrice+
                    ",updateShopProductSalePrice:"+updateShopProductSalePrice+
                    ",updateShopProductSkuImPrice:"+updateShopProductSkuImPrice+
                    ",updateProductRetailPrice:"+updateProductRetailPrice+
                    ",updateProductBoutiquePrice:"+updateProductBoutiquePrice+
                    ",updateProductImPrice:"+updateProductImPrice);
        }
        return true;
    }
}
