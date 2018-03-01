package com.intramirror.web.distributed.service;

import com.intramirror.common.help.StringUtils;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.controller.api.service.ApiCommonUtils;
import com.intramirror.web.distributed.vo.ProductImageVo;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.Product;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.service.ProductService;

public class ApiUpdateProductImageService {

    private static final Logger logger = LoggerFactory.getLogger(ApiUpdateProductImageService.class);

    private static final String method = "ApiUpdateProductImageService.UpdateProductImage";

    private ProductImageVo productImage;

    public void update() {
        try {
            logger.info("method:{},productImage:{}", method, JsonTransformUtil.toJson(productImage));
            Product product = this.queryProduct();

            if (product != null && StringUtils.isNotBlank(product.getCover_img()) && product.getCover_img().length() < 5) {
                String images = ApiCommonUtils.downloadImgs(JsonTransformUtil.toJson(productImage.getImages()));
                boolean result = this.updateImage(product.getProduct_id(), images);
                logger.info("method:{},productImage:{},result:", method, JsonTransformUtil.toJson(productImage), result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("method:{},ErrorMessage:{}", method, e);
        }
    }

    /**
     * 更新商品图片
     *
     * @param images
     * @throws Exception
     */
    private boolean updateImage(long product_id, String images) throws Exception {
        if (StringUtils.isBlank(images) || images.length() < 5) {
            return false;
        }

        Connection conn = DBConnector.sql2o.open();
        ProductService productService = new ProductService(conn);
        Product product = new Product();
        product.setProduct_id(product_id);
        product.setCover_img(images);
        product.setDescription_img(images);
        productService.updateProduct(product);
        logger.info("method:{},product:{}", method, JsonTransformUtil.toJson(product));

        if (conn != null) {
            conn.close();
        }
        return true;
    }

    /**
     * 查询商品信息
     *
     * @return product
     * @throws Exception
     */
    private Product queryProduct() throws Exception {
        Connection conn = DBConnector.sql2o.open();
        ProductService productService = new ProductService(conn);

        Map<String, Object> condition = new HashMap<>();
        condition.put("product_code", productImage.getProduct_code());
        condition.put("vendor_id", productImage.getVendor_id());
        condition.put("enabled", EnabledType.USED);
        Product product = productService.getProductByCondition(condition, "*");

        if (conn != null) {
            conn.close();
        }
        return product;
    }

    public ApiUpdateProductImageService(ProductImageVo productImage) {
        this.productImage = productImage;
    }
}
