package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.*;
import pk.shoplus.parameter.ApplicationType;
import pk.shoplus.parameter.BrandType;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.VendorStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendorService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<Vendor> vendorDao = null;
	private EntityDao<VendorBrand> vendorBrandDao = null;
	private EntityDao<VendorCategory> vendorCategoryDao = null;

	private EntityDao<VendorApplication> vendorApplicationDao = null;

	/**
	 * @param conn
	 */
	public VendorService(Connection conn) {
		vendorDao = new EntityDao<Vendor>(conn);
		vendorApplicationDao = new EntityDao<VendorApplication>(conn);
		vendorCategoryDao = new EntityDao<VendorCategory>(conn);
		vendorBrandDao = new EntityDao<VendorBrand>(conn);
	}

	/**
	 * 创建一个供应商信息
	 *
	 * @param vendor
	 * @return
	 * @throws Exception
	 */
	public Vendor createVendor(Vendor vendor) throws Exception {
		try {
			long vendor_id = vendorDao.create(vendor);
			if (vendor_id > 0) {
				vendor.vendor_id = vendor_id;
			} else {
				vendor = null;
			}
			return vendor;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 根据vendor_id 查询 vendor 信息
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Vendor getVendorByApplicationId(long id) throws Exception {
		try {
			Vendor vendor = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("vendor_applicant_id", id);

			List<Vendor> list = vendorDao.getByCondition(Vendor.class, condition);

			for (Vendor temp : list) {
				if (temp != null) {
					vendor = temp;
					break;
				}
			}
			return vendor;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public Vendor getVendorByUserId(Long user_id) throws Exception {
		try {
			Vendor vendor = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("user_id", user_id);

			List<Vendor> list = vendorDao.getByCondition(Vendor.class, condition);

			for (Vendor temp : list) {
				if (temp != null) {
					vendor = temp;
					break;
				}
			}
			return vendor;
		} catch (Exception e) {
			throw e;
		}
	}

	public VendorApplication getVendorApplicationByUserId(Long user_id) throws Exception {
		try {
			VendorApplication vendorApplication = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("user_id", user_id);

			List<VendorApplication> list = vendorApplicationDao.getByCondition(VendorApplication.class, condition);

			for (VendorApplication temp : list) {
				if (temp != null) {
					vendorApplication = temp;
					break;
				}
			}
			return vendorApplication;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据申请表ID查询申请表
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public VendorApplication getVendorApplicationById(Long id) throws Exception {
		VendorApplication vendorApplication = null;

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("enabled", EnabledType.USED);
		condition.put("vendor_application_id", id);

		List<VendorApplication> list = vendorApplicationDao.getByCondition(VendorApplication.class, condition);

		for (VendorApplication temp : list) {
			if (temp != null) {
				vendorApplication = temp;
				break;
			}
		}

		return vendorApplication;
	}

	/**
	 * 更新申请表
	 *
	 * @param vendorApplication
	 * @throws Exception
	 */
	public void updateVendorApplication(VendorApplication vendorApplication) throws Exception {
		try {
			vendorApplicationDao.updateById(vendorApplication);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新供应商表
	 *
	 * @param vendor
	 * @throws Exception
	 */
	public void updateVendor(Vendor vendor) throws Exception {
		try {
			vendorDao.updateById(vendor);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建一个供应商申请表
	 *
	 * @param vendorApplication
	 * @return
	 * @throws Exception
	 */
	public VendorApplication createVendorApplication(VendorApplication vendorApplication) throws Exception {
		long vendorApplicationId = vendorApplicationDao.create(vendorApplication);
		if (vendorApplicationId > 0) {
			vendorApplication.vendor_application_id = vendorApplicationId;
		} else {
			throw new Exception("Creation failed");
		}
		return vendorApplication;
	}

	/**
	 * @param vendorCategory
	 * @return
	 * @throws Exception
	 */
	public VendorCategory createVendorCategory(VendorCategory vendorCategory) throws Exception {
		try {
			long id = vendorCategoryDao.create(vendorCategory);
			if (id > 0) {
				vendorCategory.vendor_category_id = id;
			} else {
				vendorCategory = null;
			}
			return vendorCategory;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param vendorBrand
	 * @return
	 * @throws Exception
	 */
	public VendorBrand createVendorBrand(VendorBrand vendorBrand) throws Exception {
		try {
			Long vendorBrandId = vendorBrandDao.create(vendorBrand);
			if (vendorBrandId > 0) {
				vendorBrand.vendor_brand_id = vendorBrandId;
			} else {
				vendorBrand = null;
			}
			return vendorBrand;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param page
	 * @param vendorName
	 * @param brandType
	 * @param vendorCategoryId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public Page getVendorList(Page page, String vendorName, String brandType, String vendorCategoryId, String status,
			String orderBy,String country) throws Exception {
		try {
			StringBuilder fieldNames = new StringBuilder("");
			fieldNames.append(" v.status,v.vendor_id,v.vendor_name,v.created_at,v.brand_type,")
					.append("(SELECT count(*) FROM product p WHERE p.vendor_id = v.vendor_id ) AS count,")
					.append("(SELECT group_concat(c.`name`) FROM vendor_category vc ")
					.append(" LEFT JOIN category c ON vc.category_id = c.category_id ")
					.append(" WHERE vc.vendor_id = v.vendor_id AND vc.status=1) AS category_name ");
			String tableNames = "vendor v LEFT JOIN vendor_category vc ON vc.vendor_id=v.vendor_id LEFT JOIN vendor_brand vb ON vb.vendor_id=v.vendor_id";
			String countCondition = "v.vendor_id ";

			String whereCondition = null;
			List<Object> paramList = new ArrayList<Object>();
			int i = 1;
			whereCondition = " v.enabled=:p" + i++;
			paramList.add(EnabledType.USED);
			whereCondition += " and v.national_mark=:p" + i++;
			paramList.add(country);
			if (!Helper.isNullOrEmpty(status)) {
				whereCondition += " and v.status=:p" + i++;
				paramList.add(status);
			}
			if (!Helper.isNullOrEmpty(vendorName)) {
				whereCondition += " and v.vendor_name like :p" + i++;
				paramList.add("%" + vendorName + "%");
			}
			if (!Helper.isNullOrEmpty(brandType)) {
				whereCondition += " and v.brand_type = :p" + i++;
				paramList.add(brandType);
			}
			if (!Helper.isNullOrEmpty(vendorCategoryId)) {
				whereCondition += " and vc.category_id = :p" + i++;
				paramList.add(vendorCategoryId);
			}
			if (!Helper.isNullOrEmpty(orderBy)) {
				if (orderBy.equals("product_quantity")) {
					orderBy = " (SELECT count(*) FROM product p WHERE p.vendor_id = v.vendor_id )";
				} else if (orderBy.equals("product_quantity_desc")) {
					orderBy = " (SELECT count(*) FROM product p WHERE p.vendor_id = v.vendor_id ) DESC";
				}
			} else {
				orderBy = "v.created_at DESC";
			}

			Object[] params = null;
			if (paramList.size() > 0) {
				params = paramList.toArray();
			}
			return vendorDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames.toString(), tableNames,
					whereCondition, orderBy, params, countCondition, countCondition);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param page
	 * @param vendorName
	 * @param brandType
	 * @param vendorCategoryId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public Page getVendorApplicationList(Page page, String vendorName, String brandType, String vendorCategoryId,
			Integer status, String orderBy) throws Exception {
		try {
			String fieldNames = "va.status,va.vendor_application_id,va.vendor_name,va.created_at,va.brand_type, group_concat(c.name) as category_name ";
			String tableNames = "vendor_application va LEFT JOIN vendor_category vc ON va.vendor_application_id = vc.vendor_application_id " +
								"LEFT JOIN category c ON vc.category_id = c.category_id ";
			String countCondition = "va.vendor_application_id";
			orderBy = Helper.isNullOrEmpty(orderBy) ? "va.created_at desc" : orderBy;

			String whereCondition = null;
			List<Object> paramList = new ArrayList<Object>();
			int i = 1;
			whereCondition = "va.enabled=:p" + i++;
			paramList.add(EnabledType.USED);
			/*
			 * whereCondition += " and vc.status=:p" + i++;
			 * paramList.add(VendorBrandType.NOTREVIEW);
			 */
			whereCondition += " and va.status=:p" + i++;
			paramList.add(status);
			if (!Helper.isNullOrEmpty(vendorName)) {
				whereCondition += " and va.vendor_name like :p" + i++;
				paramList.add("%" + vendorName + "%");
			}
			if (!Helper.isNullOrEmpty(brandType)) {
				whereCondition += " and va.brand_type = :p" + i++;
				paramList.add(brandType);
			}
			if (!Helper.isNullOrEmpty(vendorCategoryId)) {
				whereCondition += " and vc.category_id = :p" + i++;
				paramList.add(vendorCategoryId);
			}

			Object[] params = null;
			if (paramList.size() > 0) {
				params = paramList.toArray();
			}
			return vendorDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames, whereCondition,
					orderBy, params, countCondition, countCondition);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过条件获取vendorList
	 *
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<Vendor> getVendorListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<Vendor> vendorList = vendorDao.getByCondition(Vendor.class, fieldName, condition);
			return vendorList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过条件获取vendorList OrderBy排序
	 *
	 * @return
	 * @throws Exception
	 */
	public  List<Map<String, Object>> getVendorListByConditionOrderBy() throws Exception {
		try {
			// 查询
			String sql = "select * from vendor v where v.status=:p1 and v.enabled=:p2 order by v.vendor_name ASC;";
			Object[] params = {VendorStatus.ACTIVED,EnabledType.USED};
			return vendorDao.executeBySql(sql, params);
		} catch (Exception e) {
			throw e;
		}
	}



	/**
	 * 根据vendor_application_id查询Vendor Category表中的信息
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<VendorCategory> getVendorCategoryByVAppId(Long id) throws Exception {
		try {

			/*
			 * Object[] params={id}; String sql=
			 * "select category_name from vendor_category where vendor_application_id=:p1"
			 * ; List<Map<String, Object>>
			 * list=vendorCategoryDao.executeBySql(sql,params);
			 * 
			 * List<VendorCategory> listVendorCategory=new ArrayList<>();
			 * 
			 * for(int i=0;i<list.size();i++){ if (list.get(i)!=null){
			 * VendorCategory vendorCategory=new VendorCategory();
			 * vendorCategory.category_name
			 * =list.get(i).get("category_name").toString();
			 * listVendorCategory.add(vendorCategory); } }
			 */

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("vendor_application_id", id);

			List<VendorCategory> list = vendorCategoryDao.getByCondition(VendorCategory.class, condition);

			return list;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据vendor_application_id查询Vendor brand表中的信息
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<VendorBrand> getVendorBrandByVAppId(Long id) throws Exception {
		try {

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("vendor_application_id", id);

			List<VendorBrand> list = vendorBrandDao.getByCondition(VendorBrand.class, condition);

			return list;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据vendor_application_id连接查询application详细信息
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	public Map<String, Object> getApplicationInfoByAppId(Long id, int status) {
		String str = null;
		if (status == 3 || status == 1) {
			str = "va.status=:p2 \n";
		} else {
			str = "vb.status=:p2 \n";
		}
		String sql = "SELECT va.*,u.username user_username,u.email user_email,u.telephone user_telephone,uu.username operate_username,group_concat(distinct(c.name)) category_name,group_concat(distinct(c.chinese_name)) chinese_name,group_concat(distinct(vb.brand_id)) brand_id,group_concat(distinct(b.logo)) logo from vendor_application va \n"
				+ " LEFT JOIN vendor_category vc ON va.vendor_application_id=vc.vendor_application_id \n"
				+ " LEFT JOIN category c ON c.category_id = vc.category_id \n"
				+ " LEFT JOIN user u ON u.user_id=va.user_id \n"
				+ " LEFT JOIN vendor_brand vb ON vb.vendor_application_id=va.vendor_application_id AND vb.status=0 \n"
				+ " LEFT JOIN brand b ON b.brand_id=vb.brand_id \n"
				+ " LEFT JOIN user uu ON uu.user_id=va.operate_user_id \n" + " WHERE va.vendor_application_id=:p1 AND "
				+ str + " GROUP BY va.vendor_application_id; ";
		Object[] params = { id, status };
		List<Map<String, Object>> list = vendorApplicationDao.executeBySql(sql, params);
		if (list != null) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据vendor_id连接查询用户信息和vendor_category表里的category_name信息
	 *
	 * @param id
	 * @return
	 */
	public Map<String, Object> getVendorInfoByVendorId(Long id) {
		String sql = "select v.*,u.username,u.telephone,u.email,u.contact_person_name,group_concat(distinct(c.name)) category_name,group_concat(distinct(c.chinese_name)) chinese_name  from vendor v  \n"
				+ " left join user u on v.user_id=u.user_id  \n"
				+ " left join vendor_category vc on v.vendor_applicant_id=vc.vendor_application_id \n"
				+ " left join category c on c.category_id = vc.category_id \n" + " where v.vendor_id=:p1; ";
		Object[] params = { id };
		List<Map<String, Object>> list = vendorDao.executeBySql(sql, params);
		if (list != null) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据vendor_id连接查询vendorBrand的信息
	 *
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getVendorBrandInfoByVendorId(Long id, Integer vendorBrandStatus) {
	    if (vendorBrandStatus == null) {
            String sql = "select b.* from vendor_brand vb left join brand b on vb.brand_id=b.brand_id where vb.vendor_id=:p1 and b.status=:p2 and vb.enabled=:p3;";
            Object[] params = { id, BrandType.ACTIVE, EnabledType.USED };

            return vendorDao.executeBySql(sql, params);
		} else {
            String sql = "select b.* from vendor_brand vb left join brand b on vb.brand_id=b.brand_id where vb.vendor_id=:p1 and b.status=:p2 and vb.enabled=:p3 and vb.status=:p4;";
            Object[] params = { id, BrandType.ACTIVE, EnabledType.USED, vendorBrandStatus };

            return vendorDao.executeBySql(sql, params);
        }
	}

	/**
	 * 更新vendorCategory
	 *
	 * @param vendorCategory
	 */
	public void updateByVendorCategoryId(VendorCategory vendorCategory) {
		try {
			vendorCategoryDao.updateById(vendorCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新vendorBrand
	 *
	 * @param vendorBrand
	 */
	public void updateByVendorBrandId(VendorBrand vendorBrand) {
		try {
			vendorBrandDao.updateById(vendorBrand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据条件查询VendorCategory的信息
	 *
	 * @param condition
	 * @return
	 */
	public List<VendorCategory> getVendorCategoryByCondition(Map<String, Object> condition) {
		try {
			List<VendorCategory> list = vendorCategoryDao.getByCondition(VendorCategory.class, condition);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据条件查询VendorBrand的信息
	 *
	 * @param condition
	 * @return
	 */
	public List<VendorBrand> getVendorBrandByCondition(Map<String, Object> condition) {
		try {
			List<VendorBrand> list = vendorBrandDao.getByCondition(VendorBrand.class, condition);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询vendor和user的关联信息
	 *
	 * @return
	 */
	public List<Map<String, Object>> getAllVendorDetail() {
		String sql = "select v.*,u.username,u.telephone,u.email,u.contact_person_name,group_concat(distinct(c.name)) category_name,\n"
				+ "group_concat(distinct(b.english_name)) brand_name from vendor v  \n"
				+ "left join user u on v.user_id=u.user_id  \n"
				+ "left join vendor_category vc on v.vendor_id=vc.vendor_id \n"
				+ "left join category c on c.category_id = vc.category_id \n"
				+ "left join vendor_brand vb on v.vendor_id = vb.vendor_id \n"
				+ "left join brand b on b.brand_id = vb.brand_id \n" + "where v.enabled = 1 and v.status = 1 \n"
				+ "group by v.vendor_id ;";
		List<Map<String, Object>> list = vendorDao.executeBySql(sql, null);
		if (list != null) {
			return list;
		}
		return null;
	}

	/**
	 * 根据有条件删除VendorCategory
	 *
	 * @param condition
	 */
	public void deleteVendorCategoryByCondition(Map<String, Object> condition) {
		try {
			vendorCategoryDao.deleteByCondition(VendorCategory.class, condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新vendorCategory表
	 *
	 * @param vendorCategory
	 * @throws Exception
	 */
	public void updateVendorCategory(VendorCategory vendorCategory) throws Exception {
		try {
			vendorCategoryDao.updateById(vendorCategory);
		} catch (Exception e) {
			throw e;
		}
	}

	public Vendor getVendorByVendorId(Long id) throws Exception {
		Vendor vendor = null;

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("enabled", EnabledType.USED);
		condition.put("vendor_id", id);

		List<Vendor> list = vendorDao.getByCondition(Vendor.class, condition);

		for (Vendor temp : list) {
			if (temp != null) {
				vendor = temp;
				break;
			}
		}
		return vendor;
	}

	public VendorApplication getLatestRejectedVendorApplicationByUserId(Long user_id) throws Exception {
		Page page = new Page();
		page.pageSize = 1L;
		page.pageNumber = 1L;

		List<VendorApplication> list = vendorApplicationDao.getBySql(VendorApplication.class, "*",
				" WHERE enabled=1 AND status=3 AND user_id=:p1", new Object[] {user_id}, page, "vendor_application_id DESC");

		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public VendorApplication getPendingVendorApplicationByUserId(Long user_id) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("enabled", EnabledType.USED);
		condition.put("status", ApplicationType.PENDING);
		condition.put("user_id", user_id);

		List<VendorApplication> list = vendorApplicationDao.getByCondition(VendorApplication.class, condition);

		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 根据 condition 删除 vendor_application 表中的 vendor_application
	 *
	 * @param condition
	 */
	public void deleteVendorApplicationByCondition(HashMap<String, Object> condition) throws Exception {
		vendorApplicationDao.deleteByCondition(VendorApplication.class, condition);
	}

	/**
	 * 根据 user_id 获取该 user 当前的 vendor_application_id
	 *
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public long getCurrentVendorApplicationId(long user_id) throws Exception {
		return getCurrentVendorApplicationId(user_id, getVendorByUserId(user_id));
	}

	public long getCurrentVendorApplicationId(long user_id, Vendor vendor) throws Exception {
		// Vendor 还没有通过 Admin 审核，没有 Vendor 数据
		if (vendor == null) {
			VendorApplication vendorApplication = getPendingVendorApplicationByUserId(user_id);
			if (vendorApplication == null) {
				vendorApplication = getLatestRejectedVendorApplicationByUserId(user_id);
			}

			if (vendorApplication == null) {
				throw new Exception("Data corrupted");
			}

			return vendorApplication.vendor_application_id;
		}
		// Vendor 已经通过审核，使用该 vendor 现在关联的 vendor_application_id
		else {
			return vendor.vendor_applicant_id;
		}
	}

	public Map<String, Object> getDashboardData(Long vendor_id) {
		String sql = "SELECT SUM(p.status = 1 OR p.status = 2) as pending, SUM(p.status = 3) as on_sale, SUM(p.status = 8) as unavailable FROM product p WHERE p.enabled = 1 AND p.vendor_id = :p1;";
		Object[] params = new Object[]{vendor_id};

		List<Map<String, Object>> list = vendorDao.executeBySql(sql, params);
		Map<String, Object> result;

		if (list.isEmpty()) {
			return null;
		}

		result = list.get(0);

		sql = "SELECT SUM(lp.amount) as daily_sales " +
				"FROM `order` o " +
				"INNER JOIN order_logistics ol ON o.order_id=ol.order_id " +
				"INNER JOIN logistics_product lp ON lp.order_logistics_id=ol.order_logistics_id " +
				"INNER JOIN shop_product_sku sps ON sps.shop_product_sku_id=lp.shop_product_sku_id " +
				"INNER JOIN sku  ON sku.sku_id=sps.sku_id " +
				"INNER JOIN product p ON p.product_id=sku.product_id " +
				"WHERE p.vendor_id = :p1 " +
				"AND fn_pk_date(o.confirmed_at) = fn_pk_curdate() " +
				"AND o.enabled = 1;";

		list = vendorDao.executeBySql(sql, params);
		if (list.isEmpty()) {
			return null;
		}

		result.putAll(list.get(0));

		return result;
	}
}
