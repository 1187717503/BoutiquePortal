package pk.shoplus.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.google.gson.Gson;

import pk.shoplus.DBConnector;
import pk.shoplus.common.Helper;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.OrderStatusMap;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.LogisticsProductService;
import pk.shoplus.service.OrderService;
import pk.shoplus.service.ProductPropertyService;

/**
 * 
 * @author wzh
 *
 */
public class OrderManagement {
	
    private final Logger LOGGER = Logger.getLogger(OrderManagement.class);
    
	//根据条件获取需要推送的订单列表
	public List<OrderMager> getOrderList(Long vendorId,Long pageTokenId,Integer limit) throws NumberFormatException, Exception{
		LOGGER.info(MessageFormat.format("getOrderList 入参-vendorId:{0},pageTokenId:{1},limit:{2} ", vendorId,pageTokenId,limit));
		
		Connection conn = null ;
		try {
			conn = DBConnector.sql2o.beginTransaction();
			OrderService orderService = new OrderService(conn);
			ProductPropertyService productPropertyService = new ProductPropertyService(conn);
			
			//拼接需要查询的参数
			Object[] params = new Object[]{EnabledType.USED,vendorId};
			StringBuffer whereCondition = new StringBuffer("and a.enabled=:p1 and a.vendor_id = :p2 ");
			String limitCondition = "";
			
			//logistics_product_id起始ID
			if(pageTokenId != null){
				whereCondition.append("and a.logistics_product_id > :p3 ");
				params = new Object[]{EnabledType.USED,vendorId,pageTokenId};
			}
			
			//根据条件查询条数
			if(limit != null && !limit.equals(0)){
				limitCondition = " limit "+limit;
			}
			
			//获取订单
			List<Map<String, Object>> listMap = orderService.getOrderListByVendorId(whereCondition,limitCondition,params);
			
			
			List<OrderMager> list = new ArrayList<OrderManagement.OrderMager>();
			
			//遍历订单列表，转换成对应VO
			if(listMap != null && listMap.size()>0){
				for(Map<String, Object> orderLogisticsMap : listMap){
					
					//暂时不需要这两个字段
					//获取商品的BrandID 跟  ColorCode 属性
					List<Map<String, Object>> productList =  productPropertyService.getProductPropertyValueById(Long.parseLong(orderLogisticsMap.get("product_id").toString()));
					System.out.println("productList:"+new Gson().toJson(productList));
					
					String BrandID = "";
					String ColorCode = "";
					
					//循环商品属性列表，取得对应的值
					if(productList != null && productList.size() > 0){
						for(Map<String, Object> productInfo : productList){
							
							if("BrandID".equals(productInfo.get("key_name").toString())){
								BrandID = productInfo.get("value").toString();
							}
							
							if("ColorCode".equals(productInfo.get("key_name").toString())){
								ColorCode = productInfo.get("value").toString();
							}
						}
						System.out.println("BrandID_ColorCode:"+BrandID+"_"+ColorCode);
						orderLogisticsMap.put("BrandID", BrandID);
						orderLogisticsMap.put("ColorCode", ColorCode);
					}

					
					OrderMager orderResult = convertMapToBean(orderLogisticsMap);
					list.add(orderResult);
				}
			}
			
			conn.commit();
			System.out.println(new Gson().toJson(list));
			return list;
			

		} catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
            if(conn != null) {conn.rollback();conn.close();}
            
        } finally {
            if(conn != null) {conn.close();}
        }
		return null;
		
	}
	
	
	
	public Map<String,Object> updateOrderStatus(String orderLineNum,Integer status){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status", StatusType.FAILURE);  //默认失败
		
		Connection conn = null ;
		try {
			conn = DBConnector.sql2o.beginTransaction();
			//参数校验
			if(StringUtils.isBlank(orderLineNum) || status == null){
				LOGGER.info("uodateOrderStatus 请求参数为空  order_line_num:"+ orderLineNum + ",status:"+status);
				resultMap.put("info", "uodateOrderStatus 请求参数为空  order_line_num:"+ orderLineNum + ",status:"+status);
			}
			
			LogisticsProductService logisticsProductService = new LogisticsProductService(conn);
			LogisticsProduct logisticsProduct = null;
			
        	// 添加获取订单条件
            Map<String, Object> logisProductCondition = new HashMap<String, Object>();
            logisProductCondition.put("order_line_num", orderLineNum);
            logisProductCondition.put("enabled", EnabledType.USED);

            //获取订单
            List<LogisticsProduct> logisticsProductList = logisticsProductService
                    .getLogisticsProductListByCondition(logisProductCondition);
            
            if(logisticsProductList == null || logisticsProductList.size() < 1){
            	LOGGER.info("updateOrderStatus 根据条件未获取到订单 order_line_num:"+ orderLineNum);
            	resultMap.put("info", "updateOrderStatus 根据条件未获取到订单 order_line_num:"+ orderLineNum);
            }else{
            	logisticsProduct = logisticsProductList.get(0);
            }
            
            //修改订单状态
            if(logisticsProduct != null){
            	logisticsProduct.setStatus(status);
            	
            	LOGGER.info("service 修改订单状态入参对象:"+new Gson().toJson(logisticsProduct));
    			logisticsProductService.updateLogisticsProduct(logisticsProduct);
    			resultMap.put("status", StatusType.SUCCESS);  //成功
    			conn.commit();
            }

			
		} catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.toString());
            resultMap.put("info",e.getMessage());
            if(conn != null) {conn.rollback();conn.close();}
            
        } finally {
            if(conn != null) {conn.close();}
        }
		
		return resultMap;
		
	}
	
	
	/**
	 * 数据转换
	 * @param map
	 * @return
	 * @throws ParseException
	 */
	public OrderMager convertMapToBean(Map<String, Object> map) throws ParseException{
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
			//添加boutiqueSkuId
//			orderResult.setBoutiqueSkuId(map.get("boutique_sku_id")==null?"":map.get("boutique_sku_id").toString());
		}
		return orderResult;
	}
	
	/**
	 * 总订单
	 * @author wenzhihao
	 *
	 */
	 public class OrderMager { 
		/** logistics_product_id **/
		public String orderId;
		
		/** logistics_product.order_line_num **/
		public String orderNumber;
		
		/** logistics_product.created_at **/
		public Date createTime;
		
		/** logistics_product.updated_at **/
		public Date updatedTime;
		
		/** 购买数量  logistics_product.amount **/
		public String itemsCount;
		
		/** product.productCode **/
		public String productCode;
		
		/** Brand ID + Color Code + Size **/
		public String sku;
		
		/** product_sku_property_value.size **/
		public String size;
		
		/** sku.inPrice **/
		public BigDecimal price;
		
		/** logistics_product.fee **/
		public BigDecimal totalPrice;
		
		/** 订单状态   logistics_product.status**/
		public String status;
		
		/** 收件人姓名   logistics**/
		public String customerName;
		
		/** 收件人地址   logistics**/
		public String address;
		
		/** 收件人地区   logistics**/
		public String area;
		
		/** 收件人城市   logistics**/
		public String city;
		
		/** 收件人省份   logistics **/
		public String province;
		
		/** 收件人国家   logistics  **/
		public String country;
		
		/** 邮政编码   logistics **/
		public String zipcode;
		
		/** 收件人手机号码   logistics**/
		public String mobilePhone;
		
		/** 邮箱   logistics**/
		public String email;
		
		/** order_line_num **/
		public String orderLineId;
		
		/** product.productCode **/
		public String boutiqueId;
		
		/** sku.sku_code **/
		public String barcode;
		
		public String getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}
		public Date getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		public String getItemsCount() {
			return itemsCount;
		}
		public void setItemsCount(String itemsCount) {
			this.itemsCount = itemsCount;
		}
		public String getProductCode() {
			return productCode;
		}
		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public BigDecimal getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getArea() {
			return area;
		}
		public void setArea(String area) {
			this.area = area;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getZipcode() {
			return zipcode;
		}
		public void setZipcode(String zipcode) {
			this.zipcode = zipcode;
		}
		public String getMobilePhone() {
			return mobilePhone;
		}
		public void setMobilePhone(String mobilePhone) {
			this.mobilePhone = mobilePhone;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getOrderLineId() {
			return orderLineId;
		}
		public void setOrderLineId(String orderLineId) {
			this.orderLineId = orderLineId;
		}
		public String getBoutiqueId() {
			return boutiqueId;
		}
		public void setBoutiqueId(String boutiqueId) {
			this.boutiqueId = boutiqueId;
		}
		public String getBarcode() {
			return barcode;
		}
		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}
		public String getOrderId() {
			return orderId;
		}
		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}
		public Date getUpdatedTime() {
			return updatedTime;
		}
		public void setUpdatedTime(Date updatedTime) {
			this.updatedTime = updatedTime;
		}
		public String getSku() {
			return sku;
		}
		public void setSku(String sku) {
			this.sku = sku;
		}
		
	}
}
