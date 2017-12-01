package pk.shoplus.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.data.Table;

import pk.shoplus.common.Helper;
import pk.shoplus.model.Page;
import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Id;

/**
 * 泛型数据库操作类
 * 
 * @author OC,YeGGo
 *
 * @param <T>
 *            实例类泛型
 */
public class EntityDao<T> {
	/**
	 * 已打开的数据库连接
	 */
	private final Connection conn;

	private Logger log = Logger.getLogger(EntityDao.class);

	/**
	 * 构造函数
	 * 
	 * @param conn
	 *            已打开的数据库连接
	 */
	public EntityDao(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Insert操作
	 * 
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public long create(T t) throws Exception {
		Class<?> klass = t.getClass();
		String tableName = DaoHelper.getTableName(klass);

		// 拼name和value
		StringBuilder fieldNames = new StringBuilder();
		List<Object> fieldValues = new ArrayList<Object>();
		StringBuilder placeholders = new StringBuilder();
		Field[] fields = klass.getDeclaredFields();
		int counter = 0;
		for (Field field : fields) {
			Object value = field.get(t);
			if (value != null && field.isAnnotationPresent(Column.class)) {
				fieldNames.append(field.getName()).append(",");
				fieldValues.add(value);
				placeholders.append(":p" + (++counter)).append(",");
			}
		}
		fieldNames.deleteCharAt(fieldNames.length() - 1);
		placeholders.deleteCharAt(placeholders.length() - 1);

		// 拼SQL
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(tableName).append(" (").append(fieldNames).append(") values (")
				.append(placeholders).append(")");

		Query query = this.createQuery(sql.toString(), fieldValues.toArray());

		Object key = query.executeUpdate().getKey();
		return key != null ? (long) key : 0;
	}

	/**
	 * 通过 batch 来批量 create - toTest by Caijl
	 * 
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public List<Long> createByBatch(List<T> tList) throws Exception {

		List<Long> resultList = new ArrayList<Long>();

		if (tList != null && tList.size() > 0) {
			Class<?> klass = tList.get(0).getClass();
			String tableName = DaoHelper.getTableName(klass);

			// 拼sql语句
			// 拼name和value
			StringBuilder sqlFieldNames = new StringBuilder();
			List<Object> sqlFieldValues = new ArrayList<Object>();
			StringBuilder sqlPlaceholders = new StringBuilder();
			Field[] sqlFields = klass.getDeclaredFields();
			int sqlCounter = 0;
			for (Field sqlField : sqlFields) {
				Object sqlValue = sqlField.get(tList.get(0));
				if (sqlValue != null && sqlField.isAnnotationPresent(Column.class)) {
					sqlFieldNames.append(sqlField.getName()).append(",");
					sqlFieldValues.add(sqlValue);
					sqlPlaceholders.append(":p" + (++sqlCounter)).append(",");
				}
			}
			sqlFieldNames.deleteCharAt(sqlFieldNames.length() - 1);
			sqlPlaceholders.deleteCharAt(sqlPlaceholders.length() - 1);
			// 拼SQL
			StringBuilder sql = new StringBuilder();
			sql.append("insert into ").append(tableName).append(" (").append(sqlFieldNames).append(") values (")
					.append(sqlPlaceholders).append(")");

			// 创建 query
			Query query = conn.createQuery(sql.toString());

			// 获取数据
			for (int i = 0; i < tList.size(); i++) {
				// 拼name和value
				List<Object> fieldValues = new ArrayList<Object>();
				Field[] fields = klass.getDeclaredFields();
				for (Field field : fields) {
					Object value = field.get(tList.get(i));
					if (value != null && field.isAnnotationPresent(Column.class)) {
						fieldValues.add(value);
					}
				}
				query.withParams(fieldValues.toArray()).addToBatch();
			}
			Object[] objectArray = query.executeBatch().getKeys();
			// System.out.println("objectArray after excute = " + objectArray);
			for (Object object : objectArray) {
				// System.out.println("for object = " + object);
				resultList.add(object != null ? (long) object : 0);
			}
		}
		return resultList;
	}

	/**
	 * 根据Id执行Update操作
	 * 
	 * @param t
	 * @throws Exception
	 */
	public void updateById(T t) throws Exception {
		// 整理id
		Class<?> klass = t.getClass();
		String idName = DaoHelper.getIdName(klass);

		StringBuilder sql = new StringBuilder();
		sql.append(" where ").append(idName).append("=:p1");

		log.info("updateById sql:" + sql.toString() + ",params:" + t.toString());

		this.updateBySql(t, sql.toString(), new Object[] { klass.getField(idName).get(t) });
	}

	public Object updateBySQL(String sql, Object[] fieldValues) throws Exception {
		Query query = this.createQuery(sql, fieldValues);

		Object key = query.executeUpdate().getKey();
		return key != null ? (long) key : 0;
	}


	/**
	 * 根据条件执行Update操作
	 * 
	 * @param t
	 * @throws Exception
	 */
	public void updateByCondition(T t, Map<String, Object> condition) throws Exception {
		// 拼SQL
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");
		log.info("updateByCondition sql:" + sql.toString() + ",params:" + params.toString());
		this.updateBySql(t, sql.toString(), params.toArray());
	}

	/**
	 * 自定义Update操作。例：updateBySql(user, " where username=:p1", new
	 * Object[]{"yeggo"});
	 * 
	 * @param t
	 * @param extensiveSql
	 * @param params
	 * @throws Exception
	 */
	public void updateBySql(T t, String extensiveSql, Object[] params) throws Exception {
		Class<?> klass = t.getClass();
		String tableName = DaoHelper.getTableName(klass);

		// 整理name和value
		List<String> fieldNames = new ArrayList<String>();
		List<Object> fieldValues = new ArrayList<Object>();
		Field[] fields = klass.getDeclaredFields();
		int counter = 0;
		for (Field field : fields) {
			Object value = field.get(t);
			if (value != null && field.isAnnotationPresent(Column.class)) {
				fieldNames.add(field.getName());
				fieldValues.add(value);
				counter++;
			}
		}

		// 拼SQL
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(tableName).append(" set ");
		for (int i = 0; i < counter; i++) {
			String name = fieldNames.get(i);
			sql.append(name).append('=').append(":" + name).append(",");
		}
		sql.deleteCharAt(sql.length() - 1);

		// 自定义条件
		sql.append(extensiveSql);

		// 输入参数
		Query query = this.createQuery(sql.toString(), params);
		for (int i = 0; i < counter; i++) {
			query.addParameter(fieldNames.get(i), fieldValues.get(i));
		}
		query.executeUpdate();
	}
	
	/**
	 * 根据 id 的list 做批量操作 - todo Caijl
	 * @param tList
	 * @throws Exception
	 */
	public void updateByBatch(List<T> tList) throws Exception{
		
	}

	/**
	 * 根据主键Id获取数据
	 * 
	 * @param klass
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public T getById(Class<T> klass, long id) throws Exception {
		String idName = DaoHelper.getIdName(klass);
		StringBuilder sql = new StringBuilder();
		sql.append(" where ").append(idName).append("=:p1");

		try {
			log.info("getById sql: " + sql.toString() + ",params: " + id);
			List<T> list = this.getBySql(klass, null, sql.toString(), new Object[] { id }, null, null);
			if (list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 按条件查询记录,分页
	 * 
	 * @param klass
	 * @param condition
	 * @param pagination
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public List<T> getByCondition(Class<T> klass, Map<String, Object> condition, Page pagination, String orderBy)
			throws Exception {
		// 拼SQL
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");

		try {
			return this.getBySql(klass, null, sql.toString(), params.toArray(), pagination, orderBy);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询数据 带 fieldname
	 * 
	 * @param klass
	 * @param fieldName
	 * @param condition
	 * @param pagination
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public List<T> getByCondition(Class<T> klass, String fieldName, Map<String, Object> condition, Page pagination,
			String orderBy) throws Exception {
		// 拼SQL
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");

		try {
			// log.info("getByCondition: " + sql.toString());
			return this.getBySql(klass, fieldName, sql.toString(), params.toArray(), pagination, orderBy);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 按条件查询
	 * 
	 * @param klass
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<T> getByCondition(Class<T> klass, Map<String, Object> condition) throws Exception {
		// 拼SQL
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");

		// log.info("getByCondition: " + sql.toString());
		return this.getBySql(klass, null, sql.toString(), params.toArray());
	}

	/**
	 * 按条件查询 带fieldNames
	 * 
	 * @param klass
	 * @param fieldNames
	 * @param condition
	 * @return
	 * @throws Exceptioin
	 */
	public List<T> getByCondition(Class<T> klass, String fieldNames, Map<String, Object> condition) throws Exception {
		// 拼SQL
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");
		// log.info("getByCondition: " + sql.toString());
		return this.getBySql(klass, fieldNames, sql.toString(), params.toArray());
	}

	/**
	 * 按用SQL查询
	 * 
	 * @param klass
	 * @param fieldNames
	 * @param extensiveSql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<T> getBySql(Class<T> klass, String fieldNames, String extensiveSql, Object[] params) throws Exception {
		String tableName = DaoHelper.getTableName(klass);
		StringBuilder sql = new StringBuilder();
		if (fieldNames == null || fieldNames.equals("*")) {

			// 返回全部列
			StringBuilder fieldNamesSB = new StringBuilder();
			Field[] fields = klass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Column.class)) {
					fieldNamesSB.append(field.getName()).append(",");
				}
			}
			fieldNamesSB.deleteCharAt(fieldNamesSB.length() - 1);
			fieldNames = fieldNamesSB.toString();

		}
		sql.append("select ").append(fieldNames).append(" from ").append(tableName).append(extensiveSql);

		Query query = this.createQuery(sql.toString(), params);

		log.info("getBySql: " + query.toString());

		return query.executeAndFetch(klass);
	}

	/**
	 * 根据SQL获取数据。例： getBySql(User.class, "email,password", " where username=:p1"
	 * , new Object[]{"yeggo"});分页
	 * 
	 * @param klass
	 * @param fieldNames
	 * @param extensiveSql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<T> getBySql(Class<T> klass, String fieldNames, String extensiveSql, Object[] params, Page pagination,
			String orderBy) throws Exception {
		String tableName = DaoHelper.getTableName(klass);
		StringBuilder sql = new StringBuilder();

		if (fieldNames == null) {

			// 返回全部列
			StringBuilder fieldNamesSB = new StringBuilder();
			Field[] fields = klass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Column.class)) {
					fieldNamesSB.append(field.getName()).append(",");
				}
			}
			fieldNamesSB.deleteCharAt(fieldNamesSB.length() - 1);
			fieldNames = fieldNamesSB.toString();
		}

		sql.append("select ").append(fieldNames).append(" from ").append(tableName).append(extensiveSql);

		if (orderBy != null) {
			sql.append(" order by ").append(orderBy);
		}

		// 连接分页和排序
		if (pagination != null) {
			long offset = pagination.pageSize * (pagination.pageNumber - 1);
			sql.append(" limit ").append(offset).append(", ").append(pagination.pageSize);
		}

		Query query = this.createQuery(sql.toString(), params);

		log.info("getBySql : " + query.toString());
		List<T> list = query.executeAndFetch(klass);

		return list;

		/*
		 * if (list.isEmpty()) { throw new Exception(); } else { return list; }
		 */
	}

