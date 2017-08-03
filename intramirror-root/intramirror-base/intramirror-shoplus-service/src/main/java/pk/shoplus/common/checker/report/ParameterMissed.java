package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class ParameterMissed extends Report {

	public ParameterMissed(String name, String value) {
		super(name, value);
	}

	@Override
	public String getDescription() {
		return "Parameter " + name + " is required.";
	}

	@Override
	public int toStatus() {
		return StatusType.PARAM_EMPTY_OR_NULL;
	}
}
