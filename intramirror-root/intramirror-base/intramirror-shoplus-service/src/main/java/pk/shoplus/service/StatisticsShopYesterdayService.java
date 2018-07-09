
package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.StatisticsShopYesterday;
import pk.shoplus.parameter.EnabledType;

/**
 * @author author : Jeff
 * @date create_at : 2016年11月17日 下午6:32:23
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class StatisticsShopYesterdayService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<StatisticsShopYesterday> statisticsShopYesterdayDao = null;

	/**
	 * @param conn
	 */
	public StatisticsShopYesterdayService(Connection conn) {
		statisticsShopYesterdayDao = new EntityDao<StatisticsShopYesterday>(conn);
	}

	public StatisticsShopYesterday getStatisticsShopYesterdayByShopId(long shopId) throws Exception {
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("shop_id", shopId);
			condition.put("enabled", EnabledType.USED);
			List<StatisticsShopYesterday> statisticsShopYesterdaylist = statisticsShopYesterdayDao
					.getByCondition(StatisticsShopYesterday.class, condition);
			if (statisticsShopYesterdaylist.size() > 0) {
				return statisticsShopYesterdaylist.get(0);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
