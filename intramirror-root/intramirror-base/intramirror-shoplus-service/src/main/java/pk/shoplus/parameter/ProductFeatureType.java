package pk.shoplus.parameter;
public class ProductFeatureType {

	/**
	 * 正常
	 */
	public static final int NORMAL = 1;

	/**
	 * 热卖
	 */
	public static final int HOT_SALE = 2;

	/**
	 * 推荐
	 */
	public static final int RECOMMENDATION = 3;
	
	/**
	 * 判断是否在feature内
	 * @param input
	 * @return
	 */
	public static Boolean isInProductFeatureType(int input) {
		if (input == NORMAL) {
			return true;
		} else if (input == HOT_SALE) {
			return true;
		} else if (input == RECOMMENDATION) {
			return true;
		}
		return false;
	}

}
