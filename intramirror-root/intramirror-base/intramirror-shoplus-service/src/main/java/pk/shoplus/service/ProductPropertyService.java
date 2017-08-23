package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ProductProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by cheezmall on 2016/10/25.
 */
public class ProductPropertyService {

    /**
     * 获取数据库连接
     */
    private EntityDao<ProductProperty> productPropertyDao = null;

    /**
     * @param conn
     */
    public ProductPropertyService(Connection conn) {
        productPropertyDao = new EntityDao<ProductProperty>(conn);
    }

    public List<Map<String, Object>> getAllProductPropertyValueBySeasonCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(" select distinct t_pro.season_code as `value` from product t_pro where t_pro.season_code !=''  and t_pro.season_code is not null ORDER BY value  ");
        List<Map<String, Object>> mapResult = null;
        try {
            mapResult = productPropertyDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }


    public List<Map<String, Object>> getProductPropertyValueBySeasonCode(String vendorId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select distinct t_pro.season_code as `value` from product t_pro where t_pro.vendor_id = " + vendorId + " and t_pro.season_code !=''  and t_pro.season_code is not null");
        List<Map<String, Object>> mapResult = null;
        try {
            mapResult = productPropertyDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }


    public List<Map<String, Object>> getProductPropertyValueById(Long productId) {
        StringBuffer sq = new StringBuffer();
        sq.append("SELECT p.product_id,p.key_name,p.`value` FROM product_property  p "
                + " WHERE (p.key_name = 'BrandID' or p.key_name = 'ColorCode') and p.product_id = :p1");
        List<Map<String, Object>> mapResult = null;
        try {
            mapResult = productPropertyDao.executeBySql(sq.toString(), new Object[]{productId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }

    public List<Map<String, Object>> getProductPropertyValueByproductIdS(String productIds) {
        StringBuffer sq = new StringBuffer();
        sq.append("SELECT p.* FROM product_property  p "
                + " WHERE p.type = 1 and p.product_id in(" + productIds + ")");
        List<Map<String, Object>> mapResult = null;
        try {
            mapResult = productPropertyDao.executeBySql(sq.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }

    /**
     * 创建Product Property信息
     *
     * @param productProperty
     * @return
     * @throws Exception
     */
    public ProductProperty createProductProperty(ProductProperty productProperty) throws Exception {
        try {
            long productPropertyId = productPropertyDao.create(productProperty);
            if (productPropertyId > 0) {
                productProperty.product_property_id = productPropertyId;
            } else {
                productProperty = null;
            }
            return productProperty;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据条件查询productinfo
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public List<ProductProperty> getProductPropertyByCondition(Map<String, Object> condition) throws Exception {
        try {
            // 查询
            List<ProductProperty> productPropertyList = productPropertyDao.getByCondition(ProductProperty.class,
                    condition);
            return productPropertyList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据条件删除ProductProperty
     *
     * @param condition
     */
    public void deleteProductPropertyByCondition(Map<String, Object> condition) {
        try {
            productPropertyDao.deleteByCondition(ProductProperty.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过实体类更新productPropperty
     *
     * @param productProperty
     */
    public void updateProductPropertyById(ProductProperty productProperty) {
        try {
            productPropertyDao.updateById(productProperty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过实体类更新productProperty
     *
     * @param productProperty
     * @param condition
     * @throws Exception
     */
    public void updateProductPropertyByCondition(ProductProperty productProperty, Map<String, Object> condition) throws Exception {
        productPropertyDao.updateByCondition(productProperty, condition);
    }

}
