package pk.shoplus.service;

import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiErrorProcessing;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApiErrorProcessingService {
    private final Logger logger = Logger.getLogger(ApiErrorProcessingService.class);

    private EntityDao<ApiErrorProcessing> apiErrorProcessingEntityDao = null;

    public ApiErrorProcessingService(Connection conn) {
        apiErrorProcessingEntityDao = new EntityDao<ApiErrorProcessing>(conn);
    }

    public void create(ApiErrorProcessing apiErrorProcessing) throws Exception {
        apiErrorProcessing.setEnabled(EnabledType.USED);
        apiErrorProcessing.setNo_process(true);
        apiErrorProcessing.setCreate_time(new Date());
        apiErrorProcessing.setProcess_time(null);
        apiErrorProcessingEntityDao.create(apiErrorProcessing);
    }

    public List<Map<String, Object>> queryMqName(String ids) throws Exception {
        String sql = "select aep.api_error_processing_id,aep.error_id,am.`name`,am.api_configuration_id from api_error_processing aep \n" +
                "left join api_mq am on(am.enabled = 1 and am.api_mq_id = aep.api_mq_id)\n" +
                "where aep.enabled = 1 and aep.api_error_processing_id in (" + ids + ")";
        return apiErrorProcessingEntityDao.executeBySql(sql, null);
    }

    public Object updateEnabledByIds(String ids) throws Exception {
        String sql = "update api_error_processing set enabled = 0,process_time = now(),no_process = 0 where api_error_processing_id in(" + ids + ")";
        return apiErrorProcessingEntityDao.updateBySQL(sql, null);
    }

    /**
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @param fieldNames
     * @param whereStringInput
     * @return
     * @throws Exception
     */
    public Page getApiErrorProcess(Long pageNumber, Long pageSize, String fieldNames, String whereStringInput) throws Exception {

        try {

            // 先计数
            StringBuilder countSql = new StringBuilder();
            countSql.append("select count(*) from api_error_processing aep " +
                    "left join api_mq am on (am.enabled = 1 and am.api_mq_id = aep.api_mq_id) " +
                    "left join api_configuration ac on (ac.enabled = 1 and ac.api_configuration_id = am.api_configuration_id) " +
                    "left join api_end_point aepp on (aepp.enabled = 1 and aepp.api_end_point_id = ac.api_end_point_id) " +
                    "left join vendor v on (v.enabled = 1 and v.vendor_id = ac.vendor_id) " +
                    "left join api_error_type aet on (aet.enabled=1 AND aep.api_error_type_id=aet.api_error_type_id) " +
                    "where aep.enabled = 1 ");
            if (whereStringInput.length() > 0) {
                countSql.append(" and ").append(whereStringInput);
            }
            System.out.println("count sql: " + countSql.toString());


            // 1. 获取 总数
            Long totalRow = apiErrorProcessingEntityDao.createQuery(countSql.toString(), null).executeScalar(Long.class);
            if (totalRow == null || totalRow <= 0) {
                return new Page(new ArrayList<Map<String, Object>>(), pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            StringBuilder sql = new StringBuilder("");
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }

            sql.append(" select aep.* , v.vendor_name, aepp.`name`, aet.error_type_name " +
                    " from api_error_processing aep " +
                    " left join api_mq am on (am.enabled = 1 and am.api_mq_id = aep.api_mq_id) " +
                    " left join api_configuration ac on (ac.enabled = 1 and ac.api_configuration_id = am.api_configuration_id) " +
                    " left join api_end_point aepp on (aepp.enabled = 1 and aepp.api_end_point_id = ac.api_end_point_id) " +
                    " left join vendor v on (v.enabled = 1 and v.vendor_id = ac.vendor_id) " +
                    " left join api_error_type aet on (aet.enabled=1 AND aep.api_error_type_id=aet.api_error_type_id) " +
                    " where aep.enabled = 1 ");
            if (whereStringInput.length() > 0) {
                sql.append(" and ").append(whereStringInput);
            }

            // pageSize或者pageNumber
            // 如果小于1就返回全部数据
            if (pageSize < 1) {
                return new Page();
            }

            if (pageNumber < 1) {
                return new Page();
            }

            // 计数
            long totalPage = totalRow / pageSize;
            if (totalRow % pageSize != 0) {
                totalPage++;
            }
            if (pageNumber > totalPage) {
                return new Page();
            }

            // 拼MySQL分页
            long offset = pageSize * (pageNumber - 1);
            sql.append(" limit ").append(offset).append(",").append(pageSize);
            System.out.println("excutesql:" + sql.toString());
            List<Map<String, Object>> apiErrorMapList = apiErrorProcessingEntityDao.executeBySql(sql.toString(), null);
            return new Page(apiErrorMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

}
