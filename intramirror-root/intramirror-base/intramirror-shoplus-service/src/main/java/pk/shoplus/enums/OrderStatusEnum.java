package pk.shoplus.enums;

public enum OrderStatusEnum {
	//CANCEL(-4,"CANCEL"),
	CLOSE(5,"CLOSE"),
	CONFIM(2,"CONFIM"),
	SHIP(3,"SHIP");
	
	private Integer code;
	private String value;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	private OrderStatusEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}
	
}
