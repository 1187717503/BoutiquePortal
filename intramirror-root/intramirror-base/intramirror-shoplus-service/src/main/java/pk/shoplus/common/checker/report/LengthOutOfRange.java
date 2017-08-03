package pk.shoplus.common.checker.report;

import pk.shoplus.parameter.StatusType;

public class LengthOutOfRange extends Report {
	private String name;
	private int min;
	private int max;
	private int value;

	public LengthOutOfRange() {
	}

	public LengthOutOfRange(String name, int value, int min, int max) {
		this.name = name;
		this.value = value;
		this.min = min;
		this.max = max;
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder("The length of ");
		sb.append(name);
		sb.append(" is too ");
		if (value < min) {
			sb.append("small ");
			sb.append("(expect ");
			sb.append(min);
		} else {
			assert (value > max);
			sb.append("large ");
			sb.append("(expect ");
			sb.append(max);
		}
		sb.append(", got ");
		sb.append(value);
		sb.append(").");
		return sb.toString();
	}

	@Override
	public int toStatus() {
		return StatusType.STRING_LENGTH_ERROR;
	}
}
