package pk.shoplus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ProductSkuPropertyKey;
import pk.shoplus.parameter.EnabledType;

public class ProductPropertyKeyService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ProductSkuPropertyKey> productSkuPropertyKeyDao = null;

	/**
	 * @param conn
	 */
	public ProductPropertyKeyService(Connection conn) {
		productSkuPropertyKeyDao = new EntityDao<ProductSkuPropertyKey>(conn);
	}

	/**
	 * 创建 productPropertyKey
	 * 
	 * @param productPropertyKey
	 * @return
	 * @throws Exception
	 */
	public ProductSkuPropertyKey createProductPropertyKey(ProductSkuPropertyKey productPropertyKey) throws Exception {
		try {
			Long id = productSkuPropertyKeyDao.create(productPropertyKey);
			if (id > 0) {
				productPropertyKey.product_sku_property_key_id = id;
			} else {
				productPropertyKey = null;
			}
			return productPropertyKey;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过
	 * 
	 * @param product_sku_property_key_id
	 * @return
	 * @throws Exception
	 */
	public ProductSkuPropertyKey getProductPropertyKeyById(Long productSkuPropertyKeyId) throws Exception {
		try {
			String fieldName = "*";
			ProductSkuPropertyKey productSkuPropertyKey = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("product_sku_property_key_id", productSkuPropertyKeyId);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ProductSkuPropertyKey> productPropertyKeyList = productSkuPropertyKeyDao
					.getByCondition(ProductSkuPropertyKey.class, fieldName, conditionMap);

			for (ProductSkuPropertyKey temp : productPropertyKeyList) {
				if (temp != null) {
					productSkuPropertyKey = new ProductSkuPropertyKey();
					productSkuPropertyKey = temp;
					break;
				}
			}
			return productSkuPropertyKey;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件查询 ProductPropertyKey List
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<ProductSkuPropertyKey> getProductPropertyKeyListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			List<ProductSkuPropertyKey> productPropertyKeyList = productSkuPropertyKeyDao
					.getByCondition(ProductSkuPropertyKey.class, "*", condition);
			return productPropertyKeyList;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据product列表 获取ProductPropertyKeyList
	public List<Map<String, Object>> getProductPropertyKeyListByProduct(String whereStringInput) throws Exception {
		// SELECT * FROM product_property_key k WHERE EXISTS (SELECT
		// t.product_id FROM product t where k.product_id = t.product_id and
		// (t.product_id = 27 or t.product_id = 2))
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");

			// SELECT k.* FROM product_property_key k WHERE EXISTS (SELECT
			// a.product_id FROM product a where k.product_id = a.product_id and
			// (a.product_id = 27 or a.product_id = 2))

			sql.append("SELECT k.* FROM product_sku_property_key k ");
			sql.append("WHERE EXISTS (");
			sql.append("SELECT a.product_id FROM product a where k.product_id = a.product_id ");
			if (whereStringInput.length() > 0) {
				sql.append(" and (" + whereStringInput + ") ");
			}
			sql.append(")");
			System.out.println("excutesql:" + sql.toString());

			result = productSkuPropertyKeyDao.executeBySql(sql.toString(), new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 根据条件删除 ProductPropertyKey
	 * 
	 * @param condition
	 * @throws Exception
	 */
	public void deleteProductPropertyKeyListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// delete
			productSkuPropertyKeyDao.deleteByCondition(ProductSkuPropertyKey.class, condition);
		} catch (Exception e) {
			throw e;
		}
	}

}
