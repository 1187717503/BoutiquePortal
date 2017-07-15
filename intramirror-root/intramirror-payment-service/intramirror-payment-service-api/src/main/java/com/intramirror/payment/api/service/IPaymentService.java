package com.intramirror.payment.api.service;

import com.intramirror.payment.api.model.Payment;



public interface IPaymentService {
	
	
	
	/**
	 * 插入payment表数据
	 * @param paymenyModel
	 * @return
	 * @throws Exception
	 */
	public Payment createPayment(Payment paymentModel);
	

	
	/**
	 * 修改payment表数据
	 * @param paymenyModel
	 * @throws Exception
	 */
	public void updatePayment(Payment paymentModel);

}
