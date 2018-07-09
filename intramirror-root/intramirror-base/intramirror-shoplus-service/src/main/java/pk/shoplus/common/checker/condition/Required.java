package pk.shoplus.common.checker.condition;

import pk.shoplus.common.Helper;
import pk.shoplus.common.checker.report.ParameterMissed;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class Required implements Condition {
	public static final Required REQUIRED = new Required();

	@Override
	public Report check(String name, String value) {
		if (Helper.isNullOrEmpty(value)) {
			return new ParameterMissed(name, value);
		}

		return new Pass(name, value);
	}
}
