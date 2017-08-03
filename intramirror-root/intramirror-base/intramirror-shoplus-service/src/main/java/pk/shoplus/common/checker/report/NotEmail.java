package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class NotEmail extends Report {

	public NotEmail(String name, String value) {
		super(name, value);
	}

	@Override
	public String getDescription() {
		return name + " is not an email.";
	}

	@Override
	public int toStatus() {
		return StatusType.EMAIL_ADDRESS_ERROR;
	}

}
