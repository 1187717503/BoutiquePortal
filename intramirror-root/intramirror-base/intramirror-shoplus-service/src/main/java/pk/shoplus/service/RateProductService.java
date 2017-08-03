package pk.shoplus.service;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.RateProduct;

/**
 * RateProduct service
 * @author Caijl
 *
 */
public class RateProductService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<RateProduct> rateProductDao = null;

	/**
	 * @param conn
	 */
	public RateProductService(Connection conn) {
		rateProductDao = new EntityDao<RateProduct>(conn);
	}
	
	/**
	 * 创建rate product
	 * @param rateProduct
	 * @return
	 * @throws Exception
	 */
	public RateProduct createRateProduct(RateProduct rateProduct) throws Exception {
		try {
			Long rate_product_id = rateProductDao.create(rateProduct);
			if (rate_product_id > 0) {
				rateProduct.rate_product_id = rate_product_id;
			} else {
				rateProduct = null;
			}
			return rateProduct;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据条件查找RateProductList
	 */
	public List<RateProduct> getRateProductListByCondition(Map<String, Object> conditionMap)
			throws Exception {
		try {
			// 查询
			List<RateProduct> rateProductList = rateProductDao.getByCondition(RateProduct.class,
					conditionMap);
			return rateProductList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * 通过condition获取平均rate //todo
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public double getRateAverageByCondition(Map<String, Object> condition) throws Exception{
		
		
		return 0;
	}
}
