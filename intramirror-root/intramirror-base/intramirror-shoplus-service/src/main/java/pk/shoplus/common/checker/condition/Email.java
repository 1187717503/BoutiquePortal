package pk.shoplus.common.checker.condition;

import pk.shoplus.common.Helper;
import pk.shoplus.common.checker.report.NotEmail;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class Email implements Condition {

	public static final Email EMAIL = new Email();

	@Override
	public Report check(String name, String value) {
		if (!Helper.isNullOrEmpty(value) && !Helper.checkEmail(value)) {
			return new NotEmail(name, value);
		}

		return new Pass(name, value);
	}
}
