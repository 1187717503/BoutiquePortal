package pk.shoplus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.FavouriteShop;
import pk.shoplus.model.FavouriteShopProduct;
import pk.shoplus.model.Page;
import pk.shoplus.model.Shop;
import pk.shoplus.parameter.EnabledType;

public class CustomerService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<FavouriteShop> favouriteShopDao = null;
	private EntityDao<FavouriteShopProduct> favouriteShopProductDao = null;
	private EntityDao<Shop> shopDao = null;

	/**
	 * @param conn
	 */
	public CustomerService(Connection conn) {
		favouriteShopDao = new EntityDao<FavouriteShop>(conn);
		favouriteShopProductDao = new EntityDao<FavouriteShopProduct>(conn);
		shopDao = new EntityDao<Shop>(conn);
	}

	/**
	 * 根据条件分页查询FavouriteShop
	 * 
	 * @param page,
	 *            conditionMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Page getFavouriteShopListByCondition(Page page, Map<String, Object> conditionMap) throws Exception {
		try {
			String fieldNames = "fs.*, s.shop_name, s.shop_pic,s.service_rating,s.type";
			String tableName = "favourite_shop as fs left join shop as s on fs.shop_id = s.shop_id";
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
			String orderBy = "fs.updated_at desc";
			page = favouriteShopDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName,
					condition.toString(), orderBy, params, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 根据条件分页查询FavouriteShopProduct
	 * 
	 * @param page,
	 *            conditionMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Page getFavouriteShopProductListByCondition(Page page, Map<String, Object> conditionMap) throws Exception {
		try {
			String fieldNames = "ff.*,p.name,p.cover_img,min(sps.sale_price) as min_price,ertb.max_up_to_percentage";
			StringBuilder tableName = new StringBuilder("");
			tableName.append("favourite_shop_product ff LEFT JOIN product p ON ff.product_id = p.product_id ")
					.append("LEFT JOIN shop_product_sku sps ON ff.shop_product_id = sps.shop_product_id ")
					.append("LEFT JOIN event_rush_to_buy ertb ON ff.shop_product_id = ertb.shop_product_id");
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
			condition.append(" group by ff.shop_product_id");
			Object[] params = list.toArray();
			String orderBy = "ff.updated_at desc";
			page = favouriteShopProductDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames,
					tableName.toString(), condition.toString(), orderBy, params, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * @param favourite_shop_id
	 * @return
	 * @throws Exception
	 */
	public FavouriteShop getFavouriteShop(long favourite_shop_id) throws Exception {
		try {
			FavouriteShop favouriteShop = favouriteShopDao.getById(FavouriteShop.class, favourite_shop_id);
			return favouriteShop;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据favouriteShopId更新favourite shop
	 * 
	 * @param favouriteShop
	 */
	public void updateFavouriteShop(FavouriteShop favouriteShop) throws Exception {
		try {
			favouriteShopDao.updateById(favouriteShop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param favourite_shop_product_id
	 * @return
	 * @throws Exception
	 */
	public FavouriteShopProduct getFavouriteShopProduct(long favourite_shop_product_id) throws Exception {
		try {
			FavouriteShopProduct favouriteShopProduct = favouriteShopProductDao.getById(FavouriteShopProduct.class,
					favourite_shop_product_id);
			return favouriteShopProduct;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据favouriteShopId更新favourite shop product
	 * 
	 * @param favouriteShop
	 */
	public void updateFavouriteShopProduct(FavouriteShopProduct favouriteShopProduct) throws Exception {
		try {
			favouriteShopProductDao.updateById(favouriteShopProduct);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据shop id查询商店被收藏数
	 * 
	 * @param shop_id
	 * @return
	 * @throws Exception
	 */
	public int getFavouriteCountByShop(Long shop_id) throws Exception {
		try {
			List<Map<String, Object>> list = shopDao.executeBySql(
					"select ifnull(count(1),0) as favouriteCount " + "from favourite_shop "
							+ "where shop_id = :p1 and enabled= :p2 and status= :p3",
					new Object[] { shop_id, EnabledType.USED, true });
			return Integer.parseInt(list.get(0).get("favouriteCount").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 根据shop id查询商店被收藏数
	 * 
	 * @param shop_id
	 * @return
	 * @throws Exception
	 */
	public int getFavouriteCountByShopProduct(Long shop_product_id) throws Exception {
		try {
			List<Map<String, Object>> list = shopDao.executeBySql(
					"select ifnull(count(1),0) as favouriteCount " + "from favourite_shop_product "
							+ "where shop_product_id = :p1 and enabled= :p2 and status= :p3",
					new Object[] { shop_product_id, EnabledType.USED, true });
			System.out.println("count = " + list.get(0).get("favouriteCount"));
			return Integer.parseInt(list.get(0).get("favouriteCount").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @param shop_product_id
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public FavouriteShopProduct getFavouriteShopProductByCondition(Long shop_product_id, Long user_id)
			throws Exception {
		try {
			Map<String, Object> condition = new HashMap<String, Object>();

			condition.put("user_id", user_id);
			condition.put("shop_product_id", shop_product_id);

			List<FavouriteShopProduct> wishList = favouriteShopProductDao.getByCondition(FavouriteShopProduct.class,
					condition);
			if (wishList.size() > 0) {
				return wishList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param shop_id
	 * @param user_id
	 * @return
	 */
	public FavouriteShop getFavouriteShopByCondition(Long shop_id, Long user_id) throws Exception {
		try {
			Map<String, Object> condition = new HashMap<String, Object>();

			condition.put("user_id", user_id);
			condition.put("shop_id", shop_id);

			List<FavouriteShop> favouriteShops = favouriteShopDao.getByCondition(FavouriteShop.class, condition);
			if (favouriteShops.size() > 0) {
				return favouriteShops.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param favouriteShop
	 * @return
	 * @throws Exception
	 */
	public FavouriteShop createFavouriteShop(FavouriteShop favouriteShop) throws Exception {
		try {
			favouriteShop.favourite_shop_id = favouriteShopDao.create(favouriteShop);
			if (favouriteShop.favourite_shop_id > 0) {
				return favouriteShop;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param favouriteShopProduct
	 * @return
	 * @throws Exception
	 */
	public FavouriteShopProduct createFavouriteShopProduct(FavouriteShopProduct favouriteShopProduct)
			throws Exception {
		try {
			favouriteShopProduct.favourite_shop_product_id = favouriteShopProductDao.create(favouriteShopProduct);
			if (favouriteShopProduct.favourite_shop_product_id > 0) {
				return favouriteShopProduct;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
