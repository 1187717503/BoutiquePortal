package com.intramirror.order.api.common;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.intramirror.common.Helper;
import com.intramirror.order.api.model.OrderMager;

public class ConverMapToOrderMager {
	
	/**
	 * 数据转换
	 * @param map
	 * @return
	 * @throws ParseException
	 */
	public static OrderMager convertMapToBean(Map<String, Object> map) throws ParseException{
		OrderMager  orderResult = null;
		if(map != null){
			orderResult = new OrderMager();
		
			orderResult.setOrderId(map.get("logistics_product_id").toString());
			orderResult.setOrderNumber(map.get("order_line_num").toString());
			orderResult.setCreateTime(Helper.convertStringToDate(map.get("created_at").toString()));
			orderResult.setUpdatedTime(Helper.convertStringToDate(map.get("updated_at").toString()));
			orderResult.setItemsCount(map.get("amount").toString());
			orderResult.setProductCode(map.get("product_code").toString());
			orderResult.setSize(map.get("size").toString());
			orderResult.setPrice(new BigDecimal(map.get("in_price").toString()));
			orderResult.setTotalPrice(new BigDecimal(map.get("in_price").toString()));
			orderResult.setStatus(OrderStatusMap.valueName.get(map.get("status")));
			//orderResult.setSku(map.get("BrandID").toString()+"_"+map.get("ColorCode").toString()+"_"+map.get("size").toString());
			String sku = "";
			if(StringUtils.isNotBlank(map.get("BrandID").toString())){
				sku +=map.get("BrandID").toString();
			}
			
			if(StringUtils.isNotBlank(map.get("ColorCode").toString())){
				sku +="_"+map.get("ColorCode").toString();
			}
			
			if(StringUtils.isNotBlank(map.get("size").toString())){
				sku +="-"+map.get("size").toString();
			}
			orderResult.setSku(sku);
		
//			switch(Integer.parseInt(map.get("status").toString())){
//			case 1:
//				orderResult.setStatus("PENDING");
//				break;
//			case 2:
//				orderResult.setStatus("COMFIRMED");
//				break;
//			case 3:
//				orderResult.setStatus("ORDERED");
//				break;
//			case 4:
//				orderResult.setStatus("PAYED");
//				break;
//			case 5:
//				orderResult.setStatus("FINISHED");
//				break;
//			case 6:
//				orderResult.setStatus("CANCELED");
//				break;
//			default: 
//				orderResult.setStatus("PENDING");
//				break; 
//			}
			
			orderResult.setCustomerName(map.get("user_rec_name").toString());
			orderResult.setAddress(map.get("user_rec_addr").toString());
			orderResult.setArea(map.get("user_rec_area").toString());
			orderResult.setCity(map.get("user_rec_city").toString());
			orderResult.setProvince(map.get("user_rec_province").toString());
			orderResult.setCountry(map.get("user_rec_country").toString());
			orderResult.setZipcode(map.get("user_rec_code").toString());
			orderResult.setMobilePhone(map.get("user_rec_mobile").toString());
			orderResult.setOrderLineId(map.get("order_line_num").toString());
			orderResult.setBarcode(map.get("sku_code").toString());
		}
		return orderResult;
	}

}
