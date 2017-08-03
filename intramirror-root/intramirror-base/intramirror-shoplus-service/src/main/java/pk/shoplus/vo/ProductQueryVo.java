package pk.shoplus.vo;

import java.util.List;

public class ProductQueryVo {
	
	private String boutiqueId = null ;
	private String brandId = null  ;
	private String colorCode = null  ;
	private String name = null  ;
	private String stockFlag = null ; // 0-All , 1-inStock , 2-outOfStock
	private String shopFlag = null ; // 0-all , 1-inShop , 2-notInShop
	private String selBrand = null;
	private String selCategory = null;
	private String selVendor = null  ;
	private String selStatus = null  ;
	private String productId = null ; 
	private String categoryId = null ;
	private List<DateVO> conditionDate;
	private String selSeason = null ;
	private String selImage = null ; // 0-hasImage 1-notHasImage
	private String selModified = null ; // 0-isModified 1-notIsModified
	
	public String getBoutiqueId() {
		return boutiqueId;
	}
	public void setBoutiqueId(String boutiqueId) {
		this.boutiqueId = boutiqueId;
	}
	public String getBrandId() {
		return brandId;
	}
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStockFlag() {
		return stockFlag;
	}
	public void setStockFlag(String stockFlag) {
		this.stockFlag = stockFlag;
	}
	public String getShopFlag() {
		return shopFlag;
	}
	public void setShopFlag(String shopFlag) {
		this.shopFlag = shopFlag;
	}
	public String getSelBrand() {
		return selBrand;
	}
	public void setSelBrand(String selBrand) {
		this.selBrand = selBrand;
	}
	public String getSelCategory() {
		return selCategory;
	}
	public void setSelCategory(String selCategory) {
		this.selCategory = selCategory;
	}
	public String getSelVendor() {
		return selVendor;
	}
	public void setSelVendor(String selVendor) {
		this.selVendor = selVendor;
	}
	public String getSelStatus() {
		return selStatus;
	}
	public void setSelStatus(String selStatus) {
		this.selStatus = selStatus;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public List<DateVO> getConditionDate() {
		return conditionDate;
	}
	public void setConditionDate(List<DateVO> conditionDate) {
		this.conditionDate = conditionDate;
	}
	public String getSelSeason() {
		return selSeason;
	}
	public void setSelSeason(String selSeason) {
		this.selSeason = selSeason;
	}
	public String getSelImage() {
		return selImage;
	}
	public void setSelImage(String selImage) {
		this.selImage = selImage;
	}
	public String getSelModified() {
		return selModified;
	}
	public void setSelModified(String selModified) {
		this.selModified = selModified;
	}
	
	
}
