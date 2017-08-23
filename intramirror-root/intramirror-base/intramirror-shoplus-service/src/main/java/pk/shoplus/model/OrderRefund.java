package pk.shoplus.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.google.gson.Gson;

import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.common.ResultMsg;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.LogisticsProductService;
import pk.shoplus.service.OrderLogisticsService;
import pk.shoplus.service.PaymentResultService;
import pk.shoplus.service.PaymentService;
import pk.shoplus.service.RefundService;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 退款
 * @author wzh
 *
 */
public class OrderRefund{

    private final Logger LOGGER = Logger.getLogger(OrderRefund.class);
	/** properties */
	public static final String OSS_CONFIG_FILE = "/refundoss.properties";

	/** 域名(回调URL) */
	private static String DOMIAN = "";

	/** 商户编号(唯一) */
	private static String MERCHANT_ID = "";

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
	public Map<String, Object> orderRefund(String logisProductId){
		LOGGER.info("根据logisProductId 退款操作  入参:"+logisProductId);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("status", StatusType.FAILURE);
        
        Connection conn = null ;
		try {
			conn = DBConnector.sql2o.beginTransaction();
	        OrderLogisticsService orderLogisticsService = new OrderLogisticsService(conn);
	        
	    	// 执行退款操作
	    	Map<String, Object> map = orderLogisticsService.getOrderPaymentInfoByLogisticsProductId(logisProductId);
	    	
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
	                conn.commit();
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
            if(conn != null) {conn.rollback();conn.close();}
        } finally {
            if(conn != null) {conn.close();}
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
    	
    	PaymentModel payment = new PaymentModel();
    	PaymentResultModel paymentResult = new PaymentResultModel();
    	
    	// payment
    	payment.setOrder_num(refundMap.get("order_num")==null?"":refundMap.get("order_num").toString());
    	payment.setMerchant_id(MERCHANT_ID);
    	payment.setRequest_id(refundMap.get("request_id").toString());
    	payment.setNotify_url(DOMIAN+"?requestID="+refundMap.get("request_id").toString());
    	payment.setCallback_url(DOMIAN+"?requestID="+refundMap.get("request_id").toString());
    	
    	// payment_result
    	paymentResult.setMarchant_id(MERCHANT_ID);
    	paymentResult.setRequest_id(refundMap.get("request_id").toString());
    	
    	if (rsMsg.getStatus()) {
    		payment.setHmac(data.getString("hmac"));
        	paymentResult.setSerial_number(data.getString("serialNumber"));
        	paymentResult.setTotal_refund_count(data.getString("totalRefundCount"));
        	paymentResult.setTotal_refund_amount(data.getString("total_refund_amount"));
        	paymentResult.setOrder_currency(data.getString("currency"));
        	paymentResult.setOrder_amount(data.getString("amount"));
        	paymentResult.setProcess_status(data.getString("status"));
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	try {
				Date comDate = sdf.parse(data.getString("completeDateTime"));
				paymentResult.setComplete_date_time(comDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        	paymentResult.setRemark(data.getString("remark"));
        	paymentResult.setHmac(data.getString("hmac"));
        	
        	// 记录退款日志
        	this.insertPayment(payment, paymentResult);
        	
        	if(Contants.REFUND_QUERY_STATUS_SUCCESS.equals(data.getString("status"))) {
        		// 修改状态
            	try(Connection conn = DBConnector.sql2o.beginTransaction()) {
            		LogisticsProductService logisticsProductService = new LogisticsProductService(conn);
            		LogisticsProduct logisticsProduct = new LogisticsProduct();
            		logisticsProduct.setLogistics_product_id(Long.parseLong(refundMap.get("request_id").toString()));
            		logisticsProduct.setStatus(Contants.LOGISTICSPRODUCT_CANCELED_STATUS);
            		logisticsProductService.updateLogisticsProduct(logisticsProduct,-4, false);
            		conn.commit();
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
    public boolean insertPayment(PaymentModel paymentModel,PaymentResultModel paymentResultModel){
    	try(Connection conn = DBConnector.sql2o.beginTransaction()) {
    		// insert payment
    		PaymentService paymentService = new PaymentService(conn);
    		PaymentModel returnPaymentModel = paymentService.createPayment(paymentModel);
    		
    		// insert paymentresult
    		PaymentResultService paymentResultService = new PaymentResultService(conn);
    		paymentResultModel.setPayment_id(returnPaymentModel.getPayment_id());
    		paymentResultService.createPaymentResult(paymentResultModel);
    		
            conn.commit();
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
    	return true;
    }
    

}
