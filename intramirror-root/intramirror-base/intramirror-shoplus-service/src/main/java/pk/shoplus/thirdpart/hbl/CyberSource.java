package pk.shoplus.thirdpart.hbl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class CyberSource {
	private static final String TEST_SECRET_KEY = "c3126dd7dabd47a89d2960cc40690ffdbd28418497814cddb5f40febca73ebd512ca0a9060f740a3930a33a8ef410e81398ff5f653d440fd9e8f18fd0630caba30f3aff58486487fb155ed664dd800d6bc53d39c6aa048b4b7f8244125ce9750b147e5fd7a514c9681eede1863f5b540b4b8cbccab6b48c99a1eecccc959a303";
	private static final String TEST_ACCESS_KEY = "bb1eec812e4830fa81776fc4a6d90877";
	private static final String TEST_PROFILE_ID = "C7C85652-4688-470A-ABDF-8FEC781CE95D";
	private static final String TEST_ACTION = "https://testsecureacceptance.cybersource.com/pay";

	private Mac sha256;
	private String accessKey;
	private String profileId;
	private String action;


	private static CyberSource test;

	public static CyberSource getTestInstance() {
		if (test == null) {
			try {
				test = new CyberSource();
				test.sha256 = Mac.getInstance("HmacSHA256");
				test.sha256.init(new SecretKeySpec(TEST_SECRET_KEY.getBytes("UTF-8"), "HmacSHA256"));
				test.accessKey = TEST_ACCESS_KEY;
				test.profileId = TEST_PROFILE_ID;
				test.action = TEST_ACTION;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return test;
	}

	public static CyberSource getLiveInstance() {
		// TODO: Wait for the live keys
		throw new UnsupportedOperationException("Not implemented, yet");
	}

	private CyberSource() {
	}


	public List<NameValuePair> generateFormFields(String referenceNumber, String amount, String currency) throws Exception {
		UUID uuid = UUID.randomUUID();
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("access_key", accessKey));
		params.add(new BasicNameValuePair("profile_id", profileId));
		params.add(new BasicNameValuePair("transaction_uuid", uuid.toString()));
		params.add(new BasicNameValuePair("signed_field_names", "access_key,profile_id,transaction_uuid,signed_field_names,unsigned_field_names,signed_date_time,locale,transaction_type,reference_number,amount,currency"));
		params.add(new BasicNameValuePair("unsigned_field_names", ""));
		params.add(new BasicNameValuePair("signed_date_time", utc.toString()));
		params.add(new BasicNameValuePair("locale", "en"));
		params.add(new BasicNameValuePair("transaction_type", "authorization"));
		params.add(new BasicNameValuePair("reference_number", referenceNumber));
		params.add(new BasicNameValuePair("amount", amount));
		params.add(new BasicNameValuePair("currency", currency));
		params.add(new BasicNameValuePair("signature", sign(params)));

		return params;
	}

	/**
	 * 生成数据签名
	 *
	 * @param params 键值对
	 * @return 生成的数据签名
	 */
	public String sign(List<NameValuePair> params) throws Exception {
		StringBuilder data = new StringBuilder();
		for (NameValuePair param : params) {
			String name = param.getName();
			String value = param.getValue();
			if (data.length() > 0) {
				data.append(",");
			}
			data.append(name).append("=").append(value);

		}

		return sign(data.toString());
	}

	public String sign(String data) throws Exception {
		Base64.Encoder base64 = Base64.getEncoder();
		return base64.encodeToString(sha256.doFinal(data.getBytes("UTF-8")));
	}

	public String getAction() {
		return action;
	}
}
