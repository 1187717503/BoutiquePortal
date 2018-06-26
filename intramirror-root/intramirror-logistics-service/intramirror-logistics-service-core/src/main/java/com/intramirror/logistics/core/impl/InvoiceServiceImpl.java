package com.intramirror.logistics.core.impl;

import com.intramirror.logistics.api.model.VendorShipment;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intramirror.logistics.api.model.Invoice;
import com.intramirror.logistics.api.service.IInvoiceService;
import com.intramirror.logistics.core.dao.BaseDao;
import com.intramirror.logistics.core.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InvoiceServiceImpl extends BaseDao implements IInvoiceService{
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
	
	private InvoiceMapper invoiceMapper;
	
	@Override
	public void init() {
		invoiceMapper = this.getSqlSession().getMapper(InvoiceMapper.class);
	}

	@Override
	public int deleteByPrimaryKey(Long invoiceId) {
		
		return invoiceMapper.deleteByPrimaryKey(invoiceId);
	}

	@Override
	public int insert(Invoice record) {
		
		return invoiceMapper.insert(record);
	}

	@Override
	public int insertSelective(Invoice record) {
		
		return invoiceMapper.insertSelective(record);
	}

	@Override
	public Invoice selectByPrimaryKey(Long invoiceId) {
		
		return invoiceMapper.selectByPrimaryKey(invoiceId);
	}
	

	@Override
	public int updateByPrimaryKeySelective(Invoice record) {
		
		return invoiceMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Invoice record) {
		
		return invoiceMapper.updateByPrimaryKey(record);
	}

	@Override
	public Invoice getInvoiceByShipmentId(Long shipmentId) {
		return invoiceMapper.getInvoiceByShipmentId(shipmentId);
	}

	@Override
	public Invoice getInvoiceByMap(Map map) {
		return invoiceMapper.getInvoiceByMap(map);
	}

	@Override
	public int updateByShipmentId(Invoice record) {
		
		return invoiceMapper.updateByShipmentId(record);
	}

	@Override
	public Map<String,Object> getMaxDdtNo() {
		return invoiceMapper.getMaxNum();
	}

	@Override
	public VendorShipment saveOrUpdateVendorShipment(VendorShipment vendorShipment) {
		if(vendorShipment == null) return null;
		if(vendorShipment.getId() != null){
			vendorShipment.setUpdateTime(new Date());
			invoiceMapper.vendorShipmentUpdateByKey(vendorShipment);
		}else {
			vendorShipment.setCreateTime(new Date());
			invoiceMapper.vendorShipmentInsertSelective(vendorShipment);
		}
		return vendorShipment;
	}

	@Override
	public VendorShipment queryVendorShipmentByShipmentId(Long shipmentId) {
		VendorShipment vendorShipment = new VendorShipment();
		vendorShipment.setShipmentId(shipmentId);
		List<VendorShipment> list =  invoiceMapper.selectVendorInvoiceByParam(vendorShipment);
		if(list !=null && list.size() >0){
			return list.get(0);
		}
		return null;
	}
}
