package com.intramirror.web.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson15.JSONObject;
import com.ehking.sdk.exception.HmacVerifyException;
import com.ehking.sdk.exception.RequestException;
import com.ehking.sdk.exception.ResponseException;
import com.ehking.sdk.exception.UnknownException;
import com.ehking.sdk.executer.ResultListenerAdpater;
import com.ehking.sdk.onlinepay.builder.RefundBuilder;
import com.ehking.sdk.onlinepay.executer.OnlinePayOrderExecuter;
import com.google.gson.Gson;
import com.intramirror.payment.api.model.RefundVO;
import com.intramirror.payment.api.model.ResultMsg;


/**
 * 退款操作
 * @author yfding
 * @since 2017-3-31 10:47:52
 */
public class RefundService {
	private static final Logger logger = LoggerFactory.getLogger(RefundService.class);
	
	private ResultMsg rMsg = new ResultMsg();
	
	/** 执行退款操作 */
	public ResultMsg refund(RefundVO refundVO) {
		RefundBuilder builder = new RefundBuilder(refundVO.getMerchantId());
		builder.setRequestId(refundVO.getRequestId()).
		setAmount(refundVO.getAmount()).
		setOrderId(refundVO.getOrderId()).
		setRemark(refundVO.getRemark()).
		setNotifyUrl(refundVO.getNotifyUrl());
		
		JSONObject requestData = builder.build();
		OnlinePayOrderExecuter executer = new OnlinePayOrderExecuter();
		logger.info("【退款】开始调用易汇金SDK,入参数据:"+new Gson().toJson(refundVO));
		try{
			executer.refund(requestData, new ResultListenerAdpater() {
				@Override
				public void failure(JSONObject jsonObject) {
					rMsg.setData(jsonObject);
					rMsg.setMsg("【退款】-失败,jsonObject:{"+new Gson().toJson(jsonObject)+"}");
				}

				@Override
				public void pending(JSONObject jsonObject) {
					rMsg.setData(jsonObject);
					rMsg.setMsg("【退款】-待处理,jsonObject:{"+new Gson().toJson(jsonObject)+"}");
				}

				@Override
				public void success(JSONObject jsonObject) {
					rMsg.setData(jsonObject);
					rMsg.setMsg("【退款】-成功,jsonObject:{"+new Gson().toJson(jsonObject)+"}");
				}
			});
		}
		catch(ResponseException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款】响应异常！" + new Gson().toJson(refundVO));;
			logger.error("【退款】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款-响应异常,errorMessage:{"+e+"}");
		}
		catch(HmacVerifyException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款】签名验证异常！" + new Gson().toJson(refundVO));
			logger.error("【退款】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款-签名验证异常,errorMessage:{"+e+"}");
		}
		catch(RequestException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款】请求异常！" + new Gson().toJson(refundVO));
			logger.error("【退款】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款-请求异常,errorMessage:{"+e+"}");
		}
		catch(UnknownException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款】未知异常！" + new Gson().toJson(refundVO));
			logger.error("【退款】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款-未知异常,errorMessage:{"+e+"}");
		}
		logger.info("【退款】结束！响应数据："+new Gson().toJson(rMsg));
		return rMsg;
	}
	
	/** 执行退款查询操作 */
	public ResultMsg refundQuery(RefundVO refundVO){
		String merchantId = refundVO.getMerchantId();
		String requestId = refundVO.getRequestId();

		RefundBuilder builder = new RefundBuilder(merchantId);
		builder.setRequestId(requestId);
		JSONObject requestData = builder.build();
		OnlinePayOrderExecuter executer = new OnlinePayOrderExecuter();
		logger.info("【退款查询】开始调用易汇金SDK,入参数据:"+new Gson().toJson(refundVO));
		try{
			executer.refundQuery(requestData, new ResultListenerAdpater() {
	            public void success(JSONObject jsonObject) {
	            	rMsg.setData(jsonObject);
					rMsg.setMsg("【退款查询】-成功,jsonObject:{"+new Gson().toJson(jsonObject)+"}");
	            }

	            public void failure(JSONObject jsonObject) {
	            	rMsg.setData(jsonObject);
					rMsg.setMsg("【退款查询】-失败,jsonObject:{"+new Gson().toJson(jsonObject)+"}");
	            }
	            
	            public void pending(JSONObject jsonObject) {
	            	rMsg.setData(jsonObject);
					rMsg.setMsg("【退款查询】-待处理,jsonObject:{"+new Gson().toJson(jsonObject)+"}");
	            }
			});
		}
		catch(ResponseException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款查询】响应异常！" + new Gson().toJson(refundVO));;
			logger.error("【退款查询】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款查询-响应异常,errorMessage:{"+e+"}");
		}
		catch(HmacVerifyException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款查询】签名验证异常！" + new Gson().toJson(refundVO));
			logger.error("【退款查询】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款查询-签名验证异常,errorMessage:{"+e+"}");
		}
		catch(RequestException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款查询】请求异常！" + new Gson().toJson(refundVO));;
			logger.error("【退款查询】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款查询-请求异常,errorMessage:{"+e+"}");
		}
		catch(UnknownException e){
			e.printStackTrace();
			rMsg.status(false).setMsg("【退款查询】未知异常！" + new Gson().toJson(refundVO));
			logger.error("【退款查询】订单号 {"+refundVO.getOrderId()+"} 交易号 {"+refundVO.getRequestId()+"} 退款查询-未知异常,errorMessage:{"+e+"}");
		}
		logger.info("【退款查询】结束！响应数据："+new Gson().toJson(rMsg));
		return rMsg;
	}
}
