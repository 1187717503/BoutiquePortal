package pk.shoplus.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.enums.OrderStatusEnum;
import pk.shoplus.model.LogisticsProduct;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EdsOrderStatusType;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.OrderStatusMap;
import pk.shoplus.parameter.OrderStatusType;
import pk.shoplus.parameter.StatusType;

public class LogisticsProductService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<LogisticsProduct> logisticsProductDao = null;
	
	 private final Logger logger = Logger.getLogger(LogisticsProductService.class);

	/**
	 * @param conn
	 */
	public LogisticsProductService(Connection conn) {
		logisticsProductDao = new EntityDao<LogisticsProduct>(conn);
	}

	/**
	 * @param logisticsProduct
	 * @throws Exception
	 */
	public LogisticsProduct createLogisticsProduct(LogisticsProduct logisticsProduct) throws Exception {
		try {
			Long logistics_product_id = logisticsProductDao.create(logisticsProduct);
			if (logistics_product_id > 0) {
				logisticsProduct.logistics_product_id = logistics_product_id;
			} else {
				logisticsProduct = null;
			}
			return logisticsProduct;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition map 来获取 OrderLogistics list
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<LogisticsProduct> getLogisticsProductListByCondition(Map<String, Object> conditionMap)
			throws Exception {
		try {
			// 查询
			List<LogisticsProduct> logisticsProductList = logisticsProductDao.getByCondition(LogisticsProduct.class,
					conditionMap);
			return logisticsProductList;
		} catch (Exception e) {
			throw e;
		}
	}

	// 通过order_logi 获取 logi_product_list
	public List<Map<String, Object>> getLogisticsProductListByOrderLogisticsId(long order_logistics_id)
			throws Exception {
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append(
					"select b.*, s.coverpic, s.sku_id, s.sku_code, s.`name` as sku_name, c.product_id, p.product_code, c.coverpic as cover_img, p.`name` as product_name from logistics_product b ");
			sql.append("left join order_logistics ol on b.order_logistics_id = ol.order_logistics_id ");
			sql.append(
					"left join shop_product_sku sps on (b.shop_product_sku_id = sps.shop_product_sku_id and ol.shop_id = sps.shop_id) ");
			sql.append(
					"left join shop_product c on (sps.shop_product_id = c.shop_product_id and ol.shop_id = c.shop_id) ");
			sql.append("left join sku s on s.sku_id = sps.sku_id ");
			sql.append("left join product p on c.product_id = p.product_id ");
			sql.append("where exists (");
			sql.append(
					"select a.order_logistics_id from order_logistics a where a.order_logistics_id = b.order_logistics_id ");
			sql.append("and a.order_logistics_id = " + order_logistics_id + ")");
			System.out.println("excutesql:" + sql.toString());
			List<Map<String, Object>> list = logisticsProductDao.executeBySql(sql.toString(), null);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 订单分组列表分页查询
	public Page getOrderListByParam(Map param, Integer sIndex, Integer eIndex)
			throws Exception {
		try {
			List<String> orderNumList = null;
			List<String> nameList = null;
			List<Map<String, String>> dateList = null;
			List<String> vendorList = null;
			List<String> statusList = null;
			List<String> productCodeList = null;

			if (null != param) {
				if (null != param.get("orderNumList")) {
					orderNumList = (List<String>) param.get("orderNumList");
				}
				if (null != param.get("nameList")) {
					nameList = (List<String>) param.get("nameList");
				}
				if (null != param.get("dateList")) {
					dateList = (List<Map<String, String>>) param.get("dateList");
				}
				if (null != param.get("vendorList")) {
					vendorList = (List<String>) param.get("vendorList");
				}
				if (null != param.get("statusList")) {
					statusList = (List<String>) param.get("statusList");
				}

				if (null != param.get("productCodeList")) {
					productCodeList = (List<String>) param.get("productCodeList");
				}
			}


			String fileds = " o.order_id, lp.vendor_id,v.vendor_name,o.order_num,o.`status`,o.created_at,sum(lp.amount) as amount,sum(o.fee) as fee,sum(o.ship_fee) as ship_fee,sum(lp.tax_fee) as tax_fee,g.name,l.rec_name ";
			StringBuilder sql = new StringBuilder("select " + fileds);
			sql.append(" from logistics_product lp \n" +
					" left join logistics l on lp.order_logistics_id = l.order_logistics_id\n" +
					" left join user_ship_address usa on (trim(usa.province) = trim(l.rec_province) and trim(usa.city) = trim(l.rec_city))\n" +
					" left join user_ship_address_geogrophy usag on usa.user_ship_address_id = usag.user_ship_address_id\n" +
					" left join geography g on g.geography_id = usag.geography_id\n" +
					" left join order_logistics ol on lp.order_logistics_id = ol.order_logistics_id\n" +
					" left join `order` o on ol.order_id = o.order_id\n" +
					" inner join vendor v on v.vendor_id = lp.vendor_id ")
				.append(" where 1=1 ");

			if (null != orderNumList && orderNumList.size() > 0) {
				sql.append(" and o.order_num in ("+converListToString(orderNumList)+")  ");
			}
			if (null != nameList && nameList.size() > 0) {
//				sql.append(" and l.rec_name in ("+converListToString(nameList)+") ");
				
				String conSql = " and ( ";
				for(String filed : nameList){
					conSql += " l.rec_name like ('%" + filed +"%') or ";
				}
				conSql = conSql.substring(0,conSql.length() -3);
				conSql += " ) ";
				sql.append(conSql);
			}
			if (null != dateList && dateList.size() > 0) {
				String dateWhereStr = "";
				StringBuffer datesb = new StringBuffer();
				for (Map<String, String> map : dateList) {
					String starDate = map.get("startDate").toString();
					String endDate = map.get("endDate").toString();
					datesb.append("\t or (o.created_at >= date_format('"+starDate+"', '%Y-%m-%d %H:%i:%s') and o.created_at <= date_format('"+endDate+"', '%Y-%m-%d %H:%i:%s'))\n");
				}
				dateWhereStr = datesb.toString().replaceFirst("or", "");
				sql.append("and ("+dateWhereStr+")");
			} else {
//				sql.append(" and (o.created_at >= date_format(date_sub(curdate(),interval 1 month), '%Y-%m-%d') and o.created_at <= date_format(NOW(), '%Y-%m-%d'))\n");
			}
			if (null != vendorList && vendorList.size() > 0) {
				sql.append(" and v.vendor_id in ("+converListToString(vendorList)+") ");
			}
			if (null != statusList && statusList.size() > 0) {
				sql.append(" and lp.`status` in ("+converListToString(statusList)+")");
			}
			if (null != productCodeList && productCodeList.size() > 0) {
				sql.append(" and exists (\n" +
						"\tselect p.product_id from product p \n" +
						"\tinner join sku s on p.product_id = s.product_id\n" +
						"\tinner join shop_product spr on spr.product_id = p.product_id\n" +
						"\tinner join shop_product_sku sps on sps.sku_id = s.sku_id\n" +
						"\twhere lp.shop_product_sku_id = sps.shop_product_sku_id and \n" +
						"\tp.product_code in ("+converListToString(productCodeList)+")\n" +
						")");
			}
			sql.append(" group by lp.vendor_id,o.order_num order by o.created_at desc ");
			String pageing = " limit "+ sIndex + "," + eIndex;
			sql.append(pageing);
			System.out.println("excutesql:" + sql.toString());
			List<Map<String, Object>> list = logisticsProductDao.executeBySql(sql.toString(), null);
			
			// 获取总行数
			String oldSQL = sql.toString();
//			oldSQL = oldSQL.replace(fileds, " count(1) as count ");
			oldSQL = oldSQL.replace(pageing, "");
			oldSQL = "select count(1) as count from (" + oldSQL + ") as s";
			
			List<Map<String, Object>> rowsList = logisticsProductDao.executeBySql(oldSQL.toString(), null);
			Integer rows = 0;
			if(rowsList != null && rowsList.size() != 0){
				rows = rowsList.get(0).get("count")==null?0:Integer.parseInt(rowsList.get(0).get("count").toString());
			}
			return new Page(list,rows);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// 订单分组列表分页查询
	public Page getOrderListLimit(Page page, String whereSql,List<Object> list,String joinSql) throws Exception {
		try {
			String fieldNames = "DISTINCT o.order_num,o.order_id,o.created_at,o.`status`,o.current_rate,o.total_sale_price_rmb,o.total_shipping_fee_rmb,o.total_tax_rmb,o.total_rmb, ";
			fieldNames +="l.rec_name,l.rec_mobile,l.user_rec_name,l.user_rec_mobile,l.user_rec_province,l.user_rec_city,l.user_rec_area,l.user_rec_addr,v.vendor_id, ";
			fieldNames +="u.contact_person_name,o.contact_phone as telephone,o.wechat as contact_info,g.`name` as geography_name ";
			StringBuilder tableName = new StringBuilder("");
			tableName.append("logistics_product lp  ");
			tableName.append("INNER JOIN order_logistics ol ON lp.order_logistics_id = ol.order_logistics_id ");
			tableName.append("INNER JOIN `order` o ON ol.order_id = o.order_id ");
			tableName.append("LEFT JOIN `user` u ON o.user_id = u.user_id ");
			tableName.append("LEFT JOIN logistics l ON lp.order_logistics_id = l.order_logistics_id ");
			tableName.append("INNER JOIN shop_product_sku sps ON lp.shop_product_sku_id = sps.shop_product_sku_id ");
			tableName.append("INNER JOIN shop_product sp ON sps.shop_product_id = sp.shop_product_id ");
			tableName.append("INNER JOIN product p ON sp.product_id = p.product_id ");
//			tableName.append("LEFT JOIN product_property pp ON p.product_id = pp.product_id ");
			tableName.append(joinSql);
			tableName.append("LEFT JOIN category c ON p.category_id = c.category_id ");
			tableName.append("LEFT JOIN brand b ON p.brand_id = b.brand_id ");
			tableName.append("LEFT JOIN vendor v ON lp.vendor_id = v.vendor_id ");
			tableName.append("LEFT JOIN geography g ON l.geography_id = g.geography_id ");

			
			Object[] params = list.toArray();
			
			String groupBy = " o.order_num ";
			String countCondition = " o.order_num ";
			String orderBy = "o.created_at desc";
		
			page = logisticsProductDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames,
					tableName.toString(), whereSql, orderBy, params, countCondition, groupBy);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	
	
	// 根据orderNumber获取分组下的订单列表
	public List<Map<String, Object>> getOrderListByOrderNumber(String paramObj,String whereSql,List<Object> listParamt)
			throws Exception {
		try {
			StringBuilder sql = new StringBuilder("\n" +
					"SELECT  o.order_num,\n" +
					"o.order_id,\n" +
					"p.name,\n" +
					"pp.colorCode,\n" +
					"pp.brandID,\n" +
					"pspv.`value` as size,\n" +
					"p.category_id,\n" +
					"s.price,\n" +
					"s.in_price,\n" +
					"sps.sale_price,\n" +
					"lp.amount,\n" +
					"lp.shipping_fee,\n" +
					"lp.tax_fee,\n" +
					"lp.order_line_num,\n" +
					"lp.`status`,\n" +
					"v.vendor_name,\n" +
					"p.cover_img,\n" +
					"lp.logistics_product_id " +
					" FROM logistics_product lp ");
			sql.append(" INNER JOIN order_logistics ol ON lp.order_logistics_id = ol.order_logistics_id ");
			sql.append(" INNER JOIN `order` o ON ol.order_id = o.order_id  ");
			sql.append(" LEFT JOIN logistics l ON lp.order_logistics_id = l.order_logistics_id  ");
			sql.append(" INNER JOIN shop_product_sku sps ON lp.shop_product_sku_id = sps.shop_product_sku_id ");
			sql.append(" INNER JOIN shop_product sp ON sps.shop_product_id = sp.shop_product_id  ");
//			sql.append(" INNER JOIN product p ON (sp.product_id = p.product_id and p.enabled = 1 ) ");
			sql.append(" INNER JOIN product p ON (sp.product_id = p.product_id ) ");
			sql.append(" INNER JOIN sku s ON (s.sku_id = sps.sku_id and s.enabled = 1) ");
			sql.append(" INNER JOIN sku_property skup on (skup.sku_id = s.sku_id and skup.enabled = 1) ");
			sql.append(" INNER JOIN product_sku_property_key pspk ON (\n" +
					"\tpspk.product_id = p.product_id \n" +
					"\tand pspk.product_sku_property_key_id = skup.product_sku_property_key_id \n" +
					"\tand pspk.enabled = 1\n" +
					")");
			sql.append(" INNER JOIN product_sku_property_value pspv ON (\n" +
					"\tpspv.product_sku_property_key_id = skup.product_sku_property_key_id \n" +
					"\tand pspv.product_sku_property_value_id = skup.product_sku_property_value_id \n" +
					"\tand pspv.enabled = 1\n" +
					")");
			sql.append(" INNER JOIN vendor v ON (lp.vendor_id = v.vendor_id and v.enabled = 1) ");
			sql.append(" LEFT JOIN (\n" +
					"\tSELECT t_pp.product_id,max(case t_pp.key_name when 'ColorCode' then t_pp.`value` else '' end) as colorCode,\n" +
					"\tmax(case t_pp.key_name when 'BrandID' then t_pp.`value` else '' end) as brandID \n" +
					"\tfrom product_property t_pp \n" +
					"\twhere t_pp.enabled = 1 and \n" +
					"\t(t_pp.key_name='BrandID' or t_pp.key_name = 'ColorCode') \n" +
					"\tgroup by t_pp.product_id \n" +
					") pp ON p.product_id = pp.product_id  ");
			sql.append("WHERE o.order_num in("+paramObj+") ");
			sql.append(whereSql);
			sql.append(" GROUP BY lp.order_line_num");
			
			System.out.println("excutesql:" + sql.toString());
			
			Object[] params = listParamt.toArray();
			
			List<Map<String, Object>> list = logisticsProductDao.executeBySql(sql.toString(), params);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	

	// 获取分组下的订单列表
	public List<Map<String, Object>> getOrderListByGroup(Object [] paramObj)
			throws Exception {
		try {
			StringBuilder sql = new StringBuilder("select distinct lp.logistics_product_id,p.product_code,p.name,pspv.`value`,p.cover_img,lp.`status`,lp.amount,lp.sale_price as fee,lp.shipping_fee,lp.tax_fee,s.sku_code,pp.`value` as brand_code");
			sql.append(" from product p \n" +
					" inner join sku s on p.product_id = s.product_id\n" +
					" inner join shop_product spr on spr.product_id = p.product_id\n" +
					" inner join shop_product_sku sps on sps.sku_id = s.sku_id\n" +
					" inner join logistics_product lp on lp.shop_product_sku_id = sps.shop_product_sku_id\n" +
					" inner join order_logistics ol on ol.order_logistics_id = lp.order_logistics_id\n" +
					" inner join `order` o on o.order_id = ol.order_id\n" +
					" inner join sku_property sp on s.sku_id = sp.sku_id\n" +
					" inner join product_sku_property_key pspk on sp.product_sku_property_key_id = pspk.product_sku_property_key_id\n" +
					" inner join product_sku_property_value pspv on sp.product_sku_property_value_id = pspv.product_sku_property_value_id\n" +
					" inner join product_property pp on (pp.product_id = p.product_id and pp.key_name = 'BrandID')\n" +
					" where pspk.name = 'Size' ")
					.append(" and lp.vendor_id = :p1 ")
					.append(" and o.order_num = :p2 ");
			System.out.println("excutesql:" + sql.toString());
			List<Map<String, Object>> list = logisticsProductDao.executeBySql(sql.toString(), paramObj);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	// 获取订单列表
	public Map<String, Object> getOrderListByStatus() throws Exception {
		try {
			StringBuilder sql = new StringBuilder("	SELECT a.logistics_product_id,a.created_at,a.`status` FROM logistics_product a ");
			sql.append(" INNER JOIN vendor v ON a.vendor_id = v.vendor_id ");
			sql.append(" WHERE a.enabled = 1 and a.`status` BETWEEN 1 and 4  ORDER BY a.created_at LIMIT 1");
			System.out.println("excutesql:" + sql.toString());
			List<Map<String, Object>> list = logisticsProductDao.executeBySql(sql.toString(),null);
			if (list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	



	private String converListToString (List<String> list) {
		StringBuffer sb = new StringBuffer();
		if (null != list && list.size() > 0) {
			for (String str : list) {
				sb.append(",'"+str+"'");
			}
		}

		return sb.toString().replaceFirst(",","");
	}

	// 通过order_logi 获取 logi_product_list
	public List<Map<String, Object>> getLogisticsProductListByOrderLogistics(String whereStringInput) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {

			// 获取数据
			// SELECT b.*, s.coverpic, s.sku_code, s.in_price, s.price,
			// s.`name`, c.product_id, c.product_code, c.cover_img, c.`name` as
			// product_name FROM logistics_product b LEFT JOIN sku s on s.sku_id
			// = b.sku_id LEFT JOIN product c on s.product_id = c.product_id
			// where EXISTS (SELECT a.order_logistics_id FROM order_logistics a
			// where a.order_logistics_id = b.order_logistics_id and
			// (a.order_logistics_id = 1 or a.order_logistics_id = 5))

			// SELECT b.*, sps.coverpic, s.sku_code, s.`name`, c.product_id,
			// p.product_code, c.coverpic as cover_img FROM logistics_product b
			// LEFT JOIN order_logistics ol ON b.order_logistics_id =
			// ol.order_logistics_id
			// LEFT JOIN shop_product_sku sps ON (b.sku_id = sps.sku_id and
			// ol.shop_id = sps.shop_id)
			// LEFT JOIN shop_product c on (sps.shop_product_id =
			// c.shop_product_id and ol.shop_id = c.shop_id)
			// LEFT JOIN sku s on s.sku_id = sps.sku_id
			// LEFT JOIN product p on c.product_id = p.product_id where EXISTS
			// (SELECT a.order_logistics_id FROM order_logistics a where
			// a.order_logistics_id = b.order_logistics_id and
			// (a.order_logistics_id = 1 or a.order_logistics_id = 5))

			StringBuilder sql = new StringBuilder("");

			sql.append(
					"select b.*, ol.ship_fee, s.coverpic, s.sku_id, s.sku_code, s.`name` as sku_name, c.product_id, p.product_code, c.coverpic as cover_img, p.`name` as product_name from logistics_product b ");
			sql.append("left join order_logistics ol on b.order_logistics_id = ol.order_logistics_id ");
			sql.append(
					"left join shop_product_sku sps on (b.shop_product_sku_id = sps.shop_product_sku_id and ol.shop_id = sps.shop_id) ");
			sql.append(
					"left join shop_product c on (sps.shop_product_id = c.shop_product_id and ol.shop_id = c.shop_id) ");
			sql.append("left join sku s on s.sku_id = sps.sku_id ");
			sql.append("left join product p on c.product_id = p.product_id ");
			sql.append("where exists (");
			sql.append(
					"select a.order_logistics_id from order_logistics a where a.order_logistics_id = b.order_logistics_id ");

			if (whereStringInput.length() > 0) {
				sql.append(" and (" + whereStringInput + ") ");
			}
			sql.append(")");
			System.out.println("excutesql:" + sql.toString());

			result = logisticsProductDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	// 通过order_logi 获取 logi_product_list (在评论时用到)
	public List<Map<String, Object>> getLogisticsProductListByOrderLogisticsForComment(String whereStringInput)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {

			// 获取数据
			// SELECT b.logistics_product_id, b.order_logistics_id,
			// sps.shop_product_id, sps.shop_product_sku_id, sps.sku_id ,
			// sp.product_id FROM logistics_product b
			// LEFT JOIN shop_product_sku sps on sps.shop_product_sku_id =
			// b.sku_id
			// LEFT JOIN shop_product sp on sp.shop_product_id =
			// sps.shop_product_id where EXISTS (SELECT a.order_logistics_id
			// FROM order_logistics a where a.order_logistics_id =
			// b.order_logistics_id and (a.order_logistics_id = 1 or
			// a.order_logistics_id = 5))

			StringBuilder sql = new StringBuilder("");

			sql.append(
					"select b.logistics_product_id, b.order_logistics_id, sps.shop_product_id, sps.shop_product_sku_id, sps.sku_id , sp.product_id from logistics_product b ");
			sql.append("left join shop_product_sku sps on sps.shop_product_sku_id = b.shop_product_sku_id ");
			sql.append("left join shop_product sp on sp.shop_product_id = sps.shop_product_id ");
			sql.append("where exists (");
			sql.append(
					"select a.order_logistics_id from order_logistics a where a.order_logistics_id = b.order_logistics_id ");

			if (whereStringInput.length() > 0) {
				sql.append(" and (" + whereStringInput + ") ");
			}
			sql.append(")");
			System.out.println("excutesql:" + sql.toString());

			result = logisticsProductDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 通过日期和 wherestring 获取 售卖商品数量，销售额，利润 的统计数量
	 * 
	 * @param dayToCount
	 * @param whereString
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getProductAmountAndRevenueByDayFromNow(int dayToCount, String whereString)
			throws Exception {
		// SELECT sum(a.amount) as amount, sum(a.amount * a.sale_price) as
		// sale_total,sum(a.amount * (a.sale_price - a.in_price)) as revenue,
		// b.`status` FROM `logistics_product` a LEFT JOIN order_logistics b on
		// a.order_logistics_id = b.order_logistics_id WHERE b.created_at >
		// DATE_ADD(CURDATE(),INTERVAL -2 DAY) and b.created_at <
		// DATE_ADD(CURDATE(),INTERVAL 0 DAY) and b.shop_id = 1 and b.enabled =
		// 1 GROUP BY b.`status`;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			// SELECT sum(a.amount) as amount, sum(a.amount * a.sale_price) as
			// sale_total,sum(a.amount * (a.sale_price - a.in_price)) as
			// revenue, b.`status` FROM `logistics_product` a
			// LEFT JOIN order_logistics b on a.order_logistics_id =
			// b.order_logistics_id
			// WHERE b.created_at > DATE_ADD(CURDATE(),INTERVAL -2 DAY) and
			// b.created_at < DATE_ADD(CURDATE(),INTERVAL 0 DAY)
			// and b.shop_id = 1 and b.enabled = 1
			// GROUP BY b.`status`;

			sql.append(
					"select sum(a.amount) as amount, sum(a.amount * a.sale_price) as sale_total,sum(a.amount * (a.sale_price - a.in_price)) as revenue, b.`status` from `logistics_product` a ");
			sql.append("left join order_logistics b on a.order_logistics_id = b.order_logistics_id ");
			sql.append("where b.created_at > date_add(curdate(),interval -" + dayToCount + " day) ");
			sql.append("and b.created_at < date_add(curdate(),interval 0 day) ");
			if (whereString.length() > 0) {
				sql.append("and " + whereString);
			}
			sql.append(" group by b.`status`");
			System.out.println("sql = " + sql);
			result = logisticsProductDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return result;

	}

	/**
	 * 根据条件获取sku 库存相关信息
	 * 
	 * @param whereStr
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getSkuStoreByCondition(String whereString) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			// SELECT a.logistics_product_id, a.order_logistics_id,
			// a.shop_product_sku_id, a.amount, sps.sku_id, ss.* FROM
			// `logistics_product` a
			// LEFT JOIN shop_product_sku sps on sps.shop_product_sku_id =
			// a.shop_product_sku_id
			// LEFT JOIN sku_store ss on sps.sku_id = ss.sku_id where
			// a.order_logistics_id = 1 AND a.enabled = 1;

			sql.append(
					"select a.logistics_product_id, a.order_logistics_id, a.shop_product_sku_id, a.amount,  ss.* from `logistics_product` a ");
			sql.append("left join shop_product_sku sps on sps.shop_product_sku_id = a.shop_product_sku_id ");
			sql.append("left join sku_store ss on sps.sku_id = ss.sku_id ");
			if (whereString.length() > 0) {
				sql.append("where " + whereString);
			}

			System.out.println("sql = " + sql);
			result = logisticsProductDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public LogisticsProduct getLogisticsProductById(long id) throws Exception {
		LogisticsProduct logisticsProduct = null;
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		// 添加条件
		conditionMap.put("logistics_product_id", id);
		conditionMap.put("enabled", EnabledType.USED);
		// 查询
		try {
			List<LogisticsProduct> list = logisticsProductDao.getByCondition(LogisticsProduct.class, conditionMap);
			if (list.size() > 0) {
				logisticsProduct = list.get(0);
				return logisticsProduct;
			}
		} catch (Exception e) {
			throw e;
		}
		return logisticsProduct;
	}
	

	/**
	 * 
	 * @param logisticsProduct 需要修改的对象信息
	 * @param status  需要修改的状态
	 * @return
	 * @throws Exception
	 */
    public Map<String, Object> updateOrderLogisticsByOrderLogisticsId(LogisticsProduct logisticsProduct,int status) throws Exception{
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	resultMap.put("status",StatusType.FAILURE);
    	
    	//根据id获取当前数据库旧的对象信息
        LogisticsProduct oldLogisticsProduct = this.getLogisticsProductById(logisticsProduct.getLogistics_product_id());
        
        // 修改状态
    	if (oldLogisticsProduct != null) {
    		logger.info(MessageFormat.format("当前订单状态:{0},需要修改后的订单状态:{1}",oldLogisticsProduct.getStatus(),status));
    		
	        //状态一致，无需修改
	        if(oldLogisticsProduct.getStatus() == status){
	        	resultMap.put("status",StatusType.SUCCESS);
//	        	resultMap.put("info", "需要修改的状态与订单当前状态一致，无需修改");
	        	resultMap.put("info", "The order status is consistent, without modification");
	        	return resultMap;
	        }
	        
	        //获取当前状态的上一个状态，校验状态机
	        int lastStatus= OrderStatusType.getLastStatus(status);
	        
			
			//如果当前订单状态，与需要修改的状态前一个状态不一致，与状态机不一致，提示错误
			if(lastStatus != oldLogisticsProduct.getStatus()){
//	        	resultMap.put("info", MessageFormat.format("状态机校验失败,只有{0}状态下才能改为{1}", 
//	        			OrderStatusMap.valueName.get(lastStatus),OrderStatusMap.valueName.get(status)));
//				resultMap.put("info","状态校验失败,请按订单流转状态进行修改");
				resultMap.put("info","The status check failed, please modify the status in order of order");
				return resultMap;
			}
			
            
//            //记录物流信息
//            if (OrderStatusType.ORDERED == logisticsProduct.status) {
//                logisticsProduct.setTracking_num(trackingNum);
//                logisticsProduct.setVat_num(vatNum);
//            }
			
            
            logisticsProduct.status = status;
            this.updateLogisticsProduct(logisticsProduct,status);
            resultMap.put("status",StatusType.SUCCESS);
        }
    	return resultMap;
    	
    }

	public void updateLogisticsProduct(LogisticsProduct logisticsProduct,Integer orderStatus) throws Exception {
		try {
			LogisticsProduct lp = this.getLogisticsProductById(logisticsProduct.getLogistics_product_id());
			if(lp.getStatus().intValue() != logisticsProduct.getStatus().intValue()) {
				logisticsProductDao.updateById(logisticsProduct);
				logisticsProduct = this.getLogisticsProductById(logisticsProduct.getLogistics_product_id());
				
				String skuId = this.getSkuId(logisticsProduct.getShop_product_sku_id().toString());
				this.updateStatus(orderStatus, skuId);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void updateLogisticsProduct(LogisticsProduct logisticsProduct) throws Exception {
		try {
				logisticsProductDao.updateById(logisticsProduct);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String getSkuId(String shop_product_sku_id){
		String sql = "select sps.sku_id from shop_product_sku  sps where sps.shop_product_sku_id = '"+shop_product_sku_id+"'";
		List<Map<String, Object>> listMap = logisticsProductDao.executeBySql(sql, null);
		if(listMap != null && listMap.size() != 0) {
			return listMap.get(0).get("sku_id")==null?"":listMap.get(0).get("sku_id").toString();
		}
		return null;
	}
	
	public boolean updateStatus(Integer statusType,String skuId){
		if(statusType != null && skuId != null) {
			String sql = "";
			if(statusType.intValue() == OrderStatusEnum.CANCEL.getCode().intValue()) {
				sql = "update sku_store ss set ss.ordered = ss.ordered - 1 where ss.sku_id = '" + skuId + "' ";
			} else if(statusType.intValue() == OrderStatusEnum.CLOSE.getCode().intValue()) {
				sql = "update sku_store ss set ss.ship = ss.ship - 1, ss.finished = ss.finished + 1 where ss.sku_id = '" + skuId + "' ";
			}  else if(statusType.intValue() == OrderStatusEnum.CONFIM.getCode().intValue()) {
				sql = "update sku_store ss set ss.ordered = ss.ordered - 1,ss.confirm = ss.confirm + 1 where ss.sku_id = '" + skuId + "' ";
			}  else if(statusType.intValue() == OrderStatusEnum.SHIP.getCode().intValue()) {
				sql = "update sku_store ss set ss.confirm = ss.confirm - 1,ss.ship = ss.ship + 1 where ss.sku_id = '" + skuId + "' ";
			} 
			if(StringUtils.isNotBlank(sql)) {
				try {
					logisticsProductDao.updateBySQL(sql, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

}
