package com.intramirror.web.service;


import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.common.Contants;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.payment.api.model.Payment;
import com.intramirror.payment.api.model.PaymentResult;
import com.intramirror.payment.api.model.RefundVO;
import com.intramirror.payment.api.model.ResultMsg;
import com.intramirror.payment.api.service.IPaymentResultService;
import com.intramirror.payment.api.service.IPaymentService;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 退款
 * @author wzh
 *
 */
public class OrderRefund{

    private final Logger LOGGER =  LoggerFactory.getLogger(OrderRefund.class);
	/** properties */
	public static final String OSS_CONFIG_FILE = "/refundoss.properties";

	/** 域名(回调URL) */
	private static String DOMIAN = "";

	/** 商户编号(唯一) */
	private static String MERCHANT_ID = "";
	
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private LogisticsProductService logisticsProductService;
	
	@Autowired
	private IPaymentService paymentService;
	
	@Autowired
	private IPaymentResultService paymentResultService;
	
	

	/** init properties */
	static {
		InputStream in = OrderRefund.class.getResourceAsStream(OSS_CONFIG_FILE);
		Properties props = new Properties();
		try {
			props.load(in);
			DOMIAN = props.getProperty("payment.callback.domain");
			MERCHANT_ID = props.getProperty("payment.merchant.id");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

    /**
     * 根据logisProductId 退款
     * @param String logisProductId
     * @return
     */
	public Map<String, Object> orderRefund(Long logisProductId){
		LOGGER.info("根据logisProductId 退款操作  入参:"+logisProductId);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("status", StatusType.FAILURE);
        
		try {
	        
	    	// 执行退款操作
			Map<String, Object> map = null;
	    	List<Map<String, Object>> mapList = orderService.getOrderPaymentByLogisProductId(logisProductId);
	    	if(mapList != null && mapList.size() > 0){
	    		map = mapList.get(0);
	    	}
	    	
	        if (null != map) {
	        	if (Contants.PAY_OFFLINE.equals(map.get("pay_way").toString())){ // 线下支付
	        		dataMap.put("status", StatusType.SUCCESS);
	        		dataMap.put("info","订单属于线下支付");
	        		LOGGER.info("logisProductId:"+logisProductId+",订单属于线下支付");
	            }else if(map.get("serial_number") != null && StringUtils.isNotBlank(map.get("serial_number").toString())){
	            	// 退款
	                ResultMsg rsMsg = this.refund(map);
	                if (!rsMsg.getStatus()) {
	                    dataMap.put("info", rsMsg.getMsg());
	                } else {
	                	 dataMap.put("status", StatusType.SUCCESS);
	                }                      
	                // 记录退款日志
	                this.recordPaymentLog(map);
	            } else {
	            	 dataMap.put("info", logisProductId+"子订单数据异常,找不到有效支付记录!!!");
	            }
	        } else {
	        	dataMap.put("info", logisProductId +"子订单数据异常,找不到有效支付记录!!!");
	        }
			

			
		} catch (Exception e) {
            dataMap.put("status", StatusType.FAILURE);
            dataMap.put("info", "系统异常: "+e.getMessage());
			LOGGER.error(e.toString());
            e.printStackTrace();
        } 


        return dataMap;
    }
    
    
    public ResultMsg refund(Map<String,Object> map) {
    	RefundVO refundVO = new RefundVO();
		RefundService rs = new RefundService();
		refundVO.setAmount(map.get("price").toString());
		refundVO.setOrderId(map.get("serial_number").toString());
//		refundVO.setRequestId(map.get("request_id").toString());
		refundVO.setRequestId(map.get("logistics_product_id").toString());
		refundVO.setMerchantId(MERCHANT_ID);
		refundVO.setNotifyUrl(DOMIAN + "/admin/callback");
		refundVO.setRemark("");
		ResultMsg rsMsg = rs.refund(refundVO);
        return rsMsg;
    }
    
    public ResultMsg refundQuery(Map<String,Object> map) {
    	RefundVO refundVO = new RefundVO();
		RefundService rs = new RefundService();
		refundVO.setAmount(map.get("price").toString());
		refundVO.setOrderId(map.get("serial_number").toString());
		refundVO.setRequestId(map.get("request_id").toString());
		refundVO.setMerchantId(MERCHANT_ID);
		refundVO.setNotifyUrl(DOMIAN + "/admin/callback");
		refundVO.setRemark("");
		ResultMsg rsMsg = rs.refundQuery(refundVO);
        return rsMsg;
    }
    
    public void recordPaymentLog(Map<String,Object> refundMap){
    	LOGGER.info("开始记录退款日志信息,refundMap:"+new Gson().toJson(refundMap));
    	ResultMsg rsMsg = this.refundQuery(refundMap);
    	com.alibaba.fastjson15.JSONObject data = rsMsg.getData();
    	
    	Payment payment = new Payment();
    	PaymentResult paymentResult = new PaymentResult();
    	
    	// payment
    	payment.setOrderNum(refundMap.get("order_num")==null?"":refundMap.get("order_num").toString());
    	payment.setMerchantId(MERCHANT_ID);
    	payment.setRequestId(refundMap.get("request_id").toString());
    	payment.setNotifyUrl(DOMIAN+"?requestID="+refundMap.get("request_id").toString());
    	payment.setCallbackUrl(DOMIAN+"?requestID="+refundMap.get("request_id").toString());
    	
    	// payment_result
    	paymentResult.setMarchantId(MERCHANT_ID);
    	paymentResult.setRequestId(refundMap.get("request_id").toString());
    	
    	if (rsMsg.getStatus()) {
    		payment.setHmac(data.getString("hmac"));
        	paymentResult.setSerialNumber(data.getString("serialNumber"));
        	paymentResult.setTotalRefundCount(data.getString("totalRefundCount"));
        	paymentResult.setTotalRefundAmount(data.getString("total_refund_amount"));
        	paymentResult.setOrderCurrency(data.getString("currency"));
        	paymentResult.setOrderAmount(data.getString("amount"));
        	paymentResult.setProcessStatus(data.getString("status"));
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	try {
				Date comDate = sdf.parse(data.getString("completeDateTime"));
				paymentResult.setCompleteDateTime(comDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        	paymentResult.setRemark(data.getString("remark"));
        	paymentResult.setHmac(data.getString("hmac"));
        	
        	// 记录退款日志
        	this.insertPayment(payment, paymentResult);
        	
        	if(Contants.REFUND_QUERY_STATUS_SUCCESS.equals(data.getString("status"))) {
        		// 修改状态
            	try {
            		logisticsProductService.updateOrderLogisticsStatusById(Long.parseLong(refundMap.get("request_id").toString()),Contants.LOGISTICSPRODUCT_CANCELED_STATUS);
                } catch (Exception e) {
                	e.printStackTrace();
                }
        	}
        	
    	}
    }


    /**
     * 记录支付日志
     * @param paymentModel
     * @param paymentResultModel
     * @return 记录支付日志是否成功 true:成功 false:失败
     */
    public boolean insertPayment(Payment paymentModel,PaymentResult paymentResultModel){
    	try {
    		// insert payment
    		Payment returnPaymentModel = paymentService.createPayment(paymentModel);
    		
    		// insert paymentresult
    		paymentResultModel.setPaymentId(returnPaymentModel.getPaymentId());
    		paymentResultService.createPaymentResult(paymentResultModel);
    		
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
    	return true;
    }
    

}
