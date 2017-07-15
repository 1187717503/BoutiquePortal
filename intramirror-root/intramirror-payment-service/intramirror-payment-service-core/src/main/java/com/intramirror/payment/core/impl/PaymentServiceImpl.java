package com.intramirror.payment.core.impl;

import org.springframework.stereotype.Service;

import com.intramirror.payment.api.model.Payment;
import com.intramirror.payment.api.service.IPaymentService;
import com.intramirror.payment.core.dao.BaseDao;
import com.intramirror.payment.core.mapper.PaymentMapper;

@Service
public class PaymentServiceImpl extends BaseDao implements IPaymentService{

    private PaymentMapper paymentMapper;
    
	@Override
	public Payment createPayment(Payment paymentModel) {
		paymentModel.setPaymentId(paymentMapper.insertSelective(paymentModel));
		return paymentModel;
	}

	@Override
	public void updatePayment(Payment paymentModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
