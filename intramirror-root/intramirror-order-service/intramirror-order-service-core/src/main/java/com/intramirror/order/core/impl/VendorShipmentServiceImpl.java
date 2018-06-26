package com.intramirror.order.core.impl;

import com.intramirror.order.api.service.IVendorShipmentService;
import com.intramirror.order.api.vo.VendorInvoiceVO;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.VendorShipmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 2018/6/26.
 *
 * @author 123
 */
@Service
public class VendorShipmentServiceImpl extends BaseDao implements IVendorShipmentService {

    private static Logger logger = LoggerFactory.getLogger(ViewOrderLinesServiceImpl.class);

    private VendorShipmentMapper vendorShipmentMapper;

    @Override
    public void init() {
        vendorShipmentMapper = this.getSqlSession().getMapper(VendorShipmentMapper.class);
    }

    @Override
    public List<VendorInvoiceVO> getVendorInvoicingList(Long shipmentId) {
        if (shipmentId == null) {
            return null;
        }
        return vendorShipmentMapper.getVendorInvoicingList(shipmentId);
    }
}
