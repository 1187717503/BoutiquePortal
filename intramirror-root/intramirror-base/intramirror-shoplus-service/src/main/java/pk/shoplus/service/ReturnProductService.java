package pk.shoplus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ReturnProduct;

public class ReturnProductService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<ReturnProduct> returnProductDao = null;

	/**
	 * @param conn
	 */
	public ReturnProductService(Connection conn) {
		returnProductDao = new EntityDao<ReturnProduct>(conn);
	}
	
	// 通过return application id 获取 return_product_list - 待调试， to test 20161107
	public List<Map<String, Object>> getReturnProductListByReturnApplication(String whereStringInput) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {

			// 获取数据
			// SELECT b.*, s.coverpic, s.sku_code, s.in_price, s.price,
			// s.`name`, c.product_id, c.product_code, c.cover_img, c.name as
			// product_name FROM return_product b LEFT JOIN sku s ON b.sku_id =
			// s.sku_id LEFT JOIN product c on s.product_id = c.product_id WHERE
			// EXISTS (SELECT a.return_application_id from return_application a
			// WHERE b.return_application_id = a.return_application_id and
			// (a.return_application_id = 1));

			StringBuilder sql = new StringBuilder("");

			// sql.append("SELECT b.*, s.coverpic, s.sku_code, s.in_price,
			// s.price, s.`name`, c.product_id, c.product_code, c.cover_img,
			// c.name as product_name FROM return_product b ");
			// sql.append("LEFT JOIN sku s ON b.sku_id = s.sku_id ");
			// sql.append("LEFT JOIN product c on s.product_id = c.product_id
			// ");
			// sql.append("WHERE EXISTS (");
			// sql.append("SELECT a.return_application_id from
			// return_application a WHERE b.return_application_id =
			// a.return_application_id");

			sql.append(
					"SELECT b.*, s.coverpic, s.sku_code, s.`name` as sku_name, p.product_id, p.product_code, p.cover_img as cover_img, p.`name` as product_name, s.sku_id FROM return_product b ");
			sql.append("LEFT JOIN return_application r ON r.return_application_id = b.return_application_id ");
			sql.append(
					"LEFT JOIN shop_product_sku sps ON (b.shop_product_sku_id = sps.shop_product_sku_id and r.shop_id = sps.shop_id) ");
			sql.append(
					"LEFT JOIN shop_product c on (sps.shop_product_id = c.shop_product_id and r.shop_id = c.shop_id) ");
			sql.append("LEFT JOIN sku s on s.sku_id = sps.sku_id ");
			sql.append("LEFT JOIN product p on c.product_id = p.product_id ");
			sql.append("WHERE EXISTS (");
			sql.append(
					"SELECT a.return_application_id from return_application a WHERE b.return_application_id = a.return_application_id ");
			if (whereStringInput.length() > 0) {
				sql.append(" and (" + whereStringInput + ") ");
			}
			sql.append(")");
			System.out.println("excutesql:" + sql.toString());

			result = returnProductDao.executeBySql(sql.toString(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public Long createReturnProduct(ReturnProduct returnProduct) throws Exception {
		try {
			return returnProductDao.create(returnProduct);
		} catch (Exception e) {
			throw e;
		}
	}

}
