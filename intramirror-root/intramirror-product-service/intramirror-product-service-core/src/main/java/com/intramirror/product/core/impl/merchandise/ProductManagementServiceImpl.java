package com.intramirror.product.core.impl.merchandise;

import com.google.gson.Gson;
import com.intramirror.common.IKafkaService;
import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.product.api.model.*;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.product.common.KafkaProperties;
import com.intramirror.product.core.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created on 2017/10/30.
 *
 * @author YouFeng.Zhu
 */
@Service
public class ProductManagementServiceImpl implements ProductManagementService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductManagementServiceImpl.class);

    private final static Long shopId = 65L;
    @Autowired
    private ProductManagementMapper productManagementMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShopProductMapper shopProductMapper;
    @Autowired
    private ShopProductSkuMapper shopProductSkuMapper;
    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    IKafkaService kafkaService;

    @Autowired
    KafkaProperties kafkaProperties;

    @Override
    public Map<String, Object> getProductStateByProductId(Long product_id) {
        return productManagementMapper.getProductStateByProductId(product_id);
    }

    @Override
    public List<Map<String, Object>> listProductService(SearchCondition searchCondition) {
    	List<Map<String, Object>> resultList=productManagementMapper.listProductDetailInfo(searchCondition);
        return resultList;
    }

    @Override
    public List<Map<String, Object>> listProductException(List<Map<String,Object>> productIds) {
        return productManagementMapper.listProductException(productIds);
    }

    @Override
    public Integer countBoutiqueException(Integer type) {
        return productManagementMapper.countBoutiqueException(type);
    }

    @Override
    @Transactional
    public void approve(int status, Long productId) {
        updateProductStatusOnly(status, productId);
    }

    @Override
    @Transactional
    public void batchApprove(int status, List<Long> productIds) {
        batchUpdateProductStatusOnly(status, productIds);
    }

    @Override
    @Transactional
    public void process(int status, Long productId) {
        updateProductStatusOnly(status, productId);
    }

    @Override
    @Transactional
    public void batchProcess(int status, List<Long> productIds) {
        batchUpdateProductStatusOnly(status, productIds);
    }

    @Override
    @Transactional
    public void remove(int status, Long productId) {
        updateProductStatusOnly(status, productId);
    }

    @Override
    @Transactional
    public void batchRemove(int status, List<Long> productIds) {
        batchUpdateProductStatusOnly(status, productIds);
    }

    @Override
    @Transactional
    public void addToShop(int status, int shopStatus, Long productId) throws Exception {
        List<Long> productList = new ArrayList<>();
        productList.add(productId);
        updateProductStatusOnly(status, productId);
        createShopProductStatus(shopStatus, productId);

        String message = new Gson().toJson(productList);
        LOGGER.info("Start to send {} to kafaka {}--->{}", message, kafkaProperties.getTopic(), kafkaProperties.getServerName());
        kafkaService.sendMsgToKafka(message, kafkaProperties.getTopic(), kafkaProperties.getServerName());
    }

    @Override
    @Transactional
    public List<ProductWithBLOBs> batchAddToShop(int status, int shopStatus, List<Long> productIds) {
        batchUpdateProductStatusOnly(status, productIds);
        List<ProductWithBLOBs> exceptionProductIds = batchCreateShopProductStatus(shopStatus, productIds);

        List<Long> successProductIds = new ArrayList<>();
        for (Long productId : productIds) {
            boolean contain = false;
            for (ProductWithBLOBs product : exceptionProductIds) {
                if (product.getProductId().longValue() == productId) {
                    contain = true;
                }
            }
            if (!contain) {
                successProductIds.add(productId);
            }
        }

        if (successProductIds.size() == 0) {
            return exceptionProductIds;
        }

        String message = new Gson().toJson(successProductIds);
        LOGGER.info("Start to send {} to kafaka {}--->{}", message, kafkaProperties.getTopic(), kafkaProperties.getServerName());
        kafkaService.sendMsgToKafka(message, kafkaProperties.getTopic(), kafkaProperties.getServerName());
        return exceptionProductIds;
    }

    @Override
    @Transactional
    public void removeFromShop(int status, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        disableShopProductStatus(shopProductId);
    }

    @Override
    @Transactional
    public void batchRemoveFromShop(int status, List<Long> productIds, List<Long> shopProductIds) {
        batchUpdateProductStatusOnly(status, productIds);
        batchDisableShopProductStatus(shopProductIds);
    }

    @Override
    @Transactional
    public void onSale(int status, int shopStatus, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        updateShopProductStatusAndSaleAt(shopStatus, productId);
    }

    @Override
    @Transactional
    public void batchOnSale(int status, int shopStatus, List<Long> productIds, List<Long> shopProductIds) {
        batchUpdateProductStatusOnly(status, productIds);
        batchUpdateShopProductStatusAndSaleAt(shopStatus, shopProductIds);
    }

    @Override
    @Transactional
    public void offSale(int status, int shopStatus, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        updateShopProductStatusOnly(shopStatus, productId);
    }

    @Override
    @Transactional
    public void batchOffSale(int status, int shopStatus, List<Long> productIds, List<Long> shopProductIds) {
        batchUpdateProductStatusOnly(status, productIds);
        batchUpdateShopProductStatusOnly(shopStatus, shopProductIds);
    }

    @Override
    @Transactional
    public void shopApprove(int status, int shopStatus, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        updateShopProductStatusOnly(shopStatus, productId);
    }

    @Override
    @Transactional
    public void batchShopApprove(int status, int shopStatus, List<Long> productIds, List<Long> shopProductIds) {
        batchUpdateProductStatusOnly(status, productIds);
        batchUpdateShopProductStatusOnly(shopStatus, shopProductIds);
    }

    @Override
    @Transactional
    public void shopProcess(int status, int shopStatus, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        updateShopProductStatusOnly(shopStatus, productId);
    }

    @Override
    @Transactional
    public void batchShopProcess(int status, int shopStatus, List<Long> productIds, List<Long> shopProductIds) {
        batchUpdateProductStatusOnly(status, productIds);
        batchUpdateShopProductStatusOnly(shopStatus, shopProductIds);
    }

    @Override
    @Transactional
    public void shopRemove(int status, int shopStatus, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        updateShopProductStatusOnly(shopStatus, productId);
    }

    @Override
    @Transactional
    public void batchShopRemove(int status, int shopStatus, List<Long> productIds, List<Long> shopProductIds) {
        batchUpdateProductStatusOnly(status, productIds);
        batchUpdateShopProductStatusOnly(shopStatus, shopProductIds);
    }

    @Override
    public List<Map<String, Object>> listPriceByProductList(List<Map<String, Object>> products) {
        return productManagementMapper.listPriceByProductList(products);
    }

    @Override
    public List<Map<String, Object>> listAllProductCountGounpByState(SearchCondition searchCondition) {
        return productManagementMapper.listAllProductCountGounpByState(searchCondition);
    }

    @Override
    public List<Map<String, Object>> listProductStateByProductIds(List<Long> productIds) {
        return productManagementMapper.listProductStateByProductIds(productIds);
    }

    @Override
    public List<Map<String, Object>> listShopProductIdMappingByProductIds(List<Long> productIds) {
        return productManagementMapper.listShopProductIdMappingByProductIds(productIds);
    }

    private void updateProductStatusOnly(int status, Long productId) {
        ProductWithBLOBs product = new ProductWithBLOBs();
        product.setStatus((byte) status);
        product.setProductId(productId);
        productMapper.updateByPrimaryKeySelective(product);
    }

    private void batchUpdateProductStatusOnly(int status, List<Long> productIds) {
        productMapper.batchUpdateProductStatus(status, productIds);
    }

    private void updateShopProductStatusOnly(int shopStatus, Long productId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setStatus((byte) shopStatus);
        shopProduct.setProductId(productId);
        shopProductMapper.updateShopProductByProductId(shopProduct);
    }

    private void updateShopProductStatusAndSaleAt(int shopStatus, Long productId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setStatus((byte) shopStatus);
        shopProduct.setProductId(productId);
        shopProduct.setSaleAt(new Date());
        shopProductMapper.updateShopProductByProductId(shopProduct);
    }

    private void batchUpdateShopProductStatusOnly(int shopStatus, List<Long> shopProductIds) {
        shopProductMapper.batchUpdateShopProductStatus(shopStatus, shopProductIds);
    }

    private void batchUpdateShopProductStatusAndSaleAt(int shopStatus, List<Long> shopProductIds) {
        ShopProductWithBLOBs shopProduct = new ShopProductWithBLOBs();
        shopProduct.setStatus((byte) shopStatus);
        shopProduct.setSaleAt(new Date());
        shopProductMapper.batchUpdateShopProductByShopProductIds(shopProduct, shopProductIds);
    }

    private void disableShopProductStatus(Long shopProductId) {
        disableShopProductSku(shopProductId);
        disableShopProduct(shopProductId);
    }

    private void batchDisableShopProductStatus(List<Long> shopProductIds) {
        batchDisableShopProductSku(shopProductIds);
        batchDisableShopProduct(shopProductIds);
    }

    private void disableShopProductSku(Long shopProductId) {
        ShopProductSku shopProductSku = new ShopProductSku();
        shopProductSku.setEnabled(false);
        shopProductSku.setShopProductId(shopProductId);
        shopProductSkuMapper.updateByShopProductId(shopProductSku);
    }

    private void batchDisableShopProductSku(List<Long> shopProductIds) {
        shopProductSkuMapper.batchDisableByShopProductIds(shopProductIds);
    }

    private void disableShopProduct(Long shopProductId) {
        ShopProductWithBLOBs shopProduct = new ShopProductWithBLOBs();
        shopProduct.setEnabled(false);
        shopProduct.setShopProductId(shopProductId);
        shopProductMapper.updateByPrimaryKeySelective(shopProduct);
    }

    private void batchDisableShopProduct(List<Long> shopProductIds) {
        shopProductMapper.batchDisableShopProductStatus(shopProductIds);
    }

    private void createShopProductStatus(int status, Long productId) throws Exception {
        ProductWithBLOBs product = productMapper.selectByPrimaryKey(productId);
        List<Sku> skuList = skuMapper.listSkuInfoByProductId(productId);
        if (skuList.isEmpty()) {
            LOGGER.error("Failed to get sku of product_id [{}]", productId);
            throw new ValidateException(new ErrorResponse("Product " + productId + " have not any sku"));
        }
        Long shopProductId = createShopProduct(status, product, skuList.get(0));
        insertShopProductSkus(skuList, shopProductId);

    }

    private List<ProductWithBLOBs> batchCreateShopProductStatus(int shopStatus, List<Long> productIds) {

        List<Sku> skuList = skuMapper.listSkuInfoByProductIds(productIds);
        Map<Long, List<Sku>> skuMap = skuList2MapByProductId(skuList);

        List<Long> haveSkuProductIds = new ArrayList<>();
        List<Long> exceptionProductIds = new ArrayList<>();
        for (Long productId : productIds) {
            if (skuMap.containsKey(productId)) {
                haveSkuProductIds.add(productId);
            } else {
                exceptionProductIds.add(productId);
            }
        }

        if (haveSkuProductIds.size() == 0) {
            return productMapper.listProductByProductIds(exceptionProductIds);
        }

        List<ProductWithBLOBs> products = productMapper.listProductByProductIds(haveSkuProductIds);
        batchCreateShopProduct(shopStatus, products, skuMap);
        List<ShopProductSku> shopProductSkuList = mergeShopProductSkuBatch(skuList, haveSkuProductIds);
        shopProductSkuMapper.batchInsertShopProductSku(shopProductSkuList);
        if (exceptionProductIds.size() == 0) {
            return new ArrayList<ProductWithBLOBs>();
        } else {
            return productMapper.listProductByProductIds(exceptionProductIds);
        }
    }

    private Map<Long, List<Sku>> skuList2MapByProductId(List<Sku> skuList) {
        Map<Long, List<Sku>> skuMap = new HashMap<>();
        for (Sku sku : skuList) {
            if (skuMap.containsKey(sku.getProductId())) {
                skuMap.get(sku.getProductId()).add(sku);
            } else {
                List<Sku> productSkuList = new LinkedList<>();
                productSkuList.add(sku);
                skuMap.put(sku.getProductId(), productSkuList);
            }
        }
        return skuMap;
    }

    private Long createShopProduct(int status, ProductWithBLOBs product, Sku sku) {
        ShopProductWithBLOBs shopProduct = makeInputShopProductWithBLOBs(status, sku, product);
        shopProductMapper.insertAndGetId(shopProduct);
        return shopProduct.getShopProductId();
    }

    private void batchCreateShopProduct(int status, List<ProductWithBLOBs> products, Map<Long, List<Sku>> skuMap) {
        List<ShopProductWithBLOBs> inputShopProductList = new LinkedList<>();
        for (ProductWithBLOBs product : products) {
            Sku sku = skuMap.get(product.getProductId()).get(0);
            ShopProductWithBLOBs shopProduct = makeInputShopProductWithBLOBs(status, sku, product);
            inputShopProductList.add(shopProduct);
        }
        shopProductMapper.batchInsertShopProduct(inputShopProductList);
    }

    private ShopProductWithBLOBs makeInputShopProductWithBLOBs(int status, Sku sku, ProductWithBLOBs product) {
        Season season = seasonMapper.selectByPrimaryKey(product.getSeasonCode());
        ShopProductWithBLOBs shopProduct = new ShopProductWithBLOBs();
        shopProduct.setShopId(shopId);
        shopProduct.setShopCategoryId(product.getCategoryId());
        shopProduct.setProductId(product.getProductId());
        shopProduct.setName(product.getName());
        shopProduct.setCoverpic(product.getCoverImg());
        shopProduct.setStatus((byte) status);
        shopProduct.setIntroduction(product.getDescription());
        shopProduct.setEnabled(true);
        shopProduct.setMinSalePrice(sku.getImPrice());
        shopProduct.setMaxSalePrice(sku.getImPrice());
        shopProduct.setSeasonSn(season.getSeasonSn());
        return shopProduct;
    }

    private void insertShopProductSkus(List<Sku> skuList, Long shopProductId) {
        List<ShopProductSku> inputShopProductSkuList = new LinkedList<>();
        for (Sku sku : skuList) {
            ShopProductSku shopProductSku = makeInputShopProductSku(sku, shopProductId);
            inputShopProductSkuList.add(shopProductSku);
        }
        shopProductSkuMapper.batchInsertShopProductSku(inputShopProductSkuList);

    }

    private List<ShopProductSku> mergeShopProductSkuBatch(List<Sku> skuList, List<Long> productIds) {
        List<Map<String, Object>> productIdMap2ShopProduct = productManagementMapper.listProductStateByProductIds(productIds);
        Map<Long, Long> productMapShopProduct = listMap2Map(productIdMap2ShopProduct);
        List<ShopProductSku> inputShopProductSkuList = new LinkedList<>();
        for (Sku sku : skuList) {
            ShopProductSku shopProductSku = makeInputShopProductSku(sku, productMapShopProduct.get(sku.getProductId()));
            inputShopProductSkuList.add(shopProductSku);
        }
        return inputShopProductSkuList;
    }

    private ShopProductSku makeInputShopProductSku(Sku sku, Long shopProductId) {
        ShopProductSku shopProductSku = new ShopProductSku();
        shopProductSku.setShopId(shopId);
        shopProductSku.setShopProductId(shopProductId);
        shopProductSku.setSkuId(sku.getSkuId());
        shopProductSku.setName(sku.getName());
        shopProductSku.setCoverpic(sku.getCoverpic());
        shopProductSku.setIntroduction(sku.getIntroduction());
        shopProductSku.setSalePrice(sku.getImPrice());
        shopProductSku.setEnabled(true);
        return shopProductSku;
    }

    private Map<Long, Long> listMap2Map(List<Map<String, Object>> idsList) {
        Map<Long, Long> idsMap = new HashMap<>();
        for (Map<String, Object> ids : idsList) {
            idsMap.put(Long.parseLong(ids.get("product_id").toString()), Long.parseLong(ids.get("shop_product_id").toString()));
        }
        return idsMap;
    }

}
