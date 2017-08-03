package pk.shoplus.service;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.PaymentModel;

/**
 * payment 表操作Service
 * @author yfding
 * @since 2017-4-7 14:29:58
 */
public class PaymentService {
	private EntityDao<PaymentModel> paymentDao = null;
	
	public PaymentService(Connection conn) {
		paymentDao = new EntityDao<PaymentModel>(conn);
	}
	
	/**
	 * 插入payment表数据
	 * @param paymenyModel
	 * @return
	 * @throws Exception
	 */
	public PaymentModel createPayment(PaymentModel paymentModel) throws Exception {
		try {
			paymentModel.payment_id = paymentDao.create(paymentModel);
			if (paymentModel.payment_id > 0) {
				return paymentModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改payment表数据
	 * @param paymenyModel
	 * @throws Exception
	 */
	public void updatePayment(PaymentModel paymentModel) throws Exception {
		try {
			paymentDao.updateById(paymentModel);
		} catch (Exception e) {
			throw e;
		}
	}
}
