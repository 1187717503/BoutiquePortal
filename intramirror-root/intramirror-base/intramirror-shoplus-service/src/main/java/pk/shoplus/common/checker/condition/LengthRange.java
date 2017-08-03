package pk.shoplus.common.checker.condition;

import pk.shoplus.common.Helper;
import pk.shoplus.common.checker.report.LengthOutOfRange;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class LengthRange implements Condition {

	private int min;
	private int max;

	public LengthRange(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public Report check(String name, String value) {
		int length = value.length();
		if (!Helper.isNullOrEmpty(value) && (length < min || length > max)) {
			return new LengthOutOfRange(name, length, min, max);
		}
		return new Pass(name, value);
	}
}