	/**
	 * 根据Id删除数据
	 * 
	 * @param klass
	 * @param id
	 * @throws Exception
	 */
	public void deleteById(Class<T> klass, long id) throws Exception {
		String idName = DaoHelper.getIdName(klass);
		StringBuilder sql = new StringBuilder();
		sql.append(" where ").append(idName).append("=:p1");
		log.info("deleteById:" + sql.toString());
		this.deleteBySql(klass, sql.toString(), new Object[] { id });
	}

	/**
	 * 根据条件删除数据
	 * 
	 * @param klass
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public void deleteByCondition(Class<T> klass, Map<String, Object> condition) throws Exception {
		// 拼SQL
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");
		log.info("deleteByCondition:" + sql.toString());
		this.deleteBySql(klass, sql.toString(), params.toArray());
	}

	/**
	 * 根据条件删除数据。例： deleteBySql(User.class, " where username=:p1", new
	 * Object[]{"yeggo"});
	 * 
	 * @param klass
	 * @param extensiveSql
	 * @param params
	 * @throws Exception
	 */
	public void deleteBySql(Class<T> klass, String extensiveSql, Object[] params) throws Exception {
		String tableName = DaoHelper.getTableName(klass);

		// 拼Sql
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(tableName).append(extensiveSql);
		log.info("deleteBySql:" + sql.toString());
		Query query = this.createQuery(sql.toString(), params);
		query.executeUpdate();
	}

