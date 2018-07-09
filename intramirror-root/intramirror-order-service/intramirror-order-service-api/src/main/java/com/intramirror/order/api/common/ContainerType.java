package com.intramirror.order.api.common;

/**
 * 打包状态
 * @author 123
 *
 */
public final class ContainerType {

	/**
	 * @Fields 装箱打开状态
	 */
	public static final int OPEN = 1;
	
	/**
	 * @Fields 装箱关闭状态
	 */
	public static final int CLOSED = 2;
	
	/**
	 * @Fields 装箱装船状态
	 */
	public static final int SHIPPED = 3;
	
	/**
	 * @Fields 装箱交货状态
	 */
	public static final int DELIVERED = 4;
	
	/**
	 * @Fields 收到状态
	 */
	public static final int RECEIVED = 5;
	
	/**
	 * @Fields 支付状态
	 */
	public static final int SUBDELIVERED = 6;
	
	
	/**
	 * @Fields 待审核
	 */
	public static final int PENDINGAUDIT = 1;
	
	/**
	 * @Fields 审核中
	 */
	public static final int INAUDIT = 2;
	
	/**
	 * @Fields 审核通过
	 */
	public static final int PASSED = 3;
	
	/**
	 * @Fields 审核失败
	 */
	public static final int AUDITFAILURE = 4;
	
	/**
	 * 根据传入的container状态，获取流转的前一个状态
	 * @param status
	 * @return
	 */
	public static int getLastStatus(int status){
        //上一个状态值，默认为open
        int lastStatus= OPEN;
        //获取当前状态的上一个状态，校验状态机
		switch(status){
		case OPEN:
			lastStatus = OPEN;
			break;
			
		case CLOSED:
			lastStatus = OPEN;
			break;
			
		case SHIPPED:
			lastStatus = CLOSED;
			break;
			
		case DELIVERED:
			lastStatus = SHIPPED;
			break;
			
		default: 
			lastStatus = OPEN;
			break; 
		}
		
		return lastStatus;
	}
	
}
