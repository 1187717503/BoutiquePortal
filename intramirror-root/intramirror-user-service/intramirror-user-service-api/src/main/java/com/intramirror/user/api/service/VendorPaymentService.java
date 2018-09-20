package com.intramirror.user.api.service;

import com.intramirror.user.api.model.VendorPayment;

public interface VendorPaymentService {

    VendorPayment selectByVendorId(Long vendorId);

    void createVendorPayment(VendorPayment vendorPayment);

    void updateVendorPayment(VendorPayment vendorPayment);
}
