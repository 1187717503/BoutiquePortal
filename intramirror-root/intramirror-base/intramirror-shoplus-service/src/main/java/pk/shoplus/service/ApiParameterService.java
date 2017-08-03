package pk.shoplus.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiConfiguration;
import pk.shoplus.model.ApiEndpoint;
import pk.shoplus.model.ApiParameter;

import java.util.List;
import java.util.Map;

public class ApiParameterService {
	
	private static Logger logger = Logger.getLogger(ApiParameterService.class);
    /**
     * 获取数据库连接
     */
    private EntityDao<ApiParameter> apiParameterDao = null;

    /**
     * @param conn
     */
    public ApiParameterService(Connection conn) {
        apiParameterDao = new EntityDao<ApiParameter>(conn);
    }

    /**
     * 根据条件获取apiParameter 的详细信息
     *
     * @return
     */
    public List<Map<String, Object>> getapiParameterByCondition(String apiEndPointId) throws Exception {
        try {

            StringBuilder sql = new StringBuilder("");
            sql.append("select * from api_parameter ap where ap.api_end_point_id = :p1");
            Object[] params = {apiEndPointId};
            List<Map<String, Object>> list = apiParameterDao.executeBySql(sql.toString(), params);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }
        

    public ApiParameter getApiParameterById(long apiParameterId) throws Exception {
        try {
            ApiParameter apiParameter = this.apiParameterDao.getById(ApiParameter.class, apiParameterId);
            return apiParameter;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public void updateApiParameter (ApiParameter apiParameter, Map<String, Object> condition) throws  Exception {
        apiParameterDao.updateByCondition(apiParameter, condition);
    }
    
    public void updateById(ApiParameter apiParameter) throws Exception {
        try {
        	apiParameterDao.updateById(apiParameter);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public boolean updateByApi (String size,String endPointId) throws  Exception {
    	if(StringUtils.isNotBlank(size) && StringUtils.isNotBlank(endPointId)) {
    		String sql = "update api_parameter ap set ap.param_value = ap.param_value + " + size + " where ap.api_end_point_id = " + endPointId + " and ap.param_key = 'offset'";
    		logger.info("ApiParameterService.updateByApi------------------------------------------------sql" + sql);
    		apiParameterDao.updateBySQL(sql, null);
    		return true;
    	}
    	return false;
    }
    
    public ApiParameter create(ApiParameter apiParameter) throws Exception {
        try {
            Long api_paramater_id = apiParameterDao.create(apiParameter);
            if (api_paramater_id > 0) {
            	apiParameter.api_paramater_id = api_paramater_id;
            } else {
            	apiParameter = null;
            }
            return apiParameter;
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<String,Object> getParamByKeyName(String keyName) throws Exception{
    	Map<String,Object> resultMap = null;
        String sql = "";
        try{
            /*select * from api_parameter ap
            left join api_configuration ac on (ap.api_end_point_id = ac.api_end_point_id and ac.enabled = 1)
            left join api_mq am on(ac.api_configuration_id = am.api_configuration_id and am.enabled = 1)
            where ap.enabled = 1 and ap.param_key = 'allowPriceChange' and am.`name` = 'AtelierUpdateProduct';*/

            sql = "select * from api_parameter ap " +
            "where ap.enabled = 1 ";
            if(StringUtils.isNotBlank(keyName)){
                sql += " and ap.param_key = '"+keyName+"'";
            }
            List<Map<String, Object>> listMap = apiParameterDao.executeBySql(sql,null);
            resultMap = listMap.get(0);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

}
