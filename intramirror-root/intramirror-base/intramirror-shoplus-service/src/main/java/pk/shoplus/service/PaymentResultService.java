package pk.shoplus.service;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.PaymentResultModel;

public class PaymentResultService {
	private EntityDao<PaymentResultModel> paymentResultDao = null;
	
	public PaymentResultService(Connection conn) {
		paymentResultDao = new EntityDao<PaymentResultModel>(conn);
	}
	
	/**
	 * 插入paymentresult表数据
	 * @param PaymentResultModel
	 * @return
	 * @throws Exception
	 */
	public PaymentResultModel createPaymentResult(PaymentResultModel paymentResultModel) throws Exception {
		try {
			paymentResultModel.payment_result_id = paymentResultDao.create(paymentResultModel);
			if (paymentResultModel.payment_result_id > 0) {
				return paymentResultModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改paymentresult表数据
	 * @param paymenyModel
	 * @throws Exception
	 */
	public void updatePaymentResult(PaymentResultModel paymentResultModel) throws Exception {
		try {
			paymentResultDao.updateById(paymentResultModel);
		} catch (Exception e) {
			throw e;
		}
	}
}
