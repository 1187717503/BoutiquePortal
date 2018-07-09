package pk.shoplus.service.price.api;

import java.math.BigDecimal;
import org.sql2o.Connection;
import pk.shoplus.model.Product;
import pk.shoplus.model.Sku;

/**
 * 价格操作接口
 */
public interface IPriceService {

    /**
     * 根据商品的折扣规则计算商品的价格
     * @param product
     * @param vendorId
     * @param price
     * @param connection
     * @return
     * @throws Exception
     */
    Sku getPriceByRule(Product product, Long vendorId, BigDecimal price,Connection connection) throws Exception;

    boolean synProductPriceRule(Product product,BigDecimal newPrice,Connection conn) throws Exception;
}
