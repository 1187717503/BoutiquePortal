package pk.shoplus.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.OrderLogistics;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

public class OrderLogisticsService {
    /**
     * 获取数据库连接
     */
    private EntityDao<OrderLogistics> orderLogisticsDao = null;

    /**
     * @param conn
     */
    public OrderLogisticsService(Connection conn) {
        orderLogisticsDao = new EntityDao<OrderLogistics>(conn);
    }

    /**
     * @param Logistics
     * @throws Exception
     */
    public OrderLogistics createOrderLogistics(OrderLogistics orderLogistics) throws Exception {
        try {
            Long order_logistics_id = orderLogisticsDao.create(orderLogistics);
            if (order_logistics_id > 0) {
                orderLogistics.order_logistics_id = order_logistics_id;
            } else {
                orderLogistics = null;
            }
            return orderLogistics;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据 id 获取 order_logistics
     *
     * @param order_logistics_id
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrderDetialByOrderLogisticsIdAndStatus(long order_logistics_id, int status)
            throws Exception {
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append(
                    "select lp.order_logistics_id, sku.price as in_price, lp.sale_price, o.order_id, o.user_id, sps.shop_product_sku_id, ");
            sql.append(
                    "lp.amount, ol.status, sps.sku_id, ol.ship_fee, pi.weight_amount as weight, lp.logistics_product_id, sps.shop_product_id, ");
            sql.append(
                    "sku.name, o.order_num, lp.enabled, o.updated_at, shop_product.product_id, product.cover_img, pi.weight_amount as weight, ");
            sql.append("group_concat(pspk.name) as pspk_name, group_concat(pspv.value) as pspv_value ");
            sql.append("from logistics_product as lp ");
            sql.append("left join order_logistics as ol on ol.order_logistics_id = lp.order_logistics_id ");
            sql.append("left join `order` as o on o.order_id = ol.order_id ");
            sql.append("left join shop_product_sku as sps on lp.shop_product_sku_id = sps.shop_product_sku_id ");
            sql.append("left join shop_product on shop_product.shop_product_id = sps.shop_product_id ");
            sql.append("left join product on product.product_id = shop_product.product_id ");
            sql.append("left join product_info as pi on pi.product_id = product.product_id ");
            sql.append("right join sku_property as sp on sp.sku_id = sps.sku_id ");
            sql.append("left join sku on sku.sku_id = sps.sku_id ");
            sql.append("left join product_sku_property_key as pspk on pspk.product_sku_property_key_id ");
            sql.append("= sp.product_sku_property_key_id ");
            sql.append("left join product_sku_property_value as pspv on pspv.product_sku_property_value_id ");
            sql.append("= sp.product_sku_property_value_id ");
            sql.append("where lp.order_logistics_id = ");
            sql.append(order_logistics_id);
            sql.append(" and ol.status = ");
            sql.append(status);
            sql.append(" and lp.enabled = 1 ");
            sql.append("group by sps.sku_id ");
            sql.append("order by updated_at desc");
            // executeSql
            List<Map<String, Object>> orderDetialList = orderLogisticsDao.executeBySql(sql.toString(), null);
            if (orderDetialList.size() > 0) {
                return orderDetialList;
            }
            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据 id 获取 order_logistics
     *
     * @param order_logistics_id
     * @return
     * @throws Exception
     */
    public OrderLogistics getOrderLogisticsById(Long order_logistics_id) throws Exception {
        try {
            String fieldName = "*";
            OrderLogistics orderLogistics = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            // 添加条件
            conditionMap.put("order_logistics_id", order_logistics_id);
            conditionMap.put("enabled", EnabledType.USED);

            // 查询
            List<OrderLogistics> orderLogisticsList = orderLogisticsDao.getByCondition(OrderLogistics.class, fieldName,
                    conditionMap);
            for (OrderLogistics temp : orderLogisticsList) {
                if (temp != null) {
                    orderLogistics = new OrderLogistics();
                    orderLogistics = temp;
                    break;
                }
            }
            return orderLogistics;
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
    public List<OrderLogistics> getOrderLogisticsListByCondition(Map<String, Object> conditionMap) throws Exception {
        try {
            // 查询
            List<OrderLogistics> orderLogisticsList = orderLogisticsDao.getByCondition(OrderLogistics.class,
                    conditionMap);
            return orderLogisticsList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 更新
     *
     * @param order_logistics_id
     * @throws Exception
     */
    public void updateOrderLogistics(OrderLogistics orderLogistics) throws Exception {
        try {
            orderLogisticsDao.updateById(orderLogistics);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /*select t4.amount*t4.sale_price as price,t2.request_id,t2.serial_number 
    from payment t1,payment_result t2,order_logistics t3,logistics_product t4,`order` t5 
    where t1.payment_id = t2.payment_id and t3.order_id = t1.order_num
    and t3.order_logistics_id = t4.order_logistics_id and t5.order_id = t3.order_id
    and t4.logistics_product_id = */
    
    public Map<String, Object> getOrderPaymentInfoByLogisticsProductId(String logisticsProductId){
    	try {
    		
    		if(StringUtils.isBlank(logisticsProductId))
    			return null;
    		
    		/*//and t2.process_status = 1
    		select * from payment t1,payment_result t2 where t1.payment_id = t2.payment_id and t1.order_num = */
            StringBuilder sql = new StringBuilder("");
            /*sql.append(" select t1.order_num,t4.amount*t4.sale_price*100 as price,t2.request_id,t2.serial_number , t5.pay_way,t4.logistics_product_id");
            sql.append(" from payment t1,payment_result t2,order_logistics t3,logistics_product t4,`order` t5 ");
            sql.append(" where t1.payment_id = t2.payment_id and t3.order_id = t1.order_num ");
            sql.append(" and t3.order_logistics_id = t4.order_logistics_id and t5.order_id = t3.order_id ");
            if(StringUtils.isNotBlank(logisticsProductId)) {
            	sql.append(" and t4.logistics_product_id = " + logisticsProductId);
            }*/
            
            sql.append(" select pt.order_num,lp.amount*lp.sale_price*100 as price,pr.request_id,pr.serial_number,o.pay_way,lp.logistics_product_id from `order` o "); 
            sql.append(" left join order_logistics ol on o.order_id = ol.order_id ");
            sql.append(" left join logistics_product lp on lp.order_logistics_id = ol.order_logistics_id ");
            sql.append(" left join payment pt on pt.order_num = ol.order_id ");
            sql.append(" left join payment_result pr on pr.payment_id = pt.payment_id ");
            sql.append(" where 1=1 "); // pr.request_id is not null
            
            if(StringUtils.isNotBlank(logisticsProductId)) {
            	sql.append(" and lp.logistics_product_id = " + logisticsProductId);
            }
            System.out.println("excutesql:" + sql.toString());
            
            // executeSql
            List<Map<String, Object>> orderMapList = orderLogisticsDao.executeBySql(sql.toString(), null);
            if (orderMapList.size() > 0) {
                return orderMapList.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public Map<String, Object> getOrderPaymentInfoByOrderId(String orderId){
    	try {
    		
    		if(StringUtils.isBlank(orderId))
    			return null;
    		
    		/*//and t2.process_status = 1
    		select * from payment t1,payment_result t2 where t1.payment_id = t2.payment_id and t1.order_num = */
            StringBuilder sql = new StringBuilder("");
            sql.append(" select t5.pay_way,t1.order_num,sum(t4.amount*t4.sale_price) as price,t2.request_id,t2.serial_number  ,t4.logistics_product_id from payment t1,payment_result t2,order_logistics t3,logistics_product t4 ,`order` t5 ");
            sql.append(" where t1.payment_id = t2.payment_id and t3.order_id = t1.order_num ");
            sql.append(" and t3.order_logistics_id = t4.order_logistics_id and t5.order_id = t3.order_id group by t2.request_id,t2.serial_number ");
            
            if(StringUtils.isNotBlank(orderId)) {
            	sql.append(" and t1.order_num = " + orderId);
            }
            System.out.println("excutesql:" + sql.toString());
            
            // executeSql
            List<Map<String, Object>> orderMapList = orderLogisticsDao.executeBySql(sql.toString(), null);
            if (orderMapList.size() > 0) {
                return orderMapList.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 获取OrderLogisticsId
    public Map<String, Object> getOrderLogisticsIdByOrderId(String orderId) throws Exception {

        /*SELECT logistics_product_id FROM logistics_product AS lp
        LEFT JOIN order_logistics AS ol ON lp.order_logistics_id=ol.order_logistics_id
        LEFT JOIN `order` AS o  ON o.order_id =ol.order_id
        WHERE o.order_num=2017030419800075 AND lp.vendor_id=7*/

        try {
            StringBuilder sql = new StringBuilder("");
            sql.append("SELECT order_logistics_id FROM order_logistics ol");
            sql.append(" LEFT JOIN `order` o  ON o.order_id =ol.order_id");
            sql.append(" WHERE o.order_id= '" + orderId +"'");
            System.out.println("excutesql:" + sql.toString());
            // executeSql
            List<Map<String, Object>> orderMapList = orderLogisticsDao.executeBySql(sql.toString(), null);
            if (orderMapList.size() > 0) {
                return orderMapList.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // 获取OrderLogistics list 以及相关信息
    public Map<String, Object> getOrderLogisticsByIdWithSql(Long order_logistics_id) throws Exception {

        // 1.找基本信息
        // SELECT a.*, b.user_id, c.username, d.rec_name, d.rec_province,
        // d.rec_city, d.rec_area, d.rec_addr, d.rec_mobile FROM
        // order_logistics a LEFT JOIN `order` b on a.order_id = b.order_id
        // LEFT JOIN `user` c on c.user_id = b.user_id LEFT JOIN logistics d
        // on a.logistics_id = d.logistics_id WHERE a.order_logistics_id =
        // 126
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append(
                    "SELECT a.*, b.user_id, b.order_num, c.username, d.rec_name, d.rec_province, d.rec_city, d.rec_area, d.rec_addr, d.rec_mobile FROM order_logistics a ");
            sql.append("LEFT JOIN `order` b on a.order_id = b.order_id ");
            sql.append("LEFT JOIN `user` c on c.user_id = b.user_id ");
            sql.append("LEFT JOIN logistics d on a.logistics_id = d.logistics_id ");
            sql.append("WHERE a.order_logistics_id =" + order_logistics_id);
            System.out.println("excutesql:" + sql.toString());
            // executeSql
            List<Map<String, Object>> orderMapList = orderLogisticsDao.executeBySql(sql.toString(), null);
            if (orderMapList.size() > 0) {
                return orderMapList.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String, Object>> getCountOrderListByShopIdAndStatus(long shop_id) throws Exception {
        StringBuilder sql = new StringBuilder("");
        sql.append("select status,  count(status) as statusCount ");
        sql.append("FROM order_logistics where shop_id = :p1 and enabled =:p2 group by status ");
        List<Map<String, Object>> orderCountList = orderLogisticsDao.executeBySql(sql.toString(), new Object[]{shop_id, EnabledType.USED});
        return orderCountList;
    }

    public Page getOrderListByShopIdAndStatus(long shop_id, int status, long pageNumber, long pageSize) {
        StringBuilder sql = new StringBuilder("");
        sql.append("SELECT o.order_num, u.username, o.status, l.rec_mobile, sps.coverpic, ol.order_logistics_id, ");
        sql.append("count(o.order_num) as product_num, ");
        sql.append("ol.ship_fee, o.pay_fee, sum((lp.sale_price - sku.price) * lp.amount) as revenue, sum(sku.price * lp.amount) as cost, ");
        sql.append("l.user_rec_addr, l.user_rec_province, l.user_rec_city, o.updated_at ");
        sql.append("FROM order_logistics as ol ");
        sql.append("left join `order` as o on o.order_id = ol.order_id ");
        sql.append("left join user as u on u.user_id = o.user_id ");
        sql.append("left join logistics as l on l.order_logistics_id = ol.order_logistics_id ");
        sql.append("left join logistics_product as lp on lp.order_logistics_id = ol.order_logistics_id ");
        sql.append("left join shop_product_sku as sps on sps.shop_product_sku_id = lp.shop_product_sku_id ");
        sql.append("left join sku on sku.sku_id = sps.sku_id ");
        sql.append("where o.status = :p1 and ol.shop_id = :p2 and ol.enabled = :p3 ");
        sql.append("group by o.order_num ");

        // 拼MySQL分页
        //long offset = pageSize * pageNumber;
        //sql.append(" limit ").append(offset);
        // executeSql
        List<Map<String, Object>> orderMapList = orderLogisticsDao.executeBySql(sql.toString(), new Object[]{status, shop_id, EnabledType.USED});
        return new Page(orderMapList, pageNumber, pageSize);
    }

    // 获取OrderLogistics list 以及相关信息
    public Page getOrderLogisticsPageWithUserName(long pageNumber, long pageSize, Map<String, Object> whereCondition,
                                                  String orderBy, String whereStringInput) throws Exception {

        // SELECT a.*, b.user_id, c.username, d.rec_name, d.rec_province,
        // d.rec_city, d.rec_area, d.rec_addr, d.rec_mobile FROM order_logistics
        // a LEFT JOIN `order` b on a.order_id = b.order_id LEFT JOIN `user` c
        // on c.user_id = b.user_id LEFT JOIN logistics d on a.logistics_id =
        // d.logistics_id WHERE a.shop_id = 1
        try {
            // 1. condition转换成 wherestr
            StringBuilder whereSql = new StringBuilder("");
            List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

            // 先计数
            // count sql: select count(*) from category where parent_id=:p1 and
            // enabled=:p2

            StringBuilder countSql = new StringBuilder("select count(*) from order_logistics a");
            countSql.append(whereSql);
            if (whereStringInput.length() > 0) {
                countSql.append(" and ").append(whereStringInput);
            }
            System.out.println("count sql: " + countSql.toString());

            // 1. 获取 总数
            Long totalRow = orderLogisticsDao.createQuery(countSql.toString(), params.toArray())
                    .executeScalar(Long.class);
            if (totalRow == null || totalRow <= 0) {
                return new Page(null, pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            // excutesql:select * from category where parent_id=:p1 and
            // enabled=:p2 order by category_id limit 0,10
            StringBuilder sql = new StringBuilder("");

            // SELECT a.*, b.user_id, c.username, d.rec_name, d.rec_province,
            // d.rec_city, d.rec_area, d.rec_addr, d.rec_mobile FROM
            // order_logistics a LEFT JOIN `order` b on a.order_id = b.order_id
            // LEFT JOIN `user` c on c.user_id = b.user_id LEFT JOIN logistics d
            // on a.logistics_id = d.logistics_id WHERE a.shop_id = 1
            sql.append(
                    "SELECT a.*, b.user_id, b.order_num, c.username, d.rec_name, d.rec_province, d.rec_city, d.rec_area, d.rec_addr, d.rec_mobile FROM order_logistics a ");
            sql.append("LEFT JOIN `order` b on a.order_id = b.order_id ");
            sql.append("LEFT JOIN `user` c on c.user_id = b.user_id ");
            sql.append("LEFT JOIN logistics d on a.logistics_id = d.logistics_id ");
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
            List<Map<String, Object>> orderMapList = orderLogisticsDao.executeBySql(sql.toString(), params.toArray());
            return new Page(orderMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 根据状态获取order logis 的数量统计
    public List<Map<String, Object>> getOrderNumberByStatus(String whereStringInput) throws Exception {

        // SELECT count(*), `status` FROM `order_logistics` where shop_id = 1
        // GROUP BY `status` ;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append("SELECT count(*), `status` FROM `order_logistics` ");
            sql.append(whereStringInput);
            sql.append("GROUP BY `status`");

            result = orderLogisticsDao.executeBySql(sql.toString(), new Object[0]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    // SELECT count(*), ol.`status` FROM `order_logistics` ol LEFT JOIN `order`
    // o ON o.order_id = ol.order_id WHERE o.user_id=233 GROUP BY ol.`status`
    // 根据状态获取order logis 的数量统计
    public List<Map<String, Object>> getOrderNumberByUserAndStatus(String whereStringInput) throws Exception {

        // SELECT count(*), `status` FROM `order_logistics` where shop_id = 1
        // GROUP BY `status` ;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append(
                    "SELECT count(*) as count, ol.`status` FROM `order_logistics` ol LEFT JOIN `order` o ON o.order_id = ol.order_id ");
            sql.append(whereStringInput);
            sql.append("  GROUP BY ol.`status`");

            result = orderLogisticsDao.executeBySql(sql.toString(), new Object[0]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    /**
     * 通过日期和 wherestring 获取order 的统计数量
     *
     * @param dayToCount
     * @param whereString
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrderLogisticsCountByDayFromNow(int dayToCount, String whereString)
            throws Exception {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            StringBuilder sql = new StringBuilder("");
            // SELECT COUNT(*), `status` FROM `order_logistics` a
            // WHERE a.created_at > DATE_ADD(CURDATE(),INTERVAL -2 DAY) and
            // a.created_at < DATE_ADD(CURDATE(),INTERVAL 0 DAY) and shop_id = 1
            // GROUP BY `status`;

            sql.append("select count(*), `status` from `order_logistics` a ");
            sql.append("WHERE a.created_at > DATE_ADD(CURDATE(),INTERVAL -" + dayToCount + " DAY) ");
            sql.append("and a.created_at < DATE_ADD(CURDATE(),INTERVAL 0 DAY) ");
            if (whereString.length() > 0) {
                sql.append("and " + whereString);
            }
            sql.append(" group by `status`");
            System.out.println("sql:" + sql.toString());
            result = orderLogisticsDao.executeBySql(sql.toString(), new Object[0]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return result;
    }

    // 把 map 转换成 OrderLogistics
    public static OrderLogistics convertToOrderLogisticsFromMap(Map<String, Object> map) {
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.order_logistics_id = (Long) map.get("order_logistics_id");
        orderLogistics.shop_id = (Long) map.get("shop_id");
        orderLogistics.order_id = (Long) map.get("order_id");
        orderLogistics.destination_type = (Integer) map.get("destination_type");
        orderLogistics.type = (Integer) map.get("type");
        orderLogistics.ship_payer = (Integer) map.get("ship_payer");
        orderLogistics.ship_fee = (BigDecimal) map.get("ship_fee");
        orderLogistics.fee = (BigDecimal) map.get("fee");
        orderLogistics.deduct_fee = (BigDecimal) map.get("deduct_fee");
        orderLogistics.remain_fee = (BigDecimal) map.get("remain_fee");
        orderLogistics.receive_fee = (BigDecimal) map.get("receive_fee");
        orderLogistics.status = (Integer) map.get("status");
        orderLogistics.logistics_id = (Long) map.get("logistics_id");
        if (map.get("return_application_id") == null) {
            orderLogistics.return_application_id = 0l;
        } else {
            orderLogistics.return_application_id = Long.parseLong(map.get("return_application_id").toString());
        }
        orderLogistics.remark = (String) map.get("remark");
        orderLogistics.created_at = (Date) map.get("created_at");
        orderLogistics.updated_at = (Date) map.get("updated_at");
        orderLogistics.enabled = (Boolean) map.get("enabled");
        return orderLogistics;
    }
}
