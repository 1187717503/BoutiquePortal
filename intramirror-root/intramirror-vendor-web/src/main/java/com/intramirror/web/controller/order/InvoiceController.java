package com.intramirror.web.controller.order;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.intramirror.order.api.service.IOrderService;

@CrossOrigin
@Controller
@RequestMapping("/invoice")
public class InvoiceController {
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private IInvoiceService invoiceService;
	
	
	
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
		
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Invoice invoice = new Invoice();
			invoice.setEnabled(EnabledType.DISCARD);
			invoice.setShipmentId(Long.parseLong(map.get("shipmentId").toString()));
			
			//先将之前的发票信息删除
			invoiceService.updateByShipmentId(invoice);
			
			invoice.setEnabled(EnabledType.USED);
			invoice.setInvoiceNum(map.get("invoiceNo").toString());
			invoice.setInvoiceDate(sdf.parse(map.get("invoiceDate").toString()));
			
			int row = invoiceService.insertSelective(invoice);
			
			if(row > 0){
				//修改logistics_product表
				
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
