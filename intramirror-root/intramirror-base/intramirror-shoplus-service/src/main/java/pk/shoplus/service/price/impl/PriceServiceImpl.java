package pk.shoplus.service.price.impl;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.model.Product;
import pk.shoplus.model.Sku;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.service.PriceChangeRuleService;
import pk.shoplus.service.SkuService;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.vo.AddShopVo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public boolean synProductPriceRule(Product product,Connection conn) throws Exception{
        Integer vendorDiscount = 100;
        Integer adminDiscount = 100;

        PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);

        Map<String,Object> map = priceChangeRuleService.getCurrentChangeRuleByProduct(product);
        if(map != null) {
            vendorDiscount = Integer.parseInt(map.get("price1").toString());
            adminDiscount = Integer.parseInt(map.get("price3").toString());
        }

        SkuService skuService = new SkuService(conn);
        Map<String,Object> conditions = new HashMap<>();
        conditions.put("enabled", EnabledType.USED);
        conditions.put("product_id",product.getProduct_id());
        List<Sku> skus = skuService.getSkuListByCondition(conditions);
        if(skus != null&& skus.size() >0) {
            for(Sku sku : skus) {
                sku.in_price = new BigDecimal(sku.price.doubleValue() * vendorDiscount / ((1 + 0.22) * 100));
                sku.im_price = new BigDecimal(sku.price.doubleValue() * adminDiscount / ((1) * 100));
                skuService.updateSku(sku);
                String sql = "update shop_product_sku sps set sps.sale_price ="+sku.im_price+" where sps.sku_id ="+sku.sku_id;
                skuService.updateBySQL(sql);
            }
        }
        return true;
    }
}
