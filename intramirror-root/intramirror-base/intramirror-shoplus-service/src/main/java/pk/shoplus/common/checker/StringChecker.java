package pk.shoplus.common.checker;

import java.util.HashMap;

public class StringChecker {
	private HashMap<String, ConditionBuilder> builders = new HashMap<>();

	public ConditionBuilder add(String name, String value) {
		ConditionBuilder builder = new ConditionBuilder(name, value);
		this.builders.put(name, builder);
		return builder;
	}

	public Reports check() {
		Reports reports = new Reports();
		for (ConditionBuilder builder : builders.values()) {
			reports.add(builder.check());
		}
		return reports;
	}

	public void clear() {
		this.builders.clear();
	}
}