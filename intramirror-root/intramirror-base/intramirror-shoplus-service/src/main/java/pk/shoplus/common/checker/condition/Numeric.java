package pk.shoplus.common.checker.condition;

import pk.shoplus.common.Helper;
import pk.shoplus.common.checker.report.NotNumeric;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class Numeric implements Condition {
	public static final Numeric NUMERIC = new Numeric();

	@Override
	public Report check(String name, String value) {
		if (!Helper.isNullOrEmpty(value) && !Helper.isNumeric(value)) {
			return new NotNumeric(name, value);
		}

		return new Pass(name, value);
	}
}
