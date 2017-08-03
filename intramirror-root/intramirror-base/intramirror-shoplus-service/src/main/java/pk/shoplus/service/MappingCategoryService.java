package pk.shoplus.service;

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

    /**
     * @param conn
     */
    public MappingCategoryService(Connection conn) {
        mappingCategoryDao = new EntityDao<ApiCategoryMap>(conn);
    }


    /**
     * 根据条件获取mappingCategory 的详细信息
     *
     * @return
     */
    public List<Map<String, Object>> getMappingCategoryInfoByCondition(Object [] params) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append("select c.*\n" +
                    " from api_configuration ac, api_category_map acm, category c\n" +
                    " where ac.api_configuration_id = acm.api_configuration_id and\n" +
                    " ac.store_code = :p1 and\n" +
                    " acm.category_id = c.category_id and\n" +
                    " acm.boutique_first_category = :p2 and\n" +
                    " acm.boutique_second_category = :p3 and\n" +
                    " acm.boutique_third_category = :p4 and\n" +
                    " c.enabled = 1 and acm.enabled = 1 and ac.enabled = 1");
            List<Map<String, Object>> list = mappingCategoryDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }
    
    

}
