package pk.shoplus.parameter;

public class ProductStatusType {

	/**
	 * 待审核
	 */
	public static final int NEW_PENDING = 1;

	/**
	 * 修改后待审核
	 */
	public static final int MODIFY_PENDING = 2;

	/**
	 * 在售
	 */
	public static final int EXISTING = 3;

	/**
	 * 下架
	 */
	public static final int OFF_SALE = 4;

	/**
	 * 拒绝
	 */
	public static final int NEW_REJECTED = 5;

	/**
	 * 修改后拒绝
	 */
	public static final int MODIFY_REJECTED = 6;

	/**
	 * 判断是否在ProductStatuType 内
	 * 
	 * @param input
	 */
	public static Boolean isInProductStatuType(int input) {
		if (input == NEW_PENDING) {
			return true;
		} else if (input == MODIFY_PENDING) {
			return true;
		} else if (input == EXISTING) {
			return true;
		} else if (input == OFF_SALE) {
			return true;
		} else if (input == NEW_REJECTED) {
			return true;
		} else if (input == MODIFY_REJECTED) {
			return true;
		}
		return false;
	}

	public static String changeProductToString(int input) {
		if (isInProductStatuType(input)) {
			if (input == NEW_PENDING) {
				return "NEW_PENDING";
			} else if (input == MODIFY_PENDING) {
				return "MODIFY_PENDING";
			} else if (input == EXISTING) {
				return "EXISTING";
			} else if (input == OFF_SALE) {
				return "OFF_SALE";
			} else if (input == NEW_REJECTED) {
				return "NEW_REJECTED";
			} else if (input == MODIFY_REJECTED) {
				return "MODIFY_REJECTED";
			}
		}
		return "";
	}

}
