package pk.shoplus.common.checker;

import java.util.ArrayList;

import pk.shoplus.common.checker.condition.*;
import pk.shoplus.common.checker.report.Pass;
import pk.shoplus.common.checker.report.Report;

public class ConditionBuilder {
	private String name;
	private String value;

	private ArrayList<Condition> conditions = new ArrayList<>();

	public ConditionBuilder(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public ConditionBuilder add(Condition condition) {
		this.conditions.add(condition);
		return this;
	}

	public Report check() {
		String name = this.name;
		String value = this.value;
		for (Condition condition : conditions) {
			Report report = condition.check(name, value);
			if (report.isError()) {
				return report;
			}
			name = report.getName();
			value = report.getValue();
		}

		return new Pass(name, value);
	}

	public ConditionBuilder required() {
		this.add(Required.REQUIRED);
		return this;
	}

	public ConditionBuilder numeric() {
		this.add(Numeric.NUMERIC);
		return this;
	}

	public ConditionBuilder length(int min, int max) {
		this.add(new LengthRange(min, max));
		return this;
	}

	public ConditionBuilder minLength(int min) {
		this.add(new LengthRange(min, Integer.MAX_VALUE));
		return this;
	}

	public ConditionBuilder maxLength(int max) {
		this.add(new LengthRange(Integer.MIN_VALUE, max));
		return this;
	}

	public ConditionBuilder json() {
		this.add(Json.JSON);
		return this;
	}

	public ConditionBuilder dateRange(String split) {
	    this.add(new DateRange(split));
        return this;
    }

	public ConditionBuilder date() {
		this.add(Date.DATE);
		return this;
	}

	public ConditionBuilder email() {
		this.add(Email.EMAIL);
		return this;
	}

	public ConditionBuilder urlDecode() {
		this.add(UrlDecode.URL_DECODE);
		return this;
	}
}