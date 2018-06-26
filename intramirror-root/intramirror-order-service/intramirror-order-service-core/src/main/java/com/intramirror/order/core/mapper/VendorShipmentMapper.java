package com.intramirror.order.core.mapper;


import com.intramirror.order.api.vo.VendorInvoiceVO;

import java.util.List;

public interface VendorShipmentMapper {

    /**
     * 查询shipment里由买手店发来的invoice文件
     * @param shipmentId
     * @return
     */
    List<VendorInvoiceVO> getVendorInvoicingList(Long shipmentId);
}