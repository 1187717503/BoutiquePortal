package pk.shoplus.mq.enums;

/**
 * 定时器Event枚举
 * @author dingyifan
 *
 */
public enum TimerEventEnum {
	RESTART("restart","开启定时器!"),
	STOP("stop","结束定时器!");
	
	private String code;
	private String value;
	
	private TimerEventEnum(String code, String value) {
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
