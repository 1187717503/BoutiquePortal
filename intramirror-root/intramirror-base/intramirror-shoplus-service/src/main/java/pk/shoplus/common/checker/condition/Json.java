package pk.shoplus.common.checker.condition;

import pk.shoplus.common.Helper;

import pk.shoplus.common.checker.report.NotJson;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class Json implements Condition {
	public static final Json JSON = new Json();

	@Override
	public Report check(String name, String value) {
		if (!Helper.isNullOrEmpty(value) && !Helper.isGoodJson(value)) {
			return new NotJson(name, value);
		}

		return new Pass(name, value);
	}
}
