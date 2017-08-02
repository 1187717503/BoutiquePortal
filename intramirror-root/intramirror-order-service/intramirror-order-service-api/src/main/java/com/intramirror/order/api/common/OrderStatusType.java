package com.intramirror.order.api.common;
public class OrderStatusType {

	
	/**
	 * @Fileds 装箱
	 */
	public static final int READYTOSHIP = 7;
	
	/**
	 * pending
	 */
	public static final int PENDING = 1;
	
	/**
	 * 确认
	 */
	public static final int COMFIRMED = 2;
	
	
	/**
	 * 待收货
	 */
	public static final int ORDERED = 3;
	
	/**
	 * 已收货
	 */
	public static final int PAYED = 4;
	
	/**
	 * 已完成
	 */
	public static final int FINISHED = 5;
	
	/**
	 * 取消
	 */
	public static final int CANCELED = 6;
	
	/**
	 * 
	 */
	public static final int SETTLEMENT= 7;
	
	/**
	 * 退款中
	 */
	public static final int REFUND = -4;
	
	/**
	 * 判断是否在ProductStatuType 内
	 * 
	 * @param input
	 */
	public static Boolean whetherInOrderStatusType(int input) {
		if (input == PENDING) {
			return true;
		} else if (input == COMFIRMED) {
			return true;
		} else if (input == ORDERED) {
			return true;
		} else if (input == PAYED) {
			return true;
		} else if (input == FINISHED) {
			return true;
		} else if (input == CANCELED) {
			return true;
		}else if (input == SETTLEMENT) {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 根据传入的订单状态，获取订单流转的前一个状态
	 * @param status
	 * @return
	 */
	public static int getLastStatus(int status){
        //上一个状态值，默认为pending
        int lastStatus= PENDING;
        
        //获取当前状态的上一个状态，校验状态机
		switch(status){
		case PENDING:
			lastStatus = PENDING;
			break;
			
		case COMFIRMED:
			lastStatus = PENDING;
			break;
			
		case READYTOSHIP:
			lastStatus = COMFIRMED;
			break;
			
		case ORDERED:
			lastStatus = READYTOSHIP;
			break;
			
		case PAYED:
			lastStatus = ORDERED;
			break;
			
		case FINISHED:
			lastStatus = PAYED;
			break;
			
		case REFUND:
			lastStatus = PENDING;
			break;
			
		case CANCELED:
			lastStatus = REFUND;
			break;
			
		default: 
			lastStatus = PENDING;
			break; 
		}
		
		return lastStatus;
	}
	
	
	/**
	 * 根据传入的订单状态，获取订单流转的下一个状态   只能获取正常下单发货流程状态，取消流程未考虑在内
	 * @param status
	 * @return
	 */
	public static int getNextStatus(int status){
        //下一个状态值，默认为pending
        int nextStatus= PENDING;
        
        //获取当前状态的下一个状态，校验状态机
		switch(status){
		case PENDING:
			nextStatus = COMFIRMED;
			break;
			
		case COMFIRMED:
			nextStatus = ORDERED;
			break;
			
		case ORDERED:
			nextStatus = PAYED;
			break;
			
		case PAYED:
			nextStatus = FINISHED;
			break;
			
		case FINISHED:
			nextStatus = SETTLEMENT;
			break;
			
		default: 
			nextStatus = PENDING;
			break; 
		}
		
		return nextStatus;
	}
	
	
	/**
	 * 根据传入的订单状态，获取数据库对应的状态值
	 * @param status
	 * @return
	 */
	public static Integer getDataBaseStatus(String status){
        //默认为pending
        Integer newStatus= null;
        
		switch(status){
		case "pending":
			newStatus = PENDING;
			break;
			
		case "confirmed":
			newStatus = COMFIRMED;
			break;
			
		case "shipped":
			newStatus = ORDERED;
			break;
			
		case "delivered":
			newStatus = PAYED;
			break;
			
		case "closed":
			newStatus = FINISHED;
			break;
			
		case "cancelled":
			newStatus = CANCELED;
			break;
//			
//		default: 
//			newStatus = PENDING;
//			break; 
		}
		
		return newStatus;
	}
	
}
