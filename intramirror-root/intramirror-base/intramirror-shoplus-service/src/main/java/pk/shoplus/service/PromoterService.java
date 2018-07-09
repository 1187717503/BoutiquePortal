package pk.shoplus.service;

import org.sql2o.Connection;

import com.amazonaws.services.kms.model.CreateAliasRequest;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.Promoter;
import pk.shoplus.model.PromoterShop;
import pk.shoplus.parameter.EnabledType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PromoterService {
	private EntityDao<Promoter> promoterDao;

	public PromoterService(Connection conn) {
		promoterDao = new EntityDao<>(conn);
	}

	public long countByEmail(String email) throws Exception {
		Map<String, Object> condition = new HashMap<>();

		condition.put("email", email);
		condition.put("enabled", EnabledType.USED);

		return this.promoterDao.countByCondition(Promoter.class, condition);
	}

	public long countByPromoterId(Long PromoterId) throws Exception {
		Map<String, Object> condition = new HashMap<>();

		condition.put("promoter_id", PromoterId);
		condition.put("enabled", EnabledType.USED);

		return this.promoterDao.countByCondition(Promoter.class, condition);
	}

	public long countByName(String name) throws Exception {
		Map<String, Object> condition = new HashMap<>();

		condition.put("name", name);
		condition.put("enabled", EnabledType.USED);

		return this.promoterDao.countByCondition(Promoter.class, condition);
	}

	public long countByTelephone(String telephone) throws Exception {
		Map<String, Object> condition = new HashMap<>();

		condition.put("telephone", telephone);
		condition.put("enabled", EnabledType.USED);

		return this.promoterDao.countByCondition(Promoter.class, condition);
	}

	public Promoter createPromoter(Promoter promoter) throws Exception {
		long promoter_id = promoterDao.create(promoter);
		if (promoter_id > 0) {
			promoter.promoter_id = promoter_id;
		} else {
			throw new Exception("Creation failed");
		}
		return promoter;
	}

	public Promoter getPromoterById(Long promoter_id) throws Exception {
		return promoterDao.getById(Promoter.class, promoter_id);
	}

	public Promoter getPromoterByShopId(Long shop_id) throws Exception {
		String sql = "SELECT p.* FROM promoter p " +
				"LEFT JOIN promoter_shop ps ON ps.promoter_id = p.promoter_id " +
				"WHERE ps.enabled = 1  AND ps.shop_id = :p1;";
		Object[] params = new Object[]{shop_id};
		List<Map<String, Object>> list = promoterDao.executeBySql(sql, params);
		if (list.isEmpty()) {
			return null;
		} else {
			return postProcess(Helper.convertMapToBean(list.get(0), Promoter.class));
		}
	}

	public Map<String, Object> getPromoterDetailById(String promoter_id) throws Exception {
		Page page = getPromoterManagementPage(1L, 1L, promoter_id, null, null);

		if (page.list.isEmpty()) {
			return null;
		} else {
			return appendShopInfo(postProcessMap(page.list.get(0)));
		}
	}

	private Map<String, Object> appendShopInfo(Map<String, Object> promoterDetail) throws Exception {
		String sql = "SELECT s.shop_name as name, r.orders_total as orders, r.fee_total as sales FROM shop s LEFT JOIN report_shop_daily r ON s.shop_id = r.shop_id WHERE s.shop_id IN(SELECT shop_id FROM promoter_shop ps WHERE ps.enabled = 1 AND ps.promoter_id = :p1);";
		Object[] params = new Object[] { promoterDetail.get("promoter_id") };
		promoterDetail.put("shops", promoterDao.executeBySql(sql, params));

		sql = "SELECT s.account_date, SUM(s.order_amount) as orders, SUM(s.sales_amount) as sales "
				+ "FROM promoter p "
				+ "LEFT JOIN promoter_shop ps ON ps.promoter_id = p.promoter_id "
				+ "LEFT JOIN statistics_shop_operation_daily s ON s.shop_id = ps.shop_id "
				+ "WHERE p.enabled = 1 AND p.promoter_id = :p1 AND ps.enabled = 1 AND s.enabled = 1 "
				+ "GROUP BY s.account_date " + "ORDER BY s.account_date DESC "
				+ "LIMIT 30;";
		promoterDetail.put("data", promoterDao.executeBySql(sql, params));

		return promoterDetail;
	}

	public Page getPromoterManagementPage(Long pageNumber, Long pageSize, String promoter_id, String promoter_name,
			String orderBy) throws Exception {
		String filedName = "p.promoter_id, p.name, COUNT(ps.shop_id) as relative_shop, COALESCE(SUM(fee_total), 0) as relative_sales ";
		String tableName = "promoter p " + "LEFT JOIN promoter_shop ps ON ps.promoter_id = p.promoter_id "
				+ "LEFT JOIN report_shop_daily r ON r.shop_id = ps.shop_id ";

		List<Object> params = new ArrayList<>();
		StringBuilder whereSB = new StringBuilder();

		params.add(EnabledType.USED);
		whereSB.append("p.enabled = :p").append(params.size());

		if (!Helper.isNullOrEmpty(promoter_id)) {
			params.add(promoter_id);
			whereSB.append(" AND p.promoter_id = :p").append(params.size());
		}
		if (!Helper.isNullOrEmpty(promoter_name)) {
			params.add("%" + promoter_name + "%");
			whereSB.append(" AND p.name LIKE :p").append(params.size());
		}
		String groupBy = "p.promoter_id";
		Page page = promoterDao.getPageBySql(pageNumber, pageSize, filedName, tableName, whereSB.toString(), orderBy,
				params.toArray(new Object[params.size()]), "p.promoter_id", groupBy);

		page.list = page.list.stream().map(PromoterService::postProcessMap).collect(Collectors.toList());

		return page;
	}

	private static Promoter postProcess(Promoter promoter) {
		promoter.code = getCodeFromId(promoter.promoter_id.toString());
		return promoter;
	}

	private static Map<String, Object> postProcessMap(Map<String, Object> map) {
		map.put("code", getCodeFromId(map.get("promoter_id").toString()));
		return map;
	}

	private static String getCodeFromId(String id) {
		String code;
		String prefix = "000000";
		int requiredIdLength = prefix.length();
		if (id.length() < requiredIdLength) {
			code = prefix.substring(0, requiredIdLength - id.length()) + id;
		} else {
			code = id.substring(0, requiredIdLength);
		}
		return code;
	}
}
