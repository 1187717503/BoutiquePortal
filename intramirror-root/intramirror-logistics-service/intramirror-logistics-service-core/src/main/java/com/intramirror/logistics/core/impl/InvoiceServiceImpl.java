package com.intramirror.logistics.core.impl;

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
	public int updateByShipmentId(Invoice record) {
		
		return invoiceMapper.updateByShipmentId(record);
	}

	@Override
	public Map<String,Object> getMaxDdtNo() {
		return invoiceMapper.getMaxNum();
	}
}
