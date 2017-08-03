package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.VendorProductCashbackLevel;
import pk.shoplus.parameter.EnabledType;

public class VendorProductCashbackLevelService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<VendorProductCashbackLevel> vendorProductCashbackLevelDao = null;

	/**
	 * @param conn
	 */
	public VendorProductCashbackLevelService(Connection conn) {
		vendorProductCashbackLevelDao = new EntityDao<VendorProductCashbackLevel>(conn);
	}

	/**
	 * 创建供应商对品牌的返点政策
	 * 
	 * @param vendorProductCashbackLevel
	 * @return
	 * @throws Exception
	 */
	public VendorProductCashbackLevel createVendorProductCashbackLevel(
			VendorProductCashbackLevel vendorProductCashbackLevel) throws Exception {
		try {
			Long vendor_product_cashback_level_id = vendorProductCashbackLevelDao.create(vendorProductCashbackLevel);
			if (vendor_product_cashback_level_id > 0) {
				vendorProductCashbackLevel.vendor_product_cashback_level_id = vendor_product_cashback_level_id;
			} else {
				vendorProductCashbackLevel = null;
			}
			return vendorProductCashbackLevel;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition 来获取 VendorProductCashbackLevel 列表
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<VendorProductCashbackLevel> getVendorProductCashbackLevelListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			List<VendorProductCashbackLevel> vendorProductCashbackLevelList = vendorProductCashbackLevelDao
					.getByCondition(VendorProductCashbackLevel.class, "*", condition);
			return vendorProductCashbackLevelList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据id查询
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public VendorProductCashbackLevel getVendorProductCashbackLevelById(Long id) throws Exception {
		try {
			VendorProductCashbackLevel vendorProductCashbackLevel = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("vendor_product_cashback_level_id", id);
			conditionMap.put("enabled", EnabledType.USED);
			List<VendorProductCashbackLevel> skuList = vendorProductCashbackLevelDao
					.getByCondition(VendorProductCashbackLevel.class, conditionMap);
			for (VendorProductCashbackLevel temp : skuList) {
				if (temp != null) {
					vendorProductCashbackLevel = new VendorProductCashbackLevel();
					vendorProductCashbackLevel = temp;
					break;
				}
			}
			return vendorProductCashbackLevel;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 按id更新
	 * 
	 * @param vendorProductCashbackLevel
	 * @throws Exception
	 */
	public void updateVendorProductCashbackLevelListByCondition(VendorProductCashbackLevel vendorProductCashbackLevel)
			throws Exception {
		try {
			vendorProductCashbackLevelDao.updateById(vendorProductCashbackLevel);
		} catch (Exception e) {
			throw e;
		}
	}

}
