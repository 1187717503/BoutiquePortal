package com.intramirror.user.core.apimq.impl;


import com.intramirror.common.parameter.EnabledType;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorApplication;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.VendorApplicationMapper;
import com.intramirror.user.core.mapper.VendorMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
