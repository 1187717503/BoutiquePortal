package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class NotUrlEncoded extends Report {
	public NotUrlEncoded(String name, String value) {
		super(name, value);
	}

	@Override
	public String getDescription() {
		return name + " is not URL encoded.";
	}

	@Override
	public int toStatus() {
		return StatusType.STRING_CONVERT_UNSUPPORTED_ENCODING_EXCEPTION;
	}
}
