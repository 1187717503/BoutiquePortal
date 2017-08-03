package pk.shoplus.thirdpart.telenor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.net.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Telenor
{
	private static final String API_ROOT_URL = "https://telenorcsms.com.pk:27677/corporate_sms2/api/auth.jsp";

	/**
	 * In order to deliver a message, the system needs to authenticate the request as coming from a valid source.
	 *
	 * You can have multiple threads open, however the session ID will expire after 30 minutes of inactivity.
	 * You will then have to re-authenticate to receive a new session ID.
	 *
	 * Example:
	 *
	 * Command: https://telenorcsms.com.pk:27677/corporate_sms2/api/auth.jsp?msisdn=xxxx&password=xxx
	 *
	 * Success Response:
	 *     <?xml version="1.0" encoding="UTF-8" ?>
	 *     <corpsms>
	 *         <command>Auth_request</command>
	 *         <data>Session ID</data>
	 *         <response>OK</response>
	 *     </corpsms>
	 *
	 * Error Response:
	 *     <?xml version="1.0" encoding="UTF-8" ?>
	 *     <corpsms>
	 *         <command>Auth_request</command>
	 *         <data>Error Code</data>
	 *         <response>ERROR</response>
	 *     </corpsms>
	 *
	 * NOTE: This Session ID must be used with all future commands to the API.
	 *
	 * @param msisdn This is the Mobile Number for your Corporate Call & SMS Account
	 * @param password This is the current password you have set for your Account
	 * @return {@link Auth}
	 */
	public static Auth auth(String msisdn, String password) throws Exception {
		URI uri = new URIBuilder(API_ROOT_URL)
				.setParameter("msisdn", msisdn)
				.setParameter("password", password)
				.build();

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPost request = new HttpPost(uri);

		HttpResponse response = httpClient.execute(request);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception();
		}

		HttpEntity entity = response.getEntity();
		CorpSms corpSms = parseResponse(entity.getContent());

		return new Auth(corpSms);
	}

	/**
	 * This command is used to send a Quick Message. To send a quick message, the destination address should be in
	 * the format 923xxxxxxxxx.
	 *
	 * Each message returns a unique identifier in the form of Message ID.For multiple destination numbers, a comma
	 * separated list of message ID's is returned. Single message ID is returned for each mobile number. If even a
	 * single mobile number is in incorrect format, the request will be rejected. The message ID can be used to track
	 * and monitor any given message. The message ID is returned after each post.
	 *
	 * Example:
	 *
	 * Command: https://telenorcsms.com.pk:27677/corporate_sms2/api/sendsms.jsp?session_id=xxxx&to=923xxxxxxxxx,923xxxxxxxxx,923xxxxxxxxx&text=xxxx&mask=xxxx
	 *
	 * Success Response:
	 *     <?xml version="1.0" encoding="UTF-8" ?>
	 *     <corpsms>
	 *         <command>Submit_SM</command>
	 *         <data>Message ID1,Message ID2,Message ID3</data>
	 *         <response>OK</response>
	 *     </corpsms>
	 *
     * Error Response:
	 *    <?xml version="1.0" encoding="UTF-8" ?>
	 *    <corpsms>
	 *        <command>Submit_SM</command>
	 *        <data>Error Code</data>
	 *        <response>ERROR</response>
	 *    </corpsms>
	 *
	 * @param sessionId The session ID returned from authentication
	 * @param to Comma separated list of destination mobile numbers
	 * @param text The content of the message
	 * @param mask The mask to be used to send the message. If this parameter is not present, then the default mask
	 *             will be used
	 * @param unicode If the text of the SMS is in any language other than English, this parameter must be sent to TRUE.
	 *                e.g., If the text is in Urdu, an extra parameter unicode=true must be passed with the request
	 * @param operatorId If the SMS is to be sent through a specific operator, this field should contain ID for the
	 *                   respective operator.
	 * @return TODO: Return the real Object
	 */
	public static Object sendMessage(String sessionId, String to, String text, String mask, String unicode, String operatorId) {
		throw new UnsupportedOperationException("Not implemented, yet");
	}

	/**
	 * This command returns the status of a quick message. You can query the status with the msg_id which is the
	 * message ID returned by the Gateway when a message has been successfully submitted. For authentication you
	 * need to pass sessionId as well while querying for the message status.
	 *
	 * Example:
	 *
	 * Command: https://telenorcsms.com.pk:27677/corporate_sms2/api/querymsg.jsp?session_id=xxx&msg_id=xxxx
	 *
	 * Success Response:
	 *     <?xml version="1.0" encoding="UTF-8" ?>
	 *     <corpsms>
	 *         <command>Message_query</command>
	 *         <data>STATUS</data>
	 *         <response>OK</response>
	 *     </corpsms>
	 *
	 * Error Response:
	 *     <?xml version="1.0" encoding="UTF-8" ?>
	 *     <corpsms>
	 *         <command>Message_query</command>
	 *         <data>Error Code</data>
	 *         <response>ERROR</response>
	 *     </corpsms>
	 *
	 * @param sessionId The session ID returned from authentication
	 * @param messageId The Message ID returned from {@link Telenor#sendMessage}
	 * @return {@link MessageStatus}
	 */
	public static int queryMessage(String sessionId, String messageId) {
		throw new UnsupportedOperationException("Not implemented, yet");
	}

	private static CorpSms parseResponse(InputStream is) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(CorpSms.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return (CorpSms) unmarshaller.unmarshal(is);
	}
}
