package pk.shoplus.common.checker;

import java.util.HashMap;

import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;
import pk.shoplus.parameter.StatusType;

public class Reports {
	static private final Pass PASS = new Pass("", "");

	private HashMap<String, Report> reports = new HashMap<>();

	public void add(Report report) {
		this.reports.put(report.getName(), report);
	}

	public Report get(String name) {
		return reports.get(name);
	}

	public boolean isSuccess() {
		return !hasError();
	}

	public boolean hasError() {
		return firstError().toStatus() != StatusType.SUCCESS;
	}

	public Report firstError() {
		for (Report report : reports.values()) {
			if (report.toStatus() != StatusType.SUCCESS) {
				return report;
			}
		}

		return PASS;
	}
}
