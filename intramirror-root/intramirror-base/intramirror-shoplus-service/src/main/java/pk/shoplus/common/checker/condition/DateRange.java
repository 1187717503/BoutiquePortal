package pk.shoplus.common.checker.condition;

import pk.shoplus.common.Helper;
import pk.shoplus.common.checker.report.NotDate;
import pk.shoplus.common.checker.report.NotDateRange;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class DateRange implements Condition {

	private String split;

	public DateRange(String split) {
		this.split = split;
	}

	@Override
	public Report check(String name, String value) {

		if (!Helper.isNullOrEmpty(value) && !Helper.isDateRange(value, this.split)) {
			return new NotDateRange(name, value);
		}

		return new Pass(name, value);
	}
}
