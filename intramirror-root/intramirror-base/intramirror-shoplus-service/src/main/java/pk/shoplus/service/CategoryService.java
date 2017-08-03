package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Category;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

import java.util.*;

public class CategoryService {

    public static int MAX_CATEGORY_LEVEL = 4;

    /**
     * 获取数据库连接
     */
    private EntityDao<Category> categoryDao = null;

    /**
     * @param conn
     */
    public CategoryService(Connection conn) {
        categoryDao = new EntityDao<Category>(conn);
    }

    /**
     * 创建类别
     *
     * @param category
     * @return 实体/null
     * @throws Exception
     */
    public Category createCategory(Category category) throws Exception {
        try {
            long categoryId = categoryDao.create(category);
            if (categoryId > 0) {
                category.category_id = categoryId;
            } else {
                category = null;
            }
            return category;
        } catch (Exception e) {
            throw e;
        }
    }


    public List<Map<String, Object>> executeSQL(String sql) throws Exception {
        try {
            return categoryDao.executeBySql(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void updateBySQL(String sql) throws Exception {
        try {
             categoryDao.updateBySQL(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * @param category
     * @throws Exception
     */
    public void updateCategory(Category category) throws Exception {
        try {
            categoryDao.updateById(category);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过 category_id 获取 category
     *
     * @param category_id
     * @return
     * @throws Exception
     */
    public Category getCategoryById(Long category_id) throws Exception {
        try {
            String fieldName = "*";
            Category category = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            // 添加条件
            conditionMap.put("category_id", category_id);
            conditionMap.put("enabled", EnabledType.USED);

            // 查询
            List<Category> categoryList = categoryDao.getByCondition(Category.class, fieldName, conditionMap);

            for (Category temp : categoryList) {
                if (temp != null) {
                    category = new Category();
                    category = temp;
                    break;
                }
            }

            return category;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过条件获取categoryList
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public List<Category> getCategoryListByCondition(Map<String, Object> condition) throws Exception {
        try {
            // 查询
            String fieldName = "*";
            List<Category> categoryList = categoryDao.getByCondition(Category.class, fieldName, condition);
            return categoryList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param pageNumber
     * @param pageSize
     * @param whereCondition
     * @param orderBy
     * @param depth
     * @return
     * @throws Exception
     */
    public Page getCategoryPageList(long pageNumber, long pageSize, Map<String, Object> whereCondition,
                                    String fieldNames, String orderBy, int fromLevel, int depth) throws Exception {
        try {
            // 1. condition转换成 wherestr
            StringBuilder whereSql = new StringBuilder("");
            List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

            // 先计数
            // count sql: select count(*) from category where parent_id=:p1 and
            // enabled=:p2

            StringBuilder countSql = new StringBuilder("select count(*) from category a");
            countSql.append(whereSql);
            // System.out.println("count sql: " + countSql.toString());

            // 1. 获取 总数
            Long totalRow = categoryDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);
            if (totalRow == null || totalRow <= 0) {
                return new Page(null, pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            // excutesql:select * from category where parent_id=:p1 and
            // enabled=:p2 order by category_id limit 0,10
            StringBuilder sql = new StringBuilder("");
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }
            sql.append("select distinct c." + fieldNames + " from category a ");
            sql.append("inner join category b on a.category_id = b.parent_id or a.category_id = b.category_id ");
            sql.append("inner join category c on b.category_id = c.parent_id or b.category_id = c.category_id ");
            sql.append(whereSql);

            // order by
            if (!Helper.isNullOrEmpty(orderBy)) {
                sql.append(" order by ").append(orderBy);
            }

            // pageSize或者pageNumber
            // 如果小于1就返回全部数据
            if (pageSize < 1) {
                return new Page();
                // return new Page(categoryDao.executeBySql(sql.toString(),
                // params.toArray()), pageNumber, pageSize, 1l, totalRow);
            }

            if (pageNumber < 1) {
                return new Page();
                // throw new Exception("pageNumber must be more than 1!!");
            }

            // 计数
            long totalPage = totalRow / pageSize;
            if (totalRow % pageSize != 0) {
                totalPage++;
            }
            if (pageNumber > totalPage) {
                return new Page();
                // throw new Exception("pageNumber must be less than
                // totalPage!!");
            }

            // 拼MySQL分页
            long offset = pageSize * (pageNumber - 1);
            sql.append(" limit ").append(offset).append(",").append(pageSize);
            System.out.println("excutesql:" + sql.toString());

            // 返回的result_list
            List<Category> resultList = new ArrayList<Category>();

            // executeSql
            List<Map<String, Object>> categoryMapList = categoryDao.executeBySql(sql.toString(), params.toArray());
            // 存储临时变量
            List<Map<String, Object>> categoryMapTempList = new ArrayList<Map<String, Object>>();

            // 首先获取第一个list的数据
            for (int i = 0; i < categoryMapList.size(); i++) {
                int level = (int) categoryMapList.get(i).get("level");
                if (level == fromLevel) {
                    Category category = convertMapToCategory(categoryMapList.get(i));
                    resultList.add(category);
                } else {
                    categoryMapTempList.add(categoryMapList.get(i));
                }
            }

            for (int i = 0; i < resultList.size(); i++) {
                resultList.get(i).children = getCategoryAndAllSubcategory(resultList.get(i), categoryMapTempList,
                        depth);
            }

            List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("categoryList", resultList);
            resultMapList.add(map);

            return new Page(resultMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取分类信息 － 包括全部子分类
     *
     * @param condition
     * @param depth
     * @return
     * @throws Exception
     */
    public List<Category> getCategoryAndAllSubcategory(Map<String, Object> condition, int depth) throws Exception {
        try {
            String filedName = "*";
            List<Category> categoryList = categoryDao.getByCondition(Category.class, filedName, condition);

            // 找到子分类
            for (int i = 0; i < categoryList.size(); i++) {
                if (depth > 0) {
                    // 根据父分类获取
                    condition.remove("category_id", "");
                    condition.put("parent_id", categoryList.get(i).category_id);
                    categoryList.get(i).children = getCategoryAndAllSubcategory(condition, depth - 1);
                }
            }
            return categoryList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取分类信息 － 包括全部子分类
     *
     * @param condition
     * @param depth
     * @return
     * @throws Exception
     */
    public List<Category> getCategoryAndAllSubcategory2(Map<String, Object> condition, int depth) throws Exception {
        try {
            String filedName = "*";
            List<Category> categoryList = categoryDao.getByCondition(Category.class, filedName, condition);

            // 找到子分类
            for (int i = 0; i < categoryList.size(); i++) {
                if (depth > 0) {
                    // 根据父分类获取
                    condition.remove("level");
                    condition.put("parent_id", categoryList.get(i).category_id);
                    categoryList.get(i).children = getCategoryAndAllSubcategory(condition, depth - 1);
                }
            }
            return categoryList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @return
     * @throws Exception
     */
    public String getShowCodeByParentId(String parentId) throws Exception {
        // SELECT max( substring(a.show_code, 1, a.`level` * 3 + 1)) + 1 as
        // show_code_result FROM `category` a where a.parent_id = 1;
        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT  CONCAT( max( substring(a.show_code, 1, a.`level` * 3 + 1)) + 1, '') as show_code_result FROM `category` a ");
        sql.append("where a.parent_id =").append(parentId);
        // System.out.println("sql = "+ sql);
        List<Map<String, Object>> categoryMapResult = categoryDao.executeBySql(sql.toString(), new Object[0]);

        if (categoryMapResult.size() > 0) {
            // System.out.println("categoryMapResult.size()" +
            // categoryMapResult.size());
            // System.out.println("categoryMapResult.get(0)" +
            // categoryMapResult.get(0));
            if (categoryMapResult.get(0).get("show_code_result") != null) {
                // 1000000000000
                String str = (String) categoryMapResult.get(0).get("show_code_result");
                str = str + "0000000000000";
                str = str.substring(0, 13);
                return str;
            } else {
                Category category = getCategoryById(Long.valueOf(parentId));
                if (Helper.checkNotNull(category)) {// System.out.println("category
                    // show_code = " +
                    // category.show_code);
                    String str = category.show_code.substring(0, category.level * 3 + 1);
                    str = str + "001";
                    // System.out.println("str = " + str);
                    str = str + "0000000000000";
                    str = str.substring(0, 13);
                    return str;
                } else {
                    return null;
                }

            }
        }

        return "";
    }

    /**
     * 使用sql 查询全部 category
     *
     * @param condition
     * @param depth
     * @return
     * @throws Exception SELECT DISTINCT c.* FROM `category` a INNER JOIN category b
     *                   on a.category_id = b.parent_id or a.category_id =
     *                   b.category_id INNER JOIN category c on b.category_id =
     *                   c.parent_id or b.category_id = c.category_id WHERE
     *                   a.parent_id = -1;
     */
    public List<Category> getCategoryListIncludeSub(Map<String, Object> condition, int fromLevel, int depth,
                                                    String fieldNames) throws Exception {
        try {
            List<Category> resultList = new ArrayList<Category>();
            StringBuilder sql = new StringBuilder();
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }

            sql.append("select distinct d." + fieldNames + " from category a ");
            sql.append(
                    "inner join category b on (a.category_id = b.parent_id or a.category_id = b.category_id) and b.enabled = 1 ");
            sql.append(
                    "inner join category c on (b.category_id = c.parent_id or b.category_id = c.category_id) and c.enabled = 1 ");
            sql.append(
                    "inner join category d on (c.category_id = d.parent_id or c.category_id = d.category_id) and d.enabled = 1 ");

			/*
             * sql.append("select distinct c." + fieldNames +
			 * " from category a "); sql.append(
			 * "inner join category b on a.category_id = b.parent_id or a.category_id = b.category_id "
			 * ); sql.append(
			 * "inner join category c on b.category_id = c.parent_id or b.category_id = c.category_id "
			 * );
			 */
            List<Object> params = DaoHelper.appendCondition(sql, condition, "a");

            System.out.println("sql = " + sql.toString());

            List<Map<String, Object>> categoryMapList = categoryDao.executeBySql(sql.toString(), params.toArray());

            // 存储临时变量
            List<Map<String, Object>> categoryMapTempList = new ArrayList<Map<String, Object>>();
            // 首先获取第一个list的数据
            for (int i = 0; i < categoryMapList.size(); i++) {
                int level = (int) categoryMapList.get(i).get("level");
                if (level == fromLevel) {
                    Category category = convertMapToCategory(categoryMapList.get(i));
                    resultList.add(category);
                } else {
                    categoryMapTempList.add(categoryMapList.get(i));
                }
            }

            for (int i = 0; i < resultList.size(); i++) {
                resultList.get(i).children = getCategoryAndAllSubcategory(resultList.get(i), categoryMapTempList,
                        depth);
            }

            return resultList;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 获取全部子category
     *
     * @param categoryinput
     * @param categoryMapList
     * @param depth
     * @return
     * @throws Exception
     */
    public List<Category> getCategoryAndAllSubcategory(Category categoryinput,
                                                       List<Map<String, Object>> categoryMapList, int depth) throws Exception {
        List<Category> categoryList = new ArrayList<Category>();
        for (int j = 0; j < categoryMapList.size(); j++) {
            long parent_id = (Long) categoryMapList.get(j).get("parent_id");
            int level = (int) categoryMapList.get(j).get("level");

            if (categoryinput.category_id == parent_id && (categoryinput.level + 1) == level) {
                Category category = convertMapToCategory(categoryMapList.get(j));
                if (depth > 0) {
                    category.children = getCategoryAndAllSubcategory(category, categoryMapList, depth - 1);
                }
                categoryList.add(category);
            }
        }

        return categoryList;
    }

    public List<Map<String, Object>> getCategoryByShopCategory(Long shop_id, Integer type)
            throws Exception {
        try {
            String sql = "SELECT c.category_id,c.`name` "
                    + " FROM shop_category sc LEFT JOIN category c ON sc.category_id = c.category_id "
                    + " LEFT JOIN shop s ON sc.shop_id = s.shop_id "
                    + " WHERE s.shop_id=:p1 AND sc.`status`=:p2 AND c.enabled=:p3 ";
            return categoryDao.executeBySql(sql, new Object[]{shop_id, type, EnabledType.USED});
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 获取所有三级目录的父级目录
     */
    public List<Map<String, Object>> getCategoryList()
            throws Exception {
        try {
            String sql = "SELECT c1.category_id,c3.name as name1,c2.name as name2,c1.name as name3  "
                    + " FROM category c1 "
                    + " INNER JOIN category c2 ON c2.category_id = c1.parent_id "
                    + " INNER JOIN category c3 ON c3.category_id = c2.parent_id "
                    + " WHERE c1.`level` = 3 ";
            return categoryDao.executeBySql(sql, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 通过map 转换成 category
     *
     * @param map
     * @return
     */
    public Category convertMapToCategory(Map<String, Object> map) {
        Category category = new Category();
        category.category_id = (Long) map.get("category_id");
        category.show_code = (String) map.get("show_code");
        category.show_code_int = (Long) map.get("show_code_int");
        category.name = (String) map.get("name");
        category.chinese_name = (String) map.get("chinese_name");
        category.parent_id = (Long) map.get("parent_id");
        category.level = (Integer) map.get("level");
        category.sort_order = (Integer) map.get("sort_order");
        category.remark = (String) map.get("remark");
        category.created_at = (Date) map.get("created_at");
        category.updated_at = (Date) map.get("updated_at");
        category.enabled = (Boolean) map.get("enabled");

        return category;

    }

    /**
     * 获得category下的所有有效子节点
     *
     * @param categories
     * @return
     */

    List<Category> categoryList = new ArrayList<Category>();

    public List<Category> getAllSubset(List<Category> categories) {
        try {
            // 初始化
            if (categories.size() == 1 && categories.get(0).getLevel() == 1) {
                categoryList = new ArrayList<Category>();
            }
            // 遍历
            for (Category category : categories) {
                // 查询这个category是不是其他的父级
                Map<String, Object> condition = new HashMap<String, Object>();
                condition.put("enabled", EnabledType.USED);
                condition.put("parent_id", category.category_id);
                List<Category> list = getCategoryListByCondition(condition);
                // 如果是，得到这个子集，继续遍历查询这个category是不是其他的父级
                // 这里有个问题，如果数据库中存在categoryid=1，parentid=2
                // categoryid=2，parentid=1，
                // 这个递归就无法结束
                if (list.size() > 0) {
                    getAllSubset(list);
                } else {
                    // 如果不是，将该category添加到集合中
                    categoryList.add(category);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryList;
    }

    public List<Category> getAllSubset(List<Category> categories, boolean attachParent) {
        try {
            // 初始化
            if (categories.size() == 1 && categories.get(0).getLevel() == 1) {
                categoryList = new ArrayList<Category>();
            }
            // 遍历
            for (Category category : categories) {
                // 查询这个category是不是其他的父级
                Map<String, Object> condition = new HashMap<String, Object>();
                condition.put("enabled", EnabledType.USED);
                condition.put("parent_id", category.category_id);
                List<Category> list = getCategoryListByCondition(condition);
                if (attachParent) {
                    for (Category c : list) {
                        c.name = category.name + "=>" + c.name;
                    }
                }

                // 如果是，得到这个子集，继续遍历查询这个category是不是其他的父级
                // 这里有个问题，如果数据库中存在categoryid=1，parentid=2
                // categoryid=2，parentid=1，
                // 这个递归就无法结束
                if (list.size() > 0) {
                    getAllSubset(list, attachParent);
                } else {
                    // 如果不是，将该category添加到集合中
                    categoryList.add(category);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryList;
    }

    //查询这个category是否有子叶
    public boolean isLastNode(Long id) throws Exception {
        try {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("enabled", EnabledType.USED);
            condition.put("parent_id", id);
            List<Category> list = getCategoryListByCondition(condition);
            if (list.size() > 0) {
                return false;
            }
        } catch (Exception e) {
            throw e;
        }

        return true;
    }


    /**
     * List<Map<String,Object>>
     *
     * @param vendor_application_id
     * @return
     */

    public List<Map<String, Object>> getCategoriesByVendorApplicationId(Long vendor_application_id) {
        String sql =
                "SELECT * FROM category " +
                        "WHERE enabled = 1 AND category_id IN (" +
                        "SELECT DISTINCT(category_id) FROM vendor_category " +
                        "WHERE enabled = 1 AND vendor_application_id = :p1" +
                        ") ORDER BY category_id ASC;";
        Object[] params = {vendor_application_id};
        return categoryDao.executeBySql(sql, params);
    }


    public List<Map<String, Object>> getAllCategory() {
        String sql = "select * from category where enabled = 1";
        return categoryDao.executeBySql(sql, null);
    }

    /**
     * 使用sql查询vendor下所有符合条件的category
     */
    public List<Category> getVendorCategorySub(Long vendorId, boolean enabled) throws Exception {
        try {
            List<Category> resultList = new ArrayList<Category>();
            String sql = "select distinct f.* from vendor_category vc \n" +
                    "left join category c on c.category_id = vc.category_id and c.enabled = 1\n" +
                    "inner join category d on (d.parent_id = c.category_id or d.category_id = c.category_id) and d.enabled = 1\n" +
                    "inner join category e on (e.parent_id = d.category_id or e.category_id = d.category_id) and e.enabled = 1\n" +
                    "inner join category f on (f.parent_id = e.category_id or f.category_id = e.category_id) and f.enabled = 1\n" +
                    "where vc.vendor_id = :p1 and vc.enabled = :p2";
            Object[] params = {vendorId, enabled};
            List<Map<String, Object>> categoryMapList = categoryDao.executeBySql(sql, params);

            List<Map<String, Object>> categoryMapTempList = new ArrayList<Map<String, Object>>();
            // 首先获取第一个list的数据
            for (int i = 0; i < categoryMapList.size(); i++) {
                int level = (int) categoryMapList.get(i).get("level");
                if (level == 1) {
                    Category category = convertMapToCategory(categoryMapList.get(i));
                    resultList.add(category);
                } else {
                    categoryMapTempList.add(categoryMapList.get(i));
                }
            }

            for (int i = 0; i < resultList.size(); i++) {
                resultList.get(i).children = getCategoryAndAllSubcategory(resultList.get(i), categoryMapTempList,
                        CategoryService.MAX_CATEGORY_LEVEL);
            }

            return resultList;
        } catch (Exception e) {
            throw e;
        }

    }

    public List<Map<String, Object>> getCategoryByCategoryNameAndParentId(Long parentId, String categoryName) throws Exception {
        StringBuffer sql = new StringBuffer("select * from category c where c.`name` = :p1 and c.enabled = 1 ");
        Category category = null;
        Object[] params = new Object[]{categoryName};
        if (null == parentId) {
            parentId = -1l;
        }
        params = new Object[]{categoryName, parentId};
        sql.append(" and c.parent_id = :p2 ");

        List<Map<String, Object>> categoryMapList = categoryDao.executeBySql(sql.toString(), params);

        return categoryMapList;
    }

//    /**
//     * List<Map<String, Object>>
//     *
//     * @param category_id
//     * @return
//     */
//
//    public List<Map<String, Object>> getCategoryByLevel(Long category_id) {
//        String sql =
//                "SELECT c1.category_id as c1_id,c1.name as c1_name,c2.category_id as c2_id ,c2.name as c2_name ,c3.category_id as c3_id ,c3.name as c3_name FROM category c1 " +
//                        "INNER JOIN category c2 ON c2.category_id = c1.parent_id " +
//                        "INNER JOIN category c3 ON c3.category_id = c2.parent_id " +
//                        "WHERE c1.category_id = :p1 ;";
//        Object[] params = {category_id};
//        return categoryDao.executeBySql(sql, params);
//    }

}
