package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class NotJson extends Report {

	public NotJson(String name, String value) {
		super(name, value);
	}

	@Override
	public String getDescription() {
		return name + " is not a json.";
	}

	@Override
	public int toStatus() {
		return StatusType.IS_NOT_GOOD_JSON;
	}
}