	/**
	 * 根据条件统计数据
	 * 
	 * @param klass
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public long countByCondition(Class<T> klass, Map<String, Object> condition) throws Exception {
		// 拼SQL
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");

		log.info("condition sql = " + sql.toString());
		return this.countBySql(klass, sql.toString(), params.toArray());
	}

	/**
	 * 根据条件计数。例： countBySql(User.class, " where username=:p1", new
	 * Object[]{"yeggo"});
	 * 
	 * @param klass
	 * @param extensiveSql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public long countBySql(Class<T> klass, String extensiveSql, Object[] params) throws Exception {
		String tableName = DaoHelper.getTableName(klass);

		// 拼Sql
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from ").append(tableName).append(extensiveSql);
		log.info("count sql=" + sql.toString());
		Query query = this.createQuery(sql.toString(), params);
		return query.executeScalar(Long.class);
	}

	/**
	 * 根据condition 计算 sum值 如 select
	 * sum(store),sum(remind),sum(ordered),sum(confirm),sum(ship) from sku_store
	 * WHERE product_id = 1 and enabled = 1
	 * 
	 * @param fieldNames，填需要sum的值，如"store,remind,ordered,confirm,ship"
	 * @param tableNames
	 * @param condition
	 * @return map，返回<filedName, sum结果>
	 * @throws Exception
	 */
	public Map<String, Object> sumByCondition(String fieldNames, String tableNames, Map<String, Object> condition)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();
		if (fieldNames == null) {
			throw new Exception();
		}
		String sumStr = "";
		String[] fieldArray = fieldNames.split(",");
		for (String item : fieldArray) {
			sumStr = sumStr + "sum(" + item + "),";
		}
		sql.append("select ").append(sumStr.substring(0, sumStr.length() - 1)).append(" from ").append(tableNames);
		// List<Object> params = appendCondition(sql, condition);
		List<Object> params = DaoHelper.appendCondition(sql, condition, "");

