package pk.shoplus.service;

import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.Sku;
import pk.shoplus.parameter.EnabledType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkuService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<Sku> skuDao = null;

	/**
	 * @param conn
	 */
	public SkuService(Connection conn) {
		skuDao = new EntityDao<Sku>(conn);
	}

	/**
	 * 创建 sku 数据
	 *
	 * @param sku
	 * @return
	 * @throws Exception
	 */
	public Sku createSku(Sku sku) throws Exception {
		try {
			Long sku_id = skuDao.create(sku);
			if (sku_id > 0) {
				sku.sku_id = sku_id;
			} else {
				sku = null;
			}
			return sku;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改sku信息
	 *
	 * @param sku
	 * @throws Exception
	 */
	public void updateSku(Sku sku) throws Exception {
		try {
			skuDao.updateById(sku);
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateBySQL(String sql) throws Exception {
		try {
			skuDao.updateBySQL(sql,null);
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * 通过sku_id 获取 Sku
	 *
	 * @param sku_id
	 * @return
	 * @throws Exception
	 */
	public Sku getSkuById(Long sku_id) throws Exception {
		try {
			Sku sku = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("sku_id", sku_id);
			conditionMap.put("enabled", EnabledType.USED);
			// 查询
			List<Sku> skuList = skuDao.getByCondition(Sku.class, conditionMap);
			for (Sku temp : skuList) {
				if (temp != null) {
					sku = new Sku();
					sku = temp;
					break;
				}
			}
			return sku;
		} catch (Exception e) {
			throw e;
		}
	}

	private static Logger logger = Logger.getLogger(SkuService.class);
	public List<Map<String,Object>> getSkuByProductId(String product_id) throws Exception {
		try {
			String sql = "select * from sku where enabled=1 and product_id ='" +product_id + "'";
			logger.info("getSkuByCode : "+sql);
			return skuDao.executeBySql(sql,null);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件获取 sku 列表
	 *
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<Sku> getSkuListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<Sku> skuList = skuDao.getByCondition(Sku.class, fieldName, condition);
			return skuList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取最高/最低价格
	 *
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getSkuMaxAndMinPrice(Map<String, Object> condition) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuilder whereStr = new StringBuilder();
		List<Object> params = DaoHelper.appendCondition(whereStr, condition, "");
		// 拼sql语句
		String fieldNames = "price";
		String tableNames = "sku";
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append("max(" + fieldNames + "), min(" + fieldNames + ")").append(" from ")
				.append(tableNames).append(whereStr);
		// executeBySql
		List<Map<String, Object>> resultList = skuDao.executeBySql(sql.toString(), params.toArray());
		result.put("min", resultList.get(0).get("min(price)"));
		result.put("max", resultList.get(0).get("max(price)"));
		return result;
	}

	// 根据product获取sku中最高价格以及最低价格
	public List<Map<String, Object>> getSkuListWithMaxAndMinPriceByProduct(String whereStringInput) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// StringBuilder whereSql = new StringBuilder("");
			// List<Object> params = DaoHelper.appendCondition(whereSql,
			// whereProductCondition, "a");

			// 2. 获取数据
			// SELECT k.product_id, MAX(price), MIN(price) FROM sku k WHERE
			// EXISTS (SELECT a.product_id FROM product a where k.product_id =
			// a.product_id and (a.product_id = 27 or a.product_id = 2) ) GROUP
			// BY product_id
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT k.product_id, MAX(price), MIN(price),MAX(in_price), MIN(in_price) FROM sku k ");
			sql.append("WHERE k.enabled = 1 and EXISTS (");
			sql.append("SELECT a.product_id FROM product a where k.product_id = a.product_id");
			if (whereStringInput.length() > 0) {
				sql.append(" and (" + whereStringInput + ") ");
			}
			sql.append(") GROUP BY product_id");

			System.out.println("excutesql:" + sql.toString());

			result = skuDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 获取sku已经相关库存信息
	 *
	 * @param pageNumber
	 * @param pageSize
	 * @param whereCondition
	 * @param fieldNames
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Page getSkuPageWithStore(long pageNumber, long pageSize, Map<String, Object> whereCondition,
									String fieldNames, String orderBy) throws Exception {
		// SELECT a.*, b.store, b.remind, b.ordered, b.confirm, b.ship,
		// b.finished, b.returned, b.changed, b.clear, b.agree_return_rate FROM
		// sku a LEFT JOIN sku_store b on a.sku_id = b.sku_id where a.product_id
		// = 1 ;
		try {
			// 1. condition转换成 wherestr
			StringBuilder whereSql = new StringBuilder("");
			List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

			// 先计数
			// count sql: select count(*) from category where parent_id=:p1 and
			// enabled=:p2

			StringBuilder countSql = new StringBuilder("select count(*) from sku a");
			countSql.append(whereSql);
			System.out.println("count sql: " + countSql.toString());

			// 1. 获取 总数
			Long totalRow = skuDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);

			// System.out.println("sku totalRow = " + totalRow);

			if (totalRow == null || totalRow <= 0) {
				return new Page(new ArrayList<Map<String, Object>>(), pageNumber, pageSize, 1l, 0l);
			}

			// 2. 获取 分页
			// excutesql:select * from category where parent_id=:p1 and
			// enabled=:p2 order by category_id limit 0,10
			StringBuilder sql = new StringBuilder("");
			if (Helper.isNullOrEmpty(fieldNames)) {
				fieldNames = "*";
			}

			// SELECT a.*, b.store, b.remind, b.ordered, b.confirm, b.ship,
			// b.finished, b.returned, b.changed, b.clear, b.agree_return_rate
			// FROM sku a LEFT JOIN sku_store b on a.sku_id = b.sku_id where
			// a.product_id = 1 ;

			sql.append(
					"SELECT a.*, b.store, b.remind, b.ordered, b.confirm, b.ship, b.finished, b.returned, b.changed, b.clear, b.agree_return_rate FROM sku a ");
			sql.append("LEFT JOIN sku_store b on a.sku_id = b.sku_id ");
			sql.append(whereSql);

			// order by
			if (!Helper.isNullOrEmpty(orderBy)) {
				sql.append(" order by ").append(orderBy);
			}

			// pageSize或者pageNumber
			// 如果小于1就返回全部数据
			if (pageSize < 1) {
				return new Page();
				// return new Page(categoryDao.executeBySql(sql.toString(),
				// params.toArray()), pageNumber, pageSize, 1l, totalRow);
			}

			if (pageNumber < 1) {
				return new Page();
				// throw new Exception("pageNumber must be more than 1!!");
			}

			// 计数
			long totalPage = totalRow / pageSize;
			if (totalRow % pageSize != 0) {
				totalPage++;
			}
			if (pageNumber > totalPage) {
				return new Page();
				// throw new Exception("pageNumber must be less than
				// totalPage!!");
			}

			// 拼MySQL分页
			long offset = pageSize * (pageNumber - 1);
			sql.append(" limit ").append(offset).append(",").append(pageSize);
			System.out.println("excutesql:" + sql.toString());
			// executeSql
			List<Map<String, Object>> productMapList = skuDao.executeBySql(sql.toString(), params.toArray());
			return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	// 根据product的条件获取sku以及 store 信息
	public List<Map<String, Object>> getSkuListWithStoreByProduct(String whereStringInput) throws Exception {
		// SELECT a.* from sku a LEFT JOIN sku_store b on a.sku_id = b.sku_id
		// WHERE EXISTS (SELECT t.product_id FROM product t where a.product_id =
		// t.product_id and (t.product_id = 1 or t.product_id = 2))

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// StringBuilder whereSql = new StringBuilder("");
			// List<Object> params = DaoHelper.appendCondition(whereSql,
			// whereProductCondition, "a");

			// 2. 获取数据
			// SELECT s.* from sku s LEFT JOIN sku_store b on s.sku_id =
			// b.sku_id WHERE EXISTS (SELECT a.product_id FROM product a where
			// s.product_id = a.product_id and (t.product_id = 1 or t.product_id
			// = 2))
			StringBuilder sql = new StringBuilder("");
			sql.append(
					"SELECT s.*, b.store, b.remind, b.ordered, b.confirm, b.ship, b.finished, b.returned, b.changed, b.clear, b.agree_return_rate from sku s ");
			sql.append("LEFT JOIN sku_store b on s.sku_id = b.sku_id ");
			sql.append("WHERE b.enabled = 1 and s.enabled = 1 and EXISTS (");
			sql.append("SELECT a.product_id FROM product a where s.product_id = a.product_id");
			if (whereStringInput.length() > 0) {
				sql.append(" and (" + whereStringInput + ") ");
			}
			sql.append(")");

			System.out.println("excutesql:" + sql.toString());

			result = skuDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public void dedeteSkuByCondition(Map<String, Object> condition) throws Exception {
		try {
			// delete
			skuDao.deleteByCondition(Sku.class, condition);
		} catch (Exception e) {
			throw e;
		}
	}


	// App根据product的条件获取sku以及 store 信息
	public List<Map<String, Object>> getAppSkuListWithStoreByProduct(String whereStringInput) throws Exception {
//		SELECT s.*, b.store, b.remind, b.ordered, b.confirm, b.ship, b.finished, b.returned, b.changed, b.clear, b.agree_return_rate
//		from sku s
//		LEFT JOIN sku_store b on s.sku_id = b.sku_id
//		LEFT JOIN product a ON a.product_id =s.product_id
//		where (a.product_id = 341 or a.product_id = 342 or a.product_id = 343 or a.product_id = 344 or a.product_id = 345 or a.product_id = 346 or a.product_id = 347 or a.product_id = 348 or a.product_id = 349 or a.product_id = 350) ;

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append(
					"SELECT s.*, b.store, b.remind, b.ordered, b.confirm, b.ship, b.finished, b.returned, b.changed, b.clear, b.agree_return_rate from sku s ");
			sql.append("LEFT JOIN sku_store b on s.sku_id = b.sku_id ");
			sql.append("LEFT JOIN product a ON a.product_id =s.product_id ");
			if (whereStringInput.length() > 0) {
				sql.append(" where (" + whereStringInput + ") ");
			}

			System.out.println("excutesql:" + sql.toString());

			result = skuDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}


}
