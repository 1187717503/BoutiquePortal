package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ProductProperty;

/**
 * @author 作者：zhangxq
 *
 */
public class ProductInfoPropertyService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ProductProperty> productPropertyDao = null;

	/**
	 * @param conn
	 */
	public ProductInfoPropertyService(Connection conn) {
		productPropertyDao = new EntityDao<ProductProperty>(conn);
	}

	/**
	 * 根据条件查询ProductInfoProperty
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<ProductProperty> getProductInfoPropertyByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			List<ProductProperty> productInfoList = productPropertyDao.getByCondition(ProductProperty.class,
					condition);
			return productInfoList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建Product Info Property信息
	 * 
	 * @param productInfoProperty
	 * @return
	 * @throws Exception
	 */
	public ProductProperty createProductInfoProperty(ProductProperty productInfoProperty) throws Exception {
		try {
			if(productInfoProperty.value != null) {
				long id = productPropertyDao.create(productInfoProperty);
				if (id > 0) {
					productInfoProperty.product_property_id = id;
				} else {
					productInfoProperty = null;
				}
			}
			return productInfoProperty;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件删除ProductInfoProperty
	 * 
	 * @param condition
	 */
	public void deleteProductInfoPropertyByCondition(Map<String, Object> condition) {
		try {
			productPropertyDao.deleteByCondition(ProductProperty.class, condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过实体类更新productInfoPropperty
	 * 
	 * @param productInfoProperty
	 */
	public void updateProductInfoPropertyById(ProductProperty productInfoProperty) {
		try {
			productPropertyDao.updateById(productInfoProperty);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
