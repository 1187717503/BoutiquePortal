
package com.intramirror.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author author : Jeff, Minwei
 * @date create_at : 2016年11月3日 下午12:38:08
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class Helper {
	/**
	 * MD5加密算法
	 *
	 * @param code
	 * @return
	 */
	public static String md5Jdk(String code) {
		String temp = "";
		try {
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");
			byte[] encodeMD5Digest = md5Digest.digest(code.getBytes());
			temp = convertByteToHexString(encodeMD5Digest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * 将字节数组转化为十六进制输出
	 *
	 * @param bytes
	 * @return
	 */
	private static String convertByteToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			int temp = bytes[i] & 0xff;
			String tempHex = Integer.toHexString(temp);
			if (tempHex.length() < 2) {
				result += "0" + tempHex;
			} else {
				result += tempHex;
			}
		}
		return result;
	}

	/**
	 * 验证邮箱
	 *
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/*
	 * 大陆身份证验证 public static boolean IDCardValidate(String IDStr) { return
	 * String
	 */

	public static String IDCardValidate(String IDStr) {
		String errorInfo = "";// 记录错误信息
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			errorInfo = "身份证号码长度应该为15位或18位。";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 数字 除最后以为都为数字 ================
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "身份证生日无效。";
			return errorInfo;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
					|| (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				errorInfo = "身份证生日不在有效范围。";
				return errorInfo;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "身份证月份无效";
			return errorInfo;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "身份证日期无效";
			return errorInfo;
		}
		// =====================(end)=====================

		// ================ 地区码时候有效 ================
		Hashtable<Integer, String> h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "身份证地区编码错误。";
			return errorInfo;
		}
		// ==============================================

		// ================ 判断最后一位的值 ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (Ai.equals(IDStr) == false) {
				errorInfo = "身份证无效，不是合法的身份证号码";
				return errorInfo;
			}

		} else {
			return "";
		}
		// =====================(end)=====================
		return "";
	}

	/**
	 * 功能：设置地区编码
	 */
	private static Hashtable<Integer, String> GetAreaCode() {
		Hashtable<Integer, String> hashtable = new Hashtable<Integer, String>();
		hashtable.put(11, "北京");
		hashtable.put(12, "天津");
		hashtable.put(13, "河北");
		hashtable.put(14, "山西");
		hashtable.put(15, "内蒙古");
		hashtable.put(21, "辽宁");
		hashtable.put(22, "吉林");
		hashtable.put(23, "黑龙江");
		hashtable.put(31, "上海");
		hashtable.put(32, "江苏");
		hashtable.put(33, "浙江");
		hashtable.put(34, "安徽");
		hashtable.put(35, "福建");
		hashtable.put(36, "江西");
		hashtable.put(37, "山东");
		hashtable.put(41, "河南");
		hashtable.put(42, "湖北");
		hashtable.put(43, "湖南");
		hashtable.put(44, "广东");
		hashtable.put(45, "广西");
		hashtable.put(46, "海南");
		hashtable.put(50, "重庆");
		hashtable.put(51, "四川");
		hashtable.put(52, "贵州");
		hashtable.put(53, "云南");
		hashtable.put(54, "西藏");
		hashtable.put(61, "陕西");
		hashtable.put(62, "甘肃");
		hashtable.put(63, "青海");
		hashtable.put(64, "宁夏");
		hashtable.put(65, "新疆");
		hashtable.put(71, "台湾");
		hashtable.put(81, "香港");
		hashtable.put(82, "澳门");
		hashtable.put(91, "国外");
		return hashtable;
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 *
	 * @param strDate
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern.compile(
				"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

    /**
     * 判断是否是时间段
     *
     * @param strDateRange
     * @param split
     * @return
     */
	public static boolean isDateRange(String strDateRange, String split) {
		String[] result = strDateRange.split(split);
        if (result.length == 2) {
            boolean isDateRange = true;
            for (String r : result) {
                isDateRange = isValidDate(r) && isDateRange;
            }

            return isDateRange;
        } else {
            return false;
        }
	}

	/**
	 * 验证手机号码
	 *
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern regex = Pattern
					.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 判断string 是否null，如果是则返回 ""，否则返回原字符串
	 *
	 * @return
	 */
	public static String convertNullToEmpty(String inputStr) {
		if (inputStr == null) {
			return "";
		} else {
			return inputStr;
		}
	}

	/**
	 * 判断字符串是null或者空
	 *
	 * @param inputStr
	 * @return
	 */
	public static boolean isNullOrEmpty(String inputStr) {
		if (inputStr == null || inputStr.trim().length() <= 0) {
			return true;
		}
		return false;

	}

	/**
	 * 判断string 能否转换成 date － 待测试 20160815 by Caijl
	 *
	 * @param str
	 * @return
	 */
	public static boolean isValidDate(String str) {
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			formatDateTime.parse(str);
			convertSuccess = true;
			return convertSuccess;
		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}

		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		try {
			formatDate.parse(str);
			convertSuccess = true;
			return convertSuccess;
		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}

		return convertSuccess;
	}

	/**
	 * 将 String 转换成 Date
	 *
	 * @param string
	 *            经过 isValidDate 检验过的格式
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToDate(String string) throws ParseException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.parse(string);
		} catch (ParseException e) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(string);
		}
	}

	/**
	 * 判断字符串是否可转换为数值类型
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		/*
		 * Pattern pattern = Pattern.compile("[0-9]*"); Matcher isNum =
		 * pattern.matcher(str.trim()); if (!isNum.matches()) { return false; }
		 * return true;
		 */
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否可转换为double类型
	 *
	 * @param str
	 * @return
	 */
	public static boolean isDouble(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断string 能否转成 json － 待测试 20160815 by Caijl
	 *
	 * @param str
	 * @return
	 */
	public static boolean isGoodJson(String str) {
		try {
			new JsonParser().parse(str);
			// System.out.println("element = " + element);
			return true;
		} catch (JsonParseException e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * <p>
	 * return false if the parameter is null.
	 * </p>
	 *
	 * @param param
	 *            the argument to be checked
	 */
	public static Boolean checkNotNull(Object param) {
		if (param == null) {
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * return false if the parameter is not positive or not -1.
	 * </p>
	 *
	 * @param name
	 *            the name of the argument showed in exception
	 */
	public static Boolean checkIsPositive(long param) {
		if (param <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * Check the list whether be null or empty.
	 * </p>
	 *
	 * @param list
	 *            the list to check
	 * @return the list whether be null or empty
	 */
	public static boolean checkListNotNullAndNotEmpty(List<Object> list) {
		if (list != null && !list.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Check the string whether be null or empty.
	 * </p>
	 *
	 * @param str
	 *            the string to check
	 * @return the string whether be null or empty
	 */
	public static boolean checkStringNotNullAndNotEmptyOfBoolean(String str) {
		if (str != null && str.trim().length() != 0) {
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * return false if the parameter is not positive or not
	 * </p>
	 *
	 * @param param
	 *            the argument to be checked
	 */
	public static Boolean checkPageNumberAndSize(String name, int param) {
		if (param <= 0 && param != -1) {
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * return false if the parameter is not positive or not
	 * </p>
	 *
	 * @param param
	 *            the argument to be checked
	 * @throws Exception
	 */
	public static String decodeStringToUTF8(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (Exception el) {
			el.printStackTrace();
		}
		return "";
	}

	public static Date getCurrentUTCTime() {
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		return Date.from(utc.toInstant());
	}

	/**
	 * 将服务器时区时间转换为UTC时间
	 *
	 * @param time
	 *            Date对象
	 * @return
	 */
	public static Date getCurrentTimeToUTCWithDate() {
		Calendar cal = Calendar.getInstance();

		cal.setTime(new Date());

		// 2、取得时间偏移量：
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);

		// 3、取得夏令时差：
		int dstOffset = cal.get(Calendar.DST_OFFSET);

		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		// 之后调用cal.get(int x)或cal.getTimeInMillis()方法所取得的时间即是UTC标准时间。
		return new Date(cal.getTimeInMillis());
	}


	/**
	 * 将服务器时区时间转换为UTC时间
	 *
	 * @param time
	 *            Date对象
	 * @return
	 */
	public static Date getCurrentTimeToUTCWithDate(Date date) {
		Calendar cal = Calendar.getInstance();

		if(date != null){
			cal.setTime(date);
		}else{
			cal.setTime(new Date());
		}


		// 2、取得时间偏移量：
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);

		// 3、取得夏令时差：
		int dstOffset = cal.get(Calendar.DST_OFFSET);

		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		// 之后调用cal.get(int x)或cal.getTimeInMillis()方法所取得的时间即是UTC标准时间。
		return new Date(cal.getTimeInMillis());
	}

	/**
	 * UTC时间转成PK
	 * 
	 * @param time
	 *            String (格式 :"yyyy-MM-dd HH:mm:ss" "yyyy-MM-dd"(默认追加 00:00:00))
	 * @return
	 * @throws Exception
	 */
	public static String changeTimeToPK(Date time) {
		TimeZone gmtTime = TimeZone.getTimeZone("IET");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(gmtTime);
		System.out.println("IETTime: " + format.format(time));
		return format.format(time);
	}

	public static <T> T convertMapToBean(Map<String, Object> map, Class<T> klass) {
		ObjectMapper m = new ObjectMapper();
		return m.convertValue(map, klass);
	}

	public static Map<String, Object> convertBeanToMap(Object bean) {
		ObjectMapper m = new ObjectMapper();
		return m.convertValue(bean, Map.class);
	}
	
	/**
	 * 生成UUID
	 * @return
	 */
	public static String getUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}