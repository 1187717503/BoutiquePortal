package pk.shoplus.vo;

public class UpdateApiCfgVO {
	private String apiCfgId;
	private String system;
	private String apiName; // 不能修改
	private String storeCode;
	private String url;
	private String offset;
	private String limit;
	private String cadence;
	private String vendorId;
	
	public String getApiCfgId() {
		return apiCfgId;
	}
	public void setApiCfgId(String apiCfgId) {
		this.apiCfgId = apiCfgId;
	}
	
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getCadence() {
		return cadence;
	}
	public void setCadence(String cadence) {
		this.cadence = cadence;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	
}
