package pk.shoplus.common.checker.condition;

import pk.shoplus.common.checker.report.Report;

public interface Condition {
	Report check(String name, String value);
}
