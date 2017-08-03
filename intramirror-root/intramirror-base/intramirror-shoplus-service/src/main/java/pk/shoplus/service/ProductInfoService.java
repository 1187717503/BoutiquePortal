package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ProductInfo;

/**
 * Created by cheezmall on 2016/10/26.
 */
public class ProductInfoService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ProductInfo> productInfoDao = null;

	/**
	 * @param conn
	 */
	public ProductInfoService(Connection conn) {
		productInfoDao = new EntityDao<ProductInfo>(conn);
	}

	/**
	 * 创建Product Info信息
	 * 
	 * @param productInfo
	 * @return
	 * @throws Exception
	 */
	public ProductInfo createProductInfo(ProductInfo productInfo) throws Exception {
		try {
			long productInfoId = productInfoDao.create(productInfo);
			if (productInfoId > 0) {
				productInfo.product_info_id = productInfoId;
			} else {
				productInfo = null;
			}
			return productInfo;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件查询productinfo
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<ProductInfo> getProductInfoByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			List<ProductInfo> productInfoList = productInfoDao.getByCondition(ProductInfo.class, condition);
			return productInfoList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件删除productInfo
	 * 
	 * @param condition
	 */
	public void deleteProductInfoByCondition(Map<String, Object> condition) {
		try {
			productInfoDao.deleteByCondition(ProductInfo.class, condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据实体类更新ProductInfo
	 * 
	 * @param productInfo
	 */
	public void updateProductInfoById(ProductInfo productInfo) {
		try {
			productInfoDao.updateById(productInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
