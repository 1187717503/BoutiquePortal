package pk.shoplus.service;

import org.apache.commons.lang3.StringUtils;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiLogBatch;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApiLogBatchService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<ApiLogBatch> apiLogBatchDao = null;

	/**
	 * @param conn
	 */
	public ApiLogBatchService(Connection conn) {
		apiLogBatchDao = new EntityDao<ApiLogBatch>(conn);
	}

	/**
	 * @param apiLogBatch
	 * @return
	 * @throws Exception
	 */
	public ApiLogBatch createApiLogBatch(ApiLogBatch apiLogBatch) throws Exception {
		try {
			apiLogBatch.api_log_batch_id = apiLogBatchDao.create(apiLogBatch);
			if (apiLogBatch.api_log_batch_id > 0) {
				return apiLogBatch;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 更新apiLogBatch
	 *
	 * @param apiLogBatch
	 * @throws Exception
	 */
	public void updateApiLogBatch(ApiLogBatch apiLogBatch) throws Exception {
		try {
			apiLogBatchDao.updateById(apiLogBatch);
		} catch (Exception e) {
			throw e;
		}
	}

	public ApiLogBatch createApiLogBatch(String code,String images) throws Exception {
		try {
			String selImages = this.selApiLogBatch(code);
			if(StringUtils.isNotBlank(selImages)) {
				String sql = "update api_log_batch set images = concat(images,',"+images+"') where product_code = '" + code + "'";
				apiLogBatchDao.updateBySQL(sql,null);
			} else {
				ApiLogBatch apiLogBatch = new ApiLogBatch();
				apiLogBatch.setApi_configuration_id(0L);
				apiLogBatch.setCreate_time(new Date());
				apiLogBatch.setError_count(0);
				apiLogBatch.setSuccess_count(0);
				apiLogBatch.setLimit_num(0);
				apiLogBatch.setOffset_num(0);
				apiLogBatch.setProduct_code(code);
				apiLogBatch.setImages(images);

				apiLogBatch.api_log_batch_id = apiLogBatchDao.create(apiLogBatch);
				if (apiLogBatch.api_log_batch_id > 0) {
					return apiLogBatch;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String selApiLogBatch(String product_code) throws Exception {
		try {
			String sql = "select images from api_log_batch where product_code = '" + product_code + "'";
			List<Map<String,Object>> mapList = apiLogBatchDao.executeBySql(sql,null);
			if(mapList != null && mapList.size() > 0) {
				return mapList.get(0).get("images") == null? "" : mapList.get(0).get("images").toString();
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
