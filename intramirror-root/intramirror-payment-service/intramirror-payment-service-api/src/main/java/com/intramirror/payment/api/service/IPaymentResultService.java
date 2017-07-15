package com.intramirror.payment.api.service;

import com.intramirror.payment.api.model.PaymentResult;



public interface IPaymentResultService {

	
	/**
	 * 插入paymentresult表数据
	 * @param PaymentResultModel
	 * @return
	 * @throws Exception
	 */
	public PaymentResult createPaymentResult(PaymentResult paymentResultModel);

	
	
	/**
	 * 修改paymentresult表数据
	 * @param paymenyModel
	 * @throws Exception
	 */
	public void updatePaymentResult(PaymentResult paymentResultModel);
}
