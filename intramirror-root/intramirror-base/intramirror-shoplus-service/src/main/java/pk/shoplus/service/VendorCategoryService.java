package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.VendorCategory;
import pk.shoplus.parameter.EnabledType;

/**
 * Created by cheezmall on 2016/9/18.
 */
public class VendorCategoryService {
    /**
     * 获取数据库连接
     */
    private EntityDao<VendorCategory> vendorCategoryDao = null;

    /**
     * @param conn
     */
    public VendorCategoryService(Connection conn) {
        vendorCategoryDao = new EntityDao<VendorCategory>(conn);
    }

    /**
     * 创建VendorCategory信息
     *
     * @param vendorCategory
     * @return
     * @throws Exception
     */
    public VendorCategory createVendorCategory(VendorCategory vendorCategory) throws Exception {
        try {
            long categoryId = this.vendorCategoryDao.create(vendorCategory);
            if (categoryId > 0) {
                vendorCategory.vendor_id = categoryId;
            } else {
                vendorCategory = null;
            }
            return vendorCategory;
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * @param
     * @return
     */
    public Map<String, Object> findVendorCategoryByVendorApplicationId(long vendor_application_id) {
        List<Map<String, Object>> list = vendorCategoryDao.executeBySql(
                "SELECT * " + "FROM vendor_category v " + "WHERE v.vendor_application_id = :p1 AND v.enabled=:p2",
                new Object[]{vendor_application_id, EnabledType.USED});
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询VendorCategory list
     */
    public List<VendorCategory> getVendorCategoriesByVendorApplicationId(Long vendorApplicationId) throws Exception {
        Map<String, Object> condition = new HashMap<>();
		condition.put("enabled", EnabledType.USED);
        condition.put("vendor_application_id", vendorApplicationId);
        return vendorCategoryDao.getByCondition(VendorCategory.class, condition);
    }

    /**
     * 更新供应商表
     *
     * @param vendorCategory
     * @throws Exception
     */
    public void updateVendorCategory(VendorCategory vendorCategory) throws Exception {
        vendorCategoryDao.updateById(vendorCategory);
    }

    /**
     * 根据 vendorapplicationid删除VendorCategory
     *
     * @param vendorapplicationid
     */
    public void deleteVendorCategoryByVendorApplicationId(long vendorapplicationid) {
        try {
            vendorCategoryDao.deleteById(VendorCategory.class, vendorapplicationid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteVendorCategoryByCondition(HashMap<String, Object> condition) {
        try {
            vendorCategoryDao.deleteByCondition(VendorCategory.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 根据条件查询VendorCategory表
    public List<VendorCategory> getVendorCategoryListByCondition(Map<String, Object> condition) throws Exception {
        try {
            // 查询
            String fieldName = "*";
            List<VendorCategory> categoryList = vendorCategoryDao.getByCondition(VendorCategory.class, fieldName,
                    condition);
            return categoryList;
        } catch (Exception e) {
            throw e;
        }
    }
    
    // 查询vendor相关的目录(1级)
    public List<Map<String,Object>> queryCategoryMapping(Map<String,Object> conditions){
    	String sql = " select * from category c where c.category_id in ( "+
    				 " select distinct vc.category_id from vendor_category vc "+
    				 " where vc.vendor_id = '" + conditions.get("vendor_id")==null?"":conditions.get("vendor_id").toString() + "' and vc.enabled = 1) and c.enabled = 1";
    	return vendorCategoryDao.executeBySql(sql, null);  	
    }

    // 查询api相关的目录(3级)
    public List<Map<String,Object>> queryCategoryByConditions(Map<String,Object> conditions){
    	String sql = "select distinct category_id from api_category_map acm "+
    				" left join api_configuration ac on(acm.api_configuration_id = ac.api_configuration_id and ac.enabled = 1) "+
    				" left join api_end_point aep on(ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1) "+
    				" where 1 = 1 ";
    	String vendor_id = conditions.get("vendor_id")==null?"":conditions.get("vendor_id").toString();
    	String api_configuration_id = conditions.get("api_configuration_id")==null?"":conditions.get("api_configuration_id").toString();
		String api_end_point_id = conditions.get("api_end_point_id")==null?"":conditions.get("api_end_point_id").toString();
		if(StringUtils.isNotBlank(vendor_id)) {
			sql += " and ac.vendor_id ='" + vendor_id + "' ";
		}
		
		if(StringUtils.isNotBlank(api_configuration_id)) {
			sql += " and ac.api_configuration_id = '" + api_configuration_id + "' ";
		}
		
		if(StringUtils.isNotBlank(api_end_point_id)) {
			sql += " and aep.api_end_point_id = '" + api_end_point_id + "' ";
		}
    	return vendorCategoryDao.executeBySql(sql, null);  	
    }
}
