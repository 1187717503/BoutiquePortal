package pk.shoplus.service;

import org.apache.commons.lang.StringUtils;
import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiMq;
import pk.shoplus.parameter.EnabledType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiMqService {

    /**
     * 获取数据库连接
     */
    private EntityDao<ApiMq> apiMqDao = null;

    /**
     * @param conn
     */
    public ApiMqService(Connection conn) {
    	apiMqDao = new EntityDao<ApiMq>(conn);
    }

    
    /**
     * 根据条件查询List<ApiMq>信息
     *
     * @param condition
     * @return
     */
    public List<ApiMq> getApiMqListByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
    	List<ApiMq> list = null;
        try {
            list = apiMqDao.getByCondition(ApiMq.class, fieldNames, condition);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }

    public ApiMq getMqByName(String mqName) throws Exception{
    	try {
    		Map<String,Object> conditions = new HashMap<>();
    		conditions.put("enabled", EnabledType.USED);
    		conditions.put("name",mqName);

    		List<ApiMq> mqList = apiMqDao.getByCondition(ApiMq.class,conditions);
    		if(mqList != null && mqList.size() > 0)
    			return mqList.get(0);
    		return null;
		} catch (Exception e) {
    		throw e;
		}
	}

    public List<Map<String,Object>> getData(String sql){
		List<Map<String, Object>> mapList = null;
    	if(StringUtils.isNotBlank(sql)) {
			mapList = apiMqDao.executeBySql("select * from ("+sql+") t",null);
		}
    	return mapList;
	}

    public List<Map<String,Object>> getApiConfByApiMq(String mqName){
    	List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
    	try {
    		/*select ac.store_code,ac.api_configuration_id,ac.api_end_point_id from api_mq am left join api_configuration ac on am.api_configuration_id = ac.api_configuration_id
    				where am.`name` = 'AtelierCreateProduct';*/
    		String sql = " select ac.store_code,ac.api_configuration_id,ac.api_end_point_id,ac.vendor_id from api_mq am "
    				+ " left join api_configuration ac on am.api_configuration_id = ac.api_configuration_id "
    				+ " where am.`name` = '" + mqName + "'";
    		listMap = apiMqDao.executeBySql(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return listMap;
    }
    
    // 查询api相关的全部vendor
    public List<Map<String,Object>> getVendor(){
    	List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
    	try {
    		String sql = " select v.vendor_id,v.vendor_name from vendor v where v.vendor_id in (select distinct ac.vendor_id from api_configuration ac) and v.enabled = 1";
    		listMap = apiMqDao.executeBySql(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return listMap;
    }
    
    // 查询System
    public List<Map<String,Object>> getSystem(String vendorId){
    	List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
    	try { // ac.vendor_id = '" + vendorId + "'
    		String sql = " select distinct ac.system from api_configuration ac where ac.enabled = 1 ";
			if(StringUtils.isNotBlank(vendorId)) {
				sql += " and ac.vendor_id = '" + vendorId + "'";
			}
    		listMap = apiMqDao.executeBySql(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return listMap;
    }
    
    // 查询Api
    public List<Map<String,Object>> getApi(String vendorId,String system){
    	List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
    	try {
    		String sql = " select aep.api_end_point_id,aep.`name` from api_end_point aep where aep.api_end_point_id in ( " + 
    				  	 " select ac.api_end_point_id from api_configuration ac where ac.vendor_id = '" + vendorId + "' and ac.system = '" + system + "')  and aep.enabled = 1 "; 
    		listMap = apiMqDao.executeBySql(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return listMap;
    }

	// ApiError查询Api
	public List<Map<String,Object>> getApiByVendorId(String vendorId){
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		try {
			String sql = " select aep.api_end_point_id,aep.`name` from api_end_point aep where aep.api_end_point_id in ( " +
					" select ac.api_end_point_id from api_configuration ac where ac.vendor_id = '" + vendorId +  "')  and aep.enabled = 1 ";
			listMap = apiMqDao.executeBySql(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return listMap;
	}


    // 查询ApiConfig
    public List<Map<String,Object>> getApiConfig(String vendorId,String apiCfgId){
    	List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
    	try {
    		/*select ac.api_configuration_id,ac.system,ac.store_code,aep.`name`,aep.url,ap.`offset`,ap.`limit`,ap.cadence from api_configuration ac 
    		left join api_end_point aep on (ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1)
    		left join (select ap.api_end_point_id,max(case when ap.param_key = 'storeCode' then ap.param_value end) as 'storeCode',
    		max(case when ap.param_key = 'limit' then ap.param_value end) as 'limit',
    		max(case when ap.param_key = 'offset' then ap.param_value end) as 'offset',
    		max(case when ap.param_key = 'cadence' then ap.param_value end) as 'cadence'
    		from api_parameter ap group by ap.api_end_point_id) ap on (aep.api_end_point_id = ap.api_end_point_id)
    		where ac.vendor_id = 7 and ac.api_configuration_id = '1';*/
    		String sql = "select ac.api_configuration_id,ac.system,ac.store_code,aep.`name`,aep.url,ap.`offset`,ap.`limit`,ap.cadence from api_configuration ac " +
    				" left join api_end_point aep on (ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1) " +
    				" left join (select ap.api_end_point_id,max(case when ap.param_key = 'storeCode' then ap.param_value end) as 'storeCode', " +
    				" max(case when ap.param_key = 'limit' then ap.param_value end) as 'limit', " +
    				" max(case when ap.param_key = 'offset' then ap.param_value end) as 'offset', " +
    				" max(case when ap.param_key = 'cadence' then ap.param_value end) as 'cadence' " +
    				" from api_parameter ap group by ap.api_end_point_id) ap on (aep.api_end_point_id = ap.api_end_point_id) where ac.enabled=1 ";
    		if(StringUtils.isNotBlank(vendorId)) {
    			sql += " and ac.vendor_id =  '" + vendorId + "'";
    		}
			if(StringUtils.isNotBlank(apiCfgId)) {
				sql += " and ac.api_configuration_id =  '" + apiCfgId + "'";
			}
    		listMap = apiMqDao.executeBySql(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return listMap;
    }
    
    public ApiMq create(ApiMq apiMq) throws Exception {
        try {
            Long api_mq_id = apiMqDao.create(apiMq);
            if (api_mq_id > 0) {
            	apiMq.api_mq_id = api_mq_id;
            } else {
            	apiMq = null;
            }
            return apiMq;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void updateById(ApiMq apiMq) throws Exception {
        try {
        	apiMqDao.updateById(apiMq);
        } catch (Exception e) {
            throw e;
        }
    }
    
    
    public void updateApiMq (ApiMq apiMq, Map<String, Object> condition) throws  Exception {
    	apiMqDao.updateByCondition(apiMq, condition);
    }
    
    /*// 查询ApiConfig
    public List<Map<String,Object>> updateApiConfig(UpdateApiCfgVO updateVO){
    	List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
    	try {
    		// apiCfgId , systemId , apiName 
    		
    		update api_configuration ac set ac.store_code = '' where api_configuration_id = 1;
    		update api_parameter ap set ap.param_value = '' 
    		where ap.param_key = 'storeCode' 
    		and ap.api_end_point_id = (select ac.api_end_point_id from api_configuration ac where ac.api_configuration_id = 1);
    		
    		// update storeCode
    		String updateApiCfgStoreCode = "update api_configuration ac set ac.store_code = '" + updateVO.getStoreCode() + "' where api_configuration_id = '" + updateVO.getApiCfgId() + "'";
    		String updateApiParamterStoreCode = "update api_parameter ap set ap.param_value = '" + updateVO.getStoreCode() + "' " +
    											"where ap.param_key = 'storeCode' " +
    											"and ap.api_end_point_id = (select ac.api_end_point_id from api_configuration ac where ac.api_configuration_id = '" + updateVO.getApiCfgId() + "')";
    		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return listMap;
    }*/
}
