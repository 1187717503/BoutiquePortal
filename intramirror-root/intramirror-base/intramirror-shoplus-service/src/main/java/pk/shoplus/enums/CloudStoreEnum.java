package pk.shoplus.enums;

public enum CloudStoreEnum {
	TOKEN("token","018513a51480a5fd0f456ee543b7c78a"),
	MERCHANTID("merchantId","55f707f6b49dbbe14ec6354d");
	
	private String code;
	private String value;
	
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
	private CloudStoreEnum(String code, String value) {
		this.code = code;
		this.value = value;
	}
	
}
