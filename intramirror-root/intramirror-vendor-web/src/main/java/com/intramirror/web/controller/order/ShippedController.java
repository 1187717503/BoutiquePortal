package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.vo.ShippedParam;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@CrossOrigin
@Controller
@RequestMapping("/shipped")
public class ShippedController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ShippedController.class);

    @Autowired
    private IOrderService orderService;

    @Autowired
    private VendorService vendorService;


	@RequestMapping(value="/getShippedList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getShippedList(@RequestBody ShippedParam shippedParam, HttpServletRequest httpRequest) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        Vendor vendor = null;
        try {
            vendor = vendorService.getVendorByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendor == null) {
            result.setMsg("Please log in again");
            return result;
        }

        Long vendorId = vendor.getVendorId();
        //根据订单状态查询订单
        List<Map<String, Object>> orderList = orderService.getShippedOrderListByStatus(vendorId, null);

        if (orderList != null && orderList.size() > 0) {

            for (Map<String, Object> info : orderList) {

                //汇率
                Double rate = Double.parseDouble(info.get("current_rate").toString());

                //按汇率计算人民币价钱
                Double sale_price2 = Double.parseDouble(info.get("sale_price").toString()) * rate;
                info.put("sale_price2", sale_price2);
                //计算利润
                Double profit = Double.parseDouble(info.get("sale_price").toString()) - Double.parseDouble(info.get("in_price").toString());
                BigDecimal b = new BigDecimal(profit);
                profit = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                info.put("profit", profit * rate);

                //计算折扣
                Double salePrice = Double.parseDouble(info.get("sale_price").toString());
                Double price = Double.parseDouble(info.get("price").toString());
                Double inPrice = Double.parseDouble(info.get("in_price").toString());

                BigDecimal sale_price_discount = new BigDecimal((salePrice / price) * 100);
//				info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
                info.put("sale_price_discount", (100 - sale_price_discount.intValue()) + " %");

                BigDecimal supply_price_discount = new BigDecimal((inPrice * (1 + 0.22) / price) * 100);
                info.put("supply_price_discount", (100 - supply_price_discount.intValue()) + " %");

            }
        }
        result.successStatus();
        result.setData(orderList);
        return result;
    }

}
