
package pk.shoplus.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * @author author :
 * @date create_at : 2016年11月2日 下午4:49:26
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class DaoHelper {
	
	/**
	 * 通过 condition 转换成 wherestr
	 * @param sql
	 * @param condition
	 * @param whereTableField 用于作为表名 类似 t.* 的 "t."
	 * @return
	 * @throws Exception
	 */
	static public List<Object> appendUpdateValueCondition(StringBuilder sql, Map<String, Object> condition, String setTableField)
			throws Exception {
		if (condition == null || condition.isEmpty()) {
			throw new Exception("Condition cannot be null!");
		}

		if (!Helper.isNullOrEmpty(setTableField)) {
			setTableField = setTableField + ".";
		}

		sql.append(" ");
		List<Object> params = new ArrayList<Object>();
		int counter = 0;
		for (Entry<String, Object> entry : condition.entrySet()) {
			sql.append(setTableField + entry.getKey()).append("=").append(":p" + (++counter)).append(" , ");
			params.add(entry.getValue());
		}
		sql.delete(sql.lastIndexOf(" , "), sql.length());
		return params;
	}
	/**
	 * 通过 condition 转换成 wherestr
	 * 
	 * @param sql
	 * @param condition
	 * @param whereTableField
	 *            用于作为表名 类似 t.* 的 "t."
	 * @return
	 * @throws Exception
	 */
	static public List<Object> appendCondition(StringBuilder sql, Map<String, Object> condition, String whereTableField)
			throws Exception {
		if (condition == null || condition.isEmpty()) {
			throw new Exception("Condition cannot be null!");
		}

		if (!Helper.isNullOrEmpty(whereTableField)) {
			whereTableField = whereTableField + ".";
		}

		sql.append(" where ");
		List<Object> params = new ArrayList<Object>();
		int counter = 0;
		for (Entry<String, Object> entry : condition.entrySet()) {
			sql.append(whereTableField + entry.getKey()).append("=").append(":p" + (++counter)).append(" and ");
			params.add(entry.getValue());
		}
		sql.delete(sql.lastIndexOf(" and "), sql.length());
		return params;
	}

	/**
	 * 获取实例类的id列名
	 * 
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	static public String getIdName(Class<?> klass) throws Exception {
		Field[] fields = klass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				return field.getName();
			}
		}

		throw new Exception("id property not found in " + klass.getName() + ".");
	}

	/**
	 * 获取实例类对应的表名
	 * 
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	static public String getTableName(Class<?> klass) throws Exception {
		if (klass.isAnnotationPresent(Entity.class)) {
			Entity entity = klass.getAnnotation(Entity.class);
			return entity.value();
		}

		throw new Exception(klass.getName() + " is not an Entity Annotation.");
	}

	public static <T> long count(Connection conn, Class<T> klass, Map<String, Object> params) throws Exception {
		EntityDao<T> dao = new EntityDao<T>(conn);

		return dao.countByCondition(klass, params);
	}

	public static <T> long count(Connection conn, Class<T> klass, String[] fields, Object[] params) throws Exception {
		if (fields.length != params.length) {
			throw new IllegalArgumentException();
		}

		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < fields.length; ++i) {
			map.put(fields[i], params[i]);
		}

		return count(conn, klass, map);
	}
}
