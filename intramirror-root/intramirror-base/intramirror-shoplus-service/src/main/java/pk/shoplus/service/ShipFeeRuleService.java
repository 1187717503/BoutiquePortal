package pk.shoplus.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.ShipFeeRule;
import pk.shoplus.parameter.EnabledType;

public class ShipFeeRuleService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ShipFeeRule> shipFeeRuleDao = null;

	/**
	 * @param conn
	 */
	public ShipFeeRuleService(Connection conn) {
		shipFeeRuleDao = new EntityDao<ShipFeeRule>(conn);
	}

	/**
	 * 创建运费信息
	 * 
	 * @param shipFeeRule
	 * @return 实体/null
	 * @throws Exception
	 */
	public ShipFeeRule createShipFeeRule(ShipFeeRule shipFeeRule) throws Exception {
		try {
			long shipFeeRuleId = shipFeeRuleDao.create(shipFeeRule);
			if (shipFeeRuleId > 0) {
				shipFeeRule.ship_fee_rule_id = shipFeeRuleId;
			} else {
				shipFeeRule = null;
			}
			return shipFeeRule;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改运费信息
	 * 
	 * @param shipFeeRule
	 * @throws Exception
	 */
	public void updateShipFeeRule(ShipFeeRule shipFeeRule) throws Exception {
		try {
			shipFeeRuleDao.updateById(shipFeeRule);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过ship_fee_rule_id 获取 ShipFeeRule
	 * 
	 * @param ship_fee_rule_id
	 * @return
	 * @throws Exception
	 */
	public ShipFeeRule getShipFeeRuleById(Long ship_fee_rule_id) throws Exception {
		try {
			ShipFeeRule shipFeeRule = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("ship_fee_rule_id", ship_fee_rule_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ShipFeeRule> shipFeeRuleList = shipFeeRuleDao.getByCondition(ShipFeeRule.class, conditionMap);

			for (ShipFeeRule temp : shipFeeRuleList) {
				if (temp != null) {
					shipFeeRule = new ShipFeeRule();
					shipFeeRule = temp;
					break;
				}
			}

			return shipFeeRule;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过条件获取shipFeeRuleList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<ShipFeeRule> getShipFeeRuleListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<ShipFeeRule> shipFeeRuleList = shipFeeRuleDao.getByCondition(ShipFeeRule.class, fieldName, condition);
			return shipFeeRuleList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询当前有效的免邮规则生效时间最接近的一条
	 * 
	 * @param nowDate
	 * @return
	 */
	public Map<String, Object> getShipFeeRuleByNowDate(Date nowDate) {
		try {
			String sql = "select * from ship_fee_rule where  timestampdiff(second,effective_at,:p1) > 0 and timestampdiff(second,expiry_at,:p2)< 0 and enabled=1 order by effective_at desc ; ";
			Object[] params = { nowDate, nowDate };
			List<Map<String, Object>> list = shipFeeRuleDao.executeBySql(sql, params);
			if (list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*	*//**
			 * 查询当前有效免邮信息的最后有效时间
			 * 
			 * @return
			 *//*
			 * public Map<String,Object> getShipFeeRuleOfMaxEffectiveTime(){
			 * try{ String sql=
			 * "select expiry_at from ship_fee_rule where enabled=1 order by expiry_at desc limit 0,1;"
			 * ; Object[] params={}; List<Map<String,Object>>
			 * list=shipFeeRuleDao.executeBySql(sql,params); if (list!=null){
			 * return list.get(0); } } catch (Exception e){ e.printStackTrace();
			 * throw e; } return null; }
			 */

	public Page getShipFeeRuleList(Page page, Map<String, Object> condition) throws Exception {
		try {
			String fieldNames = " total_fee,effective_at,expiry_at,remark ";
			String tableNames = " ship_fee_rule ";
			String whereCondition = null;
			List<Object> paramList = new ArrayList<Object>();
			int i = 1;
			whereCondition = " enabled=:p" + i++;
			paramList.add(EnabledType.USED);
			Object[] params = null;
			if (paramList.size() > 0) {
				params = paramList.toArray();
			}
			return shipFeeRuleDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames, whereCondition,
					null, params, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 检测日期是否符合免邮规则
	 * 
	 * @param effective
	 * @param expiry
	 * @return
	 */
	public Map<String, Object> testingNewShipRule(Date effective, Date expiry) {
		try {
			String sql = "select (case when (select if((select count(*) from ship_fee_rule where effective_at < :p1 and expiry_at > :p2)>0,0,1))=1  "
					+ "  then (select if((select effective_at from ship_fee_rule where effective_at >= :p3 order by effective_at asc limit 1 ) is null or  "
					+ " (select effective_at from ship_fee_rule where effective_at >= :p4 order by effective_at asc limit 1)>=:p5,'true','false')) "
					+ "  else 'false' end) as result;";
			Object[] params = { effective, effective, effective, effective, expiry };
			List<Map<String, Object>> list = shipFeeRuleDao.executeBySql(sql, params);
			if (list != null) {
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 根据totalfee 获取 shipfeerule list
	 * 
	 * @param totalFee
	 * @throws Exception
	 */
	public List<Map<String, Object>> getShipFeeRuleListByTotalFee(String totalFee) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// SELECT * FROM `ship_fee_rule` where total_fee < 10.5 and enabled
			// = 1 and NOW() > effective_at and NOW() < expiry_at order by
			// total_fee desc;

			StringBuilder sql = new StringBuilder("");

			sql.append("SELECT * FROM `ship_fee_rule`");
			sql.append("where total_fee < ");
			sql.append(totalFee);
			sql.append(" and enabled = 1 and NOW() > effective_at and NOW() < expiry_at order by total_fee desc");
			System.out.println("excutesql:" + sql.toString());
			result = shipFeeRuleDao.executeBySql(sql.toString(), new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public Map<String, Object> getShipFeeRuleByNow() throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// SELECT * FROM ship_fee_rule WHERE expiry_at > NOW() AND
			// effective_at <= NOW() AND total_fee>0
			StringBuilder sql = new StringBuilder("");

			sql.append("SELECT total_fee FROM ship_fee_rule");
			sql.append(" WHERE  expiry_at > NOW() AND effective_at <= NOW()");
			sql.append("AND total_fee> :p1");
			result = shipFeeRuleDao.executeBySql(sql.toString(), new Object[] { 0 });
			if (result.size() <= 0) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.get(0);

	}

}
