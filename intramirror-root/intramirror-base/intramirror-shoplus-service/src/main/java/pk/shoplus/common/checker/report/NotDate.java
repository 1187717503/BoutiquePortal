package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class NotDate extends Report {

	public NotDate(String name, String value) {
		super(name, value);
	}

	@Override
	public String getDescription() {
		return name + " is not a date.";
	}

	@Override
	public int toStatus() {
		return StatusType.IS_NOT_VALID_DATE;
	}
}
