package pk.shoplus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.common.Helper;
import pk.shoplus.common.utils.StringUtil;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Category;
import pk.shoplus.model.Page;
import pk.shoplus.model.ShopProduct;
import pk.shoplus.model.ShopProductSku;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.ProductStatusType;
import pk.shoplus.parameter.ShopProductType;
import pk.shoplus.vo.DateVO;
import pk.shoplus.vo.ProductQueryVo;

public class ShopProductService {

    private static Logger log = Logger.getLogger(ShopProductService.class);

    /**
     * 获取数据库连接
     */
    private EntityDao<ShopProduct> shopProductDao = null;
    private EntityDao<ShopProductSku> shopProductSkuDao = null;

    /**
     * @param conn
     */
    public ShopProductService(Connection conn) {
        shopProductDao = new EntityDao<ShopProduct>(conn);
        shopProductSkuDao = new EntityDao<ShopProductSku>(conn);
    }

    /**
     * 更新 shopProduct 实体
     *
     * @param shopProduct
     * @throws Exception
     */
    public void updateShopProduct(ShopProduct shopProduct) throws Exception {
        try {
            shopProductDao.updateById(shopProduct);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过 shop_product_id找 shopProduct数据
     *
     * @param shop_product_id
     * @return
     * @throws Exception
     */
    public ShopProduct getShopProductById(Long shop_product_id) throws Exception {
        try {
            ShopProduct shopProduct = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            // 添加条件
            conditionMap.put("shop_product_id", shop_product_id);
            conditionMap.put("enabled", EnabledType.USED);

            // 查询
            List<ShopProduct> shopProductList = shopProductDao.getByCondition(ShopProduct.class, conditionMap);

            for (ShopProduct temp : shopProductList) {
                if (temp != null) {
                    shopProduct = new ShopProduct();
                    shopProduct = temp;
                    break;
                }
            }
            return shopProduct;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过 condition 来回去 shop product list 列表
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public List<ShopProduct> getShopProductListByCondition(Map<String, Object> condition) throws Exception {
        try {
            // 查询
            List<ShopProduct> shopProductList = shopProductDao.getByCondition(ShopProduct.class, condition);
            return shopProductList;
        } catch (Exception e) {
            throw e;
        }
    }

    // sb.append(" and t_pp.brandID = '" + pqv.getBrandId() + "' ");
    // sb.append(" and t_pro.`name` like '%" + pqv.getName() + "%' ");
    private static String genManyCondition(String conditionFiled, String conditions, String conditionType) {
        String resultSql = "";
        if (StringUtils.isNotBlank(conditions)) {
            String[] filedValues = conditions.split(",");
            if (conditionType.equals("=")) {
                if (conditionFiled.equals("t_pro.category_id")) {
                    resultSql = " and " + conditionFiled + " in (";
                    Connection conn = null;
                    try {
                        conn = DBConnector.sql2o.beginTransaction();
                        CategoryService categoryService = new CategoryService(conn);
                        List<Map<String, Object>> mapList = categoryService.getAllCategory();
//			            List<Category> categoryList = convertMapToCategory(mapList, new ArrayList<Category>(), -1L);

                        for (String filedValue : filedValues) {
                            List<Category> categoryList = convertMapToCategory(mapList, new ArrayList<Category>(), Long.parseLong(filedValue));
                            if (categoryList != null && categoryList.size() != 0) {
                                for (Category category : categoryList) {
                                    List<Category> childrens = category.getChildren();
                                    if (childrens != null && childrens.size() != 0) {
                                        for (Category children : childrens) {
                                            resultSql += children.getCategory_id() + ",";
                                        }
                                    }
                                    resultSql += category.getCategory_id() + ",";
                                }
                            } else {
                                resultSql += filedValue + ",";
                            }
                        }
                        resultSql = resultSql.substring(0, resultSql.length() - 1);
                        resultSql += ") ";
                        conn.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (conn != null) {
                            conn.rollback();
                            conn.close();
                        }
                    } finally {
                        if (conn != null) {
                            conn.close();
                        }
                    }

                } else if (conditionFiled.equals("t_pro.`status`")) {
                    String statusSQL = "";
                    String appendSQL = " and " + conditionFiled + " in (";
                    boolean flag = false;
                    for (String filedValue : filedValues) {
                        if (filedValue.equals("33")) {
                            statusSQL += " (t_pro.`status` = 3 and t_sp.`status` = 0) and ";
                        } else if (filedValue.equals("44")) {
                            statusSQL += " (t_pro.`status` = 3 and t_sp.`status` = 2) and ";
                        } else if (filedValue.equals("55")) {
                            statusSQL += " (t_pro.`status` = 4 and t_sp.`status` = 1) and ";
                        } else if (filedValue.equals("66")) {
                            statusSQL += " (t_pro.`status` = 4 and t_sp.`status` = 2) and ";
                        } else if (filedValue.equals("3")) {
                            statusSQL += " (t_pro.`status` = 3 and t_sp.`status` is null) and ";
                        } else if (filedValue.equals("4")) {
                            statusSQL += " (t_pro.`status` = 4 and t_sp.`status` is null) and ";
                        } else {
                            flag = true;
                            appendSQL += "'" + filedValue + "',";
                        }
                    }
                    if (flag) {
                        appendSQL = appendSQL.substring(0, appendSQL.length() - 1);
                        appendSQL += ") ";
                    } else {
                        appendSQL = "";
                    }
                    if (StringUtils.isNotBlank(statusSQL)) {
                        statusSQL = " and (" + statusSQL.substring(0, statusSQL.length() - 4) + ") ";
                    }

                    resultSql += appendSQL + statusSQL;
                } else {
                    resultSql = " and " + conditionFiled + " in (";
                    for (String filedValue : filedValues) {
                        resultSql += "'" + filedValue + "',";
                    }
                    resultSql = resultSql.substring(0, resultSql.length() - 1);
                    resultSql += ") ";
                }

            } else if (conditionType.equals("like")) {
                for (String filedValue : filedValues) {
                    resultSql += " and " + conditionFiled + " like '%" + filedValue + "%' ";
                }
            }
        }
        return resultSql;
    }

    // 封装Category
    private static List<Category> convertMapToCategory(List<Map<String, Object>> mapList, List<Category> categorys, Long bl_parentId) {
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            Long parentId = Long.parseLong(map.get("parent_id").toString());
            String name = map.get("name").toString();
            Long categoryId = Long.parseLong(map.get("category_id").toString());
            int level = Integer.parseInt(map.get("level").toString());

            if (bl_parentId.equals(parentId)) {
                Category category = new Category();
                category.setName(name);
                category.setCategory_id(categoryId);

//					mapList.remove(i);
                category.setChildren(convertMapToCategory(mapList, new ArrayList<Category>(), categoryId));
                categorys.add(category);
            }
        }
        return categorys;
    }

    @SuppressWarnings("static-access")
    public String genSQLByProducts(Long shopId, ProductQueryVo pqv, String fileds) {
        StringBuilder sb = new StringBuilder();
        sb.append("select " + fileds + "\n" +
                "from product t_pro left join vendor t_v on (t_v.vendor_id = t_pro.vendor_id and t_v.enabled = 1)\n" +
                "left join brand t_brand on (t_brand.brand_id = t_pro.brand_id and t_brand.enabled = 1)\n" +
                "left join product_group t_pg on(t_pg.product_group_id = t_pro.product_group_id)\n" +
                "left join (-- 34473\n" +
                " select t_pp.product_id,  \n" +
                " max(case t_pp.key_name when 'ColorCode' then t_pp.`value` else '' end) as colorCode,\n" +
                " max(case t_pp.key_name when 'BrandID' then t_pp.`value` else '' end) as brandID,  \n" +
                " max(case t_pp.key_name when 'BestSeller' then t_pp.`value` else '' end) as bestSeller,  \n" +
                " max(case t_pp.key_name when 'NewThisWeek' then t_pp.`value` else '' end) as newThisWeek,  \n" +
                " max(case t_pp.key_name when 'Recommend' then t_pp.`value` else '' end) as recomment\n" +
                " from product_property t_pp inner join product p2 on (p2.product_id = t_pp.product_id and p2.enabled = 1)\n" +
                " where t_pp.enabled = 1 \n" +
                " group by t_pp.product_id  \n" +
                ") t_pp on t_pro.product_id = t_pp.product_id  \n" +
                "left join shop_product t_sp on (t_sp.product_id = t_pro.product_id and t_sp.enabled = 1)\n" +
                "left join (\n" +
                " select   p3.product_id,  sum(t_ss.store) stock, max(s.in_price) as max_in_price, max(s.im_price) as im_price, max(s.price) as max_price\n" +
                " from product p3 \n" +
                " inner join sku s on (s.enabled = 1 and p3.product_id = s.product_id)\n" +
                " inner join sku_store t_ss on  (t_ss.enabled = 1 and s.sku_id = t_ss.sku_id)\n" +
                " where p3.enabled = 1 \n" +
                " group by t_ss.product_id \n" +
                ") t_ss on t_pro.product_id = t_ss.product_id  \n" +
                "left join (\n" +
                " select p4.product_id, max(sps.sale_price) as max_sale_price from product p4\n" +
                " inner join shop_product sp on (sp.enabled = 1 and p4.product_id = sp.product_id)\n" +
                " inner join shop_product_sku sps on  (sps.enabled = 1 and sps.shop_product_id = sp.shop_product_id)\n" +
                " where p4.enabled = 1 \n" +
                " group by p4.product_id \n" +
                ") t_sps on t_pro.product_id = t_sps.product_id\n" +
                "where t_pro.enabled = 1 ");

        sb.append(this.genManyCondition("t_pro.product_code", pqv.getBoutiqueId(), "like"));

        sb.append(this.genManyCondition("t_pp.brandID", pqv.getBrandId(), "like"));

        sb.append(this.genManyCondition("t_pp.colorCode", pqv.getColorCode(), "="));

        sb.append(this.genManyCondition("t_pro.`name`", pqv.getName(), "like"));

        if (StringUtils.isNotBlank(pqv.getStockFlag())) { // 如果多个条件以下均不成立
            if (Contants.STOCK_IN_STOCK.equals(pqv.getStockFlag())) {
                sb.append(" and t_ss.stock > 0 ");
            } else if (Contants.STOCK_OUT_OF_STOCK.equals(pqv.getStockFlag())) {
                sb.append(" and (t_ss.stock = 0 or t_ss.stock is null) ");
            }
        }

        if (StringUtils.isNotBlank(pqv.getShopFlag())) {
            if (Contants.SHOP_IN_SHOP.equals(pqv.getShopFlag())) { // 如果多个条件以下均不成立
                sb.append(" and t_sp.enabled = 1 and t_sp.shop_id = " + shopId + " ");
            } else if (Contants.NOT_IN_SHOP.equals(pqv.getShopFlag())) {
                sb.append(" and (t_sp.enabled = 0 or t_sp.enabled is null) ");
            }
        }

        sb.append(this.genManyCondition("t_pro.brand_id", pqv.getSelBrand(), "="));

        sb.append(this.genManyCondition("t_pro.category_id", pqv.getSelCategory(), "="));

        sb.append(this.genManyCondition("t_pro.vendor_id", pqv.getSelVendor(), "="));

        if (StringUtil.isNotEmpty(pqv.getSelSeason())) {
            String[] filedValues = pqv.getSelSeason().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            if (null != filedValues && filedValues.length > 0) {
                for (String season : filedValues) {
                    stringBuffer.append(",'" + season + "'");
                }
            }
            String seasonStr = stringBuffer.toString().replaceFirst(",", "");
            if (StringUtil.isNotEmpty(seasonStr.trim())) {
                sb.append(" and t_pro.season_code in (" + seasonStr + ") ");
            }
        }

        sb.append(this.genManyCondition("t_pro.`status`", pqv.getSelStatus(), "="));

        List<DateVO> dateList = pqv.getConditionDate();
        if (dateList != null && dateList.size() != 0) {
            sb.append(" and (");
            for (int i = 0, len = dateList.size(); i < len; i++) {
                DateVO dv = dateList.get(i);
                if (i + 1 == len) {
                    sb.append("t_sp.created_at between '" + dv.getStartDate() + "' and '" + dv.getEndDate() + "' ");
                } else {
                    sb.append("t_sp.created_at between '" + dv.getStartDate() + "' and '" + dv.getEndDate() + "' or ");
                }
            }
            sb.append(" ) ");
        }

        String selImage = pqv.getSelImage();
        if (StringUtils.isNotBlank(selImage)) {
            if (selImage.equals(Contants.hasImage)) {
                sb.append(" and t_pro.cover_img != '[]' and t_pro.cover_img != 'null' and t_pro.cover_img != '' and t_pro.cover_img is not null ");
                // + " and t_pro.description_img != '[]' and t_pro.description_img != '' and t_pro.description_img is not null ");
            } else if (selImage.equals(Contants.notHasImage)) {
                sb.append(" and (t_pro.cover_img is null or t_pro.cover_img = 'null' or t_pro.cover_img = null or t_pro.cover_img = '[]' or trim(t_pro.cover_img) = '') ");
                // + " and (t_pro.description_img is null or t_pro.description_img = null or t_pro.description_img = '[]') ");
            }
        }

        String selModified = pqv.getSelModified();
        if (StringUtils.isNotBlank(selModified)) {
            if (selModified.equals(Contants.isModified)) {
                sb.append(" and t_pro.created_at != t_pro.updated_at ");
            } else if (selModified.equals(Contants.notIsModified)) {
                sb.append(" and t_pro.created_at = t_pro.updated_at ");
            }
        }

        String selImageModify = pqv.getSelImageModify();
        if (StringUtils.isNotBlank(selImageModify)) {
            if (selImageModify.equals(Contants.hasImageModify)) {
                sb.append(" and t_pro.img_modified = 1 ");
            } else if (selImageModify.equals(Contants.notHasImageModify)) {
                sb.append(" and t_pro.img_modified = 0 ");
            }
        }

        sb.append(" order by t_sp.updated_at desc  ");
        log.info("【genSQLByProducts】-" + sb.toString());
        return sb.toString();
    }


    /**
     * @param shopId
     * @throws Exception
     */
    public List<Map<String, Object>> getShopProductList(Long shopId, ProductQueryVo pqv, Integer sIndex, Integer eIndex)
            throws Exception {
        StringBuilder sb = new StringBuilder();

        StringBuilder fileds = new StringBuilder();
        fileds.append(" t_pro.product_code ,")
                .append(" t_v.vendor_name ,")
                .append(" t_pp.brandID , ")
                .append(" t_pg.name as pgName, ")
                .append(" t_pro.product_id , ")
                .append(" t_sp.shop_product_id , ")
                .append(" t_pp.colorCode , ")
                .append(" t_pro.season_code , ")
                .append(" t_pro.`status` as proStatus, ")
                .append(" t_sp.`status` , ")
                .append(" t_pro.`name` , ")
                .append(" t_sps.max_sale_price , ")
                .append(" t_ss.max_price , ")
                .append(" t_ss.max_in_price , ")
                .append(" t_ss.im_price, ")
//                .append(" t_pro.im_price, ")
                .append(" t_pp.bestSeller , ")
                .append(" t_pp.newThisWeek , ")
                .append(" t_pp.recomment , ")
                .append(" t_pro.brand_id , ")
                .append(" t_pro.category_id , ")
                .append(" IFNULL(t_ss.stock,0) as stock , ")
                .append(" t_pro.cover_img , ")
                .append(" t_brand.english_name , ")
                .append(" t_sp.shop_id , ")
                .append(" t_sp.enabled as sp_enabled ");

        sb.append(this.genSQLByProducts(shopId, pqv, fileds.toString()));
        sb.append(" limit " + sIndex + "," + eIndex + " ");

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Integer getShopProductListLength(Long shopId, ProductQueryVo pqv)
            throws Exception {
        Integer rows = 0;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(this.genSQLByProducts(shopId, pqv, " count(1) rows "), null);
            rows = list == null ? 0 : Integer.parseInt(list.get(0).get("rows").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    /**
     * @param shopId
     * @param status
     * @param productName
     * @param orderBy
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getShopProductList(Long shopId, Integer status, String productName, String orderBy)
            throws Exception {
        /*
		 * SELECT
		 * t.product_id,t.`name`,t.coverpic,t.`status`,t3.valid_at,t1.max_price,
		 * t1.min_price,t1.max_sale_price,t1.min_sale_price,t1.max_raise_price,
		 * t2.stock,t2.sales,t.shop_product_id FROM shop_product t LEFT JOIN
		 * product t3 ON t3.product_id=t.product_id -- left join (select
		 * product_id,max(price) max_price,min(price) min_price from sku s where
		 * EXISTS (select * from shop_product t where t.product_id=s.product_id
		 * and t.shop_id=1) group by product_id) t1 on
		 * t1.product_id=t.product_id -- left join (select
		 * shop_product_id,max(sale_price) max_sale_price,min(sale_price)
		 * min_sale_price from shop_product_sku s where EXISTS (select * from
		 * shop_product t where t.shop_product_id=s.shop_product_id and
		 * t.shop_id=1) group by shop_product_id) t3 on
		 * t3.shop_product_id=t.shop_product_id LEFT JOIN (SELECT
		 * shop_sku.`shop_product_id`,MAX(sku.`price`)
		 * max_price,MIN(sku.`price`) min_price,MAX(shop_sku.`sale_price`)
		 * max_sale_price,MIN(shop_sku.`sale_price`)
		 * min_sale_price,MAX(shop_sku.`sale_price`-sku.`price`) max_raise_price
		 * FROM shop_product_sku shop_sku INNER JOIN sku sku ON
		 * sku.`sku_id`=shop_sku.`sku_id` WHERE EXISTS (SELECT * FROM
		 * shop_product t WHERE t.shop_product_id=shop_sku.shop_product_id AND
		 * t.shop_id=1) GROUP BY shop_sku.`shop_product_id`) t1 ON
		 * t1.shop_product_id=t.shop_product_id LEFT JOIN (SELECT
		 * product_id,SUM(store) stock,SUM(finished) sales FROM sku_store s
		 * WHERE EXISTS (SELECT * FROM shop_product t WHERE
		 * t.product_id=s.product_id AND t.shop_id=1) GROUP BY product_id) t2 ON
		 * t2.product_id=t.product_id
		 */
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT t.product_id,t3.`name`,t3.cover_img,t.`status`,t3.valid_at,t3.product_code,t1.max_price,t1.min_price,t1.max_sale_price,t1.min_sale_price,t1.max_raise_price,t2.stock,t2.sales,t.shop_product_id FROM shop_product t")
                .append(" LEFT JOIN product t3 ON t3.product_id=t.product_id")
                .append(" LEFT JOIN (SELECT shop_sku.`shop_product_id`,MAX(sku.`price`) max_price,MIN(sku.`price`) min_price,MAX(shop_sku.`sale_price`) max_sale_price,MIN(shop_sku.`sale_price`) min_sale_price,MAX(shop_sku.`sale_price`-sku.`price`) max_raise_price")
                .append(" FROM shop_product_sku shop_sku INNER JOIN sku sku ON sku.`sku_id`=shop_sku.`sku_id` WHERE shop_sku.enabled=1 and shop_sku.shop_product_id in (SELECT t.shop_product_id FROM shop_product t WHERE t.enabled=1 AND t.shop_id=:p1 and t.status=:p2)")
                .append(" GROUP BY shop_sku.`shop_product_id`)  t1 ON t1.shop_product_id=t.shop_product_id")
                .append(" LEFT JOIN (SELECT product_id,SUM(store) stock,SUM(finished) sales FROM sku_store s WHERE  s.enabled=1 and s.product_id in (SELECT t.product_id FROM shop_product t WHERE t.enabled=1 AND t.shop_id=:p3 and t.status=:p4) GROUP BY product_id) t2 ON t2.product_id=t.product_id")
                .append(" where t.enabled=1 and t.shop_id=:p5 and t.status=:p6");
        List<Object> params = new ArrayList<Object>();
        params.add(shopId);
        params.add(status);
        params.add(shopId);
        params.add(status);
        params.add(shopId);
        params.add(status);
        int i = 7;
        if (!Helper.isNullOrEmpty(productName)) {
            sb.append(" and t3.`name` like :p" + i);
            params.add('%' + productName + '%');
            i++;
        }
        sb.append(" order by " + orderBy);
        // System.err.println(sb.toString());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param shopId
     * @param productName
     * @param orderBy
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getShopProductList(Long shopId, String productName, String orderBy)
            throws Exception {
		/*
		 * SELECT
		 * t.product_id,t.`name`,t.coverpic,t.`status`,t3.valid_at,t1.max_price,
		 * t1.min_price,t1.max_sale_price,t1.min_sale_price,t1.max_raise_price,
		 * t2.stock,t2.sales,t.shop_product_id FROM shop_product t LEFT JOIN
		 * product t3 ON t3.product_id=t.product_id -- left join (select
		 * product_id,max(price) max_price,min(price) min_price from sku s where
		 * EXISTS (select * from shop_product t where t.product_id=s.product_id
		 * and t.shop_id=1) group by product_id) t1 on
		 * t1.product_id=t.product_id -- left join (select
		 * shop_product_id,max(sale_price) max_sale_price,min(sale_price)
		 * min_sale_price from shop_product_sku s where EXISTS (select * from
		 * shop_product t where t.shop_product_id=s.shop_product_id and
		 * t.shop_id=1) group by shop_product_id) t3 on
		 * t3.shop_product_id=t.shop_product_id LEFT JOIN (SELECT
		 * shop_sku.`shop_product_id`,MAX(sku.`price`)
		 * max_price,MIN(sku.`price`) min_price,MAX(shop_sku.`sale_price`)
		 * max_sale_price,MIN(shop_sku.`sale_price`)
		 * min_sale_price,MAX(shop_sku.`sale_price`-sku.`price`) max_raise_price
		 * FROM shop_product_sku shop_sku INNER JOIN sku sku ON
		 * sku.`sku_id`=shop_sku.`sku_id` WHERE EXISTS (SELECT * FROM
		 * shop_product t WHERE t.shop_product_id=shop_sku.shop_product_id AND
		 * t.shop_id=1) GROUP BY shop_sku.`shop_product_id`) t1 ON
		 * t1.shop_product_id=t.shop_product_id LEFT JOIN (SELECT
		 * product_id,SUM(store) stock,SUM(finished) sales FROM sku_store s
		 * WHERE EXISTS (SELECT * FROM shop_product t WHERE
		 * t.product_id=s.product_id AND t.shop_id=1) GROUP BY product_id) t2 ON
		 * t2.product_id=t.product_id
		 */
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT t.product_id,t3.`name`,t3.cover_img,t.`status`,t3.valid_at,t1.max_price,t1.min_price,t1.max_sale_price,t1.min_sale_price,t1.max_raise_price,t2.stock,t2.sales,t.shop_product_id FROM shop_product t")
                .append(" LEFT JOIN product t3 ON t3.product_id=t.product_id")
                .append(" LEFT JOIN (SELECT shop_sku.`shop_product_id`,MAX(sku.`price`) max_price,MIN(sku.`price`) min_price,MAX(shop_sku.`sale_price`) max_sale_price,MIN(shop_sku.`sale_price`) min_sale_price,MAX(shop_sku.`sale_price`-sku.`price`) max_raise_price")
                .append(" FROM shop_product_sku shop_sku INNER JOIN sku sku ON sku.`sku_id`=shop_sku.`sku_id` WHERE shop_sku.enabled=1 and shop_sku.shop_product_id in (SELECT t.shop_product_id FROM shop_product t WHERE t.enabled=1 AND t.shop_id=:p1 )")
                .append(" GROUP BY shop_sku.`shop_product_id`)  t1 ON t1.shop_product_id=t.shop_product_id")
                .append(" LEFT JOIN (SELECT product_id,SUM(store) stock,SUM(finished) sales FROM sku_store s WHERE  s.enabled=1 and s.product_id in (SELECT t.product_id FROM shop_product t WHERE t.enabled=1 AND t.shop_id=:p2 ) GROUP BY product_id) t2 ON t2.product_id=t.product_id")
                .append(" where t.enabled=1 and t.shop_id=:p3 ");
        List<Object> params = new ArrayList<Object>();
        params.add(shopId);
        params.add(shopId);
        params.add(shopId);
        int i = 4;
        if (!Helper.isNullOrEmpty(productName)) {
            sb.append(" and t3.`name` like :p" + i);
            params.add('%' + productName + '%');
            i++;
        }
        sb.append(" order by " + orderBy);
        // System.err.println(sb.toString());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param shopProductId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getShopProductSkuAll(Long shopProductId) throws Exception {
		/*
		 * SELECT
		 * t.shop_product_id,product.product_code,sku.price,shop_sku.sale_price,
		 * shop_sku.shop_product_sku_id FROM `shop_product` t inner join product
		 * product on product.product_id=t.product_id and product.enabled=1
		 * inner join shop_product_sku shop_sku on
		 * shop_sku.shop_product_id=t.shop_product_id and shop_sku.enabled=1
		 * inner join sku sku on sku.sku_id=shop_sku.sku_id and sku.enabled=1
		 * where t.shop_product_id=1;
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
        sb.append(
                "SELECT t.shop_product_id,product.product_code,sku.price,shop_sku.sale_price,shop_sku.shop_product_sku_id,shop_sku.sku_id FROM `shop_product` t")
                .append(" inner join product product on product.product_id=t.product_id and product.enabled=1")
                .append(" inner join shop_product_sku shop_sku on shop_sku.shop_product_id=t.shop_product_id and shop_sku.enabled=1")
                .append(" inner join sku sku on sku.sku_id=shop_sku.sku_id and sku.enabled=1")
                .append(" where t.shop_product_id=:p1");
        StringBuilder sbSub = new StringBuilder();
        sbSub.append(
                "SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.`name` key_name,pro_val.`value` key_value FROM `sku_property` sku_p")
                .append(" inner JOIN product_sku_property_key pro_key on sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id and pro_key.enabled=1")
                .append(" inner JOIN product_sku_property_value pro_val on sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id and pro_val.enabled=1")
                .append(" where sku_p.enabled=1 and sku_p.sku_id=:p1");

        Object[] params = new Object[]{shopProductId};

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> sublist = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params);
            for (Map<String, Object> map : list) {
                Long skuId = Long.parseLong(map.get("sku_id").toString());
                Object[] skuParams = new Object[]{skuId};
                sublist = shopProductDao.executeBySql(sbSub.toString(), skuParams);
                map.put("sku_propertys", sublist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * @param shopProductId
     * @return
     */
    public List<Map<String, Object>> getShopProductDetail(Long shopProductId) {

        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT t.product_id,t3.`name`,t3.cover_img,t3.brand_id,t.`status`,t3.product_code,t3.valid_at,'0' rush_flag,t1.max_my_price,t1.min_my_price,t1.max_current_price,t1.min_current_price,0 max_up_to_percentage,t.shop_product_id,t2.stock,t2.orders,t4.wish_list,t3.customer_rating,t.shop_id,t3.description,t3.description_img FROM shop_product t")
                .append(" LEFT JOIN product t3 ON t3.product_id=t.product_id")
                .append(" LEFT JOIN (SELECT shop_sku.`shop_product_id`,MAX(shop_sku.`sale_price`) max_my_price,MIN(shop_sku.`sale_price`) min_my_price,MAX(shop_sku.`sale_price`) max_current_price,MIN(shop_sku.`sale_price`) min_current_price")
                .append(" FROM shop_product_sku shop_sku WHERE shop_sku.shop_product_id=:p1")
                .append(" GROUP BY shop_sku.`shop_product_id`)  t1 ON t1.shop_product_id=t.shop_product_id")
                .append(" LEFT JOIN (SELECT product_id,SUM(store) stock,SUM(ordered) orders FROM sku_store s WHERE EXISTS (SELECT * FROM shop_product t WHERE t.product_id=s.product_id AND t.shop_product_id=:p2) GROUP BY product_id) t2 ON t2.product_id=t.product_id")
                .append(" LEFT JOIN (SELECT fsp.shop_product_id,COUNT(1) wish_list FROM favourite_shop_product fsp WHERE fsp.enabled=1 and fsp.shop_product_id=:p3 GROUP BY fsp.`shop_product_id`) t4 ON t4.shop_product_id=t.shop_product_id")
                .append(" where t.shop_product_id=:p4");

        Object[] params = new Object[]{shopProductId, shopProductId, shopProductId, shopProductId};
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 未实现
     *
     * @param shopProductId
     * @return
     */
    public List<Map<String, Object>> getShopProductDetail4Rush(Long rushToBuyId, Long shopProductId) {

        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT t.event_rush_to_buy_id,sp.shop_product_id,p.product_id,p.`name`,p.cover_img,p.brand_id,p.product_code,p.valid_at,'1' rush_flag,spsg.min_my_price,spsg.max_my_price,rsg.min_current_price,rsg.max_current_price,rsg.quantity,rsg.remaining stock,t.max_up_to_percentage,ss.stock all_stock,ss.orders,fs.wish_list,p.customer_rating,sp.shop_id,p.description,p.description_img FROM event_rush_to_buy t")
                .append(" LEFT JOIN shop_product sp ON sp.shop_product_id=t.shop_product_id")
                .append(" LEFT JOIN product p ON p.product_id=sp.product_id")
                .append(" LEFT JOIN (SELECT sps.shop_product_id,MIN(sps.sale_price) min_my_price,MAX(sps.sale_price) max_my_price FROM shop_product_sku sps WHERE sps.enabled=1 AND sps.shop_product_id=:p1")
                .append("   GROUP BY sps.shop_product_id) spsg ON spsg.shop_product_id=t.shop_product_id")
                .append(" LEFT JOIN (SELECT rs.event_rush_to_buy_id,MIN(rs.rush_price) min_current_price,MAX(rs.rush_price) max_current_price,sum(quantity) quantity,sum(remaining) remaining FROM event_rush_to_buy_sku rs WHERE rs.`event_rush_to_buy_id`=:p2")
                .append("   GROUP BY rs.event_rush_to_buy_id) rsg ON rsg.event_rush_to_buy_id=t.event_rush_to_buy_id")
                .append(" LEFT JOIN (SELECT product_id,SUM(store) stock,SUM(ordered) orders FROM sku_store s WHERE EXISTS (SELECT * FROM shop_product t WHERE t.product_id=s.product_id AND t.shop_product_id=:p3) GROUP BY product_id) ss ON ss.product_id=t.product_id")
                .append(" LEFT JOIN (SELECT fsp.shop_product_id,COUNT(1) wish_list FROM favourite_shop_product fsp WHERE fsp.enabled=1 and fsp.shop_product_id=:p4 GROUP BY fsp.`shop_product_id`) fs ON fs.shop_product_id=t.shop_product_id")
                .append(" WHERE  t.enabled=1 AND t.event_rush_to_buy_id=:p5").append("");

        Object[] params = new Object[]{shopProductId, rushToBuyId, shopProductId, shopProductId, rushToBuyId};
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param productId
     * @return
     */
    public List<Map<String, Object>> getShopProductProperty(Long productId) {
        StringBuilder sbProKey = new StringBuilder();
        sbProKey.append(
                "SELECT t.product_sku_property_key_id,t.`name` FROM product_sku_property_key t  WHERE t.`enabled`=1 AND t.`product_id`=:p1");

        StringBuilder sbProVal = new StringBuilder();
        sbProVal.append(
                "SELECT t.product_sku_property_key_id,t.`product_sku_property_value_id`,t.`value` FROM product_sku_property_value t WHERE  t.`enabled`=1 AND t.`product_sku_property_key_id`=:p1");

        Object[] params = new Object[]{productId};
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> sublist = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sbProKey.toString(), params);
            for (Map<String, Object> map : list) {
                Long keyId = (Long) map.get("product_sku_property_key_id");
                Object[] keyIdParams = new Object[]{keyId};
                sublist = shopProductDao.executeBySql(sbProVal.toString(), keyIdParams);
                map.put("proVals", sublist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getShopProductSkuList(Long shopProductId) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT t.shop_product_id,product.product_code,shop_sku.sale_price current_price,shop_sku.shop_product_sku_id,shop_sku.sku_id,sku_store.store stock FROM `shop_product` t")
                .append(" inner join product product on product.product_id=t.product_id and product.enabled=1")
                .append(" inner join shop_product_sku shop_sku on shop_sku.shop_product_id=t.shop_product_id and shop_sku.enabled=1")
                .append(" inner join sku sku on sku.sku_id=shop_sku.sku_id and sku.enabled=1")
                .append(" left join  sku_store sku_store on sku_store.sku_id=shop_sku.sku_id")
                .append(" where t.shop_product_id=:p1");
        StringBuilder sbSub = new StringBuilder();
        sbSub.append(
                "SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.product_sku_property_key_id key_id,pro_key.`name` key_name,pro_val.product_sku_property_value_id value_id FROM `sku_property` sku_p")
                .append(" inner JOIN product_sku_property_key pro_key on sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id and pro_key.enabled=1")
                .append(" inner JOIN product_sku_property_value pro_val on sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id and pro_val.enabled=1")
                .append(" where sku_p.enabled=1 and sku_p.sku_id=:p1");

        Object[] params = new Object[]{shopProductId};

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> sublist = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params);
            for (Map<String, Object> map : list) {
                Long skuId = Long.parseLong(map.get("sku_id").toString());
                Object[] skuParams = new Object[]{skuId};
                sublist = shopProductDao.executeBySql(sbSub.toString(), skuParams);
                for (Map<String, Object> property : sublist) {
                    map.put("property_" + ((Long) property.get("key_id")).toString(), property.get("value_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * @param rushToBuyId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getShopProductSkuList4Rush(Long rushToBuyId) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT rs.event_rush_to_buy_sku_id,t.shop_product_id,product.product_code,shop_sku.sale_price my_price,shop_sku.shop_product_sku_id,shop_sku.sku_id,rs.rush_price current_price,rs.remaining stock,rs.quantity FROM event_rush_to_buy t")
                .append(" LEFT JOIN shop_product sp ON sp.shop_product_id=t.shop_product_id")
                .append(" inner join product product on product.product_id=sp.product_id and product.enabled=1")
                .append(" inner join shop_product_sku shop_sku on shop_sku.shop_product_id=t.shop_product_id and shop_sku.enabled=1")
                // .append(" inner join sku sku on sku.sku_id=shop_sku.sku_id
                // and sku.enabled=1")
                .append(" left join  sku_store sku_store on sku_store.sku_id=shop_sku.sku_id")
                .append(" INNER JOIN event_rush_to_buy_sku rs ON rs.shop_product_sku_id=shop_sku.shop_product_sku_id")
                .append(" where t.event_rush_to_buy_id=:p1");
        StringBuilder sbSub = new StringBuilder();
        sbSub.append(
                "SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.product_sku_property_key_id key_id,pro_key.`name` key_name,pro_val.product_sku_property_value_id value_id FROM `sku_property` sku_p")
                .append(" inner JOIN product_sku_property_key pro_key on sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id and pro_key.enabled=1")
                .append(" inner JOIN product_sku_property_value pro_val on sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id and pro_val.enabled=1")
                .append(" where sku_p.enabled=1 and sku_p.sku_id=:p1");

        Object[] params = new Object[]{rushToBuyId};

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> sublist = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params);
            for (Map<String, Object> map : list) {
                Long skuId = Long.parseLong(map.get("sku_id").toString());
                Object[] skuParams = new Object[]{skuId};
                sublist = shopProductDao.executeBySql(sbSub.toString(), skuParams);
                for (Map<String, Object> property : sublist) {
                    map.put("property_" + ((Long) property.get("key_id")).toString(), property.get("value_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    public Page getShopProductCommentList(Long shopProductId, long pageNumber, long pageSize) {
        String orderBy = " c.created_at desc";
        return getShopProductCommentList(shopProductId, pageNumber, pageSize, orderBy);
    }

    public Page getShopProductCommentList(Long shopProductId, long pageNumber, long pageSize, String orderBy) {
        String fieldNames = "c.`comment_id`,c.`content`,c.`created_at`,cr.`content` reply_content,r.`rate`,u.`username`,u.`user_image`";
        StringBuilder sb = new StringBuilder();
        sb.append("`comment` c").append(" LEFT JOIN `comment` cr ON cr.`reply_comment_id`=c.`comment_id`")
                .append(" LEFT JOIN rate_product r ON c.`comment_id`=r.`comment_id`")
                .append(" LEFT JOIN `user` u ON u.`user_id`=c.`user_id`");
        String whereCondition = "c.`enabled`=1 AND c.reply_comment_id=-1 AND r.`shop_product_id`=:p1";

        Object[] params = new Object[]{shopProductId};
        Page page = new Page();
        try {
            page = shopProductDao.getPageBySql(pageNumber, pageSize, fieldNames, sb.toString(), whereCondition, orderBy,
                    params, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    public void updateShopProductSkuPrice(List<Object[]> params) {
        String updateSql = "update shop_product_sku set sale_price=:p2,updated_at=:p3 where shop_product_sku_id=:p1";
        shopProductDao.batchExecuteBySql(updateSql, params);
    }

    public void deleteShopProduct(List<Object[]> params) {
        // 删除(enabled设置为0)对应的shop_product_sku
        String shopSkuupdateSql = "update shop_product_sku set enabled=0,updated_at=:p2 where shop_product_id=:p1";
        shopProductDao.batchExecuteBySql(shopSkuupdateSql, params);
        // 删除(enabled设置为0)对应的shop_product
        String shopProductUpdateSql = "update shop_product set enabled=0,updated_at=:p2 where shop_product_id=:p1";
        shopProductDao.batchExecuteBySql(shopProductUpdateSql, params);
    }

    /**
     * 产品上架
     *
     * @param params
     */
    public void changeShopProductSale(List<Object[]> params) {
        String shopProductUpdateSql = "update shop_product set status=" + ShopProductType.ON_SALE
                + ",updated_at=:p2,sale_at=:p3 where shop_product_id=:p1";
        shopProductDao.batchExecuteBySql(shopProductUpdateSql, params);
    }

    /**
     * 产品下架
     *
     * @param params
     */
    public void changeShopProductStop(List<Object[]> params) {
        String shopProductUpdateSql = "update shop_product set status=" + ShopProductType.STOP_SELLING
                + ",updated_at=:p2 where shop_product_id=:p1";
        shopProductDao.batchExecuteBySql(shopProductUpdateSql, params);
    }

    public void changeShopProductStopByProduct(String product_id) throws Exception {
        if(StringUtils.isNotBlank(product_id)) {
            String sql = "update shop_product set status ="+ShopProductType.STOP_SELLING+",updated_at=now() where enabled = 1 and product_id ="+product_id;
            shopProductDao.updateBySQL(sql,null);
            log.info("changeShopProductStopByProductSql : " + sql);
        }
    }

    /**
     * @param shop_id
     * @param product_id
     * @return
     * @throws Exception
     */
    public ShopProduct getShopProductByShopAndProductId(Long shop_id, Long product_id) throws Exception {
        try {
            ShopProduct shopProduct = null;

            Map<String, Object> condition = new HashMap<String, Object>();
            // 添加条件
            condition.put("shop_id", shop_id);
            condition.put("product_id", product_id);
            condition.put("enabled", true);

            List<ShopProduct> list = shopProductDao.getByCondition(ShopProduct.class, condition);

            for (ShopProduct temp : list) {
                if (temp != null) {
                    shopProduct = new ShopProduct();
                    shopProduct = temp;
                    break;
                }
            }

            return shopProduct;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param shopProduct
     * @return
     * @throws Exception
     */
    public ShopProduct createShopProduct(ShopProduct shopProduct) throws Exception {
        try {
            long shopProductId = shopProductDao.create(shopProduct);
            if (shopProductId > 0) {
                shopProduct.shop_product_id = shopProductId;
            } else {
                shopProduct = null;
            }
            return shopProduct;
        } catch (Exception e) {
            throw e;
        }

    }

    public Boolean createShopProductSkus(List<ShopProductSku> shopProductSkus) throws Exception {
        try {
            int count = 0;
            for (ShopProductSku shopProductSku : shopProductSkus) {
                shopProductSkuDao.create(shopProductSku);
                count++;
            }
            if (count == shopProductSkus.size()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * @param shop_product_sku_id
     * @return
     * @throws Exception
     */
    public ShopProductSku getShopProductSkuById(String shop_product_sku_id) throws Exception {
        try {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("enabled", true);

            condition.put("shop_product_sku_id", shop_product_sku_id);
            ShopProductSku shopProductSku = null;
            List<ShopProductSku> shopProductSkuList = shopProductSkuDao.getByCondition(ShopProductSku.class, condition);
            for (ShopProductSku temp : shopProductSkuList) {
                if (temp != null) {
                    shopProductSku = temp;
                }
            }

            return shopProductSku;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param shop_prodcut_id
     * @return
     * @throws Exception
     */
    public ShopProduct getShopProductById(String shop_product_id) throws Exception {
        try {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("enabled", true);
            condition.put("shop_product_id", shop_product_id);
            ShopProduct shopProduct = null;
            List<ShopProduct> shopProductList = shopProductDao.getByCondition(ShopProduct.class, condition);
            for (ShopProduct temp : shopProductList) {
                if (temp != null) {
                    shopProduct = temp;
                }
            }
            return shopProduct;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Map<String, Object>> getProductInCheezMall(String orderBy, int len, Long shop_id) throws Exception {
        try {
            int i = 3;
            StringBuilder sql = new StringBuilder(
                    "select sp.shop_product_id,p.`name`,min(sps.sale_price) as min_price,p.cover_img,p.customer_rating, ");
            sql.append(
                    "(select count(*) from rate_product rp where rp.shop_product_id=sp.shop_product_id ) as comments, ")
                    .append("(select count(*) from logistics_product lp left join shop_product_sku sps on lp.shop_product_sku_id = sps.shop_product_sku_id where sps.shop_product_id=sp.shop_product_id and lp.`status`=5 ) as orders ")
                    .append(" from shop_product sp").append(" left join product p on sp.product_id = p.product_id ")
                    .append(" left join shop_product_sku sps on sp.shop_product_id = sps.shop_product_id ")
                    .append(" where sp.`status`=:p1 and sp.enabled=:p2 and p.`status`=:p3 ");
            if (shop_id != null) {
                i++;
                sql.append(" and sp.shop_id=:p" + i);
            }
            i++;
            sql.append(" group by sp.shop_product_id ").append(" order by ").append(orderBy)
                    .append(" desc limit 0,:p" + i);
            System.out.println("sql = " + sql.toString());
            if (shop_id != null) {
                return shopProductDao.executeBySql(sql.toString(), new Object[]{ShopProductType.ON_SALE,
                        EnabledType.USED, ProductStatusType.EXISTING, shop_id, len});
            } else {
                return shopProductDao.executeBySql(sql.toString(),
                        new Object[]{ShopProductType.ON_SALE, EnabledType.USED, ProductStatusType.EXISTING, len});
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Page getShopProdutListByPage(Page page, String orderBy, String shop_id, String category_id)
            throws Exception {
        try {
            String fieldNames = " sp.shop_product_id,p.`name`,min(sps.sale_price) as min_price,p.cover_img,p.customer_rating, (select count(*) from rate_product rp where rp.shop_product_id=sp.shop_product_id ) as comments, (select count(*) from logistics_product lp left join shop_product_sku sps on lp.shop_product_sku_id = sps.shop_product_sku_id where sps.shop_product_id=sp.shop_product_id and lp.`status`=5 ) as orders ";
            String tableName = " shop_product sp left join product p on sp.product_id = p.product_id  left join shop_product_sku sps on sp.shop_product_id = sps.shop_product_id ";
            StringBuilder whereCondition = new StringBuilder(
                    " sp.`status`=:p1 and sp.enabled=:p2 and sp.shop_id=:p3 and p.`status`=:p4");
            List<Object> paramsList = new ArrayList<Object>();
            paramsList.add(ShopProductType.ON_SALE);
            paramsList.add(EnabledType.USED);
            paramsList.add(shop_id);
            paramsList.add(ProductStatusType.EXISTING);

            if (!Helper.isNullOrEmpty(category_id)) {
                whereCondition.append(" AND p.category_id=:p5");
                paramsList.add(category_id);
            }
            return shopProductDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName,
                    whereCondition.toString(), orderBy, paramsList.toArray(), "sp.shop_product_id",
                    "sp.shop_product_id");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 返回shop product对应的库存
     *
     * @param shopProductId
     * @return
     * @throws Exception
     */
    public Map<String, Object> getProductStock(Long shopProductId) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT ss.product_id,SUM(ss.store) stock FROM sku_store ss INNER JOIN shop_product sp ON sp.product_id=ss.product_id")
                .append(" WHERE sp.shop_product_id=:p1 GROUP BY ss.product_id");

        Object[] params = new Object[]{shopProductId};

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = shopProductDao.executeBySql(sb.toString(), params);
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }


    /**
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @return
     * @throws Exception
     */
    public Page getShopProductPage(long pageNumber, long pageSize, Map<String, Object> whereCondition)
            throws Exception {

        try {
            String countSql = getShopProductSQL("count(*) as countSum", whereCondition);
            System.out.println("count sql: " + countSql.toString());

            // 1. 获取 总数
            List<Map<String, Object>> listMap = shopProductDao.executeBySql(countSql, null);
            Long totalRow = 0L;
            if (!CollectionUtils.isEmpty(listMap)) {
                totalRow = Long.valueOf(listMap.get(0).get("countSum").toString());
            }

            if (totalRow == 0L) {
                return new Page(null, pageNumber, pageSize, 1l, 0l);
            }

            String sql = getShopProductSQL("sp.shop_product_id, t_pro.product_code,sp.`name` as ProductName  ,t_pp.brandID,t_pp.colorCode,t_pp.seasonCode,brand.english_name,brand.chinese_name,t_category.`name` as c1_name,t_category.c2_name,t_category.c3_name", whereCondition);

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
            List<Map<String, Object>> productMapList = shopProductDao.executeBySql(sql.toString(), null);
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public String getShopProductSQL(String fileds, Map<String, Object> conditions) {

        String sql = "select " + fileds + " from product t_pro left join" +
                "(select t_pp.product_id, max(case t_pp.key_name when 'ColorCode' then t_pp.`value` else '' end) as colorCode, " +
                "max(case t_pp.key_name when 'BrandID' then t_pp.`value` else '' end) as brandID, " +
                "max(case t_pp.key_name when 'SeasonCode' then t_pp.`value` else '' end) as seasonCode " +
                "from product_property t_pp where t_pp.enabled = 1 group by t_pp.product_id ) t_pp on t_pro.product_id = t_pp.product_id " +
                "left join shop_product sp on (sp.product_id = t_pro.product_id and sp.enabled = 1) " +
                "left join brand on (brand.brand_id = t_pro.brand_id and brand.enabled = 1) " +
                "left join ( select c1.category_id,c1.chinese_name , c1.name, c2.* from category c1 left join " +
                "(select c2.name as c2_name, c2.category_id as c2_categoryId,c2.chinese_name as c2_chineseName,c2.parent_id as c2_parentId,c3.name as c3_name, c3.category_id as c3_categoryId,c3.chinese_name as c3_chineseName,c3.parent_id as c3_parentId  from category c2 left join " +
                "(select c3.name,c3.category_id,c3.chinese_name,c3.parent_id from category c3 " +
                "where c3.level = 3 and c3.enabled = 1 ) c3 on c2.category_id = c3.parent_id where c2.level = 2 and c2.enabled = 1 and c3.category_id is not null " +
                ") c2 on c1.category_id = c2.c2_parentId " +
                "where c1.enabled = 1 and c1.level = 1 and c2.c2_categoryId is not null " +
                ") t_category on t_pro.category_id = t_category.c3_categoryId  WHERE 1=1  ";
        if (conditions.get("product_group_id") != null) {
            sql += " and sp.product_group_id=" + conditions.get("product_group_id") + " ";
        }
        if (conditions.get("shop_id") != null) {
            sql += "and sp.shop_id=" + conditions.get("shop_id") + " ";
        }
        return sql;
    }

	
	/*select 
	t_pro.product_code,
	t_pp.brandID,
	t_pp.colorCode,
	t_pro.`name`,
	t_ss.stock,
	t_pro.brand_id,
	t_pro.category_id,
	t_pro.vendor_id,
	t_sp.`status`,
	t_pro.*
from product t_pro 
-- product 关联 shop_product,`intra-mirror-prd` shop_product 有一条脏数据(2017-4-14 13:18:24)
left join shop_product t_sp on (t_pro.product_id = t_sp.product_id and t_sp.enabled = 1)
-- product 关联 product_property 列转行查询商品属性值(ColorCode,BrandID) 
left join (
	select t_pp.product_id,
	max(case t_pp.key_name when 'ColorCode' then t_pp.`value` else '' end) as colorCode,
	max(case t_pp.key_name when 'BrandID' then t_pp.`value` else '' end) as brandID
	from product_property t_pp where t_pp.enabled = 1 group by t_pp.product_id
) t_pp on t_pro.product_id = t_pp.product_id
-- shop_product 关联 shop_product_sku,sku表获取商品的价格信息
left join (
	select 
		t_sps.shop_product_id,
		max(t_sps.sale_price) max_sale_price,
		max(sku.in_price) max_in_price,
		max(sku.price) max_price 
	from shop_product_sku t_sps inner join sku on sku.sku_id = t_sps.sku_id
	where t_sps.enabled = 1 group by t_sps.shop_product_id
) t_sps on t_sp.shop_product_id = t_sps.shop_product_id 
-- product 关联 sku_store 获取库存信息
left join (
	select 
		t_ss.product_id,
		sum(t_ss.store) stock,
		sum(t_ss.finished) sales 
	from sku_store t_ss where t_ss.enabled = 1 group by t_ss.product_id
) t_ss on t_pro.product_id = t_ss.product_id
where 1 = 1 
-- Boutique ID product.product_code # and t_pro.product_code = '60000'
-- Brand ID # and t_pp.brandID = '452348K6D1G'
-- Color Code # and t_pp.colorCode = '5909'
-- Name # and t_pro.`name` like '%要下单%'
-- In Stock # and t_ss.stock > 0 | Out Of Stock # and (t_ss.stock = 0 or t_ss.stock is null)
-- In Shop # and t_sp.enabled = 1 | Not In Shop # and (t_sp.enabled = 0 or t_sp.enabled is null)
-- Brand # and t_pro.brand_id = 88
-- Category # and t_pro.category_id = 1600
-- Vendor # and t_pro.vendor_id = 7
-- Status # and t_sp.`status` = 0
-- limit 0,50;*/
}
