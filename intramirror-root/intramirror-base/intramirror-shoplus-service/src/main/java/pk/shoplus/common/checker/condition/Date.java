package pk.shoplus.common.checker.condition;

import pk.shoplus.common.Helper;
import pk.shoplus.common.checker.report.NotDate;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class Date implements Condition {
	public static final Date DATE = new Date();

	@Override
	public Report check(String name, String value) {

		if (!Helper.isNullOrEmpty(value) && !Helper.isDate(value)) {
			return new NotDate(name, value);
		}

		return new Pass(name, value);
	}
}
