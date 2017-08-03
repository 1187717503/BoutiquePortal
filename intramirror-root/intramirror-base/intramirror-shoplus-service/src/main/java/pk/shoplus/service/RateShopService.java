package pk.shoplus.service;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.RateShop;

public class RateShopService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<RateShop> rateShopDao = null;

	/**
	 * @param conn
	 */
	public RateShopService(Connection conn) {
		rateShopDao = new EntityDao<RateShop>(conn);
	}

	/**
	 * 
	 * @param Comment
	 * @throws Exception
	 */
	public RateShop createRateShop(RateShop rateShop) throws Exception {
		try {
			Long rate_shop_id = rateShopDao.create(rateShop);
			if (rate_shop_id > 0) {
				rateShop.rate_shop_id = rate_shop_id;
			} else {
				rateShop = null;
			}
			return rateShop;
		} catch (Exception e) {
			throw e;
		}
	}
}
