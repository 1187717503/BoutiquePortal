package pk.shoplus.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.ReturnApplication;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.ReturnApplicationStatusMap;
import pk.shoplus.vo.ReturnProductShow;

public class ReturnApplicationService {

	private Logger log = Logger.getLogger(ReturnApplicationService.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ReturnApplication> returnApplicationDao = null;

	/**
	 * @param conn
	 */
	public ReturnApplicationService(Connection conn) {
		returnApplicationDao = new EntityDao<ReturnApplication>(conn);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ReturnApplication getReturnApplication(long id) throws Exception {
		try {
			ReturnApplication orderReturn = returnApplicationDao.getById(ReturnApplication.class, id);
			return orderReturn;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param pageNumber
	 * @param pageSize
	 * @param fieldNames
	 * @param tableName
	 * @param whereCondition
	 * @param orderBy
	 * @param paras
	 * @return
	 * @throws Exception
	 */
	public Page getReturnInfoList(long pageNumber, long pageSize, String fieldNames, String tableName,
			String whereCondition, String orderBy, Object[] params) throws Exception {
		try {
			Page page = returnApplicationDao.getPageBySql(pageNumber, pageSize, fieldNames, tableName, whereCondition,
					orderBy, params, null, null);
			return page;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Map<String, Object>> getReturnInfoList(String start, String end) {
        String[] params = new String[2];
        params[0] = start;
        params[1] = end;

		StringBuilder sql = new StringBuilder("select ");
        sql.append("`return_application`.`return_application_id` `return_application_id`,`return_application`.`return_application_num` AS `return_application_num`,`order`.`order_id` `order_id`,`order`.`order_num`,`logistics`.`rec_name` `rec_name`,`logistics`.`rec_mobile` `rec_mobile`")
				.append(",`return_application`.`fee` `fee`,`return_application`.`return_reason`  `return_reason`,`shop`.`shop_name` `shop_name`,`return_application`.`return_type` `return_type`")
				.append(",`return_application`.`created_at` `created_at`,`return_application`.`status` `status`,`return_application`.`enabled` `enabled`,`order_logistics`.`order_logistics_id` `order_logistics_id`");
        sql.append(" from ");
		sql.append("`return_application` LEFT JOIN `order_logistics` ON (`order_logistics`.`order_logistics_id` = `return_application`.`order_logistics_id`)")
				.append(" LEFT JOIN `logistics` ON (`logistics`.`order_logistics_id` = `order_logistics`.`order_logistics_id`)")
				.append(" LEFT JOIN `order` ON (`order`.`order_id`=`order_logistics`.`order_id`)")
				.append(" LEFT JOIN `shop` ON (`shop`.`shop_id` = `return_application`.`shop_id`)");
        sql.append(" where `return_application`.enabled=true ");
        sql.append(" and `return_application`.created_at>=:p1 ");
        sql.append(" and `return_application`.created_at<=:p2 ");
        sql.append(" order by return_application.created_at desc");

        return returnApplicationDao.executeBySql(sql.toString(), params);
	}

	public Page getReturnInfoList(long pageNumber, long pageSize, String condition, String vendorId, String status,
			String period, StringBuilder queryStringBuiler) throws Exception {
		StringBuilder fileNames = new StringBuilder("");
		fileNames
				.append("`return_application`.`return_application_id` `return_application_id`,`return_application`.`return_application_num` AS `return_application_num`,`order`.`order_id` `order_id`,`order`.`order_num`,`logistics`.`rec_name` `rec_name`,`logistics`.`rec_mobile` `rec_mobile`")
				.append(",`return_application`.`fee` `fee`,`return_application`.`return_reason`  `return_reason`,`shop`.`shop_name` `shop_name`,`return_application`.`return_type` `return_type`")
				.append(",`return_application`.`created_at` `created_at`,`return_application`.`status` `status`,`return_application`.`enabled` `enabled`,`order_logistics`.`order_logistics_id` `order_logistics_id`");
		StringBuilder tableName = new StringBuilder("");

		tableName
				.append("`return_application` LEFT JOIN `order_logistics` ON (`order_logistics`.`order_logistics_id` = `return_application`.`order_logistics_id`)")
				.append(" LEFT JOIN `logistics` ON (`logistics`.`order_logistics_id` = `order_logistics`.`order_logistics_id`)")
				.append(" LEFT JOIN `order` ON (`order`.`order_id`=`order_logistics`.`order_id`)")
				.append(" LEFT JOIN `shop` ON (`shop`.`shop_id` = `return_application`.`shop_id`)");
		String defaultOrderBy = " `return_application`.return_application_id,`order`.order_id";
		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		int counter = 1;
		// 条件组合一定有enabled=true
		sb.append(" `return_application`.enabled=:p").append((counter++)).append(" ");
		params.add(1);
		if (!Helper.isNullOrEmpty(condition)) {
			try {
				/*
				 * long ifLong = -1; if (Helper.isNumeric(condition)) { ifLong =
				 * Long.parseLong(condition); }
				 */
				sb.append(" and (`return_application`.return_application_num=:p").append((counter++))
						.append(" or `logistics`.rec_name=:p").append((counter++))
						.append(" or `logistics`.rec_mobile=:p").append((counter++)).append(" or `shop`.shop_name=:p")
						.append((counter++)).append(") ");
				params.add(condition);
				params.add("" + condition + "");
				params.add(condition);
				params.add("" + condition + "");
				queryStringBuiler.append("condition=").append(condition);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// counter+=4;
		}
		if (!Helper.isNullOrEmpty(vendorId)) {
			sb.append(" and EXISTS (SELECT * FROM logistics_product tt")
					.append(" inner join shop_product_sku shop_sku on shop_sku.shop_product_sku_id=tt.shop_product_sku_id")
					.append(" inner join sku sku on sku.sku_id=shop_sku.sku_id")
					.append(" inner join product product on product.product_id=sku.product_id")
					.append(" where tt.order_logistics_id=`order_logistics`.order_logistics_id and product.vendor_id=:p")
					.append((counter++) + ") ");
			// sb.append(" and vendor_id=:p"+(counter++)+" ");
			params.add(Integer.valueOf(vendorId));
			if (queryStringBuiler.length() > 0)
				queryStringBuiler.append("&");
			queryStringBuiler.append("vendorId=").append(vendorId);
		}
		if (!Helper.isNullOrEmpty(period)) {
			String[] periodArr = period.split("to");
			Date beginDate = null;
			Date endDate = null;
			try {
				beginDate = sdf.parse(periodArr[0]);
				endDate = sdfLong.parse(periodArr[1] + " 23:59:59");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (beginDate != null && endDate != null) {
				sb.append(" and `return_application`.created_at>=:p" + (counter++) + " ");
				params.add(sdfLong.format(beginDate));
				sb.append(" and `return_application`.created_at<=:p" + (counter++) + " ");
				params.add(sdfLong.format(endDate));
			}
			if (queryStringBuiler.length() > 0)
				queryStringBuiler.append("&");
			queryStringBuiler.append("period=").append(period);
		}
		if (!Helper.isNullOrEmpty(status)) {
			Integer intStatus = ReturnApplicationStatusMap.nameValue.get(status);
			if (intStatus == null) {
				intStatus = 1;
			}
			sb.append(" and `return_application`.status=:p" + (counter++) + " ");
			params.add(intStatus);
			if (queryStringBuiler.length() > 0)
				queryStringBuiler.append("&");
			// queryStringBuiler.append("status=").append(status);
		}
		return getReturnInfoList(pageNumber, pageSize, fileNames.toString(), tableName.toString(), sb.toString(),
				defaultOrderBy, params.toArray());
	}

	public List<ReturnProductShow> getReturnProductShowByReturnApplicationId(Long returnApplicationId)
			throws Exception {
		List<ReturnProductShow> returnProductShowList = new ArrayList<ReturnProductShow>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT t2.product_id product_id,t2.`name` product_name FROM  return_product t")
				.append(" LEFT JOIN shop_product_sku t3 ON t3.`shop_product_sku_id`=t.`shop_product_sku_id`")
				.append(" LEFT JOIN sku t1 ON t3.sku_id=t1.sku_id")
				.append(" LEFT JOIN product t2 on t1.product_id=t2.product_id")
				.append(" where t.return_application_id=:p1");
		List<Map<String, Object>> list = returnApplicationDao.executeBySql(sb.toString(),
				new Object[] { returnApplicationId });
		log.debug("list.size:" + list.size());
		StringBuilder sb2 = new StringBuilder();
		sb2.append("SELECT t.*,t1.sku_code,t1.`sku_id` FROM  return_product t")
				.append(" LEFT JOIN shop_product_sku t2 ON t2.`shop_product_sku_id`=t.`shop_product_sku_id`")
				.append(" LEFT JOIN sku t1 on t2.sku_id=t1.sku_id where t1.product_id=:p1 and t.return_application_id=:p2");
		StringBuilder sb3 = new StringBuilder();
		sb3.append(
				"SELECT t.product_id,t.`name` property_name FROM product_sku_property_key t where t.enabled=1 and t.product_id=:p1");
		StringBuilder sb4 = new StringBuilder();
		sb4.append(
				"SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.`name` key_name,pro_val.`value` key_value FROM `sku_property` sku_p")
				.append(" inner JOIN product_sku_property_key pro_key on sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id and pro_key.enabled=1")
				.append(" inner JOIN product_sku_property_value pro_val on sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id and pro_val.enabled=1")
				.append(" where sku_p.sku_id=:p1");
		for (Map<String, Object> map : list) {
			ReturnProductShow returnProduct = new ReturnProductShow();
			Long productId = Long.parseLong(map.get("product_id").toString());
			List<Map<String, Object>> propertyNames = returnApplicationDao.executeBySql(sb3.toString(),
					new Object[] { productId });
			returnProduct.propertyNames = propertyNames;
			returnProduct.product_id = productId;
			returnProduct.product_name = (String) map.get("product_name");
			List<Map<String, Object>> mapList = returnApplicationDao.executeBySql(sb2.toString(),
					new Object[] { productId, returnApplicationId });
			for (Map<String, Object> sku : mapList) {
				log.debug("sku_id:" + sku.get("sku_id"));
				List<Map<String, Object>> propertyNameValues = returnApplicationDao.executeBySql(sb4.toString(),
						new Object[] { sku.get("sku_id") });
				if (Helper.checkNotNull(propertyNameValues)) {
					log.debug("propertyNameValues size:" + propertyNameValues.size());
				}
				for (Map<String, Object> skudProperty : propertyNameValues) {
					sku.put(skudProperty.get("key_name").toString(), skudProperty.get("key_value"));
				}
			}
			returnProduct.returnProductList = mapList;
			returnProductShowList.add(returnProduct);
		}

		return returnProductShowList;
	}

	/**
	 * 根据 id 获取 return_application
	 * 
	 * @param return_application_id
	 * @return
	 * @throws Exception
	 */
	public ReturnApplication getReturnApplicationById(Long return_application_id) throws Exception {
		try {
			String fieldName = "*";
			ReturnApplication returnApplication = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("return_application_id", return_application_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ReturnApplication> returnApplicationList = returnApplicationDao.getByCondition(ReturnApplication.class,
					fieldName, conditionMap);
			for (ReturnApplication temp : returnApplicationList) {
				if (temp != null) {
					returnApplication = new ReturnApplication();
					returnApplication = temp;
					break;
				}
			}
			return returnApplication;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新returnApplication信息
	 * 
	 * @throws Exception
	 */
	public void updateReturnApplication(ReturnApplication returnApplication) throws Exception {
		try {
			returnApplicationDao.updateById(returnApplication);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param returnApplication
	 * @throws Exception
	 */
	/*
	 * public Long createReturnApplication(ReturnApplication returnApplication)
	 * throws Exception { try { return
	 * returnApplicationDao.create(returnApplication);
	 * 
	 * } catch (Exception e) { throw e; } }
	 */
	/**
	 * 通过 returnapplication创建
	 */
	public ReturnApplication createReturnApplication(ReturnApplication returnApplication) throws Exception {
		try {

			Long return_application_id = returnApplicationDao.create(returnApplication);
			if (return_application_id > 0) {
				// 创建编号
				returnApplication.return_application_id = return_application_id;
				String returnApplicationNum = generateReturnApplicationNum(returnApplication.user_id,
						returnApplication.return_application_id);
				returnApplication.return_application_num = returnApplicationNum;
				returnApplicationDao.updateById(returnApplication);

			} else {
				returnApplication = null;
			}
			return returnApplication;

		} catch (Exception e) {
			throw e;
		}
	}

	// 退货单号生成规则：
	// 日期（8位）＋ 用户ID（末3位）＋ 退货单order ID（末5位）
	//
	// 例如：2014092100400023
	/**
	 * 订单号生成规则：
	 * 
	 * @param userId
	 * @param orderId
	 * @return
	 */
	private String generateReturnApplicationNum(Long userId, Long returnApplicationId) {
		String userStr = "000" + userId;
		String returnApplicationStr = "00000" + returnApplicationId;
		userStr = userStr.substring(userStr.length() - 3, userStr.length());
		returnApplicationStr = returnApplicationStr.substring(returnApplicationStr.length() - 5,
				returnApplicationStr.length());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return simpleDateFormat.format(new Date()) + userStr + returnApplicationStr;
	}

	/**
	 * 检查已有退换申请
	 * 
	 * @param orderLogisticsId
	 * @return
	 * @throws Exception
	 */
	public boolean hasReturn(Long orderLogisticsId) throws Exception {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		// 添加条件
		conditionMap.put("order_logistics_id", orderLogisticsId);
		conditionMap.put("enabled", EnabledType.USED);

		try {
			List<ReturnApplication> returnApplicationList = returnApplicationDao.getByCondition(ReturnApplication.class,
					"*", conditionMap);
			if (returnApplicationList != null && returnApplicationList.size() > 0) {
				return true;
			}

		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	public List<Map<String, Object>> getCountReturnApplicationByShopIdAndStatus(long shop_id) throws Exception{
		StringBuilder sql = new StringBuilder("");
		sql.append("select status,  count(status) as statusCount ");
		sql.append("FROM return_application where shop_id = :p1 and enabled =:p2 group by status ");
		List<Map<String, Object>> orderCountList = returnApplicationDao.executeBySql(sql.toString(), new Object[]{shop_id, EnabledType.USED});
		return orderCountList;
	}
	
	public Page getReturnApplicationByShopIdAndStatus(long shop_id, int status, long pageNumber, long pageSize) {
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT o.order_num, u.username, ra.status, l.rec_mobile, sps.coverpic, ol.order_logistics_id, ");
		sql.append("ra.return_application_id, count(o.order_num) as product_num, ");
		sql.append("ol.ship_fee, o.pay_fee, sum((lp.sale_price - sku.price) * lp.amount) as revenue, sum(lp.in_price * lp.amount) as cost, ");
		sql.append("l.rec_addr, l.rec_province, l.rec_city, o.updated_at ");
		sql.append("FROM return_application as ra ");
		sql.append("left join order_logistics as ol on ol.order_logistics_id = ra.order_logistics_id ");
		sql.append("left join `order` as o on o.order_id = ol.order_id ");
		sql.append("left join user as u on u.user_id = o.user_id ");
		sql.append("left join logistics as l on l.order_logistics_id = ol.order_logistics_id ");
		sql.append("left join logistics_product as lp on lp.order_logistics_id = ol.order_logistics_id ");
		sql.append("left join shop_product_sku as sps on sps.shop_product_sku_id = lp.shop_product_sku_id ");
		sql.append("left join sku on sku.sku_id = sps.sku_id ");
		sql.append("where ra.status = :p1 and ol.shop_id = :p2 and ra.enabled = :p3 ");
		sql.append("group by o.order_num ");

		// 拼MySQL分页
		//long offset = pageSize * pageNumber;
		//sql.append(" limit ").append(offset);
		// executeSql
		List<Map<String, Object>> orderMapList = returnApplicationDao.executeBySql(sql.toString(), new Object[]{status, shop_id, EnabledType.USED});
		return new Page(orderMapList, pageNumber, pageSize);
	}

	// 获取return_application 列表
	public Page getReturnApplicationPageWithUserName(long pageNumber, long pageSize, Map<String, Object> whereCondition,
			String orderBy, String whereStringInput) throws Exception {
		try {
			// 1. condition转换成 wherestr
			StringBuilder whereSql = new StringBuilder("");
			List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

			// 先计数
			// count sql: select count(*) from category where parent_id=:p1 and
			// enabled=:p2

			StringBuilder countSql = new StringBuilder("select count(*) from return_application a");
			countSql.append(whereSql);
			if (whereStringInput.length() > 0) {
				countSql.append(" and ").append(whereStringInput);
			}
			System.out.println("count sql: " + countSql.toString());

			// 1. 获取 总数
			Long totalRow = returnApplicationDao.createQuery(countSql.toString(), params.toArray())
					.executeScalar(Long.class);
			if (totalRow == null || totalRow <= 0) {
				return new Page(null, pageNumber, pageSize, 1l, 0l);
			}

			// 2. 获取 分页
			StringBuilder sql = new StringBuilder("");

			// SELECT a.*, b.order_logistics_id, c.user_id, c.username,
			// d.rec_addr, d.rec_area, d.rec_city, d.rec_province, d.rec_name,
			// d.rec_mobile FROM return_application a
			// LEFT JOIN order_logistics b on a.order_logistics_id =
			// b.order_logistics_id
			// LEFT JOIN `user` c on a.user_id = c.user_id
			// LEFT JOIN logistics d on b.logistics_id = d.logistics_id where
			// a.return_application_id = 1;
			sql.append(
					"SELECT a.*, b.order_logistics_id, c.user_id, c.username, d.rec_addr, d.rec_area, d.rec_city, d.rec_province, d.rec_name, d.rec_mobile FROM return_application a ");
			sql.append("LEFT JOIN order_logistics b on a.order_logistics_id = b.order_logistics_id ");
			sql.append("LEFT JOIN `user` c on a.user_id = c.user_id ");
			sql.append("LEFT JOIN logistics d on b.logistics_id = d.logistics_id ");
			sql.append(whereSql);

			if (whereStringInput.length() > 0) {
				sql.append(" and ").append(whereStringInput);
			}
			// order by
			if (!Helper.isNullOrEmpty(orderBy)) {
				sql.append(" order by ").append(orderBy).append(" desc ");
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
			List<Map<String, Object>> orderMapList = returnApplicationDao.executeBySql(sql.toString(),
					params.toArray());
			return new Page(orderMapList, pageNumber, pageSize, totalPage, totalRow);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// todo customer 获取 return application
	public Page getReturnApplicationPageWithShopInfoByCustomer(long pageNumber, long pageSize,
			Map<String, Object> whereCondition, String orderBy, String whereStringInput) throws Exception {
		try {
			// 1. condition转换成 wherestr
			StringBuilder whereSql = new StringBuilder("");
			List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

			// 先计数
			// count sql: select count(*) from category where parent_id=:p1 and
			// enabled=:p2

			StringBuilder countSql = new StringBuilder("select count(*) from return_application a");
			countSql.append(whereSql);
			if (whereStringInput.length() > 0) {
				countSql.append(" and ").append(whereStringInput);
			}
			System.out.println("count sql: " + countSql.toString());
			// 1. 获取 总数
			Long totalRow = returnApplicationDao.createQuery(countSql.toString(), params.toArray())
					.executeScalar(Long.class);
			if (totalRow == null || totalRow <= 0) {
				return new Page(null, pageNumber, pageSize, 1l, 0l);
			}

			// 2. 获取 分页
			StringBuilder sql = new StringBuilder("");
			// SELECT a.return_application_id as r_application_id,
			// a.return_type, a.return_reason, a.description, a.pics,
			// a.shop_reply, a.mall_reply, a.appeal_reason, b.*, sh.shop_name,
			// sh.shop_pic FROM return_application a LEFT JOIN order_logistics b
			// on a.order_logistics_id = b.order_logistics_id LEFT JOIN shop sh
			// on a.shop_id = sh.shop_id WHERE a.user_id = 1;

			sql.append(
					"SELECT a.status as r_application_status, a.return_application_id as r_application_id, a.user_id as r_user_id, a.return_application_num ,a.return_type, a.return_reason, a.description, a.pics, a.shop_reply, a.mall_reply,a.appeal_status, a.appeal_reason, b.*, sh.shop_name, sh.shop_pic  FROM return_application a ");
			sql.append("LEFT JOIN order_logistics b on a.order_logistics_id = b.order_logistics_id ");
			sql.append("LEFT JOIN shop sh on a.shop_id = sh.shop_id ");
			sql.append(whereSql);

			if (whereStringInput.length() > 0) {
				sql.append(" and ").append(whereStringInput);
			}
			// order by
			if (!Helper.isNullOrEmpty(orderBy)) {
				sql.append(" order by ").append(orderBy).append(" desc");
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
			List<Map<String, Object>> orderMapList = returnApplicationDao.executeBySql(sql.toString(),
					params.toArray());
			return new Page(orderMapList, pageNumber, pageSize, totalPage, totalRow);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	// 根据状态获取return application 的数量统计
	public List<Map<String, Object>> getReturnApplicationNumberByStatus(String whereStringInput) throws Exception {

		// SELECT count(*), `status` FROM `return_application` where shop_id = 1
		// GROUP BY `status` ;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT count(*), `status` FROM `return_application` ");
			sql.append(whereStringInput);
			sql.append("GROUP BY `status`");

			result = returnApplicationDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

}
