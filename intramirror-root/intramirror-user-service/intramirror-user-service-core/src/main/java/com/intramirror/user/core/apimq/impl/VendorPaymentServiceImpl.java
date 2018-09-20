package com.intramirror.user.core.apimq.impl;

import com.intramirror.user.api.model.VendorPayment;
import com.intramirror.user.api.service.VendorPaymentService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.VendorPaymentMapper;
import org.springframework.stereotype.Service;

@Service
public class VendorPaymentServiceImpl extends BaseDao implements VendorPaymentService {

    private VendorPaymentMapper vendorPaymentMapper;

    public void init() {
        vendorPaymentMapper = this.getSqlSession().getMapper(VendorPaymentMapper.class);
    }

    @Override
    public VendorPayment selectByVendorId(Long vendorId) {
        return vendorPaymentMapper.selectByVendorId(vendorId);
    }

    @Override
    public void createVendorPayment(VendorPayment vendorPayment) {
        vendorPaymentMapper.insertSelective(vendorPayment);
    }

    @Override
    public void updateVendorPayment(VendorPayment vendorPayment) {
        vendorPaymentMapper.updateByPrimaryKeySelective(vendorPayment);
    }
}
