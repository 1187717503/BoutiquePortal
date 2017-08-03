package pk.shoplus.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.Shop;
import pk.shoplus.model.ShopBalance;
import pk.shoplus.model.ShopBalanceFlow;
import pk.shoplus.model.ShopBank;
import pk.shoplus.model.ShopBrand;
import pk.shoplus.model.ShopCategory;
import pk.shoplus.model.ShopIndividualApplication;
import pk.shoplus.model.ShopLogisticsAddress;
import pk.shoplus.model.ShopOrganizationApplication;
import pk.shoplus.parameter.BrandType;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.OrderStatusType;
import pk.shoplus.parameter.ShopBrandStatusType;
import pk.shoplus.parameter.ShopCategoryStatusType;

public class ShopService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<Shop> shopDao = null;
	private EntityDao<ShopBrand> shopBrandDao = null;
	private EntityDao<ShopOrganizationApplication> shopOrganizationApplicationDao = null;
	private EntityDao<ShopIndividualApplication> shopIndividualApplicationDao = null;
	private EntityDao<ShopCategory> shopCategoryDao = null;
	private EntityDao<ShopBalance> shopBalanceDao = null;
	private EntityDao<ShopBank> shopBankDao = null;
	private EntityDao<ShopBalanceFlow> shopBalanceFlowDao = null;
	private EntityDao<ShopLogisticsAddress> shopLogisticsAddressDao = null;

	/**
	 * @param conn
	 */
	public ShopService(Connection conn) {
		shopDao = new EntityDao<Shop>(conn);
		shopOrganizationApplicationDao = new EntityDao<ShopOrganizationApplication>(conn);
		shopIndividualApplicationDao = new EntityDao<ShopIndividualApplication>(conn);
		shopBrandDao = new EntityDao<ShopBrand>(conn);
		shopCategoryDao = new EntityDao<ShopCategory>(conn);
		shopBalanceDao = new EntityDao<ShopBalance>(conn);
		shopBankDao = new EntityDao<ShopBank>(conn);
		shopBalanceFlowDao = new EntityDao<ShopBalanceFlow>(conn);
		shopLogisticsAddressDao = new EntityDao<ShopLogisticsAddress>(conn);
	}

	/**
	 * 插入一条店铺记录
	 * 
	 * @param shop
	 * @return
	 * @throws Exception
	 */
	public Shop createShop(Shop shop) throws Exception {
		try {
			Long shop_id = shopDao.create(shop);
			if (shop_id > 0) {
				shop.shop_id = shop_id;
			} else {
				shop = null;
			}
			return shop;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 插入一条组织店铺申请表
	 * 
	 * @param shopOrganizationApplication
	 * @return
	 * @throws Exception
	 */
	public ShopOrganizationApplication createShopOrganizationApplication(
			ShopOrganizationApplication shopOrganizationApplication) throws Exception {
		try {
			Long shop_organization_application_id = shopOrganizationApplicationDao.create(shopOrganizationApplication);
			if (shop_organization_application_id > 0) {
				shopOrganizationApplication.shop_organization_application_id = shop_organization_application_id;
			} else {
				shopOrganizationApplication = null;
			}
			return shopOrganizationApplication;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取组织店铺对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ShopOrganizationApplication getShopOrganizationApplicationById(long id) throws Exception {
		try {
			ShopOrganizationApplication shopOrganizationApplication = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			// 添加条件
			condition.put("shop_organization_application_id", id);
			condition.put("enabled", true);

			List<ShopOrganizationApplication> list = shopOrganizationApplicationDao
					.getByCondition(ShopOrganizationApplication.class, condition);

			for (ShopOrganizationApplication temp : list) {
				if (temp != null) {
					shopOrganizationApplication = new ShopOrganizationApplication();
					shopOrganizationApplication = temp;
					break;
				}
			}

			return shopOrganizationApplication;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新组织店铺对象
	 * 
	 * @param shopOrganizationApplication
	 * @throws Exception
	 */
	public void updateShopOrganizationApplication(ShopOrganizationApplication shopOrganizationApplication)
			throws Exception {
		try {
			shopOrganizationApplicationDao.updateById(shopOrganizationApplication);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 插入一条个人店铺申请表
	 * 
	 * @param shopIndividualApplication
	 * @return
	 * @throws Exception
	 */
	public ShopIndividualApplication createShopIndividualApplication(
			ShopIndividualApplication shopIndividualApplication) throws Exception {
		try {
			Long shop_individual_application_id = shopIndividualApplicationDao.create(shopIndividualApplication);
			if (shop_individual_application_id > 0) {
				shopIndividualApplication.shop_individual_application_id = (shop_individual_application_id);
			} else {
				shopIndividualApplication = null;
			}
			return shopIndividualApplication;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获得个人店铺对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ShopIndividualApplication getShopIndividualApplicationById(long id) throws Exception {
		try {
			ShopIndividualApplication shopIndividualApplication = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			// 添加条件
			condition.put("shop_individual_application_id", id);
			condition.put("enabled", true);

			List<ShopIndividualApplication> list = shopIndividualApplicationDao
					.getByCondition(ShopIndividualApplication.class, condition);

			for (ShopIndividualApplication temp : list) {
				if (temp != null) {
					shopIndividualApplication = new ShopIndividualApplication();
					shopIndividualApplication = temp;
					break;
				}
			}

			return shopIndividualApplication;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新个人信息
	 * 
	 * @param shopIndividualApplication
	 * @throws Exception
	 */
	public void updateShopIndividualApplication(ShopIndividualApplication shopIndividualApplication) throws Exception {
		try {
			shopIndividualApplicationDao.updateById(shopIndividualApplication);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建商铺种类关系
	 * 
	 * @param shopCategory
	 * @return
	 * @throws Exception
	 */
	public ShopCategory createShopCategory(ShopCategory shopCategory) throws Exception {
		try {
			long id = shopCategoryDao.create(shopCategory);
			if (id > 0) {
				shopCategory.shop_category_id = id;
			} else {
				shopCategory = null;
			}
			return shopCategory;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建商铺-商标关系
	 * 
	 * @param shopBrand
	 * @return
	 * @throws Exception
	 */
	public ShopBrand createShopBrand(ShopBrand shopBrand) throws Exception {
		try {
			Long shopBrandId = shopBrandDao.create(shopBrand);
			if (shopBrandId > 0) {
				shopBrand.shop_brand_id = shopBrandId;
			} else {
				shopBrand = null;
			}
			return shopBrand;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 批量增加商品售卖种类
	 * 
	 * @param shopCategoryList
	 * @return
	 * @throws Exception
	 */
	public boolean createShopCategoryList(List<ShopCategory> shopCategoryList) throws Exception {
		try {

			int count = 0;
			boolean isSuccess = false;
			for (ShopCategory shopCategory : shopCategoryList) {
				long id = shopCategoryDao.create(shopCategory);
				if (id > 0) {
					count++;
				}
			}

			// 如果增加数量等于需要增加的数量
			if (count == shopCategoryList.size()) {
				isSuccess = true;
			}
			return isSuccess;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * 批量增加商品商标
	 * 
	 * @param shopBrandList
	 * @return
	 * @throws Exception
	 */
	public boolean createShopBrandList(List<ShopBrand> shopBrandList) throws Exception {
		try {

			int count = 0;
			boolean isSuccess = false;
			for (ShopBrand shopBrand : shopBrandList) {
				long id = shopBrandDao.create(shopBrand);
				if (id > 0) {
					count++;
				}
			}

			// 如果增加数量等于需要增加的数量
			if (count == shopBrandList.size()) {
				isSuccess = true;
			}
			return isSuccess;
		} catch (Exception e) {
			throw e;
		}
	}

	public Shop getShop(long id) throws Exception {
		Shop shop = shopDao.getById(Shop.class, id);
		return shop;
	}

	public Page getShopOrganizationApplicationList(Page page, String shopName, String nameOrEmail, String periodOfTime,
			int status) throws Exception {
		try {
			String fieldNames = "s.shop_organization_application_id,s.shop_name,u.email,s.created_at,s.`status`";
			String tableNames = "shop_organization_application s left join `user` u on s.user_id = u.user_id ";
			String orderBy = "shop_organization_application_id desc";

			List<Object> paramList = new ArrayList<Object>();
			int i = 1;
			StringBuilder whereCondition = new StringBuilder("s.enabled=:p").append(i++);
			paramList.add(EnabledType.USED);
			whereCondition.append(" and s.status=:p").append(i++);
			paramList.add(status);
			if (!Helper.isNullOrEmpty(shopName)) {
				whereCondition.append(" and s.shop_name like :p").append(i++);
				paramList.add("%" + shopName + "%");
			}
			if (!Helper.isNullOrEmpty(nameOrEmail)) {
				whereCondition.append(" and ( s.shop_name like :p").append(i++);
				paramList.add("%" + nameOrEmail + "%");
				whereCondition.append(" or u.email like :p").append(i++).append(")");
				paramList.add("%" + nameOrEmail + "%");
			}
			if (!Helper.isNullOrEmpty(periodOfTime)) {
				whereCondition.append(" and s.created_at >= :p").append(i++).append(" and s.created_at <= :p")
						.append(i++);
				paramList.add(periodOfTime.split("to")[0].trim());
				paramList.add(periodOfTime.split("to")[1].trim());
			}
			Object[] params = null;
			if (paramList.size() > 0) {
				params = paramList.toArray();
			}
			return shopDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames,
					whereCondition.toString(), orderBy, params, null, null);
		} catch (Exception e) {
			throw e;
		}
	}

	public Page getShopList(Page page, String shopName, String periodOfTime, String categoryId, String brandId,
			String orderBy, Integer type, String filter, String min_value, String max_value) throws Exception {
		try {
			String replaceFilter = "";
			if (filter != null) {
				if (filter.equals("rating")) {
					replaceFilter = " service_rating ";
				} else if (filter.equals("order")) {
					replaceFilter = " (SELECT COUNT(*)  FROM order_logistics ol where ol.shop_id=s.shop_id) ";
				} else if (filter.equals("sales")) {
					replaceFilter = " (select sum(lp.amount) from logistics_product lp ,order_logistics ol where lp.order_logistics_id=ol.order_logistics_id and ol.shop_id=s.shop_id) ";
				} else if (filter.equals("return_rate")) {
					replaceFilter = " returned_rate ";
				} else if (filter.equals("shop_balance.revenues")) {
					replaceFilter = " shop_balance.revenues ";
				} else if (filter.equals("shop_balance.debt")) {
					replaceFilter = " shop_balance.debt ";
				}
			}
			StringBuilder fieldNames = new StringBuilder("");
			fieldNames
					.append("s.shop_id,shop_name,service_rating,(SELECT COUNT(*) FROM order_logistics ol where ol.shop_id=s.shop_id) as orderNum, ")
					.append("(SELECT group_concat(b.english_name) from shop_brand sb LEFT JOIN brand b on sb.brand_id = b.brand_id where sb.shop_id = s.shop_id ) as brand_name ,")
					.append("(SELECT group_concat(c.`name`) from shop_category sc LEFT JOIN category c on sc.category_id = c.category_id where sc.shop_id = s.shop_id ) as category_name ,")
					.append("(SELECT sum(lp.amount) from logistics_product lp ,order_logistics ol where lp.order_logistics_id=ol.order_logistics_id and ol.shop_id=s.shop_id) as amount,")
					.append("service_rating,returned_rate,shop_balance.revenues,shop_balance.debt");

			String tableNames = "shop s left join shop_brand sb on s.shop_id = sb.shop_id LEFT JOIN shop_category sc ON s.shop_id = sc.shop_id "
					+ " LEFT JOIN shop_balance shop_balance ON s.shop_id = shop_balance.shop_id";
			orderBy = Helper.isNullOrEmpty(orderBy) ? "s.created_at desc" : orderBy;
			String groupBy = "s.shop_id";
			List<Object> paramList = new ArrayList<Object>();
			int i = 1;
			StringBuilder whereCondition = new StringBuilder("s.enabled=:p").append(i++);
			paramList.add(EnabledType.USED);
			whereCondition.append(" and type=:p").append(i++);
			paramList.add(type);
			if (!Helper.isNullOrEmpty(categoryId)) {
				whereCondition.append(" and sc.category_id= :p").append(i++);
				paramList.add(categoryId);
			}
			if (!Helper.isNullOrEmpty(brandId)) {
				whereCondition.append(" and sb.brand_id =  :p").append(i++);
				paramList.add(brandId);
			}
			if (!Helper.isNullOrEmpty(shopName)) {
				whereCondition.append(" and shop_name like :p").append(i++);
				paramList.add("%" + shopName + "%");
			}
			if (!Helper.isNullOrEmpty(periodOfTime)) {
				whereCondition.append(" and s.created_at >= :p").append(i++).append(" and s.created_at <= :p")
						.append(i++);
				paramList.add(periodOfTime.split("to")[0].trim());
				paramList.add(periodOfTime.split("to")[1].trim());
			}
			if (!Helper.isNullOrEmpty(replaceFilter) && !Helper.isNullOrEmpty(min_value)) {
				whereCondition.append(" and ").append(replaceFilter).append(" >= ").append(":p").append(i++);
				paramList.add(Integer.parseInt(min_value));
			}
			if (!Helper.isNullOrEmpty(replaceFilter) && !Helper.isNullOrEmpty(max_value)) {
				whereCondition.append(" and ").append(replaceFilter).append(" <= ").append(":p").append(i++);
				paramList.add(Integer.parseInt(max_value));
			}
			Object[] params = null;
			if (paramList.size() > 0) {
				params = paramList.toArray();
			}
			return shopDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames.toString(), tableNames,
					whereCondition.toString(), orderBy, params, groupBy, groupBy);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *
	 * @param shopId
	 * @return
	 * @throws Exception
	 */
	public String getShopBrandList(Long shopId) throws Exception {
		List<Map<String, Object>> list = shopDao.executeBySql(
				"select group_concat(b.english_name) as brand_list "
						+ "from shop_brand sb inner join brand b on sb.brand_id = b.brand_id "
						+ "where sb.enabled = :p1 and sb.shop_id = :p2 group by shop_id ",
				new Object[] { true, shopId });
		if (list != null && list.size() > 0) {
			return (String) list.get(0).get("brand_list");
		}
		return null;
	}

	/**
	 *
	 * @param shopId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getShopBrandListToMapByShopId(Long shopId) throws Exception {
		List<Map<String, Object>> list = shopDao.executeBySql(
				"SELECT group_concat(b.english_name separator ',') as brand_list, group_concat(b.brand_id separator ',') as brand_id_list "
						+ "FROM shop_brand sb INNER JOIN brand b ON sb.brand_id = b.brand_id "
						+ "WHERE sb.enabled = :p1 AND sb.shop_id = :p2 AND b.enabled=:p3 AND b.`status`=:p4 AND sb.`status`=:p5 group by shop_id ",
				new Object[] { EnabledType.USED, shopId, EnabledType.USED, BrandType.ACTIVE,
						ShopBrandStatusType.ACTIVED });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 *
	 * @param ShopOrganizationApplicationId
	 * @param shopBranStatus
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getShopBrandListToMapByShopOrganizationApplicationId(long ShopOrganizationApplicationId,
			Integer shopBranStatus) throws Exception {
		List<Map<String, Object>> list = shopDao.executeBySql(
				"SELECT group_concat(b.english_name separator ',') as brand_list, "
						+ "group_concat(b.brand_id separator ',') as brand_id_list "
						+ " FROM shop_brand sb INNER JOIN brand b ON sb.brand_id = b.brand_id "
						+ " WHERE sb.enabled = :p1 AND sb.shop_organization_application_id = :p2 "
						+ " AND b.enabled=:p3 AND b.`status`=:p4 AND sb.`status`=:p5  group by shop_id ",
				new Object[] { EnabledType.USED, ShopOrganizationApplicationId, EnabledType.USED, BrandType.ACTIVE,
						shopBranStatus });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 *
	 * @param shopId
	 * @return
	 * @throws Exception
	 */
	public String getShopCategoryList(long shopId) throws Exception {
		List<Map<String, Object>> list = shopDao.executeBySql(
				"select group_concat(c.name,',') as category_list "
						+ "from shop_category shc inner join category c on shc.category_id = c.category_id "
						+ "where shc.enabled = :p1 and shc.shop_id = :p2 group by shc.shop_id",
				new Object[] { true, shopId });

		if (list != null && list.size() > 0) {
			return (String) list.get(0).get("category_List");
		}
		return null;
	}

	/**
	 * @param shopId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getShopCategoryListToMap(Long shopId, int status) throws Exception {
		try {
			List<Map<String, Object>> list = shopDao.executeBySql(
					"select group_concat(c.name separator ',') as category_list, group_concat(c.category_id separator ',') as category_id_list "
							+ "from shop_category shc left join category c on shc.category_id = c.category_id "
							+ "where shc.enabled = :p1 and shc.shop_id = :p2  and shc.`status`=:p3 and c.enabled=:p4  group by shc.shop_id ",
					new Object[] { EnabledType.USED, shopId, status, EnabledType.USED });
			if (Helper.checkNotNull(list) && list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *
	 * @param shopOrganzationApplicationId
	 * @param status
	 * @return
	 * @throws Exception
	 */

	public Map<String, Object> getShopCategoryListByApplicationId(long shopOrganzationApplicationId, int status)
			throws Exception {
		List<Map<String, Object>> list = shopDao.executeBySql(
				"select group_concat(c.name separator ',') as category_list, group_concat(c.category_id separator ',') as category_id_list "
						+ "from shop_category shc inner join category c on shc.category_id = c.category_id "
						+ "where shc.enabled = :p1 and shc.shop_organization_application_id = :p2  and shc.`status`=:p3 group by shc.shop_organization_application_id",
				new Object[] { true, shopOrganzationApplicationId, status });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 
	 * 根据shopOrganizationApplicationId得到信息
	 * 
	 * SELECT
	 * soa.shop_organization_application_id,soa.`status`,soa.shop_name,soa.
	 * shop_brand_type,soa.shop_pic,soa.
	 * company_bank_account,soa.registered_person,
	 * soa.contact_person_name,soa.electronic_business_license,soa.
	 * electronic_identification_card,soa.reason,soa.updated_at, (SELECT
	 * GROUP_CONCAT(b.logo) FROM shop_brand sb LEFT JOIN brand b ON sb.brand_id
	 * = b.brand_id AND sb.`status`=0 AND
	 * sb.shop_organization_application_id=64) as logos, (SELECT
	 * GROUP_CONCAT(c.`name`) FROM shop_category sc LEFT JOIN category c ON
	 * sc.category_id = c.category_id AND sc.`status`=0 AND
	 * sc.shop_organization_application_id=64) as categories, uu.username
	 * operate_username,u.username user_username,u.email user_email,u.telephone
	 * user_telephone, u.type user_type FROM shop_organization_application soa
	 * LEFT JOIN `user` u ON soa.user_id=u.user_id left join `user` uu ON
	 * uu.user_id=soa.operate_user_id WHERE
	 * soa.shop_organization_application_id=64 GROUP BY
	 * soa.shop_organization_application_id;
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getShopOrganizationApplicationDetailsBySOAId(Long id) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "SELECT soa.company_name,soa.shop_organization_application_id,soa.`status`,soa.shop_name,soa.shop_brand_type,soa.shop_pic,soa.company_bank_account,soa.registered_person,"
				+ " soa.contact_person_name,soa.electronic_business_license,soa.electronic_identification_card,soa.reason,soa.updated_at,"
				+ " (SELECT GROUP_CONCAT(b.logo) FROM shop_brand sb LEFT JOIN brand b ON sb.brand_id = b.brand_id AND sb.`status`=0 AND sb.shop_organization_application_id=:p1) as logos,"
				+ " (SELECT GROUP_CONCAT(c.`name`) FROM shop_category sc LEFT JOIN category c ON sc.category_id = c.category_id AND sc.`status`=:p4 AND sc.shop_organization_application_id=:p2) as categories,"
				+ " uu.username operate_username,u.username user_username,u.email user_email,u.telephone user_telephone,u.type user_type"
				+ " FROM shop_organization_application soa" + " LEFT JOIN `user` u ON soa.user_id=u.user_id"
				+ " LEFT JOIN `user` uu ON uu.user_id=soa.operate_user_id"
				+ " WHERE soa.shop_organization_application_id= :p3 "
				+ " GROUP BY soa.shop_organization_application_id;";
		Object[] params = { id, id, id, ShopCategoryStatusType.PENDING };
		list = shopDao.executeBySql(sql, params);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * SELECT s.score,s.grade,s.status,s.shop_id,s.type,s.shop_pic ,s.shop_name 
	 * basic_information_shop_name,s.shop_brand_type
	 * basic_information_shop_type,s.service_rating basic_information_rating,
	 * u.email basic_information_email,u.telephone
	 * basic_information_telephone,sla.`name` store_information_contact_name,
	 * sla.telephone store_information_telephone,sla.province
	 * store_information_province,sla.address store_information_detail_address,
	 * (SELECT GROUP_CONCAT(b.logo) FROM shop_brand sb LEFT JOIN brand b ON
	 * sb.brand_id = b.brand_id AND sb.shop_id = 1 AND sb.`status`=1 AND
	 * b.`status`=2 AND b.enabled=1) AS logos, (SELECT GROUP_CONCAT(c.`name`)
	 * FROM shop_category sc LEFT JOIN category c ON sc.category_id =
	 * c.category_id AND sc.shop_id=1 AND sc.`status`=1 AND c.enabled=1) AS
	 * categories, s.company_name
	 * organization_information_company_name,s.registered_person
	 * organization_information_registered_person,s.contact_person_name
	 * organization_information_contact_person_name,
	 * s.electronic_business_license
	 * organization_information_electronic_business_license,s.
	 * electronic_identification_card
	 * organization_information_electronic_identification_card,
	 * s.company_bank_account organization_information_bank_account,sb.account
	 * finance_information_bank_account, (SELECT COUNT(1) as count FROM `order`
	 * o INNER JOIN order_logistics ol ON o.order_id = ol.order_id AND
	 * ol.shop_id=1 AND o.`status`=1 AND o.enabled=1 AND ol.enabled=1) AS
	 * finance_information_pending_revenues, shop_balance.given_balance
	 * finance_information_given_balance,shop_balance.balance
	 * finance_information_withdraw_cash, shop_balance.withdrew_cash
	 * finance_information_withdrew_cash,shop_balance.debt
	 * finance_information_debt,shop_balance.revenues
	 * finance_information_revenues FROM shop s LEFT JOIN `user` u on
	 * s.user_id=u.user_id LEFT JOIN shop_logistics_address sla ON s.shop_id =
	 * sla.shop_id LEFT JOIN shop_bank sb ON sb.shop_id = s.shop_id AND
	 * sb.is_default=1 LEFT JOIN shop_balance shop_balance ON
	 * shop_balance.shop_id =s.shop_id WHERE s.shop_id = 1 根据shop_id得到shop的详细信息
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getShopDetailsByShopId(Long id) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "SELECT s.score,s.grade,s.status,s.shop_id,s.type,s.shop_pic ,s.shop_name basic_information_shop_name,s.shop_brand_type basic_information_shop_type,s.service_rating basic_information_rating,"
				+ " u.email basic_information_email,sla.city store_information_city ,u.telephone basic_information_telephone,sla.`name` store_information_contact_name, "
				+ " sla.telephone store_information_telephone,sla.province store_information_province,sla.address store_information_detail_address,"
				+ " (SELECT GROUP_CONCAT(b.logo) FROM shop_brand sb LEFT JOIN brand b ON sb.brand_id = b.brand_id AND sb.`status`=:p1 AND b.`status`=:p2 AND b.enabled=:p3 AND sb.shop_id=:p11) AS logos,"
				+ " (SELECT GROUP_CONCAT(DISTINCT c.`name`) FROM shop_category sc LEFT JOIN category c ON sc.category_id = c.category_id AND sc.`status`=:p4 AND c.enabled=:p5 AND sc.shop_id=:p12) AS categories,"
				+ " s.company_name organization_information_company_name,s.registered_person organization_information_registered_person,s.contact_person_name organization_information_contact_person_name,"
				+ " s.electronic_business_license organization_information_electronic_business_license,s.electronic_identification_card organization_information_electronic_identification_card,"
				+ " s.company_bank_account organization_information_bank_account,sb.account finance_information_bank_account,"
				+ " (SELECT COUNT(1) as count FROM `order` o INNER JOIN order_logistics ol ON o.order_id = ol.order_id AND o.`status`=:p6 AND o.enabled=:p7 AND ol.enabled=:p8 AND ol.shop_id=:p13) AS finance_information_pending_revenues,"
				+ " shop_balance.given_balance finance_information_given_balance,shop_balance.balance finance_information_withdraw_cash,"
				+ " shop_balance.withdrew_cash finance_information_withdrew_cash,shop_balance.debt finance_information_debt,shop_balance.revenues finance_information_revenues"
				+ " FROM shop s LEFT JOIN `user` u on s.user_id=u.user_id"
				+ " LEFT JOIN shop_logistics_address sla ON s.shop_id = sla.shop_id"
				+ " LEFT JOIN shop_bank sb ON sb.shop_id = s.shop_id AND sb.is_default=:p9"
				+ " LEFT JOIN shop_balance shop_balance ON shop_balance.shop_id =s.shop_id "
				+ " WHERE s.shop_id = :p10";
		Object[] params = { ShopBrandStatusType.ACTIVED, BrandType.ACTIVE, EnabledType.USED,
				ShopCategoryStatusType.ACTIVED, EnabledType.USED, OrderStatusType.PENDING, EnabledType.USED,
				EnabledType.USED, true, id, id, id, id };
		list = shopDao.executeBySql(sql, params);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据ShopCategoryId更新ShopCategory
	 * 
	 * @param shopCategory
	 */
	public void updateByShopCategoryId(ShopCategory shopCategory) {
		try {
			shopCategoryDao.updateById(shopCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteByShopCategoryId(long id) {
		try {
			shopCategoryDao.deleteById(ShopCategory.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据ShopBrandId更新ShopBrand
	 * 
	 * @param shopBrand
	 */
	public void updateByShopBrandId(ShopBrand shopBrand) {
		try {
			shopBrandDao.updateById(shopBrand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据条件查询ShopCategory的信息
	 * 
	 * @param condition
	 * @return
	 */
	public List<ShopCategory> getShopCategoryByCondition(Map<String, Object> condition) throws Exception {
		try {
			List<ShopCategory> list = shopCategoryDao.getByCondition(ShopCategory.class, condition);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 根据条件查询ShopBrand的信息
	 * 
	 * @param condition
	 * @return
	 */
	public List<ShopBrand> getShopBrandByCondition(Map<String, Object> condition) throws Exception {
		try {
			List<ShopBrand> list = shopBrandDao.getByCondition(ShopBrand.class, condition);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public List<Map<String, Object>> getAllShopManagmentDetail(long pageNumber, long pageSize) {
		StringBuffer sql = new StringBuffer(
				"select s.*,u.user_id user_user_id,u.username user_username,u.email user_email,u.telephone user_telephone, \n"
						+ " usa.user_ship_address_id,usa.name usa_name,usa.country usa_country,usa.province usa_province,usa.city usa_city, \n"
						+ " usa.area usa_area,usa.address usa_address,usa.telephone usa_telephone,group_concat(distinct(b.english_name)) brands, \n"
						+ " group_concat(distinct(c.name)) categories, \n"
						+ " group_concat(distinct(sk.account)) bank_account, \n"
						+ " se.revenues se_revenues, se.given_balance se_given_balance, \n"
						+ " se.balance se_withdraw_cash ,se.withdrew_cash se_withdrew_cash, \n"
						+ " se.pending se_pending ,se.given_balance se_debt from shop s \n"
						+ " left join user u on s.user_id=u.user_id \n"
						+ " left join shop_brand sb on s.shop_id=sb.shop_id \n"
						+ " left join brand b on sb.brand_id=b.brand_id \n"
						+ " left join shop_category sc on s.shop_id=sc.shop_id  \n"
						+ " left join category c on sc.category_id=c.category_id \n"
						+ " left join user_ship_address usa on u.user_id=usa.user_id \n"
						+ " left join shop_bank sk on sk.shop_id = s.shop_id \n"
						+ " left join shop_balance se on se.shop_id = s.shop_id \n" + "group by s.shop_id");
		long start = pageSize * (pageNumber - 1);
		long end = pageSize * pageNumber;
		sql.append(" limit " + start + "," + end);
		List<Map<String, Object>> list = shopDao.executeBySql(sql.toString(), null);
		if (list != null) {
			return list;
		}
		return null;
	}

	public Shop selectShopInfo(long id) throws Exception {
		try {
			Shop shop = null;
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("shop_organization_application_id", id);

			List<Shop> list = shopDao.getByCondition(Shop.class, condition);

			for (Shop temp : list) {
				if (temp != null) {
					shop = temp;
					break;
				}
			}
			return shop;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新供应商表
	 *
	 * @param shop
	 * @throws Exception
	 */
	public void updateShop(Shop shop) throws Exception {
		try {
			shopDao.updateById(shop);
		} catch (Exception e) {
			throw e;
		}
	}

	public Shop getShopById(long id) throws Exception {
		try {
			Shop shop = shopDao.getById(Shop.class, id);
			return shop;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件查询Shop的信息
	 * 
	 * @param condition
	 * @return
	 */
	public List<Shop> getShopByCondition(Map<String, Object> condition) throws Exception {
		try {
			List<Shop> list = shopDao.getByCondition(Shop.class, condition);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 根据条件查询ShopOrganizationApplication的信息
	 * 
	 * @param condition
	 * @return
	 */
	public List<ShopOrganizationApplication> getShopOrganizationApplicationByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			List<ShopOrganizationApplication> list = shopOrganizationApplicationDao
					.getByCondition(ShopOrganizationApplication.class, condition);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 根据条件查询ShopBalance的信息
	 * 
	 * @param condition
	 * @return
	 */
	public List<ShopBalance> getShopBalanceByCondition(Map<String, Object> condition) throws Exception {
		try {
			List<ShopBalance> list = shopBalanceDao.getByCondition(ShopBalance.class, condition);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 根据条件查询ShopBank的信息
	 * 
	 * @param condition
	 * @return
	 */
	public List<ShopBank> getShopBankByCondition(Map<String, Object> condition) throws Exception {
		try {
			List<ShopBank> list = shopBankDao.getByCondition(ShopBank.class, condition);
			if (list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public ShopBalance getShopBalanceById(long id) throws Exception {
		try {
			ShopBalance shopBalance = shopBalanceDao.getById(ShopBalance.class, id);
			return shopBalance;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新shopBalance
	 * 
	 * @param shopBalance
	 * @throws Exception
	 */
	public void updateShopBalance(ShopBalance shopBalance) throws Exception {
		try {
			shopBalanceDao.updateById(shopBalance);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * SELECT SUM((lp.sale_price-lp.in_price)*lp.amount) AS pendingRevenues FROM
	 * `order` o INNER JOIN order_logistics ol ON ol.order_id=o.order_id AND
	 * ol.shop_id=:p1 INNER JOIN logistics_product lp ON
	 * lp.order_logistics_id=ol.order_logistics_id WHERE o.`status`=1 AND
	 * o.enabled=1
	 */
	public BigDecimal getPendingRevenueByShopId(long shop_id) throws Exception {
		try {
			String sql = "SELECT SUM((lp.sale_price-lp.in_price)*lp.amount) AS pendingRevenues FROM `order` o "
					+ " INNER JOIN order_logistics ol ON ol.order_id=o.order_id AND ol.shop_id=:p1"
					+ " INNER JOIN logistics_product lp ON lp.order_logistics_id=ol.order_logistics_id "
					+ " WHERE o.`status`=:p2 AND o.enabled=:p3";
			List<Map<String, Object>> countList = shopBalanceDao.executeBySql(sql,
					new Object[] { shop_id, OrderStatusType.PENDING, EnabledType.USED });
			if (countList.size() > 0) {
				return (BigDecimal) countList.get(0).get("pendingRevenues");
			}
			return new BigDecimal(0.00);
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 根据条件查询ShopBalanceFlow的信息
	 * 
	 * @param condition
	 * @return
	 */
	public List<ShopBalanceFlow> getShopBalanceFlowByCondition(Map<String, Object> condition) {
		try {
			List<ShopBalanceFlow> list = shopBalanceFlowDao.getByCondition(ShopBalanceFlow.class, condition);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 创建店铺余额流水
	 * 
	 * @param shopBalanceFlow
	 * @return
	 * @throws Exception
	 */
	public ShopBalanceFlow createShopBalanceFlow(ShopBalanceFlow shopBalanceFlow) throws Exception {
		try {
			Long shopBalanceFlowId = shopBalanceFlowDao.create(shopBalanceFlow);
			if (shopBalanceFlowId > 0) {
				shopBalanceFlow.shop_balance_flow_id = shopBalanceFlowId;
			} else {
				shopBalanceFlowId = null;
			}
			return shopBalanceFlow;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件分页查询ShopBalanceFlow
	 * 
	 * @param page,
	 *            conditionMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Page getShopBalanceFlowListByCondition(Page page, Map<String, Object> conditionMap) {
		try {
			String fieldNames = "s.*, p.name as product_name ";
			String tableName = "shop_balance_flow s left join product p  on s.product_id = p.product_id";
			StringBuilder condition = new StringBuilder();
			Iterator<?> iterator = conditionMap.entrySet().iterator();
			List<Object> list = new ArrayList<Object>();
			int i = 1;
			while (iterator.hasNext()) {
				Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
				if (i != 1) {
					condition.append(" and " + entry.getKey() + "=:p" + i);
				} else {
					condition.append(entry.getKey() + "=:p" + i);
				}
				i++;
				list.add(entry.getValue());
			}
			Object[] params = list.toArray();
			String orderBy = "updated_at desc";
			page = shopBalanceFlowDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName,
					condition.toString(), orderBy, params, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 根据userid 查询商铺
	 * 
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public Shop getShopByUserId(Long user_id) throws Exception {
		try {
			Shop shop = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			// 添加条件
			condition.put("user_id", user_id);
			condition.put("enabled", EnabledType.USED);

			List<Shop> list = shopDao.getByCondition(Shop.class, condition);

			for (Shop temp : list) {
				if (temp != null) {
					shop = new Shop();
					shop = temp;
					break;
				}
			}
			return shop;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据userId查询申请表
	 * 
	 * @param user_id
	 * @return
	 */
	public ShopOrganizationApplication getShopOrganizationApplicationbyUserId(Long user_id) throws Exception {
		try {
			ShopOrganizationApplication shopOrganizationApplication = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			// 添加条件
			condition.put("user_id", user_id);
			condition.put("enabled", EnabledType.USED);

			List<ShopOrganizationApplication> list = shopOrganizationApplicationDao
					.getByCondition(ShopOrganizationApplication.class, condition);

			for (ShopOrganizationApplication temp : list) {
				if (temp != null) {
					shopOrganizationApplication = new ShopOrganizationApplication();
					shopOrganizationApplication = temp;
					break;
				}
			}
			return shopOrganizationApplication;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新shopBank
	 * 
	 * @param shopBank
	 * @throws Exception
	 */
	public void updateShopBank(ShopBank shopBank) throws Exception {
		try {
			shopBankDao.updateById(shopBank);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件分页查询ShopBankList
	 * 
	 * @param page,
	 *            conditionMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Page getShopBankListByCondition(Page page, Map<String, Object> conditionMap) {
		try {
			String fieldNames = "* ";
			String tableName = "shop_bank";
			StringBuilder condition = new StringBuilder();
			Iterator<?> iterator = conditionMap.entrySet().iterator();
			List<Object> list = new ArrayList<Object>();
			int i = 1;
			while (iterator.hasNext()) {
				Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
				if (i != 1) {
					condition.append(" and " + entry.getKey() + "=:p" + i);
				} else {
					condition.append(entry.getKey() + "=:p" + i);
				}
				i++;
				list.add(entry.getValue());
			}
			Object[] params = list.toArray();
			String orderBy = "updated_at desc";
			page = shopBankDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName, condition.toString(),
					orderBy, params, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	public ShopBank createShopBank(ShopBank shopBank) throws Exception {
		try {
			shopBank.shop_bank_id = shopBankDao.create(shopBank);
			if (shopBank.shop_bank_id > 0) {
				return shopBank;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件删除ShopCategory的信息
	 * 
	 * @param condition
	 * @throws Exception
	 */
	public void deleteShopCategoryByCondition(Map<String, Object> condition) throws Exception {
		try {
			shopCategoryDao.deleteByCondition(ShopCategory.class, condition);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件删除ShopBrand的信息
	 * 
	 * @param condition
	 * @throws Exception
	 */
	public void deleteShopBrandByCondition(Map<String, Object> condition) throws Exception {
		try {
			shopBrandDao.deleteByCondition(ShopBrand.class, condition);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取组织店铺对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ShopLogisticsAddress getShopLogisticsAddressById(long id) throws Exception {
		try {
			ShopLogisticsAddress shopLogisticsAddress = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			// 添加条件
			condition.put("shop_id", id);
			condition.put("enabled", true);

			List<ShopLogisticsAddress> list = shopLogisticsAddressDao.getByCondition(ShopLogisticsAddress.class,
					condition);

			for (ShopLogisticsAddress temp : list) {
				if (temp != null) {
					shopLogisticsAddress = new ShopLogisticsAddress();
					shopLogisticsAddress = temp;
					break;
				}
			}

			return shopLogisticsAddress;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新shopLogisticsAddress
	 * 
	 * @param shopLogisticsAddress
	 * @throws Exception
	 */
	public void updateShopLogisticsAddress(ShopLogisticsAddress shopLogisticsAddress) throws Exception {
		try {
			shopLogisticsAddressDao.updateById(shopLogisticsAddress);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 插入一条shopLogisticsAddress记录
	 * 
	 * @param shopLogisticsAddress
	 * @return
	 * @throws Exception
	 */
	public ShopLogisticsAddress createShopLogisticsAddress(ShopLogisticsAddress shopLogisticsAddress) throws Exception {
		try {
			Long shop_logistics_address_id = shopLogisticsAddressDao.create(shopLogisticsAddress);
			if (shop_logistics_address_id > 0) {
				shopLogisticsAddress.shop_logistics_address_id = shop_logistics_address_id;
			} else {
				shopLogisticsAddress = null;
			}
			return shopLogisticsAddress;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件查询NewArrivalShopProduct
	 * 
	 * @param shop_id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getNewArrivalShopProduct(Long shop_id) throws Exception {
		try {
			List<Map<String, Object>> list = shopDao.executeBySql(
					"select p.cover_img,sps.sale_price,p.name "
							+ "from shop_product as sp left join product as p on sp.product_id = p.product_id "
							+ "left join shop_product_sku as sps on sp.product_id = sps.shop_product_id "
							+ "where sp.shop_id = :p1 and p.enabled= :p2 " + "order by sp.sale_at desc " + "limit 0,3",
					new Object[] { shop_id, true });
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 根据条件查询HighestRatingShopProduct
	 * 
	 * @param shop_id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getHighestRatingShopProduct(Long shop_id) throws Exception {
		try {
			List<Map<String, Object>> list = shopDao.executeBySql(
					"select p.cover_img,p.name,p.customer_rating,100 as sale_price,200 as rush_price, 10 as commentCount, 20 as orderCount "
							+ "from shop_product as sp left join product as p on sp.product_id = p.product_id "
							+ "where sp.shop_id = :p1 and p.enabled= :p2 " + "order by p.customer_rating desc "
							+ "limit 0,4",
					new Object[] { shop_id, true });
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 根据条件查询HighestRatingShopProduct
	 * 
	 * @param shop_id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getRushToBuyShopProduct(Long shop_id) throws Exception {
		try {
			List<Map<String, Object>> list = shopDao.executeBySql(
					"select p.cover_img,p.name,100 as sale_price,200 as rush_price, 0.8 as max_up_to_percentage "
							+ "from shop_product as sp left join product as p on sp.product_id = p.product_id "
							+ "where sp.shop_id = :p1 and p.enabled= :p2 " + "limit 0,4",
					new Object[] { shop_id, true });
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public List<Map<String, Object>> getShopById(String shop_id) throws Exception {
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("select s.shop_id,s.shop_name,sla.`name`,s.shop_pic,sla.area,sla.address,sla.telephone")
					.append(" from shop s left join `user` u on s.user_id = u.user_id ")
					.append("left join shop_logistics_address sla on s.shop_id = sla.shop_id ")
					.append("where s.shop_id=:p1");
			return shopDao.executeBySql(sql.toString(), new Object[] { shop_id });
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param shop_brand_id
	 */
	public void deleteByShopBrandId(Long shop_brand_id) throws Exception {
		try {
			shopBrandDao.deleteById(ShopBrand.class, shop_brand_id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * @param shopBalance
	 * @return
	 * @throws Exception
	 */
	public ShopBalance createShopBalance(ShopBalance shopBalance) throws Exception {
		try {
			shopBalance.shop_balance_id = shopBalanceDao.create(shopBalance);
			return shopBalance;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Long getPendingOrdersByShopId(Long shop_id) throws Exception {
		try {
			String sql = "SELECT COUNT(1) as count "
					+ " FROM `order` o INNER JOIN order_logistics ol ON o.order_id = ol.order_id AND ol.shop_id=:p1"
					+ " WHERE o.`status`=:p2 AND o.enabled=:p3 AND ol.enabled=:p4";
			List<Map<String, Object>> countList = shopDao.executeBySql(sql,
					new Object[] { shop_id, OrderStatusType.PENDING, EnabledType.USED, EnabledType.USED });
			if (!Helper.checkNotNull(countList) || countList.size() == 0) {
				return null;
			} else {
				if (countList.size() > 0) {
					return (long) countList.get(0).get("count");
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}
