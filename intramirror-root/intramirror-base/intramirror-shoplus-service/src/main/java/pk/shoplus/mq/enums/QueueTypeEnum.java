package pk.shoplus.mq.enums;

/**
 * 队列类型
 * @author dingyifan
 *
 */
public enum QueueTypeEnum {
	PENDING("","Pending"),
	SUCCESS("Success","Success"),
	ERROR("Error","Error");
	
	private String code;
	private String value;
	
	private QueueTypeEnum(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
