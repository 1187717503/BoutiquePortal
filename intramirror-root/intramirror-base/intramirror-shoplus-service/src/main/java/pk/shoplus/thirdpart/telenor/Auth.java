package pk.shoplus.thirdpart.telenor;

public class Auth {
	private String sessionId;

	public Auth(CorpSms corpSms) throws Exception {
		if (!corpSms.getCommand().equals("Auth_request") || !corpSms.getResponse().equals("OK")) {
			throw new Exception("BAD RESPONSE");
		}

		sessionId = corpSms.getData();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
