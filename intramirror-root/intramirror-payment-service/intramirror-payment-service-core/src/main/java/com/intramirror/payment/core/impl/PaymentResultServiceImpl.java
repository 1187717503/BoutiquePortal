package com.intramirror.payment.core.impl;

import org.springframework.stereotype.Service;

import com.intramirror.payment.api.model.PaymentResult;
import com.intramirror.payment.api.service.IPaymentResultService;
import com.intramirror.payment.core.dao.BaseDao;

@Service
public class PaymentResultServiceImpl extends BaseDao implements IPaymentResultService{

	@Override
	public PaymentResult createPaymentResult(
			PaymentResult paymentResultModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePaymentResult(PaymentResult paymentResultModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
