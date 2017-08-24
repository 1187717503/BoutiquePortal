package pk.shoplus.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiCategoryMap;
import pk.shoplus.model.Brand;
import pk.shoplus.parameter.EnabledType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    "and acm.boutique_first_category = '"+one+"'\n"+
                    "and acm.boutique_second_category = '"+two+"'\n"+
                    "and acm.boutique_third_category = '"+three+"'\n";
            logger.info("endSelectCategoryMappingByCategory sql:"+sql);
            return mappingCategoryDao.executeBySql(sql, null);
        } catch (Exception e) {
            throw e;
        }
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
                    "and acm.boutique_category_id = '"+boutique_category_id+"'\n";
            logger.info("endSelectCategoryMappingByBoutiqueCategory By sql:"+sql);
            return mappingCategoryDao.executeBySql(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }
}
