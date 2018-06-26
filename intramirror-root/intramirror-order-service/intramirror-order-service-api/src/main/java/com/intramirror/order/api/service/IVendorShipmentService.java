package com.intramirror.order.api.service;

import com.intramirror.order.api.vo.VendorInvoiceVO;

import java.util.List;

/**
 * Created on 2018/6/26.
 *
 * @author 123
 */
public interface IVendorShipmentService {

    /**
     *
     * @param shipmentId
     * @return
     */
    List<VendorInvoiceVO> getVendorInvoicingList(Long shipmentId);
}
