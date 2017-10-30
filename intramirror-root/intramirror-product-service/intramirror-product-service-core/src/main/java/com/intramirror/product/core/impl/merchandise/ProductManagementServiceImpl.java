package com.intramirror.product.core.impl.merchandise;

import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.model.ShopProduct;
import com.intramirror.product.api.model.ShopProductSku;
import com.intramirror.product.api.model.ShopProductWithBLOBs;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductManagementMapper;
import com.intramirror.product.core.mapper.ProductMapper;
import com.intramirror.product.core.mapper.ShopProductMapper;
import com.intramirror.product.core.mapper.ShopProductSkuMapper;
import com.intramirror.product.core.mapper.SkuMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 2017/10/30.
 *
 * @author YouFeng.Zhu
 */
@Service
public class ProductManagementServiceImpl extends BaseDao implements ProductManagementService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductManagementServiceImpl.class);

    private final static Long shopId = 65L;
    private ProductManagementMapper productManagementMapper;

    private ProductMapper productMapper;
    private ShopProductMapper shopProductMapper;

    private ShopProductSkuMapper shopProductSkuMapper;

    private SkuMapper skuMapper;

    @Override
    public void init() {
        productManagementMapper = this.getSqlSession().getMapper(ProductManagementMapper.class);
        productMapper = this.getSqlSession().getMapper(ProductMapper.class);
        shopProductMapper = this.getSqlSession().getMapper(ShopProductMapper.class);
        shopProductSkuMapper = this.getSqlSession().getMapper(ShopProductSkuMapper.class);
        skuMapper = this.getSqlSession().getMapper(SkuMapper.class);
    }

    @Override
    public Map<String, Object> getProductStateByProductId(Long product_id) {
        return productManagementMapper.getProductStateByProductId(product_id);
    }

    @Override
    public List<Map<String, Object>> listProductService(SearchCondition searchCondition) {
        return productManagementMapper.listProductDetailInfo(searchCondition);
    }

    @Override
    @Transactional
    public void updateProductStatus(int status, Long productId) {
        updateProductStatusOnly(status, productId);
    }

    @Override
    @Transactional
    public void updateProductStatusAndNewShopProduct(int status, int shopStatus, Long productId) {
        updateProductStatusOnly(status, productId);
        createShopProductStatus(status, productId);
    }

    @Override
    @Transactional
    public void updateProductAndShopProductStatus(int status, int shopStatus, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        updateShopProductStatusOnly(shopStatus, productId);

    }

    @Override
    @Transactional
    public void updateProductStatusAndDisableShopProduct(int status, Long productId, Long shopProductId) {
        updateProductStatusOnly(status, productId);
        disableShopProductStatus(shopProductId);
    }

    private void updateProductStatusOnly(int status, Long productId) {
        ProductWithBLOBs product = new ProductWithBLOBs();
        product.setStatus((byte) status);
        product.setProductId(productId);
        productMapper.updateByPrimaryKeySelective(product);
    }

    private void updateShopProductStatusOnly(int shopStatus, Long productId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setStatus((byte) shopStatus);
        shopProduct.setProductId(productId);
        shopProductMapper.updateShopProductByProductId(shopProduct);
    }

    private void disableShopProductStatus(Long shopProductId) {
        disableShopProductSku(shopProductId);
        disableShopProduct(shopProductId);
    }

    private void disableShopProductSku(Long shopProductId) {
        ShopProductSku shopProductSku = new ShopProductSku();
        shopProductSku.setEnabled(false);
        shopProductSku.setShopProductId(shopProductId);
        shopProductSkuMapper.updateByShopProductId(shopProductSku);
    }

    private void disableShopProduct(Long shopProductId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setEnabled(false);
        shopProduct.setShopProductId(shopProductId);
        shopProductMapper.updateByPrimaryKey(shopProduct);
    }

    private void createShopProductStatus(int status, Long productId) {
        ProductWithBLOBs product = productMapper.selectByPrimaryKey(productId);
        List<Sku> skuList = skuMapper.listSkuInfoByProductId(productId);
        if (skuList.isEmpty()) {
            LOGGER.error("Failed to get sku of product_id [{}]", productId);
            // throw exception?
        }
        Long shopProductId = createShopProduct(status, product, skuList.get(0));
        insertShopProductSkus(skuList, shopProductId);

    }

    private Long createShopProduct(int status, ProductWithBLOBs product, Sku sku) {
        ShopProductWithBLOBs shopProduct = new ShopProductWithBLOBs();
        shopProduct.setEnabled(true);
        shopProduct.setShopId(shopId);
        shopProduct.setStatus((byte) status);
        shopProduct.setProductId(product.getProductId());
        shopProduct.setName(product.getName());
        shopProduct.setShopCategoryId(product.getCategoryId());
        shopProduct.setCoverpic(product.getCoverImg());
        shopProduct.setIntroduction(product.getDescription());
        shopProduct.setMinSalePrice(sku.getImPrice());
        shopProduct.setMaxSalePrice(sku.getImPrice());
        return shopProductMapper.insertAndGetId(shopProduct);
    }

    private void insertShopProductSkus(List<Sku> skuList, Long shopProductId) {
        for (Sku sku : skuList) {
            ShopProductSku shopProductSku = new ShopProductSku();
            shopProductSku.setEnabled(true);
            shopProductSku.setShopProductId(shopProductId);
            shopProductSku.setSkuId(sku.getSkuId());
            shopProductSku.setName(sku.getName());
            shopProductSku.setCoverpic(sku.getCoverpic());
            shopProductSku.setIntroduction(sku.getIntroduction());
            shopProductSku.setSalePrice(sku.getImPrice());
            shopProductSku.setShopId(shopId);
            shopProductSkuMapper.insertSelective(shopProductSku);
        }

    }

}
