package pk.shoplus.service;

import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiErrorType;
import pk.shoplus.parameter.EnabledType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiErrorTypeService {
    private final Logger logger = Logger.getLogger(ApiErrorTypeService.class);

    private EntityDao<ApiErrorType> apiErrorProcessingEntityDao = null;

    public ApiErrorTypeService(Connection conn) {
        apiErrorProcessingEntityDao = new EntityDao<ApiErrorType>(conn);
    }

    public List<ApiErrorType> getByCondition(Map<String, Object> conditions) throws Exception {
        return apiErrorProcessingEntityDao.getByCondition(ApiErrorType.class, conditions);
    }

    public Long create(ApiErrorType apiErrorType) throws Exception {
        apiErrorType.setEnabled(EnabledType.USED);
        return apiErrorProcessingEntityDao.create(apiErrorType);
    }

    public List<Map<String, Object>> getErrorTypeByMqId(String apiMqId) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            String sql = " select distinct aet.api_error_type_id,aet.error_type_name from api_error_processing aep  " +
                    " left join api_error_type aet on(aet.enabled = 1 and aet.api_error_type_id = aep.api_error_type_id)  " +
                    " where aet.api_error_type_id is not null and aep.api_mq_id =  '" + apiMqId + "' and aep.enabled = 1 ";
            listMap = apiErrorProcessingEntityDao.executeBySql(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return listMap;
    }
}
