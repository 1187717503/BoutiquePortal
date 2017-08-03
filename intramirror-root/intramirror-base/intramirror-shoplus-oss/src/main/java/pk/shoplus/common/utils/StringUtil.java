package pk.shoplus.common.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件工具类<br>
 * 〈功能详细描述〉
 *
 * @author jerry
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class StringUtil {// NO_UCD
	/**
	 * UTF-8的三个字节的BOM
	 */
	public static final byte[] BOM = new byte[] { (byte) 239, (byte) 187, (byte) 191 };

	/**
	 * 十六进制字符
	 */
	public static final char HexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * html标签正则表达式
	 */
	public static Pattern PHtml = Pattern.compile("<[^>]+>", Pattern.DOTALL);

	/**
	 * html注释正则表达式
	 */
	public static Pattern PComment = Pattern.compile("<\\!\\-\\-.*?\\-\\->", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // 定义注释的正则表达式

	/**
	 * script标签正则表达式
	 */
	public static Pattern PScript = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // 定义script的正则表达式

	/**
	 * style标签正则表达式
	 */
	public static Pattern PStyle = Pattern.compile("<style[^>]*?>[\\s\\S]*?<\\/style>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // 定义style的正则表达式

	/**
	 * 将字符串进行md5摘要，然后输出成十六进制形式
	 */
	public static String md5Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md = md5.digest(src.getBytes());
			return hexEncode(md);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将字符串进行sh1摘要，然后输出成十六进制形式
	 */
	public static String sha1Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("SHA1");
			byte[] md = md5.digest(src.getBytes());
			return hexEncode(md);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将二进制数组转换成十六进制表示
	 */
	public static String hexEncode(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = HexDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = HexDigits[0x0F & data[i]];
		}
		return new String(out);
	}

	/**
	 * 将字节数组转换成二制形式字符串
	 */
	public static String byteToBin(byte[] bs) {
		char[] cs = new char[bs.length * 9];
		for (int i = 0; i < bs.length; i++) {
			byte b = bs[i];
			int j = i * 9;
			cs[j] = (b >>> 7 & 1) == 1 ? '1' : '0';
			cs[j + 1] = (b >>> 6 & 1) == 1 ? '1' : '0';
			cs[j + 2] = (b >>> 5 & 1) == 1 ? '1' : '0';
			cs[j + 3] = (b >>> 4 & 1) == 1 ? '1' : '0';
			cs[j + 4] = (b >>> 3 & 1) == 1 ? '1' : '0';
			cs[j + 5] = (b >>> 2 & 1) == 1 ? '1' : '0';
			cs[j + 6] = (b >>> 1 & 1) == 1 ? '1' : '0';
			cs[j + 7] = (b & 1) == 1 ? '1' : '0';
			cs[j + 8] = ',';
		}
		return new String(cs);
	}

	/**
	 * 转换字节数组为十六进制字串
	 */

	public static String byteArrayToHexString(byte[] b) {
		StringBuilder resultSb = new StringBuilder();
		for (byte element : b) {
			resultSb.append(byteToHexString(element));
			resultSb.append(" ");
		}
		return resultSb.toString();
	}

	/**
	 * 字节转换为十六进制字符串
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return HexDigits[d1] + "" + HexDigits[d2];
	}

	/**
	 * 判断指定的二进制数组是否是一个UTF-8字符串
	 */
	public static boolean isUTF8(byte[] bs) {
		if (bs.length > 3 && bs[0] == BOM[0] && bs[1] == BOM[1] && bs[2] == BOM[2]) {
			return true;
		}
		int encodingBytesCount = 0;
		for (byte element : bs) {
			byte c = element;
			if (encodingBytesCount == 0) {
				if ((c & 0x80) == 0) {// ASCII字符范围0x00-0x7F
					continue;
				}
				if ((c & 0xC0) == 0xC0) {
					encodingBytesCount = 1;
					c <<= 2;
					// 非ASCII第一字节用来存储长度
					while ((c & 0x80) == 0x80) {
						c <<= 1;
						encodingBytesCount++;
					}
				} else {
					return false;// 不符合 UTF8规则
				}
			} else {
				// 后续字集必须以10开头
				if ((c & 0xC0) == 0x80) {
					encodingBytesCount--;
				} else {
					return false;// 不符合 UTF8规则
				}
			}
		}
		if (encodingBytesCount != 0) {
			return false;// 后续字节数不符合UTF8规则
		}
		return true;
	}


	/**
	 * 将一个字符串按照指下的分割字符串分割成数组。分割字符串不作正则表达式处理，<br>
	 * String类的split方法要求以正则表达式分割字符串，有时较为不便，可以转为采用本方法。
	 */
	public static String[] splitEx(String str, String spliter) {
		char escapeChar = '\\';
		if (spliter.equals("\\")) {
			escapeChar = '&';
		}
		return splitEx(str, spliter, escapeChar);
	}

	/**
	 * 将一个字符串按照指下的分割字符串分割成数组。分割字符串不作正则表达式处理，<br>
	 * String类的split方法要求以正则表达式分割字符串，有时较为不便，可以转为采用本方法。
	 */
	public static String[] splitEx(String str, String spliter, char escapeChar) {
		if (str == null) {
			return new String[]{};
		}
		ArrayList<String> list = new ArrayList<String>();
		if (spliter == null || spliter.equals("") || str.length() < spliter.length()) {
			return new String[] { str };
		}
		int length = spliter.length();
		int lastIndex = 0;
		int lastStart = 0;
		while (true) {
			int i = str.indexOf(spliter, lastIndex);
			if (i >= 0) {
				if (i > 0 && str.charAt(i - 1) == escapeChar) {
					lastIndex = i + 1;
					continue;
				}
				list.add(str.substring(lastStart, i));
				lastStart = lastIndex = i + length;
			} else {
				if (lastStart <= str.length()) {
					list.add(str.substring(lastStart));
				}
				break;
			}
		}
		String[] arr = new String[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}


	/**
	 * 将一个字符串中的指定片段全部替换，替换过程中不进行正则处理。<br>
	 * 使用String类的replaceAll时要求片段以正则表达式形式给出，有时较为不便，可以转为采用本方法。<br>
	 * 另:本方法和String类的replace(CharSequence target, CharSequence replacement)方法效果一样，但性能要快两倍。
	 */
	public static String replaceEx(String str, String subStr, String reStr) {
		if (str == null || str.length() == 0 || reStr == null) {
			return str;
		}
		if (subStr == null || subStr.length() == 0 || subStr.length() > str.length()) {
			return str;
		}
		StringBuilder sb = null;
		int lastIndex = 0;
		while (true) {
			int index = str.indexOf(subStr, lastIndex);
			if (index < 0) {
				break;
			} else {
				if (sb == null) {
					sb = new StringBuilder();
				}
				sb.append(str.substring(lastIndex, index));
				sb.append(reStr);
			}
			lastIndex = index + subStr.length();
		}
		if (lastIndex == 0) {
			return str;
		}
		sb.append(str.substring(lastIndex));
		return sb.toString();
	}

	/**
	 * 不区分大小写的全部替换，替换时使用了正则表达式。
	 */
	public static String replaceAllIgnoreCase(String source, String oldstring, String newstring) {
		Pattern p = Pattern.compile(oldstring, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(source);
		return m.replaceAll(newstring);
	}

	/**
	 * 以指定编码进行URL编码
	 */
	public static String urlEncode(String str, String charset) {
		try {
			return URLEncoder.encode(str, charset);
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 以指定编码进行URL解码
	 */
	public static String urlDecode(String str, String charset) {
		try {
			return URLDecoder.decode(str, charset);
		} catch (Exception e) {
			return str;
		}
	}


	/**
	 * Javascript中escape的JAVA实现
	 */
	public static String escape(String src) {
		char j;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j)) {
				sb.append(j);
			} else if (j < 256) {
				sb.append("%");
				if (j < 16) {
					sb.append("0");
				}
				sb.append(Integer.toString(j, 16));
			} else {
				sb.append("%u");
				sb.append(Integer.toString(j, 16));
			}
		}
		return sb.toString();
	}

	/**
	 * 在一字符串左边填充若干指定字符，使其长度达到指定长度
	 */
	public static String leftPad(String srcString, char c, int length) {
		if (srcString == null) {
			srcString = "";
		}
		int tLen = srcString.length();
		int i, iMax;
		if (tLen >= length) {
			return srcString;
		}
		iMax = length - tLen;
		StringBuilder sb = new StringBuilder();
		for (i = 0; i < iMax; i++) {
			sb.append(c);
		}
		sb.append(srcString);
		return sb.toString();
	}

	/**
	 * 将长度超过length的字符串截取length长度，若不足，则返回原串
	 */
	public static String subString(String src, int length) {
		if (src == null) {
			return null;
		}
		int i = src.length();
		if (i > length) {
			return src.substring(0, length);
		} else {
			return src;
		}
	}

	/**
	 * 将长度超过length的字符串截取length长度，若不足，则返回原串。<br>
	 * 其中ASCII字符算1个长度单位，非ASCII字符算2个长度单位。
	 */
	public static String subStringEx(String src, int length) {
		if (src == null) {
			return null;
		}
		int m = 0;
		try {
			byte[] b = src.getBytes("Unicode");
			boolean byteFlag = b[0] == -2;// 表明unicode字节顺中的高位在前
			for (int i = 2; i < b.length; i += 2) {
				if (b[byteFlag ? i : i + 1] == 0) {// ascii字符
					m++;
				} else {
					m += 2;
				}
				if (m > length) {
					return src.substring(0, (i - 2) / 2);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("String.getBytes(\"Unicode\") failed");
		}
		return src;
	}

	/**
	 * 在一字符串右边填充若干指定字符，使其长度达到指定长度
	 */
	public static String rightPad(String srcString, char c, int length) {
		if (srcString == null) {
			srcString = "";
		}
		int tLen = srcString.length();
		int i, iMax;
		if (tLen >= length) {
			return srcString;
		}
		iMax = length - tLen;
		StringBuilder sb = new StringBuilder();
		sb.append(srcString);
		for (i = 0; i < iMax; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 半角转全角，转除英文字母之外的字符
	 */
	public static String toSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] > 64 && c[i] < 91 || c[i] > 96 && c[i] < 123) {
				continue;
			}

			if (c[i] < 127) {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 半角转全角，转所有能转为全角的字符，包括英文字母
	 */
	public static String toNSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}

			if (c[i] < 127) {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角的函数 全角空格为12288，半角空格为32 <br>
	 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	 */
	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375) {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}


	/**
	 * 首字母大写
	 */
	public static String capitalize(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return new StringBuilder().append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
	}

	/**
	 * 字符串是否为空，null或空字符串时返回true,其他情况返回false
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 字符串是否不为空，null或空字符串时返回false,其他情况返回true
	 */
	public static boolean isNotEmpty(String str) {
		return !StringUtil.isEmpty(str);
	}

	/**
	 * 字符串是否为空，null或空字符或者为"null"字符串时返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		return isEmpty(str) || "null".equals(str);
	}

	/**
	 * 字符串是否不为空，null,空字符串,或者"null" 字符串时返回false,其他情况返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotNull(String str) {
		return isNotEmpty(str) && !"null".equals(str);
	}

	/**
	 * 字符串为空时返回defaultString，否则返回原串
	 */
	public static final String noNull(String string, String defaultString) {
		return isEmpty(string) ? defaultString : string;
	}

	/**
	 * 字符串为空时返回defaultString，否则返回空字符串
	 */
	public static final String noNull(String string) {
		return noNull(string, "");
	}

	/**
	 * 将一个数组拼成一个字符串，数组项之间以逗号分隔
	 */
	public static String join(Object arr) {
		return join(arr, ",");
	}

	/**
	 * 将一个数组以指定的分隔符拼成一个字符串
	 */
	public static String join(Object arr, String spliter) {
		if (arr == null) {
			return null;
		}
		if (!arr.getClass().isArray()) {
			return arr.toString();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Array.getLength(arr); i++) {
			if (i != 0) {
				sb.append(spliter);
			}
			sb.append(Array.get(arr, i));
		}
		return sb.toString();
	}

	/**
	 * 将一个数组拼成一个字符串，数组项之间以逗号分隔
	 */
	public static String join(Object[] arr) {
		return join(arr, ",");
	}

	/**
	 * 将一个二维数组拼成一个字符串，第二维以逗号分隔，第一维以换行分隔
	 */
	public static String join(Object[][] arr) {
		return join(arr, "\n", ",");
	}

	/**
	 * 将一个数组以指定的分隔符拼成一个字符串
	 */
	public static String join(Object[] arr, String spliter) {
		if (arr == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i != 0) {
				sb.append(spliter);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * 将一个二维数组拼成一个字符串，第二维以指定的spliter2参数分隔，第一维以换行spliter1分隔
	 */
	public static String join(Object[][] arr, String spliter1, String spliter2) {
		if (arr == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i != 0) {
				sb.append(spliter2);
			}
			sb.append(join(arr[i], spliter2));
		}
		return sb.toString();
	}

	/**
	 * 将一个List拼成一个字符串，数据项之间以逗号分隔
	 */
	public static String join(Collection<?> list) {
		return join(list, ",");
	}

	/**
	 * 将一个List拼成一个字符串，数据项之间以指定的参数spliter分隔
	 */
	public static String join(Collection<?> c, String spliter) {
		if (c == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object name : c) {
			if (first) {
				first = false;
			} else {
				sb.append(spliter);
			}
			sb.append(name);
		}
		return sb.toString();
	}

	/**
	 * 计算一个字符串中某一子串出现的次数
	 */
	public static int count(String str, String findStr) {
		int lastIndex = 0;
		int length = findStr.length();
		int count = 0;
		int start = 0;
		while ((start = str.indexOf(findStr, lastIndex)) >= 0) {
			lastIndex = start + length;
			count++;
		}
		return count;
	}

	public static final Pattern PLetterOrDigit = Pattern.compile("^\\w*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	public static final Pattern PLetter = Pattern.compile("^[A-Za-z]*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	public static final Pattern PDigit = Pattern.compile("^\\d*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	/**
	 * 判断字符串是否全部由数字或字母组成
	 */
	public static boolean isLetterOrDigit(String str) {
		return PLetterOrDigit.matcher(str).find();
	}

	/**
	 * 判断字符串是否全部字母组成
	 */
	public static boolean isLetter(String str) {
		return PLetter.matcher(str).find();
	}

	/**
	 * 判断字符串是否全部由数字组成
	 */
	public static boolean isDigit(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		return PDigit.matcher(str).find();
	}

	private static Pattern PChinese = Pattern.compile("[^\u4e00-\u9fa5]+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	/**
	 * 判断字符串中是否含有中文字符
	 */
	public static boolean containsChinese(String str) {
		if (!PChinese.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	private static Pattern PID = Pattern.compile("[\\w\\s\\_\\.\\,]*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	/**
	 * 检查ID，防止SQL注入，主要是在删除时传入多个ID时使用
	 */
	public static boolean checkID(String str) {
		if (StringUtil.isEmpty(str)) {
			return true;
		}
		if (PID.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 得到URL的文件扩展名
	 */
	public static String getURLExtName(String url) {
		if (isEmpty(url)) {
			return null;
		}
		int index1 = url.indexOf('?');
		if (index1 == -1) {
			index1 = url.length();
		}
		int index2 = url.lastIndexOf('.', index1);
		if (index2 == -1) {
			return null;
		}
		int index3 = url.indexOf('/', 8);
		if (index3 == -1) {
			return null;
		}
		String ext = url.substring(index2 + 1, index1);
		if (ext.matches("[^\\/\\\\]*")) {
			return ext;
		}
		return null;
	}

	/**
	 * 得到URL的文件名
	 */
	public static String getURLFileName(String url) {
		if (isEmpty(url)) {
			return null;
		}
		int index1 = url.indexOf('?');
		if (index1 == -1) {
			index1 = url.length();
		}
		int index2 = url.lastIndexOf('/', index1);
		if (index2 == -1 || index2 < 8) {
			return null;
		}
		String ext = url.substring(index2 + 1, index1);
		return ext;
	}


	/**
	 * 快速地Html解码，只解码&amp;nbsp;、&amp;lt;、&amp;gt;、&amp;quot;、&amp;apos;、&amp;amp;，<br>
	 * 以及&amp#0032;、&amp#x0032;之类的以数字标识的Unicode字符
	 */
	public static String quickHtmlDecode(String html) {
		boolean flag = false;
		for (int j = 0; j < html.length(); j++) {
			if (html.charAt(j) == '&') {
				flag = true;
				break;
			}
		}
		if (!flag) {
			return html;
		}
		char[] buf = new char[html.length()];
		int j = 0;
		for (int i = 0; i < html.length(); i++) {
			char c = html.charAt(i);
			if (c == '&') {
				if (html.startsWith("&quot;", j)) {
					buf[j++] = '\"';
					i += 5;
					continue;
				} else if (html.startsWith("&amp;", i)) {
					buf[j++] = '&';
					i += 4;
					continue;
				} else if (html.startsWith("&lt;", i)) {
					buf[j++] = '<';
					i += 3;
					continue;
				} else if (html.startsWith("&gt;", i)) {
					buf[j++] = '>';
					i += 3;
					continue;
				} else if (html.startsWith("&apos;", i)) {
					buf[j++] = '\'';
					i += 5;
				} else if (html.startsWith("&nbsp;", i)) {
					buf[j++] = ' ';
					i += 5;
					continue;
				} else if (html.startsWith("&#32;", i)) {
					buf[j++] = ' ';
					i += 4;
					continue;
				} else if (html.charAt(i + 1) == '#') {
					int k = i + 2;
					flag = false;
					int radix = 10;
					if (html.charAt(k) == 'x' || html.charAt(k) == 'X') {// 十六制进
						radix = 16;
						k++;
					}
					for (; k < i + 9 && k < html.length(); k++) {
						if (html.charAt(k) == ';') {
							flag = true;
							break;
						}
					}
					if (flag) {
						char ch = (char) Integer.parseInt(html.substring(radix == 10 ? i + 2 : i + 3, k), radix);
						buf[j++] = ch;
						i += k;
					}
				}
			} else {
				buf[j++] = c;
			}
		}
		return new String(buf, 0, j);
	}

}
