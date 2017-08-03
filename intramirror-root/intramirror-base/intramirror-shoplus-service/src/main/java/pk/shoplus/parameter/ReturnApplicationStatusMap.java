package pk.shoplus.parameter;
import java.util.HashMap;
import java.util.Map;

public class ReturnApplicationStatusMap {

	public static Map<String,Integer> nameValue;
	public static Map<Integer,String> valueName;
	public static Map<String,String> nameShow;
	static {
		nameValue=new HashMap<String,Integer>();
		nameValue.put("pending", ReturnApplicationStatusType.PENDING);
		nameValue.put("confirmed", ReturnApplicationStatusType.COMFIRMED);
		nameValue.put("ordered", ReturnApplicationStatusType.ORDERED);
		nameValue.put("appeal", ReturnApplicationStatusType.APPEAL);
		nameValue.put("finished", ReturnApplicationStatusType.FINISHED);
		nameValue.put("rejected", ReturnApplicationStatusType.REJECTED);
		nameValue.put("settlement", ReturnApplicationStatusType.SETTLEMENT);
		nameValue.put("cancel", ReturnApplicationStatusType.CANCEL);
		//nameValue.put("judgment", ReturnApplicationStatusType.REJECTED);//确认状态

		valueName=new HashMap<Integer,String>();
		valueName.put(ReturnApplicationStatusType.PENDING,"pending");
		valueName.put(ReturnApplicationStatusType.COMFIRMED,"confirmed");
		valueName.put(ReturnApplicationStatusType.ORDERED,"ordered");
		valueName.put(ReturnApplicationStatusType.APPEAL,"appeal");
		valueName.put(ReturnApplicationStatusType.FINISHED,"finished");
		valueName.put(ReturnApplicationStatusType.REJECTED,"rejected");
		valueName.put(ReturnApplicationStatusType.SETTLEMENT,"settlement");
		valueName.put(ReturnApplicationStatusType.CANCEL,"cancel");
		
		nameShow=new HashMap<String,String>();
		nameShow.put("S"+ReturnApplicationStatusType.PENDING,"Pending");
		nameShow.put("S"+ReturnApplicationStatusType.COMFIRMED,"Confirmed");
		nameShow.put("S"+ReturnApplicationStatusType.ORDERED,"Ordered");
		nameShow.put("S"+ReturnApplicationStatusType.APPEAL,"Appeal");
		nameShow.put("S"+ReturnApplicationStatusType.FINISHED,"Finished");
		nameShow.put("S"+ReturnApplicationStatusType.REJECTED,"Rejected");
		nameShow.put("S"+ReturnApplicationStatusType.SETTLEMENT,"settlement");
		nameShow.put("S"+ReturnApplicationStatusType.CANCEL,"cancel");
		
		
	}
	
}
