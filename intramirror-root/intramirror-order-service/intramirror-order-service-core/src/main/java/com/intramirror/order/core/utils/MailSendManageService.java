package com.intramirror.order.core.utils;

import com.intramirror.order.api.service.IVendorShipmentService;
import com.intramirror.order.api.service.IViewOrderLinesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by 123 on 2018/6/6.
 */
@Repository
public class MailSendManageService {

    @Autowired
    private IViewOrderLinesService viewOrderLinesService;

    @Autowired
    private IVendorShipmentService vendorShipmentService;

    public IViewOrderLinesService getViewOrderLinesService() {
        return viewOrderLinesService;
    }

    public IVendorShipmentService getVendorShipmentService() {
        return vendorShipmentService;
    }
}
