package pk.shoplus.enums;

public enum ApiParamterEnum {
	LIMIT("limit","每页查询多少条数据"),
	STORE_CODE("storeCode","storeCode"),
	OFFSET("offset","起始值"),
	ALLOW_PRICE_CHANGE("allowPriceChange","是否修改价格标识"),
	CADENCE("cadence","job间隔时间"),
	MERCHANT_ID("merchantId","55f707f6b49dbbe14ec6354d"),
	TOKEN("token","018513a51480a5fd0f456ee543b7c78a");
	private String code;
	private String value;
	private ApiParamterEnum(String code, String value) {
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
