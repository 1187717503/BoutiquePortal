package pk.shoplus.service;

import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.BrandHot;
import pk.shoplus.model.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrandHotService {
    private final Logger LOGGER = Logger.getLogger(BrandHotService.class);
    /**
     * 获取数据库连接
     */
    private EntityDao<BrandHot> brandHotDao = null;

    /**
     * @param conn
     */
    public BrandHotService(Connection conn) {
        brandHotDao = new EntityDao<BrandHot>(conn);
    }

    /**
     * 获取brandHot
     *
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Page getBrandHotList(Long pageNumber, Long pageSize,
                                String fieldNames, Integer type) throws Exception {

        try {
            // 先计数
            StringBuilder countSql = new StringBuilder();
            Object[] params = null;
            countSql.append("select count(*) from brand_hot bh where bh.type=:p1 ");
            params = new Object[]{type};
            System.out.println("count sql: " + countSql.toString());

            // 1. 获取 总数
            Long totalRow = brandHotDao.createQuery(countSql.toString(), params).executeScalar(Long.class);

            if (totalRow == null || totalRow <= 0) {
                return new Page(new ArrayList<Map<String, Object>>(), pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            StringBuilder sql = new StringBuilder("");
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }

            sql.append("SELECT b.english_name, bh.* FROM brand_hot bh LEFT JOIN brand b ON bh.brand_id=b.brand_id where bh.type=:p1 ");

            String orderBy="bh.weight DESC";
            // order by
            if (!Helper.isNullOrEmpty(orderBy)) {
                sql.append(" order by ").append(orderBy);
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
            List<Map<String, Object>> productMapList = brandHotDao.executeBySql(sql.toString(), params);
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }


    public int getBrandHotCountByVendor(Long vendorId, String query) {
        String sql = "SELECT DISTINCT count(1) as total FROM brandHot b " +
                "INNER JOIN vendor_brandHot vb " +
                "ON b.brandHot_id = vb.brandHot_id AND b.enabled = 1 AND " +
                "vb.enabled = 1 AND vb.vendor_id = " + vendorId + " " +
                "WHERE b.english_name LIKE '%" + query + "%' ";
        return Integer.parseInt(brandHotDao.executeBySql(sql, null).get(0).get("total").toString());
    }


    public BrandHot getBrandHotByEnglishName(String englishName) {
        BrandHot brandHot = null;
        Map<String, Object> condition = new HashMap<>();
        condition.put("english_name", englishName);
        try {
            List<BrandHot> brandHots = getBrandHotListByCondition(condition);
            if (null != brandHots && brandHots.size() > 0) {
                brandHot = brandHots.get(0);
            }
        } catch (Exception e) {

        }
        return brandHot;
    }


    /**
     * 根据brandHot id 获取 brandHot
     *
     * @param id
     * @return
     * @throws Exception
     */
    public BrandHot getBrandHotById(Long id) throws Exception {
        try {
            BrandHot brandHot = null;

            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("brand_hot_id", id);

            List<BrandHot> list = this.brandHotDao.getByCondition(BrandHot.class, condition);

            for (BrandHot temp : list) {
                if (temp != null) {
                    brandHot = temp;
                    break;
                }
            }
            return brandHot;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过条件获取BrandHotList
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public List<BrandHot> getBrandHotListByCondition(Map<String, Object> condition) throws Exception {
        try {
            // 查询
            String fieldName = "*";
            List<BrandHot> brandHotList = brandHotDao.getByCondition(BrandHot.class, fieldName, condition);
            return brandHotList;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 创建BrandHot
     *
     * @param brandHot
     * @return
     * @throws Exception
     */
    public Long createBrandHot(BrandHot brandHot) throws Exception {
        try {
            Long brandHotId = brandHotDao.create(brandHot);
            if (brandHotId != 0) {
                return brandHotId;
            }
        } catch (Exception e) {
            throw e;
        }

        return 0l;
    }


    /**
     * 更新brandHot信息
     *
     * @param brandHot
     * @throws Exception
     */
    public void updateBrandHot(BrandHot brandHot) throws Exception {
        try {
            brandHotDao.updateById(brandHot);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据 Id删除BrandHot
     *
     * @param brandHotId
     */
    public void deleteBrandHotById(long brandHotId) {
        try {
            brandHotDao.deleteById(BrandHot.class, brandHotId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void batchDeleteBrandHotById(String brandHotIds) throws Exception {
        try {
            String sql = "delete from brand_hot where brand_hot_id in (" + brandHotIds + ")";
            brandHotDao.updateBySQL(sql, null);
        } catch (Exception e) {
            throw e;
        }
    }

}
