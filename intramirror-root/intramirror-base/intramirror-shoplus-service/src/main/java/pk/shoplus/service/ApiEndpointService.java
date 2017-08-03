package pk.shoplus.service;

import org.apache.commons.lang.StringUtils;
import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiConfiguration;
import pk.shoplus.model.ApiEndpoint;
import pk.shoplus.model.ApiParameter;

import java.util.List;
import java.util.Map;

public class ApiEndpointService {

    /**
     * 获取数据库连接
     */
    private EntityDao<ApiEndpoint> apiEndpointDao = null;

    /**
     * @param conn
     */
    public ApiEndpointService(Connection conn) {
        apiEndpointDao = new EntityDao<ApiEndpoint>(conn);
    }

    /**
     * 根据条件获取apiEndpoint 的详细信息
     *
     * @return
     */
    public List<Map<String, Object>> getapiEndpointInfoByCondition(String os, String apiName) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append("select ac.api_configuration_id,ac.store_code,ac.vendor_id,aep.url,aep.api_end_point_id \n" +
                            " from api_configuration ac,api_end_point aep\n" +
                            " where ac.api_end_point_id = aep.api_end_point_id and\n" +
                            " aep.enabled = 1 and\n" +
                            " ac.enabled = 1 and\n" +
                            " ac.system = :p1 and\n" +
                            " aep.`name` = :p2");
            Object[] params = {os, apiName};
            List<Map<String, Object>> list = apiEndpointDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * 根据条件获取apiEndpoint 的详细信息
     *
     * @return
     */
    public List<Map<String, Object>> getapiEndpointInfoByCondition(String os, String apiName,String stroeCode) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append("select ac.api_configuration_id,ac.store_code,ac.vendor_id,aep.url,aep.api_end_point_id \n" +
                            " from api_configuration ac,api_end_point aep\n" +
                            " where ac.api_end_point_id = aep.api_end_point_id and\n" +
                            " aep.enabled = 1 and\n" +
                            " ac.enabled = 1 and\n" +
                            " ac.system = :p1 and\n" +
                            " aep.`name` = :p2 and ac.store_code = :p3");
            Object[] params = {os, apiName,stroeCode};
            List<Map<String, Object>> list = apiEndpointDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public List<Map<String, Object>> getApiEndPointByConfigId(Map<String,Object> conditions) {
    	String vendor_id = conditions.get("vendor_id")==null?"":conditions.get("vendor_id").toString();
    	String system = conditions.get("system")==null?"":conditions.get("system").toString();
    	String sql = "select aep.api_end_point_id,aep.`name`,ac.api_configuration_id from api_end_point aep "+
    				"left join api_configuration ac on(aep.api_end_point_id = ac.api_end_point_id and ac.enabled = 1) "+
    				"where aep.enabled = 1 ";
    	if(StringUtils.isNotBlank(vendor_id)) {
    		sql += " and ac.vendor_id = '" + vendor_id + "' ";
    	}
    	if(StringUtils.isNotBlank(system)) {
    		sql += " and ac.system = '" + system + "' ";
    	}
    	return apiEndpointDao.executeBySql(sql, null);
    }
    
    /**
     * 根据条件查询ApiEndpoint信息
     *
     * @param condition
     * @return
     */
    public ApiEndpoint getapiEndpointInfoByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<ApiEndpoint> list = apiEndpointDao.getByCondition(ApiEndpoint.class, fieldNames, condition);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
    
    public void updateById(ApiEndpoint apiEndpoint) throws Exception {
        try {
        	apiEndpointDao.updateById(apiEndpoint);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public ApiEndpoint create(ApiEndpoint apiEndpoint) throws Exception {
        try {
            Long api_end_point_id = apiEndpointDao.create(apiEndpoint);
            if (api_end_point_id > 0) {
            	apiEndpoint.api_end_point_id = api_end_point_id;
            } else {
            	apiEndpoint = null;
            }
            return apiEndpoint;
        } catch (Exception e) {
            throw e;
        }
    }

}
