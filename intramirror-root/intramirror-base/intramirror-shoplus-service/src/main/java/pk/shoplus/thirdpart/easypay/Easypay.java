package pk.shoplus.thirdpart.easypay;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Order;
import pk.shoplus.model.PaymentEasypay;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class Easypay {

    /**
     * Id of the store as provided by the Telenor POC
     */
    private final static int TEST_STORE_ID;

    /**
     * Init transaction url
     */
    private final static String TEST_INIT_URL;

    /**
     * Transaction confirmation url
     */
    private final static String TEST_CONFIRM_URL;

    /**
     * The first post back URL for confirmation
     */
    private final static String HANDSHAKE_POSTBACK_URI;

    /**
     * The second post back URL for process result
     */
    private final static String RESULT_POSTBACK_URI;

    private String handshakePostbackUrl;

    private String resultPostbackUrl;

    static {
        InputStream stream = Easypay.class.getClassLoader().getResourceAsStream("easypay.properties");
        try {
            Properties properties = new Properties();
            properties.load(stream);
            TEST_STORE_ID = Integer.parseInt(properties.getProperty("storeId"));
            TEST_INIT_URL = properties.getProperty("initUrl");
            TEST_CONFIRM_URL = properties.getProperty("confirmationUrl");
            HANDSHAKE_POSTBACK_URI = properties.getProperty("handshakePostBackUri");
            RESULT_POSTBACK_URI = properties.getProperty("resultPostBackUri");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EntityDao<Order> orderDao = null;

    public Easypay(String baseUrl, Connection conn) {
        orderDao = new EntityDao<Order>(conn);
        handshakePostbackUrl = baseUrl + HANDSHAKE_POSTBACK_URI;
        resultPostbackUrl = baseUrl + RESULT_POSTBACK_URI;
    }

    /**
     * Customer selects “Pay through Easy Pay” payment method on merchant website.
     * Easypay authenticates Merchant account, and then verifies store information
     * whether or not store can be used for online transactions (valid Store Id, Valid Easypay Account).
     * If Merchant’s account is not verified, an error message and description is sent to postBackURL.
     *
     * The merchant needs to POST following parameter to the Easypay on the following URL:
     *
     * Production (Live) Environment:
     * https://easypay.easypaisa.com.pk/easypay/Index.jsf
     *
     * Sandbox Environment:
     * https://easypaystg.easypaisa.com.pk/easypay/Index.jsf
     *
     * Params:
     *     amount                          Numeric
     *     storeId                         Numeric
     *     postBackURL                     String
     *     orderRefNum                     String
     *
     *     expiryDate (Optional)           String, format: YYYYMMDD HHMMSS
     *     merchantHashedReq (Optional)    String
     *     autoRedirect (Optional)         Numeric 0 or 1
     *     paymentMethod (Optional)        OTC_PAYMENT_METHOD/ MA_PAYMENT_METHOD/ CC_PAYMENT_METHOD
     *     emailAddr (Optional)            String
     *     mobileNum (Optional)            Numeric
     *
     * Successful Response:
     *     auth_token                      String
     *
     * Error Response:
     *     Redirect to Easypay AuthError page
     *
     * Note: Easypay will send the request to INIT_POST_BACK_URI whether success or fail
     *
     * @param orderId order id #{@link Order}
     * @return Transaction necessary information
     */
    public HashMap<String, Object> beginTransaction (Long orderId) throws Exception {
        Order order = orderDao.getById(Order.class, orderId);

        if (order == null) {
            throw new NullPointerException();
        }

        Date expireDate = new Date(System.currentTimeMillis() + 600000);
        PaymentEasypay paymentEasypay = new PaymentEasypay();
        paymentEasypay.payment_easypay_id = 1L;
        paymentEasypay.amount = order.fee;
        paymentEasypay.created_at = new Date();
        paymentEasypay.updated_at = new Date();
        paymentEasypay.order_id = orderId;
        paymentEasypay.store_id = TEST_STORE_ID;
        paymentEasypay.expire_date = expireDate;
        // TODO: 16/12/5 Add record in easypay table

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("storeId", String.valueOf(TEST_STORE_ID));
        result.put("orderRefNum", orderId.intValue());
        result.put("amount", order.fee);
        result.put("postBackURL", handshakePostbackUrl + paymentEasypay.payment_easypay_id);
        result.put("expiryDate", new SimpleDateFormat("YYYYMMdd hhmmss").format(expireDate));
        result.put("action", TEST_INIT_URL);

        return result;
    }

    /**
     * After completing the form in Step 1 the customer will be pressing the Proceed Button and
     * lands back on the merchant website on the same URL given in postbackURL variable in the first step.
     * This will be a confirmation screen on merchant’s website to perform a handshake between Easypay
     * and merchant’s website. The Easypay sends back a parameter named auth_token to the postbackURL
     * which is sent as a GET parameter.
     *
     * Now the merchant need to post back following two parameters again to the
     * Production (Live) Environment:
     * https://easypay.easypaisa.com.pk/easypay/Confirm.jsf
     *
     * Sandbox Environment:
     * https://easypaystg.easypaisa.com.pk/easypay/Confirm.jsf
     *
     * Params:
     *      auth_token     String
     *      postBackURL    String
     *
     * Successful response
     *      Redirect to Easypay checkout page
     *
     * Error Response:
     *      Redirect to Easypay AuthError page
     *
     * @param paymentId payment easypay id #{@link PaymentEasypay}
     * @param authToken auth token from easypay api
     * @return Transaction necessary information
     */
    public HashMap<String, Object> confirmTransaction(Long paymentId, String authToken) {
        PaymentEasypay paymentEasypay = new PaymentEasypay();
        paymentEasypay.payment_easypay_id = 1L;
        paymentEasypay.updated_at = new Date();
        paymentEasypay.auth_token = authToken;
        // TODO: 16/12/5 Update easypay table

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("auth_token", authToken);
        result.put("postBackURL", resultPostbackUrl + paymentEasypay.payment_easypay_id);
        result.put("action", TEST_CONFIRM_URL);

        return result;
    }

    /**
     * After this redirection the Easypay authenticates the auth_token sent by merchant with the one
     * it has in the previous step, and upon successful authentication it will make customer land on
     * the successful checkout screen sending back following two variables to the second postBackURL
     *
     * Successful Response:
     *      status
     *      desc
     *      orderRefNumber
     *
     * Error Response:
     *      status
     *      desc
     *      orderRefNumber
     *
     */
    public void processResult() {
        // TODO: 16/12/5  Update order status
        throw new UnsupportedOperationException("Not implemented, yet");
    }
}
