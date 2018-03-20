package com.intramirror.web.controller.order;

import com.intramirror.common.help.Page;
import com.intramirror.common.help.PageUtils;
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


    @RequestMapping(value = "/getShippedList", method = RequestMethod.POST)
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
        Page page = new Page();
        if (shippedParam.getPageNum() == null) {
            shippedParam.setPageNum(1);
        }
        if (shippedParam.getPageSize() == null) {
            shippedParam.setPageSize(10);
        }
        page.setCurrentPage(shippedParam.getPageNum());
        page.setPageSize(shippedParam.getPageSize());
        //根据订单状态查询订单
        PageUtils pageUtils = orderService.getShippedOrderListByStatus(page, vendorId, shippedParam);

        List<Map<String, Object>> orderList = pageUtils.getResult();
        if (orderList != null && orderList.size() > 0) {

            for (Map<String, Object> info : orderList) {

                //汇率
                String currentRate = info.get("current_rate") != null ? info.get("current_rate").toString() : "0";
                Double rate = Double.parseDouble(currentRate);

                //按汇率计算人民币价钱
                String salePriceStr = info.get("sale_price") != null ? info.get("sale_price").toString() : "0";
                Double rmb_sale_price = Double.parseDouble(salePriceStr) * rate;
                info.put("rmb_sale_price", rmb_sale_price);
                //计算利润
                String inPriceStr = info.get("in_price") != null ? info.get("in_price").toString() : "0";
                Double profit = Double.parseDouble(salePriceStr) - Double.parseDouble(inPriceStr);
                BigDecimal b = new BigDecimal(profit);
                profit = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                info.put("profit", profit * rate);

                //计算折扣
                String priceStr = info.get("price") != null ? info.get("price").toString() : "0";
                Double salePrice = Double.parseDouble(salePriceStr);
                Double price = Double.parseDouble(priceStr);
                Double inPrice = Double.parseDouble(inPriceStr);

                BigDecimal sale_price_discount = new BigDecimal((salePrice / price) * 100);
//				info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
                info.put("sale_price_discount", (100 - sale_price_discount.intValue()) + " %");

                BigDecimal supply_price_discount = new BigDecimal((inPrice * (1 + 0.22) / price) * 100);
                info.put("supply_price_discount", (100 - supply_price_discount.intValue()) + " %");

            }
        }
        result.successStatus();
        result.setData(pageUtils);
        return result;
    }

}
