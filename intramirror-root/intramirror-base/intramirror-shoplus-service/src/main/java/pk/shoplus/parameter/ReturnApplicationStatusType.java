package pk.shoplus.parameter;
public class ReturnApplicationStatusType {
	/**
	 * pending
	 */
	public static final int PENDING = 1;
	
	/**
	 * 确认
	 */
	public static final int COMFIRMED = 2;
	
	
	/**
	 * 下单
	 */
	public static final int ORDERED = 3;
	
	/**
	 * 支付
	 */
	public static final int APPEAL = 4;
	
	/**
	 * 已完成
	 */
	public static final int FINISHED = 5;
	
	/**
	 * 拒绝
	 */
	public static final int REJECTED = 6;
	
	/**
	 * 结算
	 */
	public static final int SETTLEMENT = 7;
	
	/**
	 * 取消
	 */
	public static final int CANCEL = 8;
	
	/**
	 * 判断是否在ProductStatuType 内
	 * 
	 * @param input
	 */
	public static Boolean whetherInReturnApplicationStatusType(int input) {
		if (input == PENDING) {
			return true;
		} else if (input == COMFIRMED) {
			return true;
		} else if (input == ORDERED) {
			return true;
		} else if (input == APPEAL) {
			return true;
		} else if (input == FINISHED) {
			return true;
		} else if (input == REJECTED) {
			return true;
		} else if (input == SETTLEMENT) {
			return true;
		} else if (input == CANCEL) {
			return true;
		}
		return false;
	}
}