		log.info("sumByCondition sql = " + sql);
		Query query = this.createQuery(sql.toString(), params.toArray());
		Table t = query.executeAndFetchTable();
		for (int j = 0; j < t.columns().size(); j++) {
			String columnStr = t.columns().get(j).getName();
			result.put(columnStr.substring(4, columnStr.length() - 1), t.rows().get(0).getObject(j));
		}
		return result;
	}

	/**
	 * 执行指定语句，可用于联表查询等复杂语句。
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> executeBySql(String sql, Object[] params) {
		Query query = this.createQuery(sql.toString(), params);
		Table t = query.executeAndFetchTable();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < t.rows().size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int j = 0; j < t.columns().size(); j++) {
				map.put(t.columns().get(j).getName(), t.rows().get(i).getObject(j));
			}
			list.add(map);
		}
		return list;
	}

	/**
	 * 批量执行操作
	 * 
	 * @param sql
	 * @param params
	 */
	public void batchExecuteBySql(String sql, List<Object[]> params) {
		Query query = this.conn.createQuery(sql);
		for (Object[] param : params) {
			if (param != null) {
				query.withParams(param);
			}
			query.addToBatch();
		}
		query.executeBatch();
	}

	/**
	 * 分页方法 wherecondition用map，再转成string
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param fieldNames
	 * @param tableName
	 * @param whereCondition
	 * @param orderBy
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Page getPageBySql(long pageNumber, long pageSize, String fieldNames, String tableName,
			Map<String, Object> whereConditionMap, String orderBy, String countCondition, String groupBy)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		// List<Object> params = appendCondition(sql, whereConditionMap);
		List<Object> params = DaoHelper.appendCondition(sql, whereConditionMap, "");
		return getPageBySql(pageNumber, pageSize, fieldNames, tableName, sql.toString().substring(7), orderBy,
				params.toArray(), countCondition, groupBy);
	}

	/**
	 * 分页方法
	 * 
	 * @param pageNumber
	 *            查询页码
	 * @param pageSize
	 *            页面记录数,如果小于1就返回全部数据
	 * @param fieldNames
	 *            字段名(eg. "id,username,password")
	 * @param tableName
	 *            表名(eg. "user inner join role on user.id = role.id")
	 * @param whereCondition
	 *            where查询条件(eg.
	 *            "id=:p1 and date<>:p2 or name=:p3 group by id having id > 2" )
	 * @param orderBy
	 *            orderBy(eg. "id")
	 * @param paras
	 * @return PageNav对象
	 * @throws Exception
	 */
	public Page getPageBySql(long pageNumber, long pageSize, String fieldNames, String tableName, String whereCondition,
			String orderBy, Object[] params, String countCondition, String groupBy) throws Exception {
		// tableName could not be null!!
		if (Helper.isNullOrEmpty(tableName)) {
			return new Page();
			// throw new Exception("tableName could not be null!!");
		}

		// sqlExceptSelect
		StringBuilder sqlExceptSelect = new StringBuilder(" from ");
		sqlExceptSelect.append(tableName);
		if (!Helper.isNullOrEmpty(whereCondition)) {
			sqlExceptSelect.append(" where ").append(whereCondition);
		}

		// 先计数
		if (Helper.isNullOrEmpty(countCondition)) {
			countCondition = "*";
		} else {
			countCondition = "distinct " + countCondition;
		}
		StringBuilder countSql = new StringBuilder("select count(").append(countCondition).append(")");
		countSql.append(sqlExceptSelect);
		log.info("count sql: " + countSql.toString());
		Long totalRow = this.createQuery(countSql.toString(), params).executeScalar(Long.class);
		if (totalRow == null || totalRow <= 0) {
			return new Page(new ArrayList<>(), pageNumber, pageSize, 1l, 0l);
		}
		// group by
		if (!Helper.isNullOrEmpty(groupBy)) {
			sqlExceptSelect.append(" group by ").append(groupBy);
		}

		// order by
		if (!Helper.isNullOrEmpty(orderBy)) {
			sqlExceptSelect.append(" order by ").append(orderBy);
		}

		// sql
		StringBuilder sql = new StringBuilder("select ");
		if (Helper.isNullOrEmpty(fieldNames)) {
			fieldNames = "*";
		}
		sql.append(fieldNames).append(sqlExceptSelect);

		// pageSize或者pageNumber
		// 如果小于1就返回全部数据
		if (pageSize < 1) {
			return new Page(this.executeBySql(sql.toString(), params), pageNumber, pageSize, 1L, totalRow);
		}

		if (pageNumber < 1) {
			pageNumber = 1;
			// throw new Exception("pageNumber must be more than 1!!");
		}

		// 计数
		long totalPage = totalRow / pageSize;
		if (totalRow % pageSize != 0) {
			totalPage++;
		}
		if (pageNumber > totalPage) {
			pageNumber = totalPage;
			// throw new Exception("pageNumber must be less than totalPage!!");
		}

		// 拼MySQL分页
		long offset = pageSize * (pageNumber - 1);
		sql.append(" limit ").append(offset).append(",").append(pageSize);
		log.info("excutesql:" + sql.toString());

		// executeSql
		return new Page(this.executeBySql(sql.toString(), params), pageNumber, pageSize, totalPage, totalRow);
	}

	/**
	 * 根据id执行Update操作 //UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
	 * 
	 * @param t
	 * @throws Exception
	 */
	public void updateByIdWithCondition(T t, Map<String, Object> condition) throws Exception {
		// 整理id
		Class<?> klass = t.getClass();
		String idName = DaoHelper.getIdName(klass);

		StringBuilder sql = new StringBuilder();

		List<Object> params = DaoHelper.appendUpdateValueCondition(sql, condition, "");
		params.add(klass.getField(idName).get(t));
		sql.append(" where ").append(idName).append("=:p" + params.size());
		log.info("updateById sql = " + params.toString());
		this.updateBySql(t, sql.toString(), params.toArray());
	}

	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Query createQuery(String sql, Object[] params) {
		if (params != null) {
			return this.conn.createQueryWithParams(sql, params);
		} else {
			return this.conn.createQuery(sql);
		}
	}

	/**
	 * 获取实例类对应的表名
	 * 
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	/*
	 * static private String getTableName(Class<?> klass) throws Exception { if
	 * (klass.isAnnotationPresent(Entity.class)) { Entity entity =
	 * klass.getAnnotation(Entity.class); return entity.value(); }
	 * 
	 * throw new Exception(klass.getName() + " is not an Entity Annotation."); }
	 */
	/**
	 * 获取实例类的id列名
	 * 
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	/*
	 * static private String getIdName(Class<?> klass) throws Exception {
	 * Field[] fields = klass.getDeclaredFields(); for (Field field : fields) {
	 * if (field.isAnnotationPresent(Id.class)) { return field.getName(); } }
	 * 
	 * throw new Exception("id property not found in " + klass.getName() + ".");
	 * }
	 */
	/**
	 * 添加条件
	 * 
	 * @param sql
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	/*
	 * static private List<Object> appendCondition(StringBuilder sql,
	 * Map<String, Object> condition) throws Exception { if (condition == null
	 * || condition.isEmpty()) { throw new Exception("Condition cannot be null!"
	 * ); }
	 * 
	 * sql.append(" where "); List<Object> params = new ArrayList<Object>(); int
	 * counter = 0; for (Entry<String, Object> entry : condition.entrySet()) {
	 * sql.append(entry.getKey()).append("=").append(":p" + (++counter)).append(
	 * " and "); params.add(entry.getValue()); } sql.delete(sql.lastIndexOf(
	 * " and "), sql.length()); return params; }
	 */
}