package com.intramirror.order.core.utils;

import com.intramirror.common.core.mapper.ShipEmailLogMapper;
import com.intramirror.order.api.model.ShipEmailLog;
import com.intramirror.order.api.model.ShipEmailLogExample;
import com.intramirror.order.api.service.IVendorShipmentService;
import com.intramirror.order.api.service.IViewOrderLinesService;
import com.intramirror.order.core.dao.BaseDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 123 on 2018/6/6.
 */
@Service
public class MailSendManageService extends BaseDao {

    @Autowired
    private IViewOrderLinesService viewOrderLinesService;
    @Autowired
    private IVendorShipmentService vendorShipmentService;
    private ShipEmailLogMapper shipEmailLogMapper;

    @Override
    public void init() {
        shipEmailLogMapper = this.getSqlSession().getMapper(ShipEmailLogMapper.class);
    }

    public IViewOrderLinesService getViewOrderLinesService() {
        return viewOrderLinesService;
    }

    public IVendorShipmentService getVendorShipmentService() {
        return vendorShipmentService;
    }

    public void insertShipEmailLog(String shipmentNo,String emailBody,String errMsg){
        ShipEmailLog log = new ShipEmailLog();
        log.setEmailBody(emailBody);
        log.setShipmentNo(shipmentNo);
        log.setErrorMessage(errMsg);
        shipEmailLogMapper.insertSelective(log);
    }

    public List<ShipEmailLog> getEmailLog(String shipmentNo){
        ShipEmailLogExample example = new ShipEmailLogExample();
        ShipEmailLogExample.Criteria criteria = example.createCriteria();
        if (shipmentNo != null){
            criteria.andShipmentNoEqualTo(shipmentNo);
            example.setLimit(1);
        }
        criteria.andIsDeletedEqualTo(0);
        return shipEmailLogMapper.selectByExample(example);
    }

    public void updateEmailLog(ShipEmailLog shipEmailLog){
        shipEmailLogMapper.updateByPrimaryKeySelective(shipEmailLog);
    }
}
