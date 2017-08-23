package pk.shoplus.service.product.api;

import org.sql2o.Connection;
import pk.shoplus.model.Category;
import pk.shoplus.model.Product;
import pk.shoplus.model.ProductEDSManagement;

import java.util.Map;

/**
 * @Author: DingYiFan
 * @Description: 商品接口
 * @Date: Create in 13:31 2017/5/25
 * @Modified By:
 */
public interface IProductService {

    /**
     * 修改商品
     * @param productOptions
     * @param vendorOptions
     * @return
     */
    public Map<String,Object> updateProduct(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions);


    /**
     * 新增sku信息
     * @param conn
     * @param product
     * @param category
     * @param skuOption
     * @param salePrice
     * @param vendorId
     * @return
     * @throws Exception
     */
    public void updateProductByAddSku(Connection conn, Product product, Category category, ProductEDSManagement.SkuOptions skuOption, String salePrice, Long vendorId, ProductEDSManagement.ProductOptions productOptions) throws Exception ;
}
