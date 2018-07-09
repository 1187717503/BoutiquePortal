package pk.shoplus.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.google.gson.Gson;

import pk.shoplus.DBConnector;
import pk.shoplus.parameter.OrderStatusType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.LogisticsProductService;

import java.text.MessageFormat;
import java.util.*;


/**
 * 更新订单状态  订单流转状态校验
 * @author wzh
 *
 */
public class OrderUpdateStatusMapping{

    private final Logger LOGGER = Logger.getLogger(OrderUpdateStatusMapping.class);

    /**
     * 根据orderLineNum 修改订单状态
     * @param orderLineNum
     * * @param int status 订单状态
     * @return 
     */
	public Map<String, Object> updateOrderStatus(String orderLineNum,int status){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("status", StatusType.SUCCESS);
        Connection conn = null ;
		try {
			conn = DBConnector.sql2o.beginTransaction();
	        LogisticsProductService logisticsProductService = new LogisticsProductService(conn);
	        OrderRefund orderRefund = new OrderRefund(); 
			
			//获取订单参数，更改订单状态
			if(StringUtils.isNotBlank(orderLineNum) && status > 0){
				LOGGER.info(MessageFormat.format("调用更新订单,入参  orderLineNum:{0},status:{1}", orderLineNum,status));

				//根据orderLineNum 获取 LogisticsProduct 对象信息
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("order_line_num", orderLineNum);
				List<LogisticsProduct> LogisProductList = logisticsProductService.getLogisticsProductListByCondition(conditionMap);
				LogisticsProduct logisProduct = null;
				if(LogisProductList != null && LogisProductList.size()>0){
					logisProduct = LogisProductList.get(0);
				}
				
				if(logisProduct != null){

					//转换成本地数据库对应的状态
					int newStatus = status;
					
					LOGGER.info(MessageFormat.format("订单状态修改,当前订单的状态:{0},需要修改的状态:{1}",logisProduct.getStatus(),newStatus));
					
//					newStatus=6;
					//正常下单流程  最多流转5次
					for(int i=0;i<5;i++){
						
						/** 取消订单流程 **/
						//如果修改为取消订单,则需要先修改订单状态为退货，然后修改为取消，订单取消 必须为pending状态,或者退货状态
						//客户不存在refund 状态
						if(newStatus == OrderStatusType.CANCELED){
							
							//如果状态为pending 修改为refund
							if(logisProduct.getStatus() == OrderStatusType.PENDING){
								Map<String, Object> refundMap = orderRefund.orderRefund(logisProduct.getLogistics_product_id()+"");
								String refundStatus = refundMap.get("status").toString();
								
								//只有退款成功，才去修改状态
								if(StatusType.SUCCESS == Integer.parseInt(refundStatus)){
									
									Map<String, Object> resultMap = logisticsProductService.updateOrderLogisticsByOrderLogisticsId(logisProduct, OrderStatusType.REFUND, false);
									LOGGER.info("orderUpdateStatus mapping 更新订单状态"+OrderStatusType.REFUND+"结果:"+new Gson().toJson(resultMap));
									
									if(Integer.parseInt(resultMap.get("status").toString()) ==StatusType.SUCCESS){
										//如果成功，则更新状态，避免数据库重复调用查询   只会有这一个地方修改状态,不会出现脏数据
										logisProduct.setStatus(OrderStatusType.REFUND);
									}else{
										dataMap.put("info", resultMap.get("info"));
										break;
									}
									
									
								//退款失败，退出循环
								}else{
									dataMap.put("status", StatusType.FAILURE);
									dataMap.put("info", refundMap.get("info"));
									break;
								}

								
							//如果状态为refund 修改为canceled
							}else if(logisProduct.getStatus() == OrderStatusType.REFUND){
								Map<String, Object> resultMap = logisticsProductService.updateOrderLogisticsByOrderLogisticsId(logisProduct, OrderStatusType.CANCELED, false);
								LOGGER.info("orderUpdateStatus mapping 更新订单状态"+OrderStatusType.CANCELED+"结果:"+new Gson().toJson(resultMap));
								dataMap.put("info", resultMap.get("info"));
								break; //跳出循环
								
							//否则为错误的状态修改
							}else{
								 dataMap.put("status", StatusType.FAILURE);
								 dataMap.put("info", "订单状态错误,未按流转状态修改");
								 LOGGER.info("订单状态错误,未按流转状态修改");
								 break; //跳出循环
							}
							
							
						/** 正常下订单流程 **/
						}else{
							
							//如果当前状态与需要修改的状态不一致，则按照订单流转状态获取下一个状态，修改订单
							if(logisProduct.status != newStatus && logisProduct.status < newStatus){
								//获取下一个流转状态,更新订单
								int nextStatus = OrderStatusType.getNextStatus(logisProduct.status);
								LOGGER.info("orderUpdateStatus mapping: current state:"+logisProduct.getStatus()+"After modification:"+nextStatus);
								Map<String, Object> resultMap = logisticsProductService.updateOrderLogisticsByOrderLogisticsId(logisProduct,nextStatus, false);
								LOGGER.info("orderUpdateStatus mapping: Modify order status results:"+new Gson().toJson(resultMap));
								
								if(Integer.parseInt(resultMap.get("status").toString()) ==StatusType.SUCCESS){
									logisProduct.setStatus(nextStatus);
								}else{
									dataMap.put("info", resultMap.get("info"));
									break;
								}
							
							//状态未发生改变，无需修改订单状态
							}else if(logisProduct.status == newStatus){
								//dataMap.put("info", "状态未发生改变，无需修改订单状态");
								LOGGER.info("状态未发生改变,无需修改订单状态 status:"+ logisProduct.status);
								break;
								
							}else{
								 dataMap.put("status", StatusType.FAILURE);
								 dataMap.put("info", "订单状态错误,未按流转状态修改");
								 LOGGER.info("订单状态错误,未按流转状态修改");
								 break; //跳出循环
							}
						}
					}


				}
				
			}else{
				 dataMap.put("status", StatusType.FAILURE);
				 dataMap.put("info", "修改订单状态 参数存在空值或0,请检查入参是否正确 orderLineNum:"+orderLineNum+",status:"+status);
				 LOGGER.info("修改订单状态 参数存在 空值或0,请检查入参是否正确 orderLineNum:"+orderLineNum+",status:"+status);
			}
			conn.commit();
		} catch (Exception e) {
			if(conn != null) {
				conn.rollback();
				conn.close();
			}
            dataMap.put("status", StatusType.FAILURE);
            dataMap.put("info", "系统异常: "+e.getMessage());
			LOGGER.error(e.toString());
            e.printStackTrace();
        } finally {
        	if(conn != null) {
				conn.close();
			}
		}


        return dataMap;
    }
   
    

}
