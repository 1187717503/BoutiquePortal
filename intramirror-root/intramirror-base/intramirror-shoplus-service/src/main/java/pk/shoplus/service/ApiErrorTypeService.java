package pk.shoplus.service;

import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiErrorProcessing;
import pk.shoplus.model.ApiErrorType;
import pk.shoplus.parameter.EnabledType;

import java.util.List;
import java.util.Map;

public class ApiErrorTypeService {
    private final Logger logger = Logger.getLogger(ApiErrorTypeService.class);

    private EntityDao<ApiErrorType> apiErrorProcessingEntityDao = null;

    public ApiErrorTypeService(Connection conn) {
        apiErrorProcessingEntityDao = new EntityDao<ApiErrorType>(conn);
    }

    public List<ApiErrorType> getByCondition(Map<String,Object> conditions) throws Exception {
        return apiErrorProcessingEntityDao.getByCondition(ApiErrorType.class,conditions);
    }

    public Long create(ApiErrorType apiErrorType) throws Exception{
        apiErrorType.setEnabled(EnabledType.USED);
        return apiErrorProcessingEntityDao.create(apiErrorType);
    }
}
