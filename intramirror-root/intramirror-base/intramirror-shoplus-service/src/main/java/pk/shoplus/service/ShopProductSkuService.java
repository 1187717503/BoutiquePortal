package pk.shoplus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ShopProductSku;

/**
 * 
 * @author cjl
 *
 */
public class ShopProductSkuService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<ShopProductSku> shopProductSkuDao = null;

	/**
	 * @param conn
	 */
	public ShopProductSkuService(Connection conn) {
		shopProductSkuDao = new EntityDao<ShopProductSku>(conn);
	}

	/**
	 * 根据条件获取 shop product sku list
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<ShopProductSku> getShopProductSkuListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<ShopProductSku> shopProductSkuList = shopProductSkuDao.getByCondition(ShopProductSku.class, fieldName,
					condition);
			return shopProductSkuList;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据whereString 获取数据 包括sku相关信息 - to test
	public List<Map<String, Object>> getShopProductSkuListWithSku(String whereStringInput) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// SELECT a.*, b.price as in_price , c.product_id FROM
			// `shop_product_sku` a LEFT JOIN sku b on a.sku_id = b.sku_id LEFT
			// JOIN shop_product c on a.shop_product_id = c.shop_product_id
			// where a.shop_product_sku_id = 1 //获取数据
			// SELECT a.*, b.price as in_price, b.name as sku_name,
			// c.product_id, d.name as product_name, b.coverpic as sku_coverpic,
			// e.weight, e.weight_amount, e.weight_unit, e.length,
			// e.length_amount, e.length_unit, e.height, e.height_amount,
			// e.height_unit, e.width, e.width_amount, e.width_unit, e.size,
			// e.size_amount, e.size_unit FROM `shop_product_sku` a LEFT JOIN
			// sku b on a.sku_id = b.sku_id LEFT JOIN shop_product c on
			// a.shop_product_id = c.shop_product_id LEFT JOIN product d on
			// d.product_id = c.product_id LEFT JOIN product_info e on
			// e.product_id = d.product_id where (a.shop_product_sku_id = 4)
			StringBuilder sql = new StringBuilder("");

			// sql.append("SELECT a.*, b.price as in_price, b.name as sku_name,
			// c.product_id, d.name as product_name, b.coverpic as sku_coverpic
			// FROM `shop_product_sku` a ");
			sql.append(
					"SELECT a.*, b.price as in_price, b.name as sku_name, c.product_id, d.name as product_name, b.coverpic as sku_coverpic, ");
			sql.append(
					"e.weight, e.weight_amount, e.weight_unit, e.length, e.length_amount, e.length_unit, e.height, e.height_amount, e.height_unit, e.width, e.width_amount, e.width_unit, e.size, e.size_amount, e.size_unit  FROM `shop_product_sku` a ");
			sql.append("LEFT JOIN sku b on a.sku_id = b.sku_id ");
			sql.append("LEFT JOIN shop_product c on a.shop_product_id = c.shop_product_id ");
			sql.append("LEFT JOIN product d on d.product_id = c.product_id ");
			sql.append("LEFT JOIN product_info e on e.product_id = d.product_id");
			if (whereStringInput.length() > 0) {
				sql.append(" where (" + whereStringInput + ") ");
				// sql.append(" and (" + whereStringInput + ") ");
			}
			// sql.append(")");
			System.out.println("excutesql:" + sql.toString());
			result = shopProductSkuDao.executeBySql(sql.toString(), new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	// 根据whereString 获取数据 包括sku相关信息 - to test
	public List<Map<String, Object>> getShopProductSkuListWithProductAndSkuId(String whereStringInput)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// SELECT a.shop_product_sku_id, a.shop_id, a.shop_product_id,
			// a.sku_id, c.product_id FROM `shop_product_sku` a
			// LEFT JOIN shop_product c on a.shop_product_id = c.shop_product_id
			// where (a.shop_product_sku_id = 4)
			StringBuilder sql = new StringBuilder("");

			sql.append(
					"SELECT a.shop_product_sku_id, a.shop_id, a.shop_product_id, a.sku_id, c.product_id   FROM `shop_product_sku` a ");
			sql.append("LEFT JOIN shop_product c on a.shop_product_id = c.shop_product_id ");
			if (whereStringInput.length() > 0) {
				sql.append(" where (" + whereStringInput + ") ");
				// sql.append(" and (" + whereStringInput + ") ");
			}
			// sql.append(")");
			System.out.println("excutesql:" + sql.toString());
			result = shopProductSkuDao.executeBySql(sql.toString(), new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	/** 根据product获取sku */
	public List<Map<String, Object>> getSkuByProductId(String productId)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			
			sql.append(" select sku.sku_id,sku.sku_code,t_sp.`name`,t_pspk.`name`,t_pspk.product_sku_property_key_id,t_pspv.`value`,t_ss.store,t_ss.reserved from sku " +  
					" left join product t_pro on (sku.product_id = t_pro.product_id and t_pro.enabled = 1) " + 
					" left join sku_property t_sp on (sku.sku_id = t_sp.sku_id and t_sp.enabled = 1) " + 
					" left join product_sku_property_key t_pspk on (t_pspk.product_sku_property_key_id = t_sp.product_sku_property_key_id and t_pspk.enabled = 1 and t_pspk.`name` = 'Size') " + 
					" inner join product_sku_property_value t_pspv on (t_pspv.product_sku_property_value_id = t_sp.product_sku_property_value_id and t_pspv.enabled = 1) " + 
					" left join sku_store t_ss on (sku.sku_id = t_ss.sku_id and t_ss.enabled = 1) " + 
					" where sku.enabled = 1 and sku.product_id = " + productId + " ");
			
			System.out.println("excutesql:" + sql.toString());
			result = shopProductSkuDao.executeBySql(sql.toString(), new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public void create(ShopProductSku shopProductSku) throws Exception {
		shopProductSkuDao.create(shopProductSku);
	}
	
}
