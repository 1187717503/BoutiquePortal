package pk.shoplus.common.checker.condition;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import pk.shoplus.common.Helper;
import pk.shoplus.common.checker.report.NotUrlEncoded;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class UrlDecode implements Condition {
	public static final UrlDecode URL_DECODE = new UrlDecode();

	@Override
	public Report check(String name, String value) {
		try {
			if (!Helper.isNullOrEmpty(value)) {
				value = URLDecoder.decode(value, "UTF-8");
			}

			return new Pass(name, value);
		} catch (UnsupportedEncodingException e) {
			return new NotUrlEncoded(name, value);
		}
	}
}
