package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class NotNumeric extends Report {

	public NotNumeric(String name, String value) {
		super(name, value);
	}

	@Override
	public String getDescription() {
		return name + " is not a number.";
	}

	@Override
	public int toStatus() {
		return StatusType.IS_NOT_NUMERIC;
	}
}
