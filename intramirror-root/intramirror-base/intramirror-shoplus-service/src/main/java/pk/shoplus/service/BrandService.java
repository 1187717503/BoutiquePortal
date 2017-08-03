package pk.shoplus.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.google.gson.Gson;

import pk.shoplus.common.BrandHelper;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Brand;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.BrandType;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.vo.BrandMapping;
import pk.shoplus.vo.PageResultData;

import java.util.*;

public class BrandService {
    private final Logger LOGGER = Logger.getLogger(BrandService.class);
    /**
     * 获取数据库连接
     */
    private EntityDao<Brand> brandDao = null;

    /**
     * @param conn
     */
    public BrandService(Connection conn) {
        brandDao = new EntityDao<Brand>(conn);
    }

    /**
     * BrandList 查询方法 - 分页
     *
     * @param page
     * @param englishName
     * @param chineseName
     * @param vendorName
     * @param status
     * @return
     * @throws Exception
     */
    public Page getBrandListWithStatus(Page page, String englishName, String chineseName, String vendorName,
                                       Integer status) throws Exception {
        try {
            String fieldNames = " brand.updated_at, brand.status, brand.creator as vendor_name, brand.vendor_application_id, brand_id,logo,english_name,chinese_name, brand.enabled ";
            String tableNames = " brand";

            // orderBy = Helper.isNullOrEmpty(orderBy) ? "brand_id" : orderBy;
            StringBuilder whereCondition = new StringBuilder("");
            List<Object> paramList = new ArrayList<Object>();
            int i = 1;
            whereCondition.append(" brand.enabled=:p" + i++);
            paramList.add(EnabledType.USED);

            if (!Helper.isNullOrEmpty(englishName)) {
                whereCondition.append(" and english_name like :p" + i++);
                paramList.add("%" + englishName + "%");
            }
            if (!Helper.isNullOrEmpty(chineseName)) {
                whereCondition.append(" and chinese_name like :p" + i++);
                paramList.add("%" + chineseName + "%");
            }
            if (!Helper.isNullOrEmpty(vendorName)) {
                whereCondition.append(" and vendor_name like :p" + i++);
                paramList.add("%" + vendorName + "%");
            }
            whereCondition.append(" and brand.status = :p" + i++);
            paramList.add(status);

            Object[] params = null;
            if (paramList.size() > 0) {
                params = paramList.toArray();
            }
            String orderBy = " brand.updated_at desc ";
            return brandDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames,
                    whereCondition.toString(), orderBy, params, null, null);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Map<String, Object>> queryBrandMappingByPaging(PageResultData pageData) {
        String fileds = "brand.brand_id,brand.english_name";
        String sql = this.brandMappingSQL(fileds, pageData);
        sql += " limit " + pageData.sIndex() + "," + pageData.eIndex();
        List<Map<String, Object>> listMap = brandDao.executeBySql(sql, null);
        return listMap;
    }


    public List<Map<String, Object>> getBrandByVendor(Long vendorId, String query, int startIndex, int size) {
        String sql = "SELECT * FROM brand b " +
                "INNER JOIN vendor_brand vb " +
                "ON b.brand_id = vb.brand_id AND b.enabled = 1 AND " +
                "vb.enabled = 1 AND vb.vendor_id = :p1 " +
                "WHERE b.english_name LIKE '%" + query + "%' " +
                "GROUP BY b.brand_id " +
                "LIMIT :p2, :p3";
        return brandDao.executeBySql(sql, new Object[]{
                vendorId,
                startIndex,
                size
        });
    }


    public int getBrandCountByVendor(Long vendorId, String query) {
        String sql = "SELECT DISTINCT count(1) as total FROM brand b " +
                "INNER JOIN vendor_brand vb " +
                "ON b.brand_id = vb.brand_id AND b.enabled = 1 AND " +
                "vb.enabled = 1 AND vb.vendor_id = " + vendorId + " " +
                "WHERE b.english_name LIKE '%" + query + "%' ";
        return Integer.parseInt(brandDao.executeBySql(sql, null).get(0).get("total").toString());
    }

    public List<Map<String, Object>> queryBrandMappingByBrand(List<BrandMapping> list, Map<String, Object> conditions) {
        String sql = "select api_brand_map_id,brand_id,boutique_brand_name from api_brand_map abm where 1 = 1 and enabled=1";
        String brands = "";
        for (BrandMapping bm : list) {
            brands += bm.getBrandId() + ",";
        }
        if (StringUtils.isNotBlank(brands)) {
            sql += " and abm.brand_id in (" + brands.substring(0, brands.length() - 1) + ") ";
        }
        String api_configuration_id = conditions.get("api_configuration_id") == null ? "" : conditions.get("api_configuration_id").toString();
        if (StringUtils.isNotBlank(api_configuration_id)) {
            sql += " and abm.api_configuration_id = '" + api_configuration_id + "' ";
        }
        return brandDao.executeBySql(sql, null);
    }

    public int queryBrandMappingCount(PageResultData pageData) {
        int count = 0;
        String fileds = "count(1) ct";
        String sql = this.brandMappingSQL(fileds, pageData);
        List<Map<String, Object>> map = brandDao.executeBySql(sql, null);
        if (map != null && map.size() != 0) {
            count = Integer.parseInt(map.get(0).get("ct").toString());
        }
        return count;
    }

    public String brandMappingSQL(String fileds, PageResultData pageData) {
        Map<String, Object> conditions = pageData.getConditions();
        String vendor_id = conditions.get("vendor_id") == null ? "" : conditions.get("vendor_id").toString();
        String english_name = conditions.get("english_name") == null ? "" : conditions.get("english_name").toString();
        String api_configuration_id = conditions.get("api_configuration_id") == null ? "" : conditions.get("api_configuration_id").toString();
        String api_end_point_id = conditions.get("api_end_point_id") == null ? "" : conditions.get("api_end_point_id").toString();

        String sql = " select " + fileds + " from brand where brand.brand_id in ( " +
                " select distinct vb.brand_id from vendor_brand vb " +
                " left join api_brand_map abm on(vb.brand_id = abm.brand_id and abm.enabled = 1) " +
                " left join api_configuration ac on(abm.api_configuration_id = ac.api_configuration_id and ac.enabled = 1) " +
                " left join api_end_point aep on(ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1) " +
                " where vb.enabled = 1 ";

//		String sql = "select " + fileds + " from brand "+
//				" left join (select distinct brand_id,vendor_id from vendor_brand where vendor_brand.enabled = 1 and vendor_brand.vendor_id = " + vendor_id + ") vb on vb.brand_id = brand.brand_id  "+ 
//				" left join (select distinct api_configuration_id,brand_id from api_brand_map group by brand_id) abm on  (brand.brand_id = abm.brand_id and abm.enabled = 1)  "+
//				" left join api_configuration ac on abm.api_configuration_id = ac.api_configuration_id  "+
//				" left join api_end_point aep on (ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1) where brand.enabled = 1 and vb.vendor_id is not null " ; 
        // " where brand.enabled = 1 and brand.english_name like '%" + english_name + "%' and ac.api_configuration_id = " + api_configuration_id + " and aep.api_end_point_id = ";
        if (StringUtils.isNotBlank(vendor_id)) {
            sql += " and vb.vendor_id ='" + vendor_id + "' ";
        }

        if (StringUtils.isNotBlank(english_name)) {
            sql += " and brand.english_name like '%" + english_name + "%' ";
        }

        if (StringUtils.isNotBlank(api_configuration_id)) {
            sql += " and ac.api_configuration_id = '" + api_configuration_id + "' ";
        }

        if (StringUtils.isNotBlank(api_end_point_id)) {
            sql += " and aep.api_end_point_id = '" + api_end_point_id + "' ";
        }

        sql += ") and brand.enabled = 1 ";
        return sql;
    }

    public Brand getBrandByEnglishName(String englishName) {
        Brand brand = null;
        Map<String, Object> condition = new HashMap<>();
        condition.put("english_name", englishName);
        try {
            List<Brand> brands = getBrandListByCondition(condition);
            if (null != brands && brands.size() > 0) {
                brand = brands.get(0);
            }
        } catch (Exception e) {

        }
        return brand;
    }

    /**
     * 查询方法 - 分页
     *
     * @param page
     * @param englishName
     * @param chineseName
     * @param orderBy
     * @return
     * @throws Exception
     */
    public Page getBrandList(Page page, String englishName, String chineseName, String orderBy) throws Exception {
        try {
            String fieldNames = "brand_id,logo,english_name,chinese_name,(SELECT count(*) FROM vendor_brand vb WHERE vb.brand_id = b.brand_id AND vb.enabled=1 ) AS vendorCount ,(SELECT count(*) FROM product p WHERE p.brand_id = b.brand_id AND p.enabled=1 ) AS productCount ";
            String tableNames = "brand b";

            orderBy = Helper.isNullOrEmpty(orderBy) ? "brand_id" : orderBy;
            StringBuilder whereCondition = new StringBuilder("");
            List<Object> paramList = new ArrayList<Object>();
            int i = 1;
            whereCondition.append("enabled=:p" + i++);
            paramList.add(EnabledType.USED);
            whereCondition.append(" AND  status=:p" + i++);
            paramList.add(BrandType.ACTIVE);
            if (!Helper.isNullOrEmpty(englishName)) {
                whereCondition.append(" AND english_name like :p" + i++);
                paramList.add("%" + englishName + "%");
            }
            if (!Helper.isNullOrEmpty(chineseName)) {
                whereCondition.append(" AND chinese_name like :p" + i++);
                paramList.add("%" + chineseName + "%");
            }
            Object[] params = null;
            if (paramList.size() > 0) {
                params = paramList.toArray();
            }
            return brandDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames,
                    whereCondition.toString(), orderBy, params, null, null);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取vendor创建的所有brand
     *
     * @param page
     * @param vendor_application_id
     * @return
     * @throws Exception
     */
    public Page getAllBrandByVendorApplicationId(Page page, Long vendor_application_id) throws Exception {
        String fieldNames = "*";
        String tableNames = " brand ";
        String whereCondition = " vendor_application_id = :p1 AND enabled = :p2 ";
        List<Object> paramList = new ArrayList<>();
        paramList.add(vendor_application_id);
        paramList.add(EnabledType.USED);
        String orderBy = " updated_at DESC ";
        return brandDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames, whereCondition,
                orderBy, paramList.toArray(), null, null);
    }


    /**
     * 根据条件获取Brand 相关的 api_brand_map与api_configuration与api_end_point信息
     *
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @param orderBy
     * @return
     * @throws Exception
     */
    public Page getBrandPageWithBrandMapAndConfigurationAndEndPoint(long pageNumber, long pageSize,
                                                                    Map<String, Object> whereCondition, String whereStringInput)
            throws Exception {

        try {
            // 1. condition转换成 wherestr
            StringBuilder whereSql = new StringBuilder("");
            List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

            StringBuilder sql = new StringBuilder("");
            sql.append("SELECT a.*,c.system,d.name FROM brand a ");
            sql.append("INNER JOIN api_brand_map b ON a.brand_id = b.brand_id ");
            sql.append("LEFT JOIN api_configuration c ON b.api_configuration_id = c.api_configuration_id ");
            sql.append("LEFT JOIN api_end_point d ON c.api_end_point_id = d.api_end_point_id ");
            //sql.append("GROUP BY a.brand_id");


            // 先计数
            // enabled=:p2

            StringBuilder countSql = new StringBuilder("SELECT  count(*) from (");
            countSql.append(sql);
            countSql.append(whereSql);
            if (whereStringInput.length() > 0) {
                countSql.append(" and ").append(whereStringInput);
            }
            countSql.append(" GROUP BY a.brand_id");
            countSql.append(") as coun");
            System.out.println("count sql: " + countSql.toString());


            // 1. 获取 总数
            Long totalRow = brandDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);
            if (totalRow == null || totalRow <= 0) {
                return new Page(null, pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            sql.append(whereSql);
            if (whereStringInput.length() > 0) {
                sql.append(" and ").append(whereStringInput);
            }

            sql.append(" GROUP BY a.brand_id");

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
            List<Map<String, Object>> productMapList = brandDao.executeBySql(sql.toString(), params.toArray());
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * 根据brand id 获取 brand
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Brand getBrandById(Long id) throws Exception {
        try {
            Brand brand = null;

            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("enabled", EnabledType.USED);
            condition.put("brand_id", id);

            List<Brand> list = this.brandDao.getByCondition(Brand.class, condition);

            for (Brand temp : list) {
                if (temp != null) {
                    brand = temp;
                    break;
                }
            }
            return brand;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过条件获取BrandList
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public List<Brand> getBrandListByCondition(Map<String, Object> condition) throws Exception {
        try {
            // 查询
            String fieldName = "*";
            List<Brand> brandList = brandDao.getByCondition(Brand.class, fieldName, condition);
            return brandList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过条件获取BrandList
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public List<Brand> getBrandListByCondition(String englishName, int status, Long vendorId) throws Exception {
        List<Map<String, Object>> brandList = null;
        List<Brand> list = new ArrayList<Brand>();
        try {
            // 查询
            StringBuilder sql = new StringBuilder("SELECT a.* FROM brand a INNER JOIN vendor_brand b ON a.brand_id = b.brand_id ");
            sql.append("WHERE a.english_name = :p1 AND a.status = :p2 and b.vendor_id = :p3 ");
            Object[] params = {englishName, status, vendorId};
            brandList = brandDao.executeBySql(sql.toString(), params);
            LOGGER.info("getBrandListByCondition brandList:" + new Gson().toJson(brandList));

            if (brandList != null && brandList.size() > 0) {
                list.add(BrandHelper.convertToBrandFromMap(brandList.get(0)));
            }
            return list;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 通过条件获取BrandList
     *
     * @param orderBy
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getAllBrandList(String orderBy) throws Exception {
        List<Map<String, Object>> brandList = null;
        try {
            // 查询
            String sql = "SELECT * FROM brand WHERE enabled = 1 AND status = 2 order by " + orderBy + " asc";
            brandList = brandDao.executeBySql(sql, null);

        } catch (Exception e) {
            throw e;
        }

        return brandList;
    }


    public List<Map<String, Object>> getActiveBrand() throws Exception {
        List<Map<String, Object>> brandList = null;
        try {
            // 查询
            String sql = "select brand_id,english_name from brand t1 where t1.enabled = 1 and t1.brand_id in(select DISTINCT t2.brand_id from product t2) ORDER BY t1.english_name";
            brandList = brandDao.executeBySql(sql, null);

        } catch (Exception e) {
            throw e;
        }

        return brandList;
    }

    /**
     * 创建Brand
     *
     * @param brand
     * @return
     * @throws Exception
     */
    public Long createBrand(Brand brand) throws Exception {
        try {
            Long brandId = brandDao.create(brand);
            if (brandId != 0) {
                return brandId;
            }
        } catch (Exception e) {
            throw e;
        }

        return 0l;
    }

    /**
     * 可选择的brandlist
     *
     * @param vendor_application_id
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getBrandsByApplicationId(Long vendor_application_id) throws Exception {
        String sql =
                "SELECT b.* FROM brand b " +
                        "WHERE b.enabled = 1 AND b.status = 2 AND b.brand_id IN (" +
                        "SELECT DISTINCT(vb.brand_id) FROM vendor_brand vb " +
                        "WHERE vb.enabled = 1 AND vb.vendor_application_id = :p1" +
                        ");";
        Object[] params = {vendor_application_id};
        return brandDao.executeBySql(sql, params);
    }

    // 查询当前页brand的信息和它所关联的vendor数量和product数量
    public List<Map<String, Object>> getAllBrandDetail(long pageNumber, long pageSize) throws Exception {
        try {
            StringBuilder sql = new StringBuilder("SELECT brand_id,logo,english_name,chinese_name,")
                    .append("(SELECT count(*) FROM vendor_brand vb WHERE vb.brand_id = b.brand_id AND vb.enabled=1 ) AS vendorCount ,")
                    .append("(SELECT count(*) FROM product p WHERE p.brand_id = b.brand_id AND p.enabled=1 ) AS productCount ")
                    .append("FROM brand b WHERE enabled = :p1 ");
            long start = pageSize * (pageNumber - 1);
            long end = pageSize * pageNumber;
            sql.append(" limit " + start + "," + end);
            Object[] params = {EnabledType.USED};
            List<Map<String, Object>> list = brandDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 更新brand信息
     *
     * @param brand
     * @throws Exception
     */
    public void updateBrand(Brand brand) throws Exception {
        try {
            brandDao.updateById(brand);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过vendorbrand 获取brand
     */
    public List<Map<String, Object>> getBrandByVendorBrand(Long vendorId, boolean enabled) throws Exception {
        try {
            String sql = "select distinct b.* from vendor_brand vb \n" +
                    "inner join brand b on b.brand_id = vb.brand_id \n" +
                    "where vb.vendor_id = :p1 and vb.enabled = :p2 ORDER BY b.english_name ASC";
            Object[] params = {vendorId, enabled};
            List<Map<String, Object>> brandList = brandDao.executeBySql(sql, params);
            return brandList;
        } catch (Exception e) {
            throw e;
        }
    }

}
