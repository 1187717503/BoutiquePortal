package pk.shoplus.parameter;

public class EdsOrderStatusType {

	/**
	 * 对应Pending 待确认  status =1
	 */
	public static final String PLACED = "placed";

	/**
	 * 对应Confirmed 待发货  status =2
	 */
	public static final String PROCESSING = "processing";

	/**
	 * 对应Shipped 待收货  status =3
	 */
	public static final String SHIPPING = "shipping";

	/**
	 * 对应Closed 已关闭 status =5
	 */
	public static final String COMPLETED = "completed";

	/**
	 * 对应Cancelled 已取消  status =6
	 */
	public static final String DELETED = "deleted";



	/**
	 * 根据eds的订单状态 转换成IM对应的订单状态
	 * 
	 * @param status
	 */
	public static int getStatus(String status) {
		if (status.equals(PLACED)) {
			return 1;
		} else if (status.equals(PROCESSING)) {
			return 2;
		} else if (status.equals(SHIPPING)) {
			return 3;
		} else if (status.equals(COMPLETED)) {
			return 5;
		} else if (status.equals(DELETED)) {
			return 6;
		} 
		return 0;
	}



}
