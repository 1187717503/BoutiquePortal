package com.intramirror.user.core.apimq.impl;

import com.intramirror.common.parameter.EnabledType;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorApplication;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.VendorApplicationMapper;
import com.intramirror.user.core.mapper.VendorMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * vendor服务
 */
@Service
public class VendorServiceImpl extends BaseDao implements VendorService {

    private VendorMapper vendorMapper;

    private VendorApplicationMapper vendorApplicationMapper;

    public void init() {
        vendorMapper = this.getSqlSession().getMapper(VendorMapper.class);
        vendorApplicationMapper = this.getSqlSession().getMapper(VendorApplicationMapper.class);
    }

    public VendorApplication getVendorApplicationByUserId(Long userId) throws Exception {
        try {
            VendorApplication vendorApplication = null;
            List<VendorApplication> vendorApplicationList = vendorApplicationMapper.getVendorApplicationListByUserIdAndEnabled(userId, EnabledType.USED);
            for (VendorApplication temp : vendorApplicationList) {
                if (temp != null) {
                    vendorApplication = temp;
                    break;
                }
            }
            return vendorApplication;
        } catch (Exception e) {
            throw e;
        }
    }

    public Vendor getVendorByUserId(Long userId) throws Exception {
        try {
            Vendor vendor = null;
            List<Vendor> vendorList = vendorMapper.getVendorListByUserIdAndEnabled(userId, EnabledType.USED);
            for (Vendor temp : vendorList) {
                if (temp != null) {
                    vendor = temp;
                    break;
                }
            }
            return vendor;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Vendor> getVendorsByUserId(Long userId) throws Exception {
        try {
            return vendorMapper.getChildVendorListByUserIdAndEnabled(userId, EnabledType.USED);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Vendor getVendorByVendorId(Map<String, Object> params) {
        try {
            return vendorMapper.queryVendorByVendorId(params);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Map<String, String> getProductSkuVendorIdMap(String[] shopProductSkuIds) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            List<Map<String, Object>> list = vendorMapper.getProductSkuVendorIdMap(shopProductSkuIds);
            for (Map<String, Object> m : list) {
                String vendorId = m.get("vendor_id").toString();
                String skuId = m.get("shop_product_sku_id").toString();
                map.put(skuId, vendorId);
            }
        } catch (Exception e) {
            throw e;
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> getAllVendorCountryById(String[] vendorIds) {
        try {
            return vendorMapper.getAllVendorCountryById(vendorIds);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Vendor> getVendorByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids))
            return null;
        Map<String, Object> param = new HashMap<>();
        param.put("ids", ids);
        return vendorMapper.queryVendorByIds(param);
    }

    @Override
    public void updateByPrimaryKeySelective(Vendor vendor) {
        vendorMapper.updateByPrimaryKeySelective(vendor);
    }

    @Override
    public Long selectAllowImportProductByVendorId(Long vendorId) {
        return vendorMapper.selectAllowImportProductByVendorId(vendorId);
    }
}
