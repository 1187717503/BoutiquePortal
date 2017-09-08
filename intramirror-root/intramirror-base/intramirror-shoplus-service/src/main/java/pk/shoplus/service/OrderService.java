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
import pk.shoplus.model.Logistics;
import pk.shoplus.model.LogisticsProduct;
import pk.shoplus.model.Order;
import pk.shoplus.model.OrderLogistics;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.OrderStatusMap;
import pk.shoplus.vo.OrderProductShow;

public class OrderService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Logger log = Logger.getLogger(OrderService.class);

	/**
	 * 获取数据库连接
	 */
	private EntityDao<Order> orderDao = null;

	private EntityDao<OrderLogistics> orderLogisticsDao = null;

	private EntityDao<Logistics> logisticsDao = null;

	private EntityDao<LogisticsProduct> logisticsProductDao = null;

	/**
	 * @param conn
	 */
	public OrderService(Connection conn) {
		orderDao = new EntityDao<Order>(conn);
		orderLogisticsDao = new EntityDao<OrderLogistics>(conn);
		logisticsDao = new EntityDao<Logistics>(conn);
		logisticsProductDao = new EntityDao<LogisticsProduct>(conn);
	}

	/**
	 * @param pageNumber
	 * @param pageSize
	 * @param fieldNames
	 * @param tableName
	 * @param whereCondition
	 * @param orderBy
	 * @param
	 * @return
	 * @throws Exception
	 */
	public Page getOrderInfoList(long pageNumber, long pageSize, String fieldNames, String tableName,
			String whereCondition, String orderBy, Object[] params) throws Exception {
		try {
			Page page = orderDao.getPageBySql(pageNumber, pageSize, fieldNames, tableName, whereCondition, orderBy,
					params, null, null);
			return page;
		} catch (Exception e) {
			throw e;
		}
	}

	public Page getOrderInfoList(long pageNumber, long pageSize, String condition, String vendorId, String period,
			String status, StringBuilder queryStringBuiler) throws Exception {
		String fileNames = "`order`.`order_id` `order_id`,`order`.`order_num` `order_num`,`logistics`.`rec_name` `rec_name`,`logistics`.`rec_mobile` `rec_mobile`,`logistics`.`rec_addr`,`logistics`.`rec_area`,`logistics`.`rec_city`,`logistics`.`rec_province`"
				+ ",`shop`.`shop_name`  `shop_name`,`order`.`created_at`  `created_at`,`order`.`status`  `status`,`order`.`enabled` `enabled`,`order_logistics`.`order_logistics_id` `order_logistics_id`";
		String tableName = " `order` LEFT JOIN `order_logistics` ON (`order_logistics`.`order_id` = `order`.`order_id`)"
				+ " LEFT JOIN `shop` ON (`shop`.`shop_id` = `order_logistics`.`shop_id`)"
				+ " LEFT JOIN `logistics` ON (`logistics`.`order_logistics_id` = `order_logistics`.`order_logistics_id`)";
		String defaultOrderBy = " `order`.order_id";
		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		int counter = 1;
		// 条件组合一定有enabled=true
		sb.append(" `order`.enabled=:p" + (counter++) + " ");
		params.add(1);
		if (!Helper.isNullOrEmpty(condition)) {
			try {
				/*
				 * long ifLong = -1; if (Helper.isNumeric(condition)) { ifLong =
				 * Long.parseLong(condition); }
				 */
				sb.append(" and (`order`.order_num=:p" + (counter++) + " or `logistics`.rec_name=:p" + (counter++)
						+ " or `logistics`.rec_mobile=:p" + (counter++) + " or `shop`.shop_name=:p" + (counter++)
						+ ") ");
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
				sb.append(" and `order`.created_at>=:p" + (counter++) + " ");
				params.add(sdfLong.format(beginDate));
				sb.append(" and `order`.created_at<=:p" + (counter++) + " ");
				params.add(sdfLong.format(endDate));
			}
			if (queryStringBuiler.length() > 0)
				queryStringBuiler.append("&");
			queryStringBuiler.append("period=").append(period);
		}
		if (!Helper.isNullOrEmpty(status)) {
			Integer intStatus = OrderStatusMap.nameValue.get(status);
			if (intStatus == null) {
				intStatus = 1;
			}
			sb.append(" and `order`.status=:p" + (counter++) + " ");
			params.add(intStatus);
			if (queryStringBuiler.length() > 0)
				queryStringBuiler.append("&");
			// queryStringBuiler.append("status=").append(status);
		}

		Page page = getOrderInfoList(pageNumber, pageSize, fileNames, tableName, sb.toString(), defaultOrderBy,
				params.toArray());

		return page;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Order getOrder(long id) throws Exception {
		try {
			Order order = orderDao.getById(Order.class, id);
			return order;
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateOrder(Order order) throws Exception {
		try {
			orderDao.updateById(order);
		} catch (Exception e) {
			throw e;
		}
	}

	public OrderLogistics getOrderLogisticsById(Long id) throws Exception {
		return orderLogisticsDao.getById(OrderLogistics.class, id);
	}

	public OrderLogistics getOrderLogisticsByOrderId(Long orderId) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("order_id", orderId);
		List<OrderLogistics> list = orderLogisticsDao.getByCondition(OrderLogistics.class,
				"order_logistics_id,shop_id,logistics_id", condition);
		if (list.size() > 0) {
			return list.get(0);
		} else
			return null;
	}

	public Logistics getLogisticsByOrderLogisticsId(Long orderLogisticsId) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("order_logistics_id", orderLogisticsId);
		List<Logistics> list = logisticsDao.getByCondition(Logistics.class, " * ", condition);
		if (list.size() > 0) {
			return list.get(0);
		} else
			return null;
	}

	public List<LogisticsProduct> getLogisticsProductByOrderLogisticsId(Long orderLogisticsId) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("order_logistics_id", orderLogisticsId);
		List<LogisticsProduct> list = logisticsProductDao.getByCondition(LogisticsProduct.class, " * ", condition);
		return list;
	}

	/**
	 * 
	 * @param orderLogisticsId
	 * @return
	 * @throws Exception
	 */
	public List<OrderProductShow> getLogisticsProductShowByOrderLogisticsId(Long orderLogisticsId) throws Exception {
		List<OrderProductShow> orderProductShowList = new ArrayList<OrderProductShow>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT t2.product_id product_id,t2.`name` product_name FROM  logistics_product t")
				.append(" LEFT JOIN shop_product_sku t3 ON t3.`shop_product_sku_id`=t.`shop_product_sku_id`")
				.append(" LEFT JOIN sku t1 ON t3.sku_id=t1.sku_id")
				.append(" LEFT JOIN product t2 on t1.product_id=t2.product_id")
				.append(" where t.order_logistics_id=:p1");
		List<Map<String, Object>> list = logisticsProductDao.executeBySql(sb.toString(),
				new Object[] { orderLogisticsId });
		log.debug("list.size:" + list.size());
		StringBuilder sb2 = new StringBuilder();
		sb2.append("SELECT t.*,t1.sku_code,t1.`sku_id` FROM  logistics_product t")
				.append(" LEFT JOIN shop_product_sku t2 ON t2.`shop_product_sku_id`=t.`shop_product_sku_id`")
				.append(" LEFT JOIN sku t1 on t2.sku_id=t1.sku_id where t1.product_id=:p1 and t.order_logistics_id=:p2");
		StringBuilder sb3 = new StringBuilder();
		sb3.append(
				"SELECT t.product_id,t.`name` property_name FROM `product_sku_property_key` t where t.enabled=1 and t.product_id=:p1");
		StringBuilder sb4 = new StringBuilder();
		sb4.append(
				"SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.`name` key_name,pro_val.`value` key_value FROM `sku_property` sku_p")
				.append(" inner JOIN product_sku_property_key pro_key on sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id and pro_key.enabled=1")
				.append(" inner JOIN product_sku_property_value pro_val on sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id and pro_val.enabled=1")
				.append(" where sku_p.sku_id=:p1");
		for (Map<String, Object> map : list) {
			OrderProductShow orderProduct = new OrderProductShow();
			Long productId = (Long.parseLong(map.get("product_id").toString()));
			List<Map<String, Object>> propertyNames = logisticsProductDao.executeBySql(sb3.toString(),
					new Object[] { productId });
			orderProduct.propertyNames = propertyNames;
			orderProduct.product_id = productId;
			orderProduct.product_name = (String) map.get("product_name");
			List<Map<String, Object>> mapList = logisticsProductDao.executeBySql(sb2.toString(),
					new Object[] { productId, orderLogisticsId });
			for (Map<String, Object> sku : mapList) {
				log.debug("sku_id:" + sku.get("sku_id"));
				List<Map<String, Object>> propertyNameValues = logisticsProductDao.executeBySql(sb4.toString(),
						new Object[] { sku.get("sku_id") });
				log.debug("propertyNameValues size:" + propertyNameValues.size());
				for (Map<String, Object> skudProperty : propertyNameValues) {
					sku.put(skudProperty.get("key_name").toString(), skudProperty.get("key_value"));
				}
			}
			orderProduct.logisticsProductList = mapList;
			orderProductShowList.add(orderProduct);
		}

		return orderProductShowList;
	}

	/**
	 * 查询当前页的order详情
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getOrderDetailList(String start, String end) {
		try {

			StringBuffer sql = new StringBuffer("select o.order_id order_id,o.fee,o.coupon_fee,\n");
			sql.append("(o.fee - o.coupon_fee) actual_price,o.ship_fee,o.pay_fee ,\n")
					.append(" o.created_at,\n")
					.append(" (l.begin_time - l.receive_time) estimated_delivery,\n")
					.append(" l.rec_name,l.rec_mobile,l.rec_addr,s.shop_name,\n")
					.append(" ol.order_logistics_id\n")
					.append(" from `order` o \n")
                    .append(" left join order_logistics ol on o.order_id = ol.order_id \n")
					.append(" left join logistics l on l.order_logistics_id = ol.order_logistics_id \n")
					.append(" left join shop s on s.shop_id = ol.shop_id ")
					.append(" where o.created_at >=:p1 and o.created_at <= :p2 order by o.created_at desc");
            String[] params = new String[2];
            params[0] = start;
            params[1] = end;

			List<Map<String, Object>> orders = orderDao.executeBySql(sql.toString(), params);
			return orders;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据id 找 order
	public Order getOrderById(long order_id) throws Exception {
		try {
			Order order = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("order_id", order_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<Order> orderList = orderDao.getByCondition(Order.class, conditionMap);
			for (Order temp : orderList) {
				if (temp != null) {
					order = new Order();
					order = temp;
					break;
				}
			}
			return order;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param
	 * @throws Exception
	 */
	public Order createOrder(Order order) throws Exception {
		try {
			Long order_id = orderDao.create(order);
			if (order_id > 0) {
				// 创建订单编号
				order.order_id = order_id;
				String orderNum = generateOrderNum(order.user_id, order.order_id);
				order.order_num = orderNum;
				orderDao.updateById(order);
			} else {
				order = null;
			}
			return order;
		} catch (Exception e) {
			throw e;
		}
	}

	// 订单号生成规则：
	// 日期（8位）＋ 用户ID（末3位）＋ 订单order ID（末5位）
	//
	// 例如：2014092100400023
	/**
	 * 订单号生成规则：
	 * 
	 * @param userId
	 * @param orderId
	 * @return
	 */
	private String generateOrderNum(Long userId, Long orderId) {
		String userStr = "000" + userId;
		String orderStr = "00000" + orderId;
		userStr = userStr.substring(userStr.length() - 3, userStr.length());
		orderStr = orderStr.substring(orderStr.length() - 5, orderStr.length());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return simpleDateFormat.format(new Date()) + userStr + orderStr;
	}

	/**
	 * 根据condition map 来获取 order list
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<Order> getOrderListByCondition(Map<String, Object> conditionMap) throws Exception {
		try {
			// 查询
			List<Order> orderList = orderDao.getByCondition(Order.class, conditionMap);
			return orderList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<Map<String, Object>> getCustomerOrderDetailByOrderLogisticsId(long order_logistics_id){
		try{
			StringBuilder sql = new StringBuilder("");
			sql.append("select o.order_id, o.order_num, o.updated_at, o.status, o.fee, ol.ship_fee, o.pay_fee, ol.logistics_id, ");
			sql.append("sps.sku_id, group_concat(pspk.name) as pspk_name, group_concat(pspv.value) as pspv_value, ");
			sql.append("sku.coverpic, lp.amount, sku.price as in_price, sps.sale_price, ol.return_application_id, rs.rate_shop_id, ");
			sql.append("l.user_rec_name,  l.user_rec_province, l.user_rec_city, l.user_rec_area, l.user_rec_addr ");
			sql.append(", l.user_rec_mobile, shop.shop_name, shop.user_id as shop_user_id, shop.shop_pic, sku.`name` as sku_name, ");
			sql.append("l.rec_name,  l.rec_province, l.rec_city, l.rec_area, l.rec_addr, l.rec_mobile, ");
			sql.append("om.message, ro.rate_order_id ");
			sql.append("from `order` as o ");
			sql.append("left join order_message as om on o.order_id = om.order_id and om.order_status = o.status ");
			sql.append("left join order_logistics as ol on ol.order_id = o.order_id ");
			sql.append("left join logistics as l on ol.order_logistics_id = l.order_logistics_id ");
			sql.append("left join shop on shop.shop_id = ol.shop_id ");
			sql.append("left join rate_order as ro on ro.order_id = o.order_id and ro.shop_id = ol.shop_id and o.status = ro.status ");
			sql.append("left join logistics_product as lp on ol.order_logistics_id = lp.order_logistics_id ");
			sql.append("left join shop_product_sku as sps on lp.shop_product_sku_id = sps.shop_product_sku_id ");
			sql.append("left join shop_product on shop_product.shop_product_id = sps.shop_product_id ");
			sql.append("right join sku_property as sp on sp.sku_id = sps.sku_id ");
			sql.append("left join sku on sku.sku_id = sps.sku_id ");
			sql.append("left join product_sku_property_key as pspk on pspk.product_sku_property_key_id ");
			sql.append("= sp.product_sku_property_key_id ");
			sql.append("left join product_sku_property_value as pspv on pspv.product_sku_property_value_id ");
			sql.append("= sp.product_sku_property_value_id ");
			sql.append("left join rate_shop  rs on ro.rate_order_id = rs.rate_order_id ");
			sql.append("where ol.order_logistics_id = :p1 and o.enabled = :p2 ");
			sql.append("group by sps.sku_id order by o.updated_at desc");
			List<Map<String, Object>> mapList = orderDao.executeBySql(sql.toString(), new Object[]{order_logistics_id, EnabledType.USED });
			return mapList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public List<Map<String, Object>> getCountOrderListByUserIdAndStatus(long user_id) throws Exception{
		StringBuilder sql = new StringBuilder("");
		sql.append("select `status`,  count(status) as statusCount ");
		sql.append("FROM `order` where user_id = :p1 and enabled = :p2 group by status  ");
		List<Map<String, Object>> orderCountList = orderLogisticsDao.executeBySql(sql.toString(), new Object[]{user_id, EnabledType.USED});
		return orderCountList;
	}
	
	public Page getCustomerOrderListByUserId(long user_id, int status, long pageNumber, long pageSize){
		try{
			StringBuilder sql = new StringBuilder("");
			sql.append("select ol.order_logistics_id, o.pay_way, o.pay_way, o.online_payment_id, ");
			sql.append("o.`status` , lp.sale_price, sku.price as in_price, lp.amount, shop.shop_id");
			sql.append(", o.fee, o.ship_fee, o.pay_fee, shop.user_id as shop_user_id, shop.shop_name, shop.shop_pic, ");
			sql.append("sps.sku_id, group_concat(pspk.name) as pspk_name, group_concat(pspv.value) as pspv_value, ");
			sql.append("sku.coverpic, sku.`name` as sku_name, o.order_id, sps.shop_product_sku_id ");
			sql.append("from `order`as o ");
			sql.append("left join order_logistics ol ON o.order_id = ol.order_id ");
			sql.append("left join shop on shop.shop_id = ol.shop_id ");
			sql.append("left join logistics_product as lp on ol.order_logistics_id = lp.order_logistics_id ");
			sql.append("left join shop_product_sku as sps on lp.shop_product_sku_id = sps.shop_product_sku_id ");
			sql.append("left join shop_product on shop_product.shop_product_id = sps.shop_product_id ");
			sql.append("right join sku_property as sp on sp.sku_id = sps.sku_id ");
			sql.append("left join sku on sku.sku_id = sps.sku_id ");
			sql.append("left join product_sku_property_key as pspk on pspk.product_sku_property_key_id ");
			sql.append("= sp.product_sku_property_key_id ");
			sql.append("left join product_sku_property_value as pspv on pspv.product_sku_property_value_id ");
			sql.append("= sp.product_sku_property_value_id ");
			sql.append("where o.user_id = :p1 and o.status = :p2 and o.enabled = :p3 ");
			sql.append("group by sps.sku_id ");
			sql.append("order by o.updated_at desc");
			List<Map<String, Object>> mapList = orderDao.executeBySql(sql.toString(), new Object[]{user_id, status, EnabledType.USED });
			return new Page(mapList, pageNumber, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public List<Map<String, Object>> getOrderListByVendorId(StringBuffer whereCondition ,String limitCondition, Object[] params){
		try{
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT distinct a.logistics_product_id,a.amount,a.order_line_num,a.fee,a.vendor_id,a.`status`,a.updated_at,a.created_at,c.order_id,c.order_num,");
			sql.append("p.product_code,p.product_id,s.sku_id,s.product_id,s.in_price,s.sku_code,pspk.`name`,pspv.`value` as size,");
			sql.append("l.user_rec_name,l.user_rec_province,l.user_rec_city,l.user_rec_area,l.user_rec_addr,");
			sql.append("l.user_rec_mobile,l.user_rec_country,l.user_rec_code,s.boutique_sku_id ");
			sql.append("from logistics_product a  ");
			sql.append("INNER JOIN order_logistics b ON a.order_logistics_id = b.order_logistics_id ");
			sql.append("INNER JOIN `order` c ON b.order_id = c.order_id ");
			sql.append("INNER JOIN shop_product_sku d ON a.shop_product_sku_id = d.shop_product_sku_id ");
			sql.append("INNER JOIN shop_product e ON d.shop_product_id = e.shop_product_id ");
			sql.append("INNER JOIN product p ON e.product_id = p.product_id ");
			sql.append("INNER JOIN sku s ON d.sku_id = s.sku_id ");
			sql.append("INNER JOIN sku_property sp on s.sku_id = sp.sku_id ");
			sql.append("INNER JOIN product_sku_property_key pspk  ");
			sql.append("on sp.product_sku_property_key_id = pspk.product_sku_property_key_id ");
			sql.append("INNER JOIN product_sku_property_value pspv  ");
			sql.append("on sp.product_sku_property_value_id = pspv.product_sku_property_value_id  ");
			sql.append("INNER JOIN vendor v ON a.vendor_id = v.vendor_id ");
			sql.append("INNER JOIN logistics l ON l.order_logistics_id = b.order_logistics_id ");
			sql.append("WHERE pspk.name = 'Size' and a.`status` = 1 ");
			sql.append(whereCondition);
			sql.append("order by a.logistics_product_id ");
			sql.append(limitCondition);
			
			List<Map<String, Object>> mapList = orderDao.executeBySql(sql.toString(),params);
			return mapList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public List<Map<String, Object>> getOrderLogisticsListByOrderId(Object[] params){
		try{
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT a.order_id,a.order_num,b.order_logistics_id,c.amount FROM `order` a ");
			sql.append("INNER JOIN order_logistics b ON a.order_id = b.order_id ");
			sql.append("INNER JOIN logistics_product c ON b.order_logistics_id = c.order_logistics_id ");
			sql.append("WHERE a.enabled = :p1 and a.order_id= :p2");
			List<Map<String, Object>> mapList = orderDao.executeBySql(sql.toString(),params);
			return mapList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	
	

	// todo 获取Order list 以及相关信息
	public Page getOrderPageWithShopInfoByCustomer(long pageNumber, long pageSize, Map<String, Object> whereCondition,
			String orderBy, String whereStringInput) throws Exception {
		try {
			// SELECT a.user_id, a.pay_way, a.online_payment_id, a.`status` as
			// order_status, b.*, sh.shop_name, sh.shop_pic, l.rec_name,
			// l.rec_mobile, l.rec_province, l.rec_city, l.rec_area, l.rec_addr,
			// ro.rate_order_id FROM `order` a LEFT JOIN order_logistics b ON
			// a.order_id = b.order_id LEFT JOIN shop sh ON b.shop_id =
			// sh.shop_id LEFT JOIN logistics l ON l.logistics_id =
			// b.logistics_id LEFT JOIN rate_order ro on a.order_id =
			// ro.order_id WHERE a.user_id = 1 ;
			// 1. condition转换成 wherestr
			StringBuilder whereSql = new StringBuilder("");
			List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

			// 先计数
			// count sql: select count(*) from category where parent_id=:p1 and
			// enabled=:p2

			StringBuilder countSql = new StringBuilder("select count(*) from `order` a");
			countSql.append(whereSql);
			if (whereStringInput.length() > 0) {
				countSql.append(" and ").append(whereStringInput);
			}
			System.out.println("count sql: " + countSql.toString());

			// 1. 获取 总数
			Long totalRow = orderDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);
			if (totalRow == null || totalRow <= 0) {
				return new Page(null, pageNumber, pageSize, 1l, 0l);
			}

			// 2. 获取 分页
			StringBuilder sql = new StringBuilder("");

			// SELECT a.user_id, a.pay_way, a.online_payment_id, a.`status` as
			// order_status, b.*, sh.shop_name, sh.shop_pic, l.rec_name,
			// l.rec_mobile, l.rec_province, l.rec_city, l.rec_area, l.rec_addr,
			// ro.rate_order_id FROM `order` a
			// LEFT JOIN order_logistics b ON a.order_id = b.order_id
			// LEFT JOIN shop sh ON b.shop_id = sh.shop_id
			// LEFT JOIN logistics l ON l.logistics_id = b.logistics_id
			// LEFT JOIN rate_order ro on a.order_id = ro.order_id WHERE
			// a.user_id = 1 ;

			sql.append(
					"SELECT a.user_id, a.order_num, a.pay_way, a.online_payment_id, a.`status` as order_status, a.order_num, b.*, sh.shop_name, sh.shop_pic, sh.user_id as shop_user_id, l.rec_name, l.rec_mobile, l.rec_province, l.rec_city, l.rec_area, l.rec_addr, ro.rate_order_id, ra.return_application_id, rs.rate_shop_id FROM `order` a ");
			sql.append("LEFT JOIN order_logistics b ON a.order_id = b.order_id ");
			sql.append("LEFT JOIN return_application ra ON ra.order_logistics_id = b.order_logistics_id ");
			sql.append("LEFT JOIN shop sh ON b.shop_id = sh.shop_id ");
			sql.append("LEFT JOIN logistics l ON l.logistics_id = b.logistics_id ");
			sql.append("LEFT JOIN rate_order ro on a.order_id = ro.order_id ");
			sql.append("LEFT JOIN rate_shop  rs on ro.rate_order_id = rs.rate_order_id");
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
			List<Map<String, Object>> orderMapList = orderDao.executeBySql(sql.toString(), params.toArray());
			return new Page(orderMapList, pageNumber, pageSize, totalPage, totalRow);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 针对退换获取订单相关信息
	 * 
	 * @param orderId
	 * @return
	 */
	public List<Map<String, Object>> getOrderForApplyForReturn(Long orderId) {
		/*
		 * SELECT
		 * od.*,s.`shop_name`,s.`shop_pic`,ol.`order_logistics_id`,od.`pay_fee`
		 * FROM `order` od LEFT JOIN order_logistics ol ON
		 * ol.`order_id`=od.`order_id` LEFT JOIN shop s ON
		 * s.`shop_id`=ol.`shop_id` WHERE od.`enabled`=1 AND od.`order_id`=:p1
		 */

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT od.*,s.`shop_name`,s.`shop_pic`,ol.`order_logistics_id`,od.`pay_fee` FROM `order` od ")
				.append(" LEFT JOIN order_logistics ol ON ol.`order_id`=od.`order_id`")
				.append(" LEFT JOIN shop s ON s.`shop_id`=ol.`shop_id`")
				.append(" WHERE od.`enabled`=1 AND od.`order_id`=:p1");

		Object[] params = new Object[] { orderId };
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = orderDao.executeBySql(sb.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	/**
	 * SELECT lp.*,s.`name`,s.`coverpic` FROM logistics_product lp LEFT JOIN sku
	 * s ON s.`sku_id`=lp.`sku_id` WHERE lp.`order_logistics_id`=1
	 * 
	 * @param orderLogisticsId
	 * @return
	 */
	public List<Map<String, Object>> getLogisticsProductByOrder(Long orderLogisticsId) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT lp.*,p.`name`,p.`cover_img`,s.`sku_id` FROM logistics_product lp")
				.append(" LEFT JOIN shop_product_sku ss ON ss.`shop_product_sku_id`=lp.`shop_product_sku_id`")
				.append(" LEFT JOIN sku s ON s.`sku_id`=ss.`sku_id`")
				.append(" LEFT JOIN product p ON p.`product_id`=s.`product_id`")
				.append(" WHERE lp.`order_logistics_id`=:p1");

		Object[] params = new Object[] { orderLogisticsId };
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = orderDao.executeBySql(sb.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * SELECT
	 * sku_p.sku_property_id,sku_p.sku_id,pro_key.product_sku_property_key_id
	 * key_id,pro_key.`name` pro_name,pro_val.`value` pro_val FROM
	 * `sku_property` sku_p INNER JOIN product_sku_property_key pro_key ON
	 * sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id AND
	 * pro_key.enabled=1 INNER JOIN product_sku_property_value pro_val ON
	 * sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id
	 * AND pro_val.enabled=1 WHERE sku_p.enabled=1 AND sku_p.sku_id=1
	 * 
	 * @param skuId
	 * @return
	 */
	public List<Map<String, Object>> getPropertyBySkuId(Long skuId) {

		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.product_sku_property_key_id key_id,pro_key.`name` pro_name,pro_val.`value` pro_val FROM `sku_property` sku_p")
				.append(" INNER JOIN product_sku_property_key pro_key ON sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id AND pro_key.enabled=1")
				.append(" INNER JOIN product_sku_property_value pro_val ON sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id AND pro_val.enabled=1")
				.append(" WHERE sku_p.enabled=1 AND sku_p.sku_id=:p1");
		Object[] params = new Object[] { skuId };
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = orderDao.executeBySql(sb.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// 根据状态获取order logis 的数量统计
	public List<Map<String, Object>> getOrderNumberByStatus(String whereStringInput) throws Exception {

		// SELECT count(*), `status` FROM `order_logistics` where shop_id = 1
		// GROUP BY `status` ;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT count(*), `status` FROM `order` ");
			sql.append(whereStringInput);
			sql.append("GROUP BY `status`");

			result = orderDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

}
