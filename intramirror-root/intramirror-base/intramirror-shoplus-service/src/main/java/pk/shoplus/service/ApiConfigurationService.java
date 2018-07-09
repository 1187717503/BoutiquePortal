package pk.shoplus.service;

import org.apache.commons.lang.StringUtils;
import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiConfiguration;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiConfigurationService {

    /**
     * 获取数据库连接
     */
    private EntityDao<ApiConfiguration> mappingDao = null;

    /**
     * @param conn
     */
    public ApiConfigurationService(Connection conn) {
        mappingDao = new EntityDao<ApiConfiguration>(conn);
    }

    /**
     * 创建Mapping
     *
     * @param apiConfiguration
     * @return
     * @throws Exception
     */
    public ApiConfiguration createMapping(ApiConfiguration apiConfiguration) throws Exception {
        try {
            Long api_configuration_id = mappingDao.create(apiConfiguration);
            if (api_configuration_id > 0) {
                apiConfiguration.api_configuration_id = api_configuration_id;
            } else {
                apiConfiguration = null;
            }
            return apiConfiguration;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改Mapping信息
     *
     * @param apiConfiguration
     * @throws Exception
     */
    public void updateMapping(ApiConfiguration apiConfiguration) throws Exception {
        try {
            mappingDao.updateById(apiConfiguration);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * 修改Mapping信息
     *
     * @param apiConfiguration
     * @throws Exception
     */
    public void updateById(ApiConfiguration apiConfiguration) throws Exception {
        try {
            mappingDao.updateById(apiConfiguration);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过Mapping_id 获取 ApiConfiguration
     *
     * @param mapping_id
     * @return
     * @throws Exception
     */
    public ApiConfiguration getMappingById(Long mapping_id) throws Exception {
        try {
            ApiConfiguration apiConfiguration = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            // 添加条件
            conditionMap.put("mapping_id", mapping_id);
            conditionMap.put("enabled", EnabledType.USED);

            // 查询
            List<ApiConfiguration> apiConfigurationList = mappingDao.getByCondition(ApiConfiguration.class, conditionMap);

            System.out.println("apiConfigurationList.size = " + apiConfigurationList.size());

            if (apiConfigurationList.size() > 0) {
                System.out.println("ApiConfiguration 0  in servce" + apiConfigurationList.get(0));
                return apiConfigurationList.get(0);
            }

            return apiConfiguration;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public List<Map<String,Object>> getApiConfigurationByVendor(Map<String,Object> conditions){
    	String vendor_id = conditions.get("vendor_id")==null?"":conditions.get("vendor_id").toString();
    	String sql = "select distinct ac.system from api_configuration ac where ac.enabled = 1 ";
    	if(StringUtils.isNotBlank(vendor_id)) {
    		sql += " and ac.vendor_id = '" + vendor_id + "' ";
    	}
    	return mappingDao.executeBySql(sql, null);
    }
    
    public List<Map<String,Object>> getApiCategoryMap(String categoryId,String cfgId,String epId){
    	
    	String sql = " select acm.boutique_category_id,acm.api_category_map_id,acm.category_id,acm.boutique_first_category,acm.boutique_second_category,acm.boutique_third_category from api_category_map acm " +
    			" left join api_configuration ac on (acm.api_configuration_id = ac.api_configuration_id and acm.enabled = 1) " +
    			" left join api_end_point aep on (aep.api_end_point_id = ac.api_end_point_id and aep.enabled = 1) " +
    			" where acm.enabled = 1 ";
    	// and aep.api_end_point_id =1 and ac.api_configuration_id = 1 and acm.category_id = 1617
    	if(StringUtils.isNotBlank(categoryId)) {
    		sql += " and acm.category_id = '"+categoryId+"' ";
    	}
    	
    	if(StringUtils.isNotBlank(cfgId)) {
    		sql += " and ac.api_configuration_id = '"+cfgId+"' ";
    	}
    	
    	if(StringUtils.isNotBlank(epId)) {
    		sql += " and aep.api_end_point_id = '"+epId+"' ";
    	}
    	return mappingDao.executeBySql(sql, null);
    }
    

    /**
     * 根据条件查询Mapping信息
     *
     * @param condition
     * @return
     */
    public ApiConfiguration getMappingByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<ApiConfiguration> list = mappingDao.getByCondition(ApiConfiguration.class, fieldNames, condition);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public ApiConfiguration getMq(Map<String, Object> condition) throws Exception {
        try {
            String sql = // "select * from api_configuration ac \n" +
                    "\nac left join api_end_point aep on(ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1)\n" +
                    "where ac.enabled = 1 and ac.store_code = '"+condition.get("store_code")+"' and ac.system = '"+condition.get("system")+"' and aep.`name` = '"+condition.get("aep_name")+"'";
            List<ApiConfiguration> list = mappingDao.getBySql(ApiConfiguration.class,"ac.api_configuration_id,ac.vendor_id,ac.store_code,ac.api_end_point_id,ac.system,ac.enabled",sql,null);
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
     * 根据条件查询Mapping信息
     *
     * @param condition
     * @return
     */
    public List<ApiConfiguration> getMappingListByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<ApiConfiguration> list = mappingDao.getByCondition(ApiConfiguration.class, fieldNames, condition);
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * 根据条件获取Mapping 的详细信息
     *
     * @return
     */
    public List<Map<String, Object>> getMappingInfoByCondition(String status, boolean enabled) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append(
                    "select p.*,v.vendor_name vendor_name,c.name category_name,b.chinese_name chinese_name,b.english_name english_name from ApiConfiguration p \n")

                    .append("left join vendor v on p.vendor_id = v.vendor_id \n")
                    .append("left join category c on p.category_id = c.category_id\n")
                    .append("left join brand b on p.brand_id = b.brand_id \n")
                    .append("where p.enabled = :p1 and p.status = :p2");
            Object[] params = {enabled, status};
            List<Map<String, Object>> list = mappingDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }
        

    public ApiConfiguration getMappingById(long MappingId) throws Exception {
        try {
            ApiConfiguration ApiConfiguration = this.mappingDao.getById(ApiConfiguration.class, MappingId);

            return ApiConfiguration;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
    
}
