package pk.shoplus.parameter;
import java.util.HashMap;
import java.util.Map;

public class OrderStatusMap {

	public static Map<String,Integer> nameValue;
	public static Map<Integer,String> valueName;
	public static Map<String,String> nameShow;
	static {
		nameValue=new HashMap<String,Integer>();
		nameValue.put("pending", OrderStatusType.PENDING);
		nameValue.put("confirmed", OrderStatusType.COMFIRMED);
		nameValue.put("payed", OrderStatusType.PAYED);
		nameValue.put("ordered", OrderStatusType.ORDERED);
		nameValue.put("finished", OrderStatusType.FINISHED);
		nameValue.put("canceled", OrderStatusType.CANCELED);
		nameValue.put("settlement", OrderStatusType.SETTLEMENT);
		nameValue.put("refund", OrderStatusType.REFUND);

		valueName=new HashMap<Integer,String>();
		valueName.put(OrderStatusType.PENDING,"pending");
		valueName.put(OrderStatusType.COMFIRMED,"confirmed");
		valueName.put(OrderStatusType.PAYED,"payed");
		valueName.put(OrderStatusType.ORDERED,"ordered");
		valueName.put(OrderStatusType.FINISHED,"finished");
		valueName.put(OrderStatusType.CANCELED,"canceled");
		valueName.put(OrderStatusType.SETTLEMENT,"settlement");
		valueName.put(OrderStatusType.REFUND,"refund");
		
		nameShow=new HashMap<String,String>();
		nameShow.put("status"+OrderStatusType.PENDING,"Pending");
		nameShow.put("status"+OrderStatusType.COMFIRMED,"Confirmed");
		nameShow.put("status"+OrderStatusType.PAYED,"Receviced");
		nameShow.put("status"+OrderStatusType.ORDERED,"In Delivery");
		nameShow.put("status"+OrderStatusType.FINISHED,"Finished");
		nameShow.put("status"+OrderStatusType.CANCELED,"Canceled");
		nameShow.put("status"+OrderStatusType.SETTLEMENT,"Settlement");
		
	}
	
}
