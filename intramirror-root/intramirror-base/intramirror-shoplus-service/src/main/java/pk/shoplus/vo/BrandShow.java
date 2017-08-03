package pk.shoplus.vo;


import java.util.List;

import pk.shoplus.model.ApiBrandMap;
import pk.shoplus.model.Brand;

/**
 * 
 * @author wenzhihao
 *
 */
public class BrandShow extends Brand{
	/**
	 *  apiBrandMap 实体
	 */
	public List<ApiBrandMap> apiBrandMap;

	public List<ApiBrandMap> getApiBrandMap() {
		return apiBrandMap;
	}
	

}
