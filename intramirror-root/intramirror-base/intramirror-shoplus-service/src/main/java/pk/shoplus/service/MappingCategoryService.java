package pk.shoplus.service;

import com.alibaba.fastjson15.JSONObject;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiCategoryMap;

public class MappingCategoryService {

    /**
     * 获取数据库连接
     */
    private EntityDao<ApiCategoryMap> mappingCategoryDao = null;

    private static Logger logger = Logger.getLogger(MappingCategoryService.class);

    /**
     * @param conn
     */
    public MappingCategoryService(Connection conn) {
        mappingCategoryDao = new EntityDao<ApiCategoryMap>(conn);
    }

    /**
     * 根据一级，二级，三级目录查询category映射
     * @param vendor_id
     * @param one
     * @param two
     * @param three
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getMappingCategoryInfoByCondition(String vendor_id,String one,String two,String three) throws Exception {
        try {
            if(StringUtils.isBlank(vendor_id) ) {vendor_id = "-1";}
            if(StringUtils.isBlank(one)) {one = "-1";} else if(one.contains("'")){one = one.replace("'","\'");}
            if(StringUtils.isBlank(two) ) {two = "-1";} else if(two.contains("'")){two = two.replace("'","\'");}
            if(StringUtils.isBlank(three) ) {three = "-1";} else if(three.contains("'")){three = three.replace("'","\'");}

            logger.info("startSelectCategoryMappingByCategory vendor_id:"+vendor_id+",one:"+one+",two:"+two+",three:"+three);
            String sql = "select distinct c.* from api_category_map acm\n" +
                    "left join api_configuration ac on(ac.enabled = 1 and ac.api_configuration_id = acm.api_configuration_id)\n" +
                    "left join category c on(c.enabled = 1 and c.category_id = acm.category_id)\n" +
                    "where acm.enabled = 1 and ac.vendor_id = '"+vendor_id+"'\n"+
                    "and LOWER(acm.boutique_first_category) = LOWER('"+one+"')\n"+
                    "and LOWER(acm.boutique_second_category) = LOWER('"+two+"')\n"+
                    "and LOWER(acm.boutique_third_category) = LOWER('"+three+"')\n"+
                    " and c.category_id is not null ";
            logger.info("endSelectCategoryMappingByCategory sql:"+sql);
            return mappingCategoryDao.executeBySql(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }

    public String getDeafultCategory(Long vendor_id,String one,String two) throws Exception {
        try {
            if(vendor_id == null) {vendor_id = -1L;}
            if(StringUtils.isBlank(one)) {one = "-1";} else if(one.contains("'")){one = one.replace("'","\'");}
            if(StringUtils.isBlank(two) ) {two = "-1";} else if(two.contains("'")){two = two.replace("'","\'");}
            logger.info("MappingCategoryService,getDeafultCategory,vendor_id:"+vendor_id+",one:"+one+",two:"+two);

            String rsCategoryId = null;

            String selCategory = "select c3.`category_id` ,c1.`name` ,c2.`name` ,c3.`name`  from `category`  c1\n" +
                    "inner join `category`  c2 on(c1.`category_id` = c2.`parent_id` ) \n" +
                    "inner join `category`  c3 on(c2.`category_id` = c3.`parent_id` )\n" +
                    "where c1.`enabled`  = 1 and c2.`enabled`  = 1 and c3.`enabled`  = 1 \n" +
                    "and c1.`name`  = '"+one+"' and c2.`name` = '"+two+"'";
            List<Map<String,Object>> listMap = mappingCategoryDao.executeBySql(selCategory, null);

            if(listMap == null || listMap.size() == 0) {
                String selApiCategorySQL = "select distinct c.* from api_category_map acm\n" +
                        "left join api_configuration ac on(ac.enabled = 1 and ac.api_configuration_id = acm.api_configuration_id)\n" +
                        "left join category c on(c.enabled = 1 and c.category_id = acm.category_id)\n" +
                        "where acm.enabled = 1 and ac.vendor_id = '"+vendor_id+"'\n"+
                        "and LOWER(acm.boutique_first_category) = LOWER('"+one+"')\n"+
                        "and LOWER(acm.boutique_second_category) = LOWER('"+two+"')\n"+
                        " and c.category_id is not null ";
                logger.info("MappingCategoryService,getDeafultCategory,selApiCategorySQL:"+selApiCategorySQL+",vendor_id:"+vendor_id+",one:"+one+",two:"+two);
                listMap = mappingCategoryDao.executeBySql(selApiCategorySQL, null);
                logger.info("MappingCategoryService,getDeafultCategory,listMap:"+ JSONObject.toJSONString(listMap)+",sql:"+selApiCategorySQL+",vendor_id:"+vendor_id+",one:"+one+",two:"+two);
            }

            if(listMap != null && listMap.size() > 0) {
                String category_id = listMap.get(0).get("category_id").toString();
                String selCategorySql = "select "
                        + "c1.`name` as c1Name,c1.`category_id` as c1Category_id,"
                        + "c2.`name` as c2Name,c2.`category_id` as c2Category_id,"
                        + "c3.`name` as c3Name,c3.`category_id` as c3Category_id "
                        + "from `category`  c1\n" +
                        "inner join `category`  c2 on(c1.`category_id` = c2.`parent_id` ) \n" +
                        "inner join `category`  c3 on(c2.`category_id` = c3.`parent_id` )\n" +
                        "where c1.`enabled`  = 1 and c2.`enabled`  = 1 and c3.`enabled`  = 1 \n" +
                        "and c3.`category_id`  = '"+category_id+"'";

                logger.info("MappingCategoryService,getDeafultCategory,selCategory,start,sql:"+selCategorySql+",vendor_id:"+vendor_id+",one:"+one+",two:"+two);
                List<Map<String,Object>> categoryMap = mappingCategoryDao.executeBySql(selCategorySql, null);
                logger.info("MappingCategoryService,getDeafultCategory,selCategory,end,categoryMap:"+JSONObject.toJSONString(categoryMap)+",selCategorySql:"+selCategorySql+",vendor_id:"+vendor_id+",one:"+one+",two:"+two);
                if(categoryMap != null && categoryMap.size() > 0) {
                    String c1Name = categoryMap.get(0).get("c1Name").toString();
                    String c1Category_id = categoryMap.get(0).get("c1Category_id").toString();

                    String c2Name = categoryMap.get(0).get("c2Name").toString();
                    String c2Category_id = categoryMap.get(0).get("c2Category_id").toString();

                    if(StringUtils.trim(c1Name).equals("Men") && StringUtils.trim(c1Category_id).equals("1499")) {

                        if(StringUtils.trim(c2Name).equals("Bags") && StringUtils.trim(c2Category_id).equals("1505")) { // 二级为包,放入单肩包
                            rsCategoryId = "1551";
                        } else if(StringUtils.trim(c2Name).equals("Accessories") && StringUtils.trim(c2Category_id).equals("1507")){ // 二级为配饰,放入others
                            rsCategoryId = "1634";
                        } else if(StringUtils.trim(c2Name).equals("Clothing") && StringUtils.trim(c2Category_id).equals("1504")) { // 二级为衣服，放入Top,上衣
                            rsCategoryId = "1524";
                        } else if(StringUtils.trim(c2Name).equals("Shoes") && StringUtils.trim(c2Category_id).equals("1506")) { // 二级为鞋，放入休闲商务鞋
                            rsCategoryId = "1653";
                        }
                    } else if(StringUtils.trim(c1Name).equals("Women") && StringUtils.trim(c1Category_id).equals("1568")) {

                        if(StringUtils.trim(c2Name).equals("Bags") && StringUtils.trim(c2Category_id).equals("1598")) { // 二级为包,放入单肩包
                            rsCategoryId = "1604";
                        } else if(StringUtils.trim(c2Name).equals("Accessories") && StringUtils.trim(c2Category_id).equals("1608")){ // 二级为配饰,放入others
                            rsCategoryId = "1625";
                        } else if(StringUtils.trim(c2Name).equals("Clothing") && StringUtils.trim(c2Category_id).equals("1569")) { // 二级为衣服，放入Top,上衣
                            rsCategoryId = "1582";
                        } else if(StringUtils.trim(c2Name).equals("Shoes") && StringUtils.trim(c2Category_id).equals("1584")) { // 二级为鞋，放入高跟鞋
                            rsCategoryId = "1594";
                        }
                    }
                }
            }
            logger.info("MappingCategoryService,outputParams,getDeafultCategory,vendor_id:"+vendor_id+",one:"+one+",two:"+two+",rsCategoryId:"+rsCategoryId);
            return rsCategoryId;
        } catch (Exception e) {
            throw e;
        }
    }

    public String getMappingCategoryInfoByCondition(String one,String two,String three){
        if(StringUtils.isBlank(one)) {one = "-1";} else if(one.contains("'")){one = one.replace("'","\'");}
        if(StringUtils.isBlank(two) ) {two = "-1";} else if(two.contains("'")){two = two.replace("'","\'");}
        if(StringUtils.isBlank(three) ) {three = "-1";} else if(three.contains("'")){three = three.replace("'","\'");}
        logger.info("getMappingCategoryInfoByCondition inputParams,one:"+one+",two:"+two+",three:"+three);
        String sql = "select c3.`category_id` ,c1.`name` ,c2.`name` ,c3.`name`  from `category`  c1\n" +
                "inner join `category`  c2 on(c1.`category_id` = c2.`parent_id` ) \n" +
                "inner join `category`  c3 on(c2.`category_id` = c3.`parent_id` )\n" +
                "where c1.`enabled`  = 1 and c2.`enabled`  = 1 and c3.`enabled`  = 1 \n" +
                "and c1.`name`  = '"+one+"' and c2.`name` = '"+two+"' and c3.`name` = '"+three+"'";
        logger.info("getMappingCategoryInfoByCondition sql:"+sql);
        List<Map<String,Object>> dataMap = mappingCategoryDao.executeBySql(sql,null);

        if(dataMap != null && dataMap.size() > 0) {
            return dataMap.get(0).get("category_id").toString();
        }
        return null;
    }

    /**
     * 根据boutique_category_id查询category映射
     * @param vendor_id
     * @param boutique_category_id
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getMappingCategoryInfoByCondition(String vendor_id,String boutique_category_id) throws Exception {
        try {
            if(StringUtils.isBlank(vendor_id) ) {vendor_id = "-1";}
            if(StringUtils.isBlank(boutique_category_id)) {boutique_category_id = "-1";} else if(boutique_category_id.contains("'")){boutique_category_id = boutique_category_id.replace("'","\'");}

            logger.info("startSelectCategoryMappingByBoutiqueCategory vendor_id:"+vendor_id+",boutique_category_id:"+boutique_category_id);
            String sql = "select distinct c.* from api_category_map acm\n" +
                    "left join api_configuration ac on(ac.enabled = 1 and ac.api_configuration_id = acm.api_configuration_id)\n" +
                    "left join category c on(c.enabled = 1 and c.category_id = acm.category_id)\n" +
                    "where acm.enabled = 1 and ac.vendor_id = '"+vendor_id+"'\n"+
                    "and LOWER(acm.boutique_category_id) = LOWER('"+boutique_category_id+"')\n"+
                    " and c.category_id is not null ";
            logger.info("endSelectCategoryMappingByBoutiqueCategory By sql:"+sql);
            return mappingCategoryDao.executeBySql(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }
}
