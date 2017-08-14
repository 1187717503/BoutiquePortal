package com.intramirror.web.controller.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.LogisticsProductService;

@CrossOrigin
@Controller
@RequestMapping("/orderShip")
public class OrderShipController extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(OrderShipController.class);
	 
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private LogisticsProductService logisticsProductService;
	
	@Autowired
	private ProductPropertyService productPropertyService;
	
	@Autowired
	private VendorService vendorService;
	
	@Autowired
	private ISkuStoreService skuStoreService;
	
	@Autowired
	private ILogisticsProductService iLogisticsProductService;
	
	@Autowired
	private IShipmentService iShipmentService;
	
	@Autowired
	private IContainerService containerService;
	
	
	
	
	/***
	 * 获取所有箱子信息
	 * @param status containerId
	 * @param httpRequest
	 * @return
	 */
	@RequestMapping(value="/getReadyToShipCartonList", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage getPackOrderList(@RequestBody Map<String,Object> map,HttpServletRequest httpRequest){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		if(map == null || map.size() == 0 || map.get("status") == null){
			result.setMsg("Parameter cannot be empty");
			return result;
		}
		
		User user = this.getUser(httpRequest);
		if(user == null){
			result.setMsg("Please log in again");
			return result;
		}
		
		Vendor vendor= null;
		try {
			vendor= vendorService.getVendorByUserId(user.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(vendor == null){
			result.setMsg("Please log in again");
			return result;
		}
		
		
		
		try{
			Map<String, Object> paramtMap = new HashMap<String, Object>();
			//获取箱子列表信息
			List<Map<String, Object>> containerList = containerService.getContainerList(paramtMap);
			
			Map<String, List<Map<String, Object>>> shipMentCartonList = new HashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> shipMentList = new ArrayList<Map<String, Object>>();
			
			if(containerList != null && containerList.size() > 0){

//				for(Map<String, Object> container : containerList){
//					
//					//根据shipment_id 分组
//					if(!shipMentCartonList.containsKey(container.get("shipment_id").toString())){
//						//获取shipMent信息
//						Map<String, Object> shipMent = new HashMap<String, Object>();
//						shipMent.put("shipment_id", container.get("shipment_id").toString());
//						shipMent.put("shipment_no", container.get("shipment_no").toString());
//						shipMent.put("shipment_type", container.get("shipment_type").toString());
//						shipMent.put("product_qty", container.get("product_qty").toString());
//						shipMent.put("ship_to_geography", container.get("ship_to_geography").toString());
////						shipMent.put("cartonList", );
//						shipMentList.add(shipMent);
//					}
//				}
				
				
				for(Map<String, Object> container : containerList){
				
					//根据shipment_id 分组
					if(shipMentCartonList.containsKey(container.get("shipment_id").toString())){
						List<Map<String, Object>> cons = shipMentCartonList.get(container.get("shipment_id").toString());
						cons.add(container);
					}else{
						List<Map<String, Object>> cons = new ArrayList<Map<String,Object>>();
						cons.add(container);
						shipMentCartonList.put(container.get("shipment_id").toString(), cons);
						
						//获取shipMent信息
						Map<String, Object> shipMent = new HashMap<String, Object>();
						shipMent.put("shipment_id", container.get("shipment_id").toString());
						shipMent.put("shipment_no", container.get("shipment_no").toString());
						shipMent.put("shipment_type", container.get("shipment_id").toString());
						shipMent.put("product_qty", container.get("product_qty").toString());
						shipMent.put("ship_to_geography", container.get("ship_to_geography").toString());
						shipMentList.add(shipMent);
					}
				}
				
				
				if(shipMentList != null && shipMentList.size() >0){
					//将cartonList 存入shipMent详情
					for(Map<String, Object> shipMent:shipMentList){
						shipMent.put("cartonList", shipMentCartonList.get(shipMent.get("shipment_id").toString()));
					}
				}


				
			}


			result.successStatus();
			result.setData(shipMentList);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("Query container list fail,Check parameters, please ");
			return result;
		}

		
		
		return result;
	}
	
	
}
