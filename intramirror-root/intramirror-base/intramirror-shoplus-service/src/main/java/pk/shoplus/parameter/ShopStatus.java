
package pk.shoplus.parameter;

/**
 * @author author : Jeff
 * @date create_at : 2016年11月29日 下午10:25:57
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class ShopStatus {
	public static final int ACTIVED = 1;
	public static final int PUNISHED = 2;

	/**
	 * 将状态进行转换
	 * @param str
	 * @return
	 */
	public static Integer changeStatusByString(String str) {
		if (str.equalsIgnoreCase("actived")) {
			return ACTIVED;
		} else if (str.equalsIgnoreCase("punished")) {
			return PUNISHED;
		} else {
			return null;
		}
	}
}
