package pk.shoplus.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrandMapping implements Serializable{
	private static final long serialVersionUID = 1L;
	private String brandId;
	private String brandName;
	private String no_img;
	private List<Map<String,Object>> boutiqueNames = new ArrayList<>();
	
	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public List<Map<String, Object>> getBoutiqueNames() {
		return boutiqueNames;
	}

	public void setBoutiqueNames(List<Map<String, Object>> boutiqueNames) {
		this.boutiqueNames = boutiqueNames;
	}

	public void addBoutiqueNames(Map<String, Object> item) {
		this.boutiqueNames.add(item);
	}

	public String getNo_img() {
		return no_img;
	}

	public void setNo_img(String no_img) {
		this.no_img = no_img;
	}
}
