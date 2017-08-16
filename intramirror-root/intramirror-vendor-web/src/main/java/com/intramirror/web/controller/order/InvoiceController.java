package com.intramirror.web.controller.order;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.logistics.api.model.Invoice;
import com.intramirror.logistics.api.service.IInvoiceService;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;

@CrossOrigin
@Controller
@RequestMapping("/invoice")
public class InvoiceController extends BaseController{
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private IInvoiceService invoiceService;
	
	@Autowired
	private IContainerService containerService;
	
	@Autowired
	private ILogisticsProductService iLogisticsProductService;
	
	@Autowired
	private VendorService vendorService;
	
	
	
	/**
	 * 创建invoice
	 * @param invoice invoiceDate shipmentId
	 * @param httpRequest
	 * @return
	 */
	@RequestMapping(value="/addInvoice", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage deletePackingCheckOrder(@RequestBody Map<String,Object> map,HttpServletRequest httpRequest){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		if(map == null || map.size() == 0 || map.get("shipmentId") == null || map.get("invoiceNo") == null || map.get("invoiceDate") == null){
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Invoice invoice = new Invoice();
			invoice.setEnabled(EnabledType.DISCARD);
			invoice.setShipmentId(Long.parseLong(map.get("shipmentId").toString()));
			
			Invoice oldInvoice = invoiceService.getInvoiceByShipmentId(Long.parseLong(map.get("shipmentId").toString()));
			
			//根据shipMentId 获取contain 列表
			List<Map<String, Object>> containList = containerService.getShipmentList(map);
			//shipMent 必须有关联的箱子
			if(containList == null ||containList.size() == 0){
				result.setMsg("The corresponding container cannot be found");
				return result;
			}
			
			String containIds = "";
			/****************************************删除*******************************************/
			if(oldInvoice != null ){
				//先将之前的发票信息删除
				invoiceService.updateByShipmentId(invoice);
			}
				
			/****************************************创建*******************************************/
			invoice.setEnabled(EnabledType.USED);
			invoice.setInvoiceNum(map.get("invoiceNo").toString());
			invoice.setInvoiceDate(sdf.parse(map.get("invoiceDate").toString()));
			invoice.setVatNum(0l);
			invoice.setVendorId(vendor.getVendorId());
			
			invoiceService.insertSelective(invoice);
			
			if(invoice.getInvoiceId() != null){
				
				if(containList != null && containList.size() > 0){
					for(Map<String, Object> conInfo : containList){
						containIds +=","+conInfo.get("container_id").toString();
					}
					containIds = containIds.substring(1);
					
					//修改logistics_product表
					Map<String, Object> conditionMap = new HashMap<String, Object>();
					conditionMap.put("invoice_id", invoice.getInvoiceId());
					conditionMap.put("container_ids", containIds.split(","));
					//添加相关订单对invoice的关联
					iLogisticsProductService.updateByContainerId(conditionMap);
					
//					Map<String, Object> conditionMap = new HashMap<String, Object>();
//					conditionMap.put("invoice_id", 0);
//					conditionMap.put("container_ids", containIds.split(","));
//					//取消相关订单对invoice的关联
//					iLogisticsProductService.updateByContainerId(conditionMap);
					
				}

				
				result.successStatus();
			}else{
				result.setMsg("Failed to add invoice");
			}
	
			

			
		}catch(Exception e){
			e.printStackTrace();
		}

		
		
		return result;
	}
	

}
