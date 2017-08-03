
package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.StatisticsShopOperationDaily;

/**
 * @author author : Jeff
 * @date create_at : 2016年11月18日 下午12:07:27
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class StatisticsShopOperationDailyService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<StatisticsShopOperationDaily> statisticsShopOperationDailyDao = null;

	/**
	 * @param conn
	 */
	public StatisticsShopOperationDailyService(Connection conn) {
		statisticsShopOperationDailyDao = new EntityDao<StatisticsShopOperationDaily>(conn);
	}

	public List<Map<String, Object>> getThirtyStatisticsShopOperationDailyDataByShopId(long shopId) throws Exception {
		try {
			String sql = "SELECT DATE_FORMAT(ssod.account_date,'%Y-%m-%d') AS date,ssod.revenue,order_amount,return_amount FROM "
					+ " statistics_shop_operation_daily ssod"
					+ " WHERE ssod.shop_id=:p1 AND account_date >= DATE_SUB(CURDATE(),INTERVAL 30 DAY)";
			List<Map<String, Object>> statisticsShopOperationDailyList = statisticsShopOperationDailyDao
					.executeBySql(sql, new Object[] { shopId });
			if (!Helper.checkNotNull(statisticsShopOperationDailyList)
					|| statisticsShopOperationDailyList.size() == 0) {
				return null;
			}
			return statisticsShopOperationDailyList;
		} catch (Exception e) {
			throw e;
		}

	}
}
