package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

import java.util.HashMap;
import java.util.Map;

public abstract class Report {
	protected String name;
	protected String value;

	public Report() {
	}

	public Report(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public abstract String getDescription();

	public abstract int toStatus();

	@Override
	public String toString() {
		return this.getDescription();
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> result = new HashMap<>();
		result.put("status", toStatus());
		result.put("description", getDescription());
		return result;
	}

	public boolean isError() {
		return toStatus() != StatusType.SUCCESS;
	}
}
