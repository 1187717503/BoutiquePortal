package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ProductSkuPropertyValue;
import pk.shoplus.parameter.EnabledType;

public class ProductPropertyValueService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ProductSkuPropertyValue> productSkuPropertyValueDao = null;

	/**
	 * @param conn
	 */
	public ProductPropertyValueService(Connection conn) {
		productSkuPropertyValueDao = new EntityDao<ProductSkuPropertyValue>(conn);
	}

	/**
	 * 创建 ProductPropertyValue 数据
	 * 
	 * @param productPropertyValue
	 * @return
	 * @throws Exception
	 */
	public ProductSkuPropertyValue createProductPropertyValue(ProductSkuPropertyValue productPropertyValue)
			throws Exception {
		try {
			Long id = productSkuPropertyValueDao.create(productPropertyValue);
			if (id > 0) {
				productPropertyValue.product_sku_property_value_id = id;
			} else {
				productPropertyValue = null;
			}
			return productPropertyValue;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 通过 product_sku_property_value_id 获取 ProductPropertyValue
	 * 
	 * @param product_sku_property_value_id
	 * @return
	 * @throws Exception
	 */
	public ProductSkuPropertyValue getProductPropertyValueById(Long productSkuPropertyValueId) throws Exception {
		try {
			String fieldName = "*";
			ProductSkuPropertyValue productPropertyValue = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("product_sku_property_value_id", productSkuPropertyValueId);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ProductSkuPropertyValue> productPropertyValueList = productSkuPropertyValueDao
					.getByCondition(ProductSkuPropertyValue.class, fieldName, conditionMap);

			for (ProductSkuPropertyValue temp : productPropertyValueList) {
				if (temp != null) {
					productPropertyValue = new ProductSkuPropertyValue();
					productPropertyValue = temp;
					break;
				}
			}
			return productPropertyValue;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<ProductSkuPropertyValue> getProductPropertyValueListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			List<ProductSkuPropertyValue> productPropertyValueList = productSkuPropertyValueDao
					.getByCondition(ProductSkuPropertyValue.class, condition);
			return productPropertyValueList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件删除ProductPropertyValue
	 * 
	 * @param condition
	 * @throws Exception
	 */
	public void deleteProductPropertyValueListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// delete
			productSkuPropertyValueDao.deleteByCondition(ProductSkuPropertyValue.class, condition);
		} catch (Exception e) {
			throw e;
		}
	}
}
