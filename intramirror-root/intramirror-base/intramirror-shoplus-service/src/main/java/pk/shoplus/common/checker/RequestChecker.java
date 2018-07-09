package pk.shoplus.common.checker;

import spark.Request;

public class RequestChecker extends StringChecker {
	private Request req;

	public RequestChecker(Request req) {
		this.req = req;
	}

	public ConditionBuilder param(String name) {
		return add(name, req.queryParams(name));
	}
}
