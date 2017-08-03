package pk.shoplus.service.product.impl;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Helper;
import pk.shoplus.data.ProductStatus;
import pk.shoplus.enums.ProductStatusEnum;
import pk.shoplus.model.*;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.ShopProductType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.*;
import pk.shoplus.service.product.api.IProductStatusService;
import pk.shoplus.vo.AddShopVo;
import pk.shoplus.vo.ResultMessage;
import pk.shoplus.vo.StatusMachineVO;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by dingyifan on 2017/6/1.
 */
public class ProductStatusServiceImpl implements IProductStatusService{
    private static Logger logger = Logger.getLogger(ProductStatusServiceImpl.class);

    @Override
    public ResultMessage statusMachineChange(Connection conn, StatusMachineVO statusMachineVO, ProductStatusEnum changeEnum) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setStatus(true);

        try {
            // get params
            Long product_id = statusMachineVO.getProductId();
            Long user_id = statusMachineVO.getUserId();

            // query product
            ProductService productService = new ProductService(conn);
            Product product = productService.getProductById(product_id);

            // checked product
            if(product == null) {
                resultMessage.setStatus(false);
                resultMessage.setMsg("errorMessage : product is null! statusMachineVO : " + new Gson().toJson(statusMachineVO));
                return resultMessage;
            }

            // query shop_product,product
            ShopService shopService = new ShopService(conn);
            ShopProductService shopProductService = new ShopProductService(conn);
            Shop shop = shopService.getShop(user_id);
            ShopProduct shopProductByUser = null;
            ShopProduct shopProductByProduct = null;
            if(shop != null) {
                shopProductByUser = shopProductService.getShopProductByShopAndProductId(shop.shop_id,
                        Long.valueOf(product_id));
                Map<String,Object> shopProductConditions = new HashMap<>();
                shopProductConditions.put("enabled",EnabledType.USED);
                shopProductConditions.put("product_id",product_id);

                List<ShopProduct> shopProducts = shopProductService.getShopProductListByCondition(shopProductConditions);
                if(shopProducts != null  && shopProducts.size() > 0) {
                    shopProductByProduct = shopProducts.get(0);
                }
            }

            // get current status
            int currentStatus = product.getStatus().intValue();
            int changeStatus = changeEnum.getProductStatus();
            int currentShopStatus = shopProductByUser == null ? -1 : shopProductByUser.getStatus();

            // change status and update_at
            product.status = changeStatus ;
            product.updated_at = Helper.getCurrentTimeToUTCWithDate();

            if(currentStatus == ProductStatusEnum.SHOP_OFF_SALE.getProductStatus()
                    && currentShopStatus == ProductStatusEnum.SHOP_OFF_SALE.getShopProductStatus()){ // shop off sale 完成

                if(changeEnum.getCode().equals(ProductStatusEnum.SHOP_ON_SALE.getCode())) {

                    // 1. shop off sale -> shop on sale
                    statusMachineVO.setShopProductId(shopProductByUser.getShop_product_id());
                    ResultMessage resultMessage1 = this.onSaleAndoffSaleProduct(conn,statusMachineVO,ProductStatusEnum.SHOP_ON_SALE);
                    if(!resultMessage1.getStatus()) {
                        resultMessage.setStatus(false);
                        resultMessage.sMsg(resultMessage1.getMsg());
                    }
                } else if(changeEnum.getCode().equals(ProductStatusEnum.SHOP_REMOVED.getCode())){

                    // 2. shop off sale -> shop removed
                    statusMachineVO.setShopProductId(shopProductByUser.getShop_product_id());
                    this.deleteShop(conn,statusMachineVO);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_PENDING.getCode())) {

                    // 3. shop off sale -> modify pending
                    productService.updateProduct(product);
                }else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else if(currentStatus == ProductStatusEnum.SHOP_ON_SALE.getProductStatus()
                    && currentShopStatus == ProductStatusEnum.SHOP_ON_SALE.getShopProductStatus()){ // shop on sale 完成
                if(changeEnum.getCode().equals(ProductStatusEnum.SHOP_OFF_SALE.getCode())) {

                    // 1. shop on sale -> shop off sale
                    ResultMessage resultMessage1 = this.onSaleAndoffSaleProduct(conn,statusMachineVO,ProductStatusEnum.SHOP_OFF_SALE);
                    if(!resultMessage1.getStatus()) {
                        resultMessage.setStatus(false);
                        resultMessage.sMsg(resultMessage1.getMsg());
                    }
                } else if(changeEnum.getCode().equals(ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getCode())){ // TODO 待确认

                    // 2. shop on sale -> admin shop off sale
                    productService.updateProduct(product);
                    shopProductByUser.status = ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getShopProductStatus();
                    shopProductService.updateShopProduct(shopProductByUser);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.SHOP_SOLD_OUT.getCode())){ // TODO 待确认

                    // 3. shop on sale -> shop sold out
                    productService.updateProduct(product);
                    shopProductByUser.status = ProductStatusEnum.SHOP_SOLD_OUT.getShopProductStatus();
                    shopProductService.updateShopProduct(shopProductByUser);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else if(currentStatus == ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getProductStatus()
                    && currentShopStatus == ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getShopProductStatus()) { // admin shop off sale 完成
                if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_PENDING.getCode())) {

                    // admin shop off sale -> modify pending
                    productService.updateProduct(product);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }

            } else if(currentStatus == ProductStatusEnum.SHOP_SOLD_OUT.getProductStatus()
                    && currentShopStatus == ProductStatusEnum.SHOP_SOLD_OUT.getShopProductStatus()){ // shop sold out 完成
                if(changeEnum.getCode().equals(ProductStatusEnum.SHOP_ON_SALE.getCode())) { // TODO 待确认

                    // 1. shop sold out -> shop on sale
                    statusMachineVO.setShopProductId(shopProductByUser.getShop_product_id());
                    ResultMessage resultMessage1 = this.onSaleAndoffSaleProduct(conn,statusMachineVO,ProductStatusEnum.SHOP_ON_SALE);
                    if(!resultMessage1.getStatus()) {
                        resultMessage.setStatus(false);
                        resultMessage.sMsg(resultMessage1.getMsg());
                    }
                } else if(changeEnum.getCode().equals(ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getCode())){
                    // 2. shop sold out -> admins shop off sale
                    shopProductByUser.status = ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getShopProductStatus();
                    shopProductService.updateShopProduct(shopProductByUser);
                }else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else if(currentStatus == ProductStatusEnum.ADMIN_APPROVED.getProductStatus() && shopProductByUser == null) { // admin approved 完成

                if(changeEnum.getCode().equals(ProductStatusEnum.SHOP_OFF_SALE.getCode()) && user_id != null) {

                    // 1. admin approved -> shop off sale
                    AddShopVo addShopVo = new AddShopVo();
                    addShopVo.setBrandId(product.getBrand_id().toString());
                    addShopVo.setCategoryId(product.getCategory_id().toString());
                    addShopVo.setProductId(product.getProduct_id().toString());
                    ResultMessage resultMessage1 = this.addToShop(addShopVo,user_id); // 内部有校验 user相关的shop_product为null
                    if(!resultMessage1.getStatus()) {
                        resultMessage.setStatus(false);
                        resultMessage.sMsg(resultMessage1.getMsg());
                    }
                } else if(changeEnum.getCode().equals(ProductStatusEnum.ADMIN_OFF_SALE.getCode())){

                    // 2. admin approved -> admin off sale
                    productService.updateProduct(product);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else if(currentStatus == ProductStatusEnum.ADMIN_OFF_SALE.getProductStatus() && shopProductByUser == null) { // admin off sale 完成
                if(changeEnum.getCode().equals(ProductStatusEnum.ADMIN_APPROVED.getCode())) {

                    // 1. admin off sale -> admin approved
                    productService.updateProduct(product);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_PENDING.getCode())) {

                    // 2. amdin off sale -> modify pending
                    productService.updateProduct(product);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_REJECT.getCode())){

                    // 3. admin off sale -> modify reject
                    productService.updateProduct(product);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else if(currentStatus == ProductStatusEnum.MODIFY_PENDING.getProductStatus()) { // modify pending 完成

                if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_REJECT.getCode())) {

                    // modify pending -> modify Reject
                    productService.updateProduct(product);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.SHOP_OFF_SALE.getCode())
                        && currentShopStatus == ProductStatusEnum.SHOP_OFF_SALE.getShopProductStatus()){

                    // TODO modify pending -> shop off sale
                    productService.updateProduct(product);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else if(currentStatus == ProductStatusEnum.MODIFY_REJECT.getProductStatus()){ // modify reject 完成
                if(changeEnum.getCode().equals(ProductStatusEnum.ADMIN_APPROVED.getCode())) {
                    // 1. modify reject -> admin approved
                    productService.updateProduct(product);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_PENDING.getCode())){

                    // 2. modify reject -> modify pending
                    productService.updateProduct(product);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else if(currentStatus == ProductStatusEnum.NEW_REJECTED.getProductStatus()){ // new rejected 完成

                if(changeEnum.getCode().equals(ProductStatusEnum.ADMIN_APPROVED.getCode())) {

                    // 1.new rejected -> admin approved
                    productService.updateProduct(product);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_PENDING.getCode())){

                    // 2. modify reject -> modify pending
                    productService.updateProduct(product);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }
            }else if(currentStatus == ProductStatusEnum.NEW_PENDING.getProductStatus()) { // new pending 完成

                if(changeEnum.getCode().equals(ProductStatusEnum.ADMIN_APPROVED.getCode())) {

                    // 1.new pending -> admin approved
                    productService.updateProduct(product);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.NEW_REJECTED.getCode())) {

                    // 2.new pending -> new Rejected
                    product.rejected_reason = statusMachineVO.getRejectedReason();
                    product.rejected_at = Helper.getCurrentTimeToUTCWithDate();
                    productService.updateProduct(product);
                } else if(changeEnum.getCode().equals(ProductStatusEnum.MODIFY_PENDING.getCode())){

                    // 3. new pending -> modify pending
                    productService.updateProduct(product);
                } else {

                    // error
                    resultMessage.setStatus(false);
                }
            } else {
                logger.info(".statusMachineChange status is error!");
            }

            if(!resultMessage.getStatus()) {
                resultMessage.sMsg("user_id : " +user_id +
                        ",shop_product_id : " + (shopProductByUser==null?"null":shopProductByUser.getShop_product_id()) +
                        ",currentStatus : " + currentStatus +
                        ", changeStatus : " + changeStatus +
                        ", 请查询状态机,此状态跳转不被允许！");
            } else {
                resultMessage.setMsg("SUCCESS");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMessage;
    }

    @Override
    public ResultMessage addToShop(AddShopVo asv, Long userId) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setStatus(false);
        String status = "";
        String product_id = asv.getProductId();
        String categoryId = asv.getCategoryId();
        String brandId = asv.getBrandId();
        Connection conn = null ;
        try  {
            conn = DBConnector.sql2o.beginTransaction();
            ShopProductService shopProductService = new ShopProductService(conn);
            ShopService shopService = new ShopService(conn);
            SkuService skuService = new SkuService(conn);
            ProductService productService = new ProductService(conn);
            PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);

            List<Map<String, Object>> priceRuleList = null;
//            Integer discountPercentage = 1;

            /*//获取商品的折扣率
            try {
                if (null != categoryId && !"".equals(categoryId)
                        && null != brandId && !"".equals(brandId)
                        && null != product_id && !"".equals(product_id)) {
                    priceRuleList = priceChangeRuleService.getSalesPriceChangeRuleByProduct(Long.valueOf(categoryId), Long.valueOf(brandId), product_id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != priceRuleList && priceRuleList.size() > 0) {
                Map<String, Object> map = priceRuleList.get(0);
                if (null != map && null != map.get("discount_percentage")) {
                    discountPercentage = Integer.valueOf(map.get("discount_percentage").toString());
                }
            }*/

            Shop shop = shopService.getShopByUserId(userId);
            if (shop != null) {

                ShopLogisticsAddressService shopLogisticsAddressService = new ShopLogisticsAddressService(conn);
                ShopLogisticsAddress shopLogisticsAddress = shopLogisticsAddressService
                        .getShopLogisticsAddressByShopId(shop.shop_id);
                if (Helper.checkNotNull(shopLogisticsAddress) && !Helper.isNullOrEmpty(shopLogisticsAddress.province)
                        && !Helper.isNullOrEmpty(shopLogisticsAddress.city)
                        && !Helper.isNullOrEmpty(shopLogisticsAddress.name)
                        && !Helper.isNullOrEmpty(shopLogisticsAddress.address)
                        && !Helper.isNullOrEmpty(shopLogisticsAddress.telephone)) {

                    ShopProduct shopProduct = shopProductService.getShopProductByShopAndProductId(shop.shop_id,
                            Long.valueOf(product_id));
                    Product product = productService.getProductById(Long.valueOf(product_id));
                    if(!"3".equals(product.status.toString())){
                        status  = product_id + "-" + "Only Product in Available status can be Added to Shop!!!";
                    }else if (shopProduct == null) {

                        // update
                        Map<String, Object> condition = new HashMap<String, Object>();
                        condition.put("product_id", product_id);
                        condition.put("enabled", EnabledType.USED);
                        List<Sku> skus = skuService.getSkuListByCondition(condition);
                        if(skus == null || skus.size() == 0) {
                            status  = product_id + "-" + " sku is null!!!";//StatusType.SHOPPRODUCTEXIST;
                        }
                        // update
                        else  {
                            shopProduct = new ShopProduct();
                            shopProduct.created_at = Helper.getCurrentTimeToUTCWithDate();
                            shopProduct.updated_at = Helper.getCurrentTimeToUTCWithDate();
                            shopProduct.enabled = EnabledType.USED;
                            shopProduct.shop_id = shop.shop_id;
                            shopProduct.status = ShopProductType.STOP_SELLING;
                            shopProduct.product_id = Long.valueOf(product_id);
                            shopProduct.name = product.name;
                            shopProduct.shop_category_id = product.category_id;
                            shopProduct.coverpic = product.cover_img;
                            shopProduct.introduction = product.description;
                            shopProduct = shopProductService.createShopProduct(shopProduct);
                            if (shopProduct != null) {


                                List<ShopProductSku> shopProductSkus = new ArrayList<ShopProductSku>();
                                ShopProductSku shopProductSku = null;
                                for (Sku sku : skus) {
                                    shopProductSku = new ShopProductSku();
                                    shopProductSku.created_at = Helper.getCurrentTimeToUTCWithDate();
                                    shopProductSku.updated_at = Helper.getCurrentTimeToUTCWithDate();
                                    shopProductSku.enabled = EnabledType.USED;
                                    shopProductSku.shop_product_id = shopProduct.shop_product_id;
                                    shopProductSku.sku_id = sku.sku_id;
                                    shopProductSku.name = sku.name;
                                    shopProductSku.coverpic = sku.coverpic;
                                    shopProductSku.introduction = sku.introduction;
                                    //shopProductSku.sale_price = sku.price.add(new BigDecimal(1));
                                    shopProductSku.sale_price = sku.im_price;
                                    shopProductSku.shop_id = shop.shop_id;
                                    shopProductSkus.add(shopProductSku);
                                }
                                if (shopProductService.createShopProductSkus(shopProductSkus)) {
                                    resultMessage.setStatus(true);
                                    status = product_id + "-" + "SUCCESS !!!";//StatusType.SUCCESS;
                                }
                                conn.commit();
                            }
                        }


                    } else {
                        status  = product_id + "-" + "Product is Already in Shop!!!";//StatusType.SHOPPRODUCTEXIST;
                    }

                } else {
                    status  = product_id + "-" +"shop logistics address not exist!!!"; //StatusType.SHOP_LOGISTICS_ADDRESS_NOT_EXIST;
                }

            } else {
                status = product_id + "-" + "User's Shop doesn't exist!!!"; //StatusType.SHOP_NOT_EXIST_FROM_SESSION_USER;
            }
        } catch (Exception e) {
            e.printStackTrace();
            status  = product_id + "-" + "Internal ERROR: Can't connect to database!!!"; //StatusType.DATABASE_ERROR;
            if(conn != null) {
                conn.rollback();
                conn.close();
            }
        } finally {
            if(conn != null) {
                conn.rollback();
                conn.close();
            }
        }
        resultMessage.setMsg(status);
        return resultMessage;
    }

    @Override
    public ResultMessage onSaleAndoffSaleProduct(Connection conn,StatusMachineVO statusMachineVO,ProductStatusEnum changeEnum) {
        ResultMessage resultMessage = new ResultMessage();
        try{
            // init
            ShopProductService shopProductService = new ShopProductService(conn);
            List<Object[]> params = new ArrayList<Object[]>();
            resultMessage.setStatus(false);
            Object[] param = null;

            // get params
            long shopProductId = statusMachineVO.getShopProductId();
            Date now = Helper.getCurrentTimeToUTCWithDate();

            if(changeEnum.getProductStatus() == ProductStatusEnum.SHOP_ON_SALE.getProductStatus()) {

                // get stock
                Map<String, Object> stockObject = shopProductService.getProductStock(shopProductId);
                if (stockObject != null) {
                    BigDecimal stock = (BigDecimal) stockObject.get("stock");
                    if (stock.intValue() <= 0) {
                        resultMessage.setMsg(shopProductId + " - product stock is 0!!!");
                    } else {
                        param = new Object[] { shopProductId, now, now };
                    }
                } else {
                    resultMessage.setMsg(shopProductId + " - product stock is 0!!!");
                }

                // update
                shopProductService.changeShopProductSale(params);
                resultMessage.sMsg("update success !!!").setStatus(true);
            } else if(changeEnum.getProductStatus() == ProductStatusEnum.SHOP_OFF_SALE.getProductStatus()){
                param = new Object[] { shopProductId, now };

                // update
                shopProductService.changeShopProductStop(params);
                resultMessage.sMsg("update success !!!").setStatus(true);
            } else {
                resultMessage.setMsg("update faild !!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMessage.setMsg("errorMessage : " + e.getMessage());
        }
        return resultMessage;
    }

    @Override
    public ResultMessage deleteShop(Connection conn, StatusMachineVO statusMachineVO) {
        ResultMessage resultMessage = new ResultMessage();
        Long shopProductId = statusMachineVO.getShopProductId();
        logger.info("start ProductStatusServiceImpl.deleteShop statusMachineVO : " + new Gson().toJson(statusMachineVO));

        if(statusMachineVO!= null && shopProductId != null) {

            ShopProductService shopProductService=new ShopProductService(conn);
            List<Object[]> params=new ArrayList<Object[]>();
            Object[] Param=new Object[]{shopProductId,Helper.getCurrentTimeToUTCWithDate()};
            shopProductService.deleteShopProduct(params);
            resultMessage.sMsg("success !!!").setStatus(true);
        } else {
            resultMessage.sMsg("param is empty !!!").setStatus(false);
        }

        logger.info("end ProductStatusServiceImpl.deleteShop statusMachineVO : " + new Gson().toJson(statusMachineVO));
        return resultMessage;
    }
}
