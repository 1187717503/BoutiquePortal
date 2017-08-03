package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class Pass extends Report {

	public Pass(String name, String value) {
		super(name, value);
	}

	@Override
	public String getDescription() {
		return "Passed";
	}

	@Override
	public int toStatus() {
		return StatusType.SUCCESS;
	}
}
