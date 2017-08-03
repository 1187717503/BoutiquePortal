package pk.shoplus.service.request;

import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.ProductGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductGroupService {

    /**
     * 获取数据库连接
     */
    private EntityDao<ProductGroup> productGroupDao = null;

    /**
     * @param conn
     */
    public ProductGroupService(Connection conn) {
        productGroupDao = new EntityDao<ProductGroup>(conn);
    }

    /**
     * 创建 ProductGroup 数据
     *
     * @param productGroup
     * @return
     * @throws Exception
     */
    public ProductGroup createProductGroup(ProductGroup productGroup) throws Exception {
        try {
            Long product_group_id = productGroupDao.create(productGroup);
            if (product_group_id > 0) {
                productGroup.product_group_id = product_group_id;
            } else {
                productGroup = null;
            }
            return productGroup;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改 ProductGroup 信息
     *
     * @param productGroup
     * @throws Exception
     */
    public void updateProductGroup(ProductGroup productGroup) throws Exception {
        try {
            productGroupDao.updateById(productGroup);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * @return
     * @throws Exception
     */
    public ProductGroup getProductGroupById(long id) throws Exception {
        try {
            ProductGroup productGroup = this.productGroupDao.getById(ProductGroup.class, id);
            return productGroup;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 根据条件查询productGroup信息
     *
     * @param condition
     * @return
     */
    public ProductGroup getProductGroupByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<ProductGroup> list = productGroupDao.getByCondition(ProductGroup.class, fieldNames, condition);
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
     * 根据条件查询productGroup信息
     *
     * @param condition
     * @return
     */
    public List<ProductGroup> getProductGroupListByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<ProductGroup> list = productGroupDao.getByCondition(ProductGroup.class, fieldNames, condition);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * 根据 productGroupId删除ProductGroup
     *
     * @param productGroupId
     */
    public void deleteProductGroupById(long productGroupId) {
        try {
            productGroupDao.deleteById(ProductGroup.class, productGroupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteProductGroupByCondition(HashMap<String, Object> condition) {
        try {
            productGroupDao.deleteByCondition(ProductGroup.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取ProductGroup已经相关库存信息
     *
     * @param pageNumber
     * @param pageSize
     * @param fieldNames
     * @return
     * @throws Exception
     */
    public Page getProductGroupPageWithStore(Long pageNumber, Long pageSize,
                                             String fieldNames, Integer groupType, Long userId) throws Exception {

        try {
            // 先计数
            StringBuilder countSql = new StringBuilder();
            Object[] params = null;
            if (groupType == 1l) {
                countSql.append("select count(*) from product_group a where a.group_type=:p1 and a.vendor_id=:p2");
                params = new Object[]{groupType, userId};
            } else {
                countSql.append("select count(*) from product_group a where a.group_type=:p1 and a.shop_id=:p2");
                params = new Object[]{groupType, userId};
            }
            System.out.println("count sql: " + countSql.toString());


            // 1. 获取 总数
            Long totalRow = productGroupDao.createQuery(countSql.toString(), params).executeScalar(Long.class);

            if (totalRow == null || totalRow <= 0) {
                return new Page(new ArrayList<Map<String, Object>>(), pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            StringBuilder sql = new StringBuilder("");
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }

            if (groupType == 1l) {
                sql.append("SELECT a.* FROM product_group a  inner join vendor v on a.vendor_id = v.vendor_id where a.group_type=:p1 and a.vendor_id=:p2");
            } else {
                sql.append("SELECT a.* FROM product_group a  inner join shop s on a.shop_id= s.shop_id where a.group_type=:p1 and a.shop_id=:p2");
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
            List<Map<String, Object>> productMapList = productGroupDao.executeBySql(sql.toString(), params);
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

}
