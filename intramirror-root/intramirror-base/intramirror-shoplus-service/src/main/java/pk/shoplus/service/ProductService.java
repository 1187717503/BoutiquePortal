package pk.shoplus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Category;
import pk.shoplus.model.Page;
import pk.shoplus.model.Product;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.ProductStatusType;
import pk.shoplus.util.StringUtils;

public class ProductService {

    /**
     * 获取数据库连接
     */
    private EntityDao<Product> productDao = null;

    /**
     * @param conn
     */
    public ProductService(Connection conn) {
        productDao = new EntityDao<Product>(conn);
    }

    public List<Map<String, Object>> getData(String sql) throws Exception {
        try {
            List<Map<String, Object>> data = productDao.executeBySql("select * from (" + sql + ") as t", null);
            return data;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 创建product
     *
     * @param product
     * @return
     * @throws Exception
     */
    public Product createProduct(Product product) throws Exception {
        try {
            Long product_id = productDao.create(product);
            if (product_id > 0) {
                product.product_id = product_id;
            } else {
                product = null;
            }
            return product;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Map<String, Object>> getProductByBrandID(String brandIds, Long vendorId) throws Exception {
        try {
            String sql = "select p.product_id from product p where p.enabled = 1 and p.vendor_id = '" + vendorId + "' and p.product_id in \n" + "(\n"
                    + "select distinct pp.product_id from product_property pp where pp.enabled = 1 and pp.key_name = 'BrandID' and pp.`value` = \""
                    + StringUtils.escapeStr(brandIds) + "\" " + ")";
            return productDao.executeBySql(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Map<String, Object>> getBrandIdListByVendorId(Long vendorId) throws Exception {
        try {
            String sql = "SELECT * FROM brand b WHERE b.brand_id in(\n" + "select distinct brand_id   from product p where p.enabled = 1 and vendor_id = '"
                    + vendorId + "' " + ") AND b.enabled =1  ORDER BY  b.english_name ";
            return productDao.executeBySql(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改product信息
     *
     * @param product
     * @throws Exception
     */
    public void updateProduct(Product product) throws Exception {
        try {
            productDao.updateById(product);
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateBySQL(String sql) throws Exception {
        productDao.updateBySQL(sql, null);
    }

    public List<Map<String, Object>> executeSQL(String sql) throws Exception {
        return productDao.executeBySql(sql, null);
    }

    /**
     * 通过product_id 获取 product
     *
     * @param product_id
     * @return
     * @throws Exception
     */
    public Product getProductById(Long product_id) throws Exception {
        try {
            Product product = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            // 添加条件
            conditionMap.put("product_id", product_id);
            conditionMap.put("enabled", EnabledType.USED);

            // 查询
            List<Product> productList = productDao.getByCondition(Product.class, conditionMap);

            System.out.println("productList.size = " + productList.size());

            if (productList.size() > 0) {
                System.out.println("product 0  in servce" + productList.get(0));
                System.out.println("updated_at 0  in servce" + productList.get(0).updated_at);
                System.out.println("product_id in servce" + productList.get(0).product_id);
                return productList.get(0);
            }

			/*
             * for (Product temp : productList) { if (temp != null) { product =
			 * new Product(); product = temp; break; } }
			 */
            return product;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取 product list 分页
     *
     * @param pagination
     * @param orderBy
     * @return
     * @throws Exception
     */
    /*
     * public List<Product> getProductListByCondition(Pagination pagination,
	 * Map<String, Object> condintionmap, String orderBy) throws Exception{ try
	 * { String fieldName = "*"; List<Product> productList =
	 * this.productDao.getByCondition(Product.class, fieldName, condintionmap,
	 * pagination, orderBy); return productList; } catch (Exception e) { throw
	 * e; } }
	 */

    /**
     * 根据分页情况 获取ProductList
     *
     * @param pageNumber
     * @param pageSize
     * @param fieldNames
     * @param tableName
     * @param whereCondition
     * @param orderBy
     * @param params
     * @return
     * @throws Exception
     */
    public Page getProductListByCondition(long pageNumber, long pageSize, String fieldNames, String tableName, String whereCondition, String orderBy,
            Object[] params) throws Exception {
        try {
            Page productPageList = productDao.getPageBySql(pageNumber, pageSize, fieldNames, tableName, whereCondition, orderBy, params, null, null);
            return productPageList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据条件查找有多少条数据
     *
     * @param condintionmap
     * @return
     */
    public long countProductByCondition(Map<String, Object> condintionmap) throws Exception {
        try {
            // 返回查询结果
            return this.productDao.countByCondition(Product.class, condintionmap);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @param orderBy
     * @return
     * @throws Exception
     */
    public Page getProductPageList(long pageNumber, long pageSize, Map<String, Object> whereCondition, String orderBy) throws Exception {
        try {
            String fieldNames = "*";
            String tableName = "product";
            orderBy = Helper.isNullOrEmpty(orderBy) ? "product_id" : orderBy;
            Page productList = productDao.getPageBySql(pageNumber, pageSize, fieldNames, tableName, whereCondition, orderBy, null, null);
            return productList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 根据条件获取product 相关的 vendor与brand信息
     *
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @param orderBy
     * @return
     * @throws Exception
     */
    public Page getProductPageWithVendorAndBrandAndCategory(long pageNumber, long pageSize, Map<String, Object> whereCondition, String fieldNames,
            String orderBy, String whereStringInput) throws Exception {
        // SELECT a.*, b.vendor_name, c.chinese_name, c.english_name, d.name AS
        // category_name FROM product a LEFT JOIN vendor b on a.vendor_id =
        // b.vendor_id LEFT JOIN brand c on a.brand_id = c.brand_id LEFT JOIN
        // category d on a.category_id = d.category_id where a.enabled =
        // 1 or a.enabled = 0; System.out.println("in
        // getProductPageWithVendorAndBrand");
        try {
            // 1. condition转换成 wherestr
            StringBuilder whereSql = new StringBuilder("");
            List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

            // 先计数
            // count sql: select count(*) from category where parent_id=:p1 and
            // enabled=:p2

            StringBuilder countSql = new StringBuilder("select count(*) from product a LEFT JOIN product_property e ON a.product_id = e.product_id");
            countSql.append(whereSql);
            countSql.append(" and ").append("e.key_name = 'BrandID'");
            if (whereStringInput.length() > 0) {
                countSql.append(" and ").append(whereStringInput);
            }
            System.out.println("count sql: " + countSql.toString());

            // 1. 获取 总数
            Long totalRow = productDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);
            if (totalRow == null || totalRow <= 0) {
                return new Page(null, pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            // excutesql:select * from category where parent_id=:p1 and
            // enabled=:p2 order by category_id limit 0,10
            StringBuilder sql = new StringBuilder("");
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }

            // SELECT a.*, b.vendor_name, c.chinese_name, c.english_name FROM
            // product a LEFT JOIN vendor b on a.vendor_id = b.vendor_id LEFT
            // JOIN brand c on a.brand_id = c.brand_id where a.enabled = 1 or
            // a.enabled = 0;

            sql.append("SELECT a.*, b.vendor_name, c.chinese_name, c.english_name, d.name AS category_name,e.value,a.season_code FROM product a ");
            sql.append("LEFT JOIN vendor b on a.vendor_id = b.vendor_id ");
            sql.append("LEFT JOIN brand c on a.brand_id = c.brand_id ");
            sql.append("LEFT JOIN category d on a.category_id = d.category_id ");
            sql.append("LEFT JOIN product_property e ON a.product_id = e.product_id ");
            sql.append(whereSql);
            if (whereStringInput.length() > 0) {
                sql.append(" and ").append(whereStringInput);
            }

            sql.append(" and ").append("e.key_name = 'BrandID'");

            // order by
            if (!Helper.isNullOrEmpty(orderBy)) {
                sql.append(" order by ").append(orderBy);
            }

            // pageSize或者pageNumber
            // 如果小于1就返回全部数据
            if (pageSize < 1) {
                return new Page();
                // return new Page(categoryDao.executeBySql(sql.toString(),
                // params.toArray()), pageNumber, pageSize, 1l, totalRow);
            }

            if (pageNumber < 1) {
                return new Page();
                // throw new Exception("pageNumber must be more than 1!!");
            }

            // 计数
            long totalPage = totalRow / pageSize;
            if (totalRow % pageSize != 0) {
                totalPage++;
            }
            if (pageNumber > totalPage) {
                return new Page();
                // throw new Exception("pageNumber must be less than
                // totalPage!!");
            }

            // 拼MySQL分页
            long offset = pageSize * (pageNumber - 1);
            sql.append(" limit ").append(offset).append(",").append(pageSize);
            System.out.println("excutesql:" + sql.toString());
            // executeSql
            List<Map<String, Object>> productMapList = productDao.executeBySql(sql.toString(), params.toArray());
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 获取 whereString
    public String getProductWhereString(long pageNumber, long pageSize, Map<String, Object> whereCondition, String fieldNames, String orderBy,
            String whereStringInput) throws Exception {
        try {
            // 1. condition转换成 wherestr
            StringBuilder whereSql = new StringBuilder("");
            // List<Object> params = DaoHelper.appendCondition(whereSql,
            // whereCondition, "a");

            if (whereStringInput.length() > 0) {
                whereSql.append(" and ").append(whereStringInput);
            }

            // order by
            if (!Helper.isNullOrEmpty(orderBy)) {
                whereSql.append(" order by ").append(orderBy);
            }

            // 拼MySQL分页
            long offset = pageSize * (pageNumber - 1);
            whereSql.append(" limit ").append(offset).append(",").append(pageSize);

            return whereSql.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据条件查询Product信息
     *
     * @param condition
     * @return
     */
    public Product getProductByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<Product> list = productDao.getByCondition(Product.class, fieldNames, condition);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * 根据条件获取Product 的详细信息
     *
     * @return
     */
    public List<Map<String, Object>> getProductInfoByCondition(String status, boolean enabled) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append("select p.*,v.vendor_name vendor_name,c.name category_name,b.chinese_name chinese_name,b.english_name english_name from product p \n")

               .append("left join vendor v on p.vendor_id = v.vendor_id \n").append("left join category c on p.category_id = c.category_id\n").append(
                    "left join brand b on p.brand_id = b.brand_id \n").append("where p.enabled = :p1 and p.status = :p2");
            Object[] params = { enabled, status };
            List<Map<String, Object>> list = productDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Map<String, Object>> getProductsByAmazonaws() throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append("select * from product where LOCATE('amazonaws.com',cover_img) > 0 ");
            //sql.append("select * from product where product_id = 485 ");
            List<Map<String, Object>> list = productDao.executeBySql(sql.toString(), null);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据productCode条件获取Product 的详细信息
     *
     * @return
     */
    public List<Map<String, Object>> getProductIdAndNameByCode(String productCode) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append("SELECT product_id, product_code, name FROM product WHERE product_code in ('" + productCode + "')");
            List<Map<String, Object>> list = productDao.executeBySql(sql.toString(), null);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Excel data export
     *
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getProductInfoByCondition(String start, String end) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql
                    .append("select p.*,v.vendor_name vendor_name,c.name category_name,b.chinese_name chinese_name,b.english_name english_name from product p \n")
                    .append("left join vendor v on p.vendor_id = v.vendor_id \n")
                    .append("left join category c on p.category_id = c.category_id\n")
                    .append("left join brand b on p.brand_id = b.brand_id \n")
                    .append("where p.enabled = true ")
                    .append(" and p.created_at >= :p1 and p.created_at <= :p2");
            Object[] params = { start, end };
            List<Map<String, Object>> list = productDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 分页获取Product的详细信息
     *
     * @param pageNumber
     * @param pageSize
     * @param status
     * @param enabled
     * @return
     */
    public List<Map<String, Object>> getProductInfoWithLimit(long pageNumber, long pageSize, String status, boolean enabled, String vendor_id)
            throws Exception {
        try {
            StringBuilder sql = new StringBuilder("");
            sql
                    .append("select p.*,v.vendor_name vendor_name,c.name category_name,b.chinese_name chinese_name,b.english_name english_name from product p ")
                    .append("left join vendor v on p.vendor_id = v.vendor_id ")
                    .append("left join category c on p.category_id = c.category_id ")
                    .append("left join brand b on p.brand_id = b.brand_id " + "where p.enabled = :p1 and p.status = :p2");
            if (vendor_id != null) {
                sql.append(" and v.vendor_id = " + vendor_id);
            }
            long start = pageSize * (pageNumber - 1);
            long end = pageSize * pageNumber;
            sql.append(" limit " + start + "," + end);
            Object[] params = { enabled, status };
            List<Map<String, Object>> list = productDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    public Product getProductById(long productId) throws Exception {
        try {
            Product product = this.productDao.getById(Product.class, productId);

            return product;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public Page getProductList(Page page) throws Exception {
        try {
            String fieldNames = "p.valid_at,p.`name`,p.product_id,max(s.price) as max_price,min(s.price) as min_price ,p.cover_img ,sum(ss.store) as p_store ";
            String tableName = "sku s left join product p  on s.product_id = p.product_id  left join sku_store ss on s.sku_id = ss.sku_id ";
            StringBuilder condition = new StringBuilder("p.enabled=:p1 and s.enabled=:p2 and ss.enabled=:p3 and  p.`status`=:p4 ");
            // List<Object> paramList = new ArrayList<Object>();
            String orderBy = "";
            Object[] params = new Object[] { EnabledType.USED, EnabledType.USED, EnabledType.USED, ProductStatusType.EXISTING };
            page = productDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName, condition.toString(), orderBy, params, "p.product_id",
                    "p.product_id");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return page;
    }

    public List<Map<String, Object>> getProductByIds(String shop_product_ids) throws Exception {
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append("SELECT p.cover_img FROM shop_product sp LEFT JOIN product p ON sp.product_id = p.product_id WHERE sp.shop_product_id IN (");
            List<Object> map = new ArrayList<Object>();
            String[] ids = shop_product_ids.split(",");

            for (int i = 1; i < ids.length + 1; i++) {
                if (Helper.isNumeric(ids[i - 1])) {
                    sql.append(":p" + i);
                    if (i < ids.length) {
                        sql.append(",");
                    }
                    map.add(Long.parseLong(ids[i - 1]));
                }
            }
            sql.append(")");
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            list = productDao.executeBySql(sql.toString(), map.toArray());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String, Object>> getProvince() throws Exception {
        String sql = "SELECT * FROM address_province where 1=:p1";

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = productDao.executeBySql(sql, new Object[] { 1 });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }

    public List<Map<String, Object>> getCityByProvince(Integer pid) throws Exception {
        String sql = "SELECT id,`name` FROM address_city where address_province_id=:p1";

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = productDao.executeBySql(sql, new Object[] { pid });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }

    /**
     * supply center product detail
     *
     * @param productId
     * @return
     */
    public List<Map<String, Object>> getProductDetail(Long productId) throws Exception {
        /*
         * SELECT
		 * p.`product_id`,p.`product_code`,p.`name`,p.`cover_img`,p.`valid_at`,p
		 * .`updated_at`,p.`product_rating`,sk.max_price,sk.min_price,s.stock,s.
		 * orders,p.`description`,p.`description_img` FROM `product` p LEFT JOIN
		 * (SELECT product_id,MAX(sk.`price`) max_price,MIN(sk.`price`)
		 * min_price FROM sku sk WHERE sk.`product_id`=1 GROUP BY
		 * sk.`product_id`) sk ON sk.product_id=p.`product_id` LEFT JOIN (SELECT
		 * product_id,SUM(store) stock,SUM(ordered) orders FROM sku_store s
		 * WHERE s.`product_id`= 1 GROUP BY product_id) s ON
		 * s.product_id=p.product_id WHERE p.`enabled`=1 AND p.`product_id`=1
		 */

        StringBuilder sb = new StringBuilder();
        sb
                .append("SELECT p.product_id,p.product_code,p.brand_id,p.`name`,p.cover_img,p.valid_at,p.updated_at,p.customer_rating,sk.max_price,sk.min_price,s.stock,s.orders,p.`description`,p.`description_img` FROM `product` p")
                .append(" LEFT JOIN (SELECT product_id,MAX(sk.`price`) max_price,MIN(sk.`price`) min_price FROM sku sk WHERE sk.`product_id`=:p1 GROUP BY sk.`product_id`) sk ON sk.product_id=p.`product_id`")
                .append(" LEFT JOIN (SELECT product_id,SUM(store) stock,SUM(ordered) orders FROM sku_store s WHERE s.`product_id`=:p2 GROUP BY product_id) s ON s.product_id=p.product_id")
                .append(" WHERE p.`enabled`=1 AND p.`product_id`=:p3");

        Object[] params = new Object[] { productId, productId, productId };
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = productDao.executeBySql(sb.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }

    /**
     * supply center product sku list
     *
     * @param productId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getProductSkuList(Long productId) throws Exception {
        /*
         * SELECT p.`product_code`,sk.`price`,sk.`sku_id` FROM product p INNER
		 * JOIN sku sk ON sk.`product_id`=p.`product_id` AND sk.`enabled`=1
		 * WHERE p.`product_id`=1
		 *
		 */
        /*
         * SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.`name`
		 * key_name,pro_val.`value` key_value FROM `sku_property` sku_p inner
		 * JOIN product_property_key pro_key on
		 * sku_p.product_property_key_id=pro_key.product_property_key_id and
		 * pro_key.enabled=1 inner JOIN product_sku_property_value pro_val on
		 * sku_p.product_sku_property_value_id=pro_val.
		 * product_sku_property_value_id and pro_val.enabled=1 where
		 * sku_p.enabled=1 and sku_p.sku_id=3;
		 */
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p.`product_code`,sk.`price`,sk.`sku_id` FROM product p ").append(
                " INNER JOIN sku sk ON sk.`product_id`=p.`product_id` AND sk.`enabled`=1").append(" WHERE p.`product_id`=:p1");
        StringBuilder sbSub = new StringBuilder();
        sbSub
                .append("SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.product_sku_property_key_id key_id,pro_key.`name` key_name,pro_val.value pro_value FROM `sku_property` sku_p")
                .append(" INNER JOIN product_sku_property_key pro_key on sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id and pro_key.enabled=1")
                .append(" INNER JOIN product_sku_property_value pro_val on sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id and pro_val.enabled=1")
                .append(" WHERE sku_p.enabled=1 and sku_p.sku_id=:p1");

        Object[] params = new Object[] { productId };

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> sublist = new ArrayList<Map<String, Object>>();
        try {
            list = productDao.executeBySql(sb.toString(), params);
            for (Map<String, Object> map : list) {
                Long skuId = Long.parseLong(map.get("sku_id").toString());
                Object[] skuParams = new Object[] { skuId };
                sublist = productDao.executeBySql(sbSub.toString(), skuParams);
                map.put("propertyList", sublist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;

    }

    /**
     * supply center product comment
     *
     * @param productId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page getProductCommentList(Long productId, long pageNumber, long pageSize) throws Exception {
        String orderBy = " c.created_at desc";
        return getProductCommentList(productId, pageNumber, pageSize, orderBy);
    }

    public Page getProductCommentList(Long productId, long pageNumber, long pageSize, String orderBy) throws Exception {
        String fieldNames = "c.`comment_id`,c.`content`,c.`created_at`,cr.`content` reply_content,r.`rate`,u.`username`,u.`user_image`";
        StringBuilder sb = new StringBuilder();
        sb.append("`comment` c").append(" LEFT JOIN `comment` cr ON cr.`reply_comment_id`=c.`comment_id`").append(
                " LEFT JOIN rate_product r ON c.`comment_id`=r.`comment_id`").append(" LEFT JOIN `user` u ON u.`user_id`=c.`user_id`");
        String whereCondition = "c.`enabled`=1 AND c.reply_comment_id=-1 AND r.`product_id`=:p1";

        Object[] params = new Object[] { productId };
        Page page = new Page();
        try {
            page = productDao.getPageBySql(pageNumber, pageSize, fieldNames, sb.toString(), whereCondition, orderBy, params, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return page;
    }

    public Page getProductList(Page page, String brand_id, List<Category> sub_category_ids, String min_price, String max_price, String order_by,
            String product_name, String is_added, String vendor_id) throws Exception {
        try {
            String fieldNames = "v.score as vendor_score ,p.score as product_score,p.valid_at,p.`name`,p.product_id,p.category_id,p.brand_id,max(s.price) as max_price,min(s.price) as min_price ,p.cover_img ,sum(ss.store) as p_store ";
            String tableName = "sku s left join product p  on s.product_id = p.product_id left join sku_store ss on s.sku_id = ss.sku_id left join vendor v on p.vendor_id = v.vendor_id";
            StringBuilder condition = new StringBuilder("p.enabled=:p1 and s.enabled=:p2 and ss.enabled=:p3 and  p.`status`=:p4 and p.valid_at>:p5");
            ArrayList<Object> paramList = new ArrayList<Object>();
            paramList.add(EnabledType.USED);
            paramList.add(EnabledType.USED);
            paramList.add(EnabledType.USED);
            paramList.add(ProductStatusType.EXISTING);
            paramList.add(Helper.getCurrentTimeToUTCWithDate());

            int i = 6;
            if (!Helper.isNullOrEmpty(brand_id)) {
                condition.append(" and  p.brand_id=:p" + i);
                paramList.add(brand_id);
                i++;
            }

            if (!Helper.isNullOrEmpty(min_price)) {
                condition.append(" and  s.price >=:p" + i);
                paramList.add(Long.parseLong(min_price));
                i++;
            }

            if (!Helper.isNullOrEmpty(max_price)) {
                condition.append(" and  s.price <=:p" + i);
                paramList.add(Long.parseLong(max_price));
                i++;
            }

            if (sub_category_ids.size() > 0) {
                condition.append(" and  p.category_id in (");
                for (Category category : sub_category_ids) {
                    condition.append(":p" + i + ",");
                    paramList.add(category.category_id);
                    i++;
                }
                condition = new StringBuilder(condition.subSequence(0, condition.length() - 1));
                condition.append(')');
            }

            if (!Helper.isNullOrEmpty(product_name)) {
                condition.append(" and  exists p.`name` like :p" + i);
                paramList.add("%" + product_name + "%");
                i++;
            }

            if (!Helper.isNullOrEmpty(vendor_id)) {
                condition.append(" and  v.vendor_id=:p" + i);
                paramList.add(vendor_id);
                i++;
            }

            if (!Helper.isNullOrEmpty(is_added)) {
                if ("1".equals(is_added)) {
                    condition.append(" and  exists (select shop_product_id from shop_product sp where sp.shop_id = 65 and sp.product_id = p.product_id)");
                } else if ("0".equals(is_added)) {
                    condition.append(" and not exists (select shop_product_id from shop_product sp where sp.shop_id = 65 and sp.product_id = p.product_id)");
                }

            }

            String orderBy = "";
            if (!Helper.isNullOrEmpty(order_by)) {
                orderBy = order_by;
            }

            System.out.println("condition.toString():" + condition.toString());

            System.out.println("paramList.toArray():" + paramList.toArray().toString());

            page = productDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName, condition.toString(), orderBy, paramList.toArray(),
                    "p.product_id", "p.product_id");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return page;
    }

    /**
     * App端根据条件获取product 相关的 vendor与brand信息
     *
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @param orderBy
     * @return
     * @throws Exception
     */
    public Page getAppProductPageWithVendorAndBrandAndCategory(long pageNumber, long pageSize, Map<String, Object> whereCondition, String fieldNames,
            String orderBy, String whereStringInput) throws Exception {
        // SELECT a.*, b.vendor_name, c.chinese_name, c.english_name, d.name AS
        // category_name FROM product a LEFT JOIN vendor b on a.vendor_id =
        // b.vendor_id LEFT JOIN brand c on a.brand_id = c.brand_id LEFT JOIN
        // category d on a.category_id = d.category_id where a.enabled =
        // 1 or a.enabled = 0; System.out.println("in
        // getProductPageWithVendorAndBrand");
        try {
            // 1. condition转换成 wherestr
            StringBuilder whereSql = new StringBuilder("");
            List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

            StringBuilder countSql = new StringBuilder(
                    "select count(*) from product a LEFT JOIN product_property e  ON a.product_id = e.product_id  LEFT JOIN sku f  ON  a.product_id = f.product_id  INNER JOIN product_property g ");

            countSql.append(whereSql);
            countSql.append(" and ").append("e.product_id=g.product_id");
            countSql.append(" and ").append("e.key_name = 'ColorCode'");
            countSql.append(" and ").append("g.key_name = 'BrandID'");
            if (whereStringInput.length() > 0) {
                countSql.append(" and ").append(whereStringInput);
            }
            System.out.println("count sql: " + countSql.toString());

            // 1. 获取 总数
            Long totalRow = productDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);
            if (totalRow == null || totalRow <= 0) {
                return new Page(null, pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            // excutesql:select * from category where parent_id=:p1 and
            // enabled=:p2 order by category_id limit 0,10
            StringBuilder sql = new StringBuilder("");
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }

            // SELECT a.*, b.vendor_name, c.chinese_name, c.english_name FROM
            // product a LEFT JOIN vendor b on a.vendor_id = b.vendor_id LEFT
            // JOIN brand c on a.brand_id = c.brand_id where a.enabled = 1 or
            // a.enabled = 0;

            sql.append(
                    "SELECT a.*, b.vendor_name, c.chinese_name, c.english_name, d.name AS category_name,e.value AS ColorCode,g.value AS BrandID FROM product a ");
            sql.append("LEFT JOIN vendor b on a.vendor_id = b.vendor_id ");
            sql.append("LEFT JOIN brand c on a.brand_id = c.brand_id ");
            sql.append("LEFT JOIN category d on a.category_id = d.category_id ");
            sql.append("LEFT JOIN product_property e ON a.product_id = e.product_id ");
            sql.append("LEFT JOIN sku f ON a.product_id = f.product_id ");
            sql.append("INNER JOIN product_property g ");

            sql.append(whereSql);
            if (whereStringInput.length() > 0) {
                sql.append(" and ").append(whereStringInput);
            }
            sql.append("and e.product_id=g.product_id");

            sql.append(" and ").append("e.key_name = 'ColorCode'").append(" and ").append("g.key_name='BrandID'").append(" group by ").append("a.product_id");

            // order by
            if (!Helper.isNullOrEmpty(orderBy)) {
                sql.append(" order by ").append(orderBy);
            }

            // pageSize或者pageNumber
            // 如果小于1就返回全部数据
            if (pageSize < 1) {
                return new Page();
                // return new Page(categoryDao.executeBySql(sql.toString(),
                // params.toArray()), pageNumber, pageSize, 1l, totalRow);
            }

            if (pageNumber < 1) {
                return new Page();
                // throw new Exception("pageNumber must be more than 1!!");
            }

            // 计数
            long totalPage = totalRow / pageSize;
            if (totalRow % pageSize != 0) {
                totalPage++;
            }
            if (pageNumber > totalPage) {
                return new Page();
                // throw new Exception("pageNumber must be less than
                // totalPage!!");
            }

            // 拼MySQL分页
            long offset = pageSize * (pageNumber - 1);
            sql.append(" limit ").append(offset).append(",").append(pageSize);
            System.out.println("excutesql:" + sql.toString());
            // executeSql
            List<Map<String, Object>> productMapList = productDao.executeBySql(sql.toString(), params.toArray());
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    //
    //	public Page searchProductListWithLimit(Page page, Map<String, Object> conditionMap,String whereStringInput) throws Exception {
    //		try {
    //			String fieldNames = "p.*,b.chinese_name,b.english_name,c.name AS category_name, v.vendor_name";
    //			StringBuilder tableName = new StringBuilder("");
    //			tableName.append("product p ");
    //			tableName.append("LEFT JOIN vendor v ON p.vendor_id = v.vendor_id ");
    //			tableName.append("LEFT JOIN brand b ON p.brand_id = b.brand_id ");
    //			tableName.append("LEFT JOIN category c ON p.category_id = c.category_id ");
    //			tableName.append("INNER JOIN product_property pp ON p.product_id = pp.product_id ");
    //			tableName.append("LEFT JOIN sku s ON s.product_id = p.product_id  ");
    //
    //			StringBuilder condition = new StringBuilder();
    //			Iterator<?> iterator = conditionMap.entrySet().iterator();
    //			List<Object> list = new ArrayList<Object>();
    //			int i = 1;
    //			while (iterator.hasNext()) {
    //				Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
    //				if (i != 1) {
    //					condition.append(" and p." + entry.getKey() + "=:p" + i);
    //				} else {
    //					condition.append("p."+ entry.getKey() + "=:p" + i);
    //				}
    //				i++;
    //				list.add(entry.getValue());
    //			}
    //
    //			condition.append(" and (pp.key_name = 'ColorCode' or pp.key_name='BrandID') ");
    //
    //			if (Helper.checkNotNull(whereStringInput)) {
    //				condition.append(" and " + whereStringInput);
    //			}
    //
    //			Object[] params = list.toArray();
    //			String groupBy = " p.product_id ";
    //			String countCondition = " p.product_id ";
    //
    //			page = productDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames,
    //					tableName.toString(), condition.toString(), null, params, countCondition, groupBy);
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //		}
    //		return page;
    //	}

    public Page searchProductListWithLimit(Page page, Map<String, Object> conditionMap, String whereStringInput) throws Exception {
        try {
            String fieldNames = "p.*,b.chinese_name,b.english_name,c.name AS category_name, v.vendor_name";
            StringBuilder tableName = new StringBuilder("");
            tableName.append("product p ");
            tableName.append("LEFT JOIN vendor v ON p.vendor_id = v.vendor_id ");
            tableName.append("LEFT JOIN brand b ON p.brand_id = b.brand_id ");
            tableName.append("LEFT JOIN category c ON p.category_id = c.category_id ");
            tableName.append("INNER JOIN product_property pp ON p.product_id = pp.product_id ");
            tableName.append("LEFT JOIN sku s ON s.product_id = p.product_id  ");

            StringBuilder condition = new StringBuilder();
            Iterator<?> iterator = conditionMap.entrySet().iterator();
            List<Object> list = new ArrayList<Object>();
            int i = 1;
            while (iterator.hasNext()) {
                Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
                if (i != 1) {
                    condition.append(" and p." + entry.getKey() + "=:p" + i);
                } else {
                    condition.append("p." + entry.getKey() + "=:p" + i);
                }
                i++;
                list.add(entry.getValue());
            }

            condition.append(" and (pp.key_name = 'ColorCode' or pp.key_name='BrandID') ");

            if (Helper.checkNotNull(whereStringInput)) {
                condition.append(" and " + whereStringInput);
            }

            Object[] params = list.toArray();
            String groupBy = " p.product_id ";
            String countCondition = " p.product_id ";

            page = productDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName.toString(), condition.toString(), null, params, countCondition,
                    groupBy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    /**
     * 获取商品分组里面的商品信息
     *
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @return
     * @throws Exception
     */
    public Page getVendorProductPage(long pageNumber, long pageSize, Map<String, Object> whereCondition) throws Exception {

        try {
            String countSql = getVendorProductSQL("count(*) as countSum", whereCondition);
            System.out.println("count sql: " + countSql.toString());

            // 1. 获取 总数
            List<Map<String, Object>> listMap = productDao.executeBySql(countSql, null);
            Long totalRow = 0L;
            if (!CollectionUtils.isEmpty(listMap)) {
                totalRow = Long.valueOf(listMap.get(0).get("countSum").toString());
            }

            if (totalRow == 0L) {
                return new Page(null, pageNumber, pageSize, 1l, 0l);
            }

            String sql = getVendorProductSQL(
                    "t_pro.product_id,t_pro.product_code,t_pro.`name` as productName ,t_pp.brandID,t_pp.colorCode,t_pp.seasonCode,brand.english_name,brand.chinese_name,t_category.`name` as c1_name,t_category.c2_name,t_category.c3_name",
                    whereCondition);

            // pageSize或者pageNumber
            // 如果小于1就返回全部数据
            if (pageSize < 1) {
                return new Page();
            }

            if (pageNumber < 1) {
                return new Page();
            }

            // 计数
            long totalPage = totalRow / pageSize;
            if (totalRow % pageSize != 0) {
                totalPage++;
            }
            if (pageNumber > totalPage) {
                return new Page();
            }

            // 拼MySQL分页
            long offset = pageSize * (pageNumber - 1);
            sql += " limit " + offset + "," + pageSize;
            System.out.println("excutesql:" + sql.toString());
            // executeSql
            List<Map<String, Object>> productMapList = productDao.executeBySql(sql.toString(), null);
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String getVendorProductSQL(String fileds, Map<String, Object> conditions) {

        String sql = "select " + fileds + " from product t_pro left join"
                + "(select t_pp.product_id, max(case t_pp.key_name when 'ColorCode' then t_pp.`value` else '' end) as colorCode, "
                + "max(case t_pp.key_name when 'BrandID' then t_pp.`value` else '' end) as brandID, "
                + "max(case t_pp.key_name when 'SeasonCode' then t_pp.`value` else '' end) as seasonCode "
                + "from product_property t_pp where t_pp.enabled = 1 group by t_pp.product_id ) t_pp on t_pro.product_id = t_pp.product_id "
                + "left join brand on (brand.brand_id = t_pro.brand_id and brand.enabled = 1) "
                + "left join ( select c1.category_id,c1.chinese_name , c1.name, c2.* from category c1 left join "
                + "(select c2.name as c2_name, c2.category_id as c2_categoryId,c2.chinese_name as c2_chineseName,c2.parent_id as c2_parentId,c3.name as c3_name, c3.category_id as c3_categoryId,c3.chinese_name as c3_chineseName,c3.parent_id as c3_parentId  from category c2 left join "
                + "(select c3.name,c3.category_id,c3.chinese_name,c3.parent_id from category c3 "
                + "where c3.level = 3 and c3.enabled = 1 ) c3 on c2.category_id = c3.parent_id where c2.level = 2 and c2.enabled = 1 and c3.category_id is not null "
                + ") c2 on c1.category_id = c2.c2_parentId " + "where c1.enabled = 1 and c1.level = 1 and c2.c2_categoryId is not null "
                + ") t_category on t_pro.category_id = t_category.c3_categoryId  WHERE 1=1  ";
        if (conditions.get("product_group_id") != null) {
            sql += " and t_pro.product_group_id=" + conditions.get("product_group_id") + " ";
        }
        if (conditions.get("vendor_id") != null) {
            sql += "and t_pro.vendor_id=" + conditions.get("vendor_id") + " ";
        }
        return sql;
    }

    /**
     * 根据groupId和VendorId条件查询Product信息
     *
     * @param condition
     * @return
     */
    public List<Product> getProductByGroupIdAndVendorId(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<Product> list = productDao.getByCondition(Product.class, fieldNames, condition);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --

    private static final Logger logger = Logger.getLogger(ProductService.class);

    public Map<String, Object> getBoutiqueCategory(String vendor_id, String boutique_category_id) throws Exception {
        try {
            String sql = "select distinct c.category_id from api_category_map acm\n"
                    + "inner join api_configuration ac on(ac.enabled = 1 and ac.api_configuration_id = acm.api_configuration_id)\n"
                    + "inner join category c on(c.enabled = 1 and c.category_id = acm.category_id)\n" + "where acm.enabled = 1 and ac.vendor_id = '" + vendor_id
                    + "'\n" + "and trim(acm.boutique_category_id) = trim(\"" + boutique_category_id + "\")\n" + " and c.category_id is not null ";
            List<Map<String, Object>> categoryMap = productDao.executeBySql(sql, null);
            if (categoryMap != null && categoryMap.size() > 0) {
                return categoryMap.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public Map<String, Object> getCategory(String one, String two, String three) throws Exception {
        try {
            String sql = "select c3.`category_id` ,c1.`name` ,c2.`name` ,c3.`name`  from `category`  c1\n"
                    + "inner join `category`  c2 on(c1.`category_id` = c2.`parent_id` ) \n"
                    + "inner join `category`  c3 on(c2.`category_id` = c3.`parent_id` )\n"
                    + "where c1.`enabled`  = 1 and c2.`enabled`  = 1 and c3.`enabled`  = 1 \n" + "and c1.`name`  = trim(\"" + one
                    + "\") and c2.`name` = trim(\"" + two + "\") and c3.`name` = trim(\"" + three + "\")";
            List<Map<String, Object>> categoryMap = productDao.executeBySql(sql, null);
            if (categoryMap != null && categoryMap.size() > 0) {
                return categoryMap.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public boolean getNoImg(String brandName, Long vendor_id) {
        try {
            String sql = "select abm.`api_configuration_id` from `api_brand_map` abm \n"
                    + "inner join `api_configuration`  ac on (abm.`api_configuration_id` = ac.`api_configuration_id`  and ac.`enabled`  = 1 and abm.`enabled`  = 1) \n"
                    + "where (abm.`brand_name`  = trim(\"" + brandName + "\")  or trim(abm.`boutique_brand_name`) = trim(\"" + brandName
                    + "\") ) and abm.`no_img`  = 1 and ac.`vendor_id`  =" + vendor_id;
            logger.info("ProductService,getNoImg,sql:" + sql);
            List<Map<String, Object>> imgMap = productDao.executeBySql(sql, null);
            if (imgMap == null || imgMap.size() == 0) {
                return false;
            }
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    public Map<String, Object> getThreeCategory(String vendor_id, String one, String two, String three) throws Exception {
        try {
            String sql = "select distinct c.category_id from api_category_map acm\n"
                    + "inner join api_configuration ac on(ac.enabled = 1 and ac.api_configuration_id = acm.api_configuration_id)\n"
                    + "inner join category c on(c.enabled = 1 and c.category_id = acm.category_id)\n" + "where acm.enabled = 1 and ac.vendor_id = '" + vendor_id
                    + "'\n" + "and trim(acm.boutique_first_category) = trim(\"" + one + "\")\n" + "and trim(acm.boutique_second_category) = trim(\"" + two
                    + "\")\n" + "and trim(acm.boutique_third_category) = trim(\"" + three + "\")\n" + " and c.category_id is not null ";
            List<Map<String, Object>> categoryMap = productDao.executeBySql(sql, null);
            if (categoryMap != null && categoryMap.size() > 0) {
                return categoryMap.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public Map<String, Object> getCategoryWithoutC2(String vendor_id, String one, String three) throws Exception {
        try {
            String sql = "select distinct c.category_id from api_category_map acm\n"
                    + "inner join api_configuration ac on(ac.enabled = 1 and ac.api_configuration_id = acm.api_configuration_id)\n"
                    + "inner join category c on(c.enabled = 1 and c.category_id = acm.category_id)\n" + "where acm.enabled = 1 and ac.vendor_id = '" + vendor_id
                    + "'\n" + "and trim(acm.boutique_first_category) = trim(\"" + one + "\")\n" + "and trim(acm.boutique_third_category) = trim(\"" + three
                    + "\")\n" + " and c.category_id is not null ";
            List<Map<String, Object>> categoryMap = productDao.executeBySql(sql, null);
            if (categoryMap != null && categoryMap.size() > 0) {
                return categoryMap.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public Map<String, Object> getBrand(String brandName) throws Exception {
        try {
            String sql = "select b.brand_id,b.english_name from brand b where trim(b.english_name) = trim(\"" + brandName + "\") and b.enabled = 1";

            List<Map<String, Object>> brandMap = productDao.executeBySql(sql, null);
            if (brandMap != null && brandMap.size() > 0) {
                return brandMap.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public Map<String, Object> getBrandMapping(String brandName) throws Exception {
        try {
            String sql = " select abm.brand_id,b.english_name from `api_brand_map`  abm\n"
                    + " inner join `api_configuration`  ac on(abm.`api_configuration_id` = ac.`api_configuration_id`  and ac.`enabled`  = 1 and abm.`enabled`  = 1)\n"
                    + " inner join brand b on(b.brand_id = abm.brand_id and b.enabled = 1)\n"
                    //                    + "where ac.`vendor_id`  = "+vendor_id+" and trim(abm.`boutique_brand_name`)  =trim(\"" + brandName + "\")";
                    + " where trim(abm.`boutique_brand_name`)  = trim(\"" + brandName + "\")";

            List<Map<String, Object>> brandMap = productDao.executeBySql(sql, null);
            if (brandMap != null && brandMap.size() > 0) {
                return brandMap.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public boolean ifBrand(String brand_id) throws Exception {
        if (org.apache.commons.lang.StringUtils.isNotBlank(brand_id)) {
            String sql = "select brand_id from brand where brand_id='" + brand_id + "' and enabled = 1";
            List<Map<String, Object>> brandMap = productDao.executeBySql(sql, null);
            if (brandMap == null || brandMap.size() == 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean ifCategory(String category_id) throws Exception {
        if (org.apache.commons.lang.StringUtils.isNotBlank(category_id)) {
            String sql = "select category_id from category where category_id='" + category_id + "' and enabled = 1";
            List<Map<String, Object>> categoryMap = productDao.executeBySql(sql, null);
            if (categoryMap == null || categoryMap.size() == 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    public Map<String, Object> getSeason(String seasonCode) throws Exception {
        try {
            String sql = "select season_code from `season_mapping` where enabled = 1 and (trim(`season_code`)  = trim(\"" + seasonCode
                    + "\") or trim(`boutique_season_code`)  = trim(\"" + seasonCode + "\"))";

            List<Map<String, Object>> seasonMap = productDao.executeBySql(sql, null);
            if (seasonMap != null && seasonMap.size() > 0) {
                return seasonMap.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public String getDeafultCategory(Long vendor_id, String one, String two) throws Exception {
        try {

            String rsCategoryId = null;

            String selCategory = "select c3.`category_id` ,c1.`name` ,c2.`name` ,c3.`name`  from `category`  c1\n"
                    + "inner join `category`  c2 on(c1.`category_id` = c2.`parent_id` ) \n"
                    + "inner join `category`  c3 on(c2.`category_id` = c3.`parent_id` )\n"
                    + "where c1.`enabled`  = 1 and c2.`enabled`  = 1 and c3.`enabled`  = 1 \n" + "and c1.`name`  = trim(\"" + one
                    + "\") and c2.`name` = trim(\"" + two + "\")";
            List<Map<String, Object>> listMap = productDao.executeBySql(selCategory, null);

            if (listMap == null || listMap.size() == 0) {
                String selApiCategorySQL = "select distinct c.* from api_category_map acm\n"
                        + "left join api_configuration ac on(ac.enabled = 1 and ac.api_configuration_id = acm.api_configuration_id)\n"
                        + "left join category c on(c.enabled = 1 and c.category_id = acm.category_id)\n" + "where acm.enabled = 1 and ac.vendor_id = '"
                        + vendor_id + "'\n" + "and trim(acm.boutique_first_category) = trim(\"" + one + "\")\n"
                        + "and trim(acm.boutique_second_category) = trim(\"" + two + "\")\n" + " and c.category_id is not null ";
                listMap = productDao.executeBySql(selApiCategorySQL, null);
            }

            if (listMap != null && listMap.size() > 0) {
                String category_id = listMap.get(0).get("category_id").toString();
                String selCategorySql =
                        "select " + "c1.`name` as c1Name,c1.`category_id` as c1Category_id," + "c2.`name` as c2Name,c2.`category_id` as c2Category_id,"
                                + "c3.`name` as c3Name,c3.`category_id` as c3Category_id " + "from `category`  c1\n"
                                + "inner join `category`  c2 on(c1.`category_id` = c2.`parent_id` ) \n"
                                + "inner join `category`  c3 on(c2.`category_id` = c3.`parent_id` )\n"
                                + "where c1.`enabled`  = 1 and c2.`enabled`  = 1 and c3.`enabled`  = 1 \n" + "and c3.`category_id`  = '" + category_id + "'";

                List<Map<String, Object>> categoryMap = productDao.executeBySql(selCategorySql, null);
                if (categoryMap != null && categoryMap.size() > 0) {
                    String c1Name = categoryMap.get(0).get("c1Name").toString();
                    String c1Category_id = categoryMap.get(0).get("c1Category_id").toString();

                    String c2Name = categoryMap.get(0).get("c2Name").toString();
                    String c2Category_id = categoryMap.get(0).get("c2Category_id").toString();

                    if (org.apache.commons.lang3.StringUtils.trim(c1Name).equals("Men") && org.apache.commons.lang3.StringUtils.trim(c1Category_id).equals(
                            "1499")) {

                        if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Bags") && org.apache.commons.lang3.StringUtils.trim(c2Category_id).equals(
                                "1505")) { // 二级为包,放入单肩包
                            rsCategoryId = "1551";
                        } else if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Accessories") && org.apache.commons.lang3.StringUtils.trim(
                                c2Category_id).equals("1507")) { // 二级为配饰,放入others
                            rsCategoryId = "1634";
                        } else if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Clothing") && org.apache.commons.lang3.StringUtils.trim(
                                c2Category_id).equals("1504")) { // 二级为衣服，放入Top,上衣
                            rsCategoryId = "1524";
                        } else if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Shoes") && org.apache.commons.lang3.StringUtils
                                .trim(c2Category_id)
                                .equals("1506")) { // 二级为鞋，放入休闲商务鞋
                            rsCategoryId = "1653";
                        }
                    } else if (org.apache.commons.lang3.StringUtils.trim(c1Name).equals("Women") && org.apache.commons.lang3.StringUtils
                            .trim(c1Category_id)
                            .equals("1568")) {

                        if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Bags") && org.apache.commons.lang3.StringUtils.trim(c2Category_id).equals(
                                "1598")) { // 二级为包,放入单肩包
                            rsCategoryId = "1604";
                        } else if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Accessories") && org.apache.commons.lang3.StringUtils.trim(
                                c2Category_id).equals("1608")) { // 二级为配饰,放入others
                            rsCategoryId = "1625";
                        } else if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Clothing") && org.apache.commons.lang3.StringUtils.trim(
                                c2Category_id).equals("1569")) { // 二级为衣服，放入Top,上衣
                            rsCategoryId = "1582";
                        } else if (org.apache.commons.lang3.StringUtils.trim(c2Name).equals("Shoes") && org.apache.commons.lang3.StringUtils
                                .trim(c2Category_id)
                                .equals("1584")) { // 二级为鞋，放入高跟鞋
                            rsCategoryId = "1594";
                        }
                    }
                }
            }
            return rsCategoryId;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean duplicateColorBrandByADCreate(String brandID, String colorCode, String seasonCode) {
        try {
            String sql = "select p.product_id from `product`  p where p.`designer_id`  = \"" + brandID + "\" and p.`color_code`  = \"" + colorCode
                    + "\" and p.`season_code`  = \"" + seasonCode + "\" and p.`vendor_id`  = 22 and p.`enabled`  = 1";

            List<Map<String, Object>> productMap = productDao.executeBySql(sql, null);
            if (productMap != null && productMap.size() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean duplicateColorBrandByADUpdate(String brandID, String colorCode, String seasonCode) {
        try {
            String sql = "select p.product_id from `product`  p where p.`designer_id`  = \"" + brandID + "\" and p.`color_code`  = \"" + colorCode
                    + "\" and p.`season_code`  = \"" + seasonCode + "\" and p.`vendor_id`  = 22 and p.`enabled`  = 1";

            List<Map<String, Object>> productMap = productDao.executeBySql(sql, null);
            if (productMap != null && productMap.size() > 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean isClothing(Long categorId) throws Exception {
        String sql =
                "select c.`category_id`  from `category`  c where c.`enabled`  = 1 and c.`parent_id` in(1504,1569)  and c.`category_id`  = '" + categorId + "'";
        List<Map<String, Object>> categoryMap = productDao.executeBySql(sql, null);
        if (categoryMap != null && categoryMap.size() > 0) {
            return true;
        }
        return false;
    }

    public Map<String, Object> selProductImage(Long product_id) throws Exception {
        String selSql = "select product_id,image from product_image where product_id =" + product_id;
        List<Map<String, Object>> selMap = productDao.executeBySql(selSql, null);
        if (selMap == null || selMap.size() == 0) {
            return null;
        }
        return selMap.get(0);
    }

    /*public boolean isProductImage(Long product_id,String image) {
        String selSql = "select product_id,image from product_image where product_id ="+product_id;
        List<Map<String,Object>> selMap = productDao.executeBySql(selSql,null);
        if(selMap != null && selMap.size() >0){
            String productImage = selMap.get(0).get("image").toString();
            if(productImage.contains(image)) {
                return true;
            }
        }
        return false;
    }*/

    public void insertProductImage(Long product_id, String image) {
        try {
            String deleteSql = "DELETE FROM product_image where product_id = " + product_id;
            productDao.updateBySQL(deleteSql, null);
            String insertSql = "insert into product_image (product_id,image) values(" + product_id + ",'" + image + "')";
            productDao.updateBySQL(insertSql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> selectProductByDesignerColor(String designerId, String colorCode, Long vendor_id) throws Exception {
        String sql = "select p.`product_id` ,p.`designer_id` ,p.`color_code` ,s.`season_sort`,p.`season_code`  from `product`  p \n"
                + "inner join `season`  s  on(p.`enabled` = 1 and p.`season_code`  = s.`season_code` )\n" + "where p.`vendor_id` = " + vendor_id
                + "  and p.`designer_id` = \"" + designerId + "\" and p.`color_code`  = \"" + colorCode + "\" \n" + "order by s.`season_sort`  desc ";
        return productDao.executeBySql(sql, null);
    }

}
