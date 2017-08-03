package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.VendorBrand;
import pk.shoplus.parameter.EnabledType;

/**
 * Created by hyc on 2016/9/9.
 */
public class VendorBrandService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<VendorBrand> vendorBrandDao = null;

	/**
	 * @param conn
	 */
	public VendorBrandService(Connection conn) {
		vendorBrandDao = new EntityDao<VendorBrand>(conn);
	}

	/**
	 * 根据vendor_brand_id获取信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public VendorBrand getVendorBrandByVendorBrandId(long id) throws Exception {
		try {
			VendorBrand vendorBrand = this.vendorBrandDao.getById(VendorBrand.class, id);
			return vendorBrand;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 插入一条记录
	 *
	 * @param vendorBrand
	 * @return
	 * @throws Exception
	 */
	public VendorBrand createVendorBrand(VendorBrand vendorBrand) throws Exception {
		try {
			Long vendor_brand_id = this.vendorBrandDao.create(vendorBrand);
			if (vendor_brand_id > 0) {
				vendorBrand.vendor_brand_id = vendor_brand_id;
			} else {
				vendorBrand = null;
			}
			return vendorBrand;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get select VendorBrand list VendorBrand
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<VendorBrand> getSelectVendorBrandList() throws Exception {
		return vendorBrandDao.getBySql(VendorBrand.class, "vendor_brand_id", " WHERE enabled = 1", null);
	}

	public List<VendorBrand> getVendorBrandById(Long id) throws Exception {
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("vendor_application_id", id);
			/* condition.put("enabled", EnabledType.USED); */
			List<VendorBrand> list = vendorBrandDao.getByCondition(VendorBrand.class, condition);
			return list;
		} catch (Exception e) {
			throw e;
		}
	}


	public List<Map<String,Object>> selectNotBrand(Long vendor_id) throws Exception{
		try {
			String sql = "select * from brand b where b.brand_id not in (\n" +
					"select brand_id from vendor_brand vb where vb.vendor_id = "+vendor_id+" and enabled = 1\n" +
					") and enabled = 1";
			return vendorBrandDao.executeBySql(sql,null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	public List<VendorBrand> getVendorBrandrListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<VendorBrand> vendorBrandList = vendorBrandDao.getByCondition(VendorBrand.class, fieldName, condition);
			return vendorBrandList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *
	 * 更新供应商表
	 *
	 * @param vendorBrand
	 * @throws Exception
	 */
	public void updateVendorBrand(VendorBrand vendorBrand) throws Exception {
		vendorBrandDao.updateById(vendorBrand);
	}

	public List<VendorBrand> getVendorBrandsByApplicationId(Long id) throws Exception {
		Map<String, Object> condition = new HashMap<>();
		condition.put("enabled", EnabledType.USED);
		condition.put("vendor_application_id", id);
		return vendorBrandDao.getByCondition(VendorBrand.class, condition);
	}


	/**
	 * 根据ｉｄ获取VendorBrand
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public VendorBrand getVendorBrandByBrandId(long id) throws Exception {
		try {
			VendorBrand vendorBrand = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("brand_id", id);

			List<VendorBrand> list = vendorBrandDao.getByCondition(VendorBrand.class, condition);

			for (VendorBrand temp : list) {
				if (temp != null) {
					vendorBrand = temp;
					break;
				}
			}
			return vendorBrand;
		} catch (Exception e) {
			throw e;
		}
	}



	/**
	 * 根据有条件删除vendorbrand
	 * 
	 * @param condition
	 */
	public void deleteVendorBrandByCondition(Map<String, Object> condition) {
		try {
			vendorBrandDao.deleteByCondition(VendorBrand.class, condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<VendorBrand> getVendorBrandByCondition(Map<String, Object> condition) throws Exception {
		return vendorBrandDao.getByCondition(VendorBrand.class, condition);
	}
}
