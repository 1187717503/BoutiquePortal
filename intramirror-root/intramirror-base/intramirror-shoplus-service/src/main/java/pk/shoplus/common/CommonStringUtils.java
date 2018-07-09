package pk.shoplus.common;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author yfding
 * @since 2017-5-23 14:46:03
 */
public class CommonStringUtils {
	
	public static boolean isNotBlank(String...strings) {
		for(String str : strings) {
			if(StringUtils.isBlank(str)) {
				return false;
			}
		}
		return true;
	}
}
