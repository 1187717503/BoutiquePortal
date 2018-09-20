package com.intramirror.web.controller.apimq;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorPayment;
import com.intramirror.user.api.service.VendorPaymentService;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pk.shoplus.common.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@Controller
@RequestMapping("/vendorPayment")
public class VendorPaymentController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(VendorPaymentController.class);

    @Autowired
    private VendorPaymentService vendorPaymentService;

    @Autowired
    private VendorService vendorService;

    @RequestMapping(value = "/getByVendorId", method = RequestMethod.GET)
    @ResponseBody
    public Map getByVendorId( HttpServletRequest httpRequest) throws Exception {
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        try{
            User user = super.getUser(httpRequest);
            Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
            VendorPayment vendorPayment = vendorPaymentService.selectByVendorId(vendor.getVendorId());
            stringObjectMap.put("data",vendorPayment);
            stringObjectMap.put("status", StatusType.SUCCESS);
        }catch (Exception e){
            logger.error("查询vendorPayment异常"+e.getMessage(),e);
            stringObjectMap.put("status", StatusType.FAILURE);
            stringObjectMap.put("message", "System error!");
        }
        return stringObjectMap;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Map create(HttpServletRequest httpRequest,@RequestBody VendorPayment vendorPayment) throws Exception {
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        try{
            if(StringUtil.isNotEmpty(vendorPayment.getPostalCode1())&&vendorPayment.getCountry1()!=null){
                String pattent = getPattent(Long.parseLong(vendorPayment.getCountry1()));
                if(pattent!=null){
                    Pattern r = Pattern.compile(pattent);
                    Matcher m = r.matcher(vendorPayment.getPostalCode1());
                    if (!m.find()) {
                        stringObjectMap.put("status", StatusType.FAILURE);
                        stringObjectMap.put("message","postalcode error");
                        return stringObjectMap;
                    }
                }
            }

            if(StringUtil.isNotEmpty(vendorPayment.getPostalCod2())&&vendorPayment.getCountry2()!=null){
                String pattent = getPattent(Long.parseLong(vendorPayment.getCountry2()));
                if(pattent!=null){
                    Pattern r = Pattern.compile(pattent);
                    Matcher m = r.matcher(vendorPayment.getPostalCod2());
                    if (!m.find()) {
                        stringObjectMap.put("status", StatusType.FAILURE);
                        stringObjectMap.put("message","postalcode error");
                        return stringObjectMap;
                    }
                }
            }
            User user = super.getUser(httpRequest);
            Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
            vendorPayment.setVendorId(vendor.getVendorId());
            vendorPayment.setUserId(user.getUserId());
            vendorPayment.setCreatedAt(new Date());
            vendorPayment.setUpdatedAt(new Date());
            vendorPaymentService.createVendorPayment(vendorPayment);
            stringObjectMap.put("status", StatusType.SUCCESS);
        }catch (Exception e){
            logger.error("创建vendorPayment异常"+e.getMessage(),e);
            stringObjectMap.put("status", StatusType.FAILURE);
            stringObjectMap.put("message", "System error!");
        }
        return stringObjectMap;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Map update(@RequestBody VendorPayment vendorPayment) throws Exception {
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        try{
            if(StringUtil.isNotEmpty(vendorPayment.getPostalCode1())&&vendorPayment.getCountry1()!=null){
                String pattent = getPattent(Long.parseLong(vendorPayment.getCountry1()));
                if(pattent!=null){
                    Pattern r = Pattern.compile(pattent);
                    Matcher m = r.matcher(vendorPayment.getPostalCode1());
                    if (!m.find()) {
                        stringObjectMap.put("status", StatusType.FAILURE);
                        stringObjectMap.put("message","postalcode error");
                        return stringObjectMap;
                    }
                }
            }
            if(StringUtil.isNotEmpty(vendorPayment.getPostalCod2())&&vendorPayment.getCountry2()!=null){
                String pattent = getPattent(Long.parseLong(vendorPayment.getCountry2()));
                if(pattent!=null){
                    Pattern r = Pattern.compile(pattent);
                    Matcher m = r.matcher(vendorPayment.getPostalCod2());
                    if (!m.find()) {
                        stringObjectMap.put("status", StatusType.FAILURE);
                        stringObjectMap.put("message","postalcode error");
                        return stringObjectMap;
                    }
                }
            }
            vendorPayment.setUpdatedAt(new Date());
            vendorPaymentService.updateVendorPayment(vendorPayment);
            stringObjectMap.put("status", StatusType.SUCCESS);
        }catch (Exception e){
            logger.error("更新vendorPayment异常"+e.getMessage(),e);
            stringObjectMap.put("status", StatusType.FAILURE);
            stringObjectMap.put("message", "System error!");
        }
        return stringObjectMap;
    }

    private String getPattent(Long countryId){
        if(countryId==1){
            return "(^[0-9]{5}$)";
        }else if(countryId==2){
            return "(^[0-9]{6}$)";
        }else if(countryId==3){
            return null;
        }else if(countryId==4){
            return null;
        }else if(countryId==5){
            return "(^[0-9]{4}$)";
        }else if(countryId==6){
            return "(^[0-9]{4}$)";
        }else if(countryId==7){
            return "(^[0-9]{4}$)";
        }else if(countryId==8){
            return "(^[0-9]{5}$)";
        }else if(countryId==9){
            return "(^[0-9]{4}$)";
        }else if(countryId==10){
            return "(^[0-9]{5}$)";
        }else if(countryId==11){
            return "(^[0-9]{4}$)";
        }else if(countryId==12){
            return "(^[0-9]{5}$)";
        }else if(countryId==13){
            return "(^[0-9]{5}$)";
        }else if(countryId==14){
            return "(^[0-9]{5}$)";
        }else if(countryId==15){
            return "(^[0-9]{5}$)";
        }else if(countryId==16){
            return "(^[0-9]{5}$)";
        }else if(countryId==17){
            return "(^[0-9]{4}$)";
        }else if(countryId==18){
            return null;
        }else if(countryId==19){
            return "(^[0-9]{5}$)";
        }else if(countryId==20){
            return "(^[0-9]{4}$)";
        }else if(countryId==21){
            return "(^[0-9]{5}$)";
        }else if(countryId==22){
            return "(^[0-9]{4}$)";
        }else if(countryId==23){
            return null;
        }else if(countryId==24){
            return "(^[0-9]{4}$)";
        }else if(countryId==25){
            return "(^[0-9]{5}$)";
        }else if(countryId==26){
            return "(^[0-9]{7}$)";
        }else if(countryId==27){
            return "(^[0-9]{6}$)";
        }else if(countryId==28){
            return "(^[0-9]{5}$)";
        }else if(countryId==29){
            return "(^[0-9]{4}$)";
        }else if(countryId==30){
            return "(^[0-9]{5}$)";
        }else if(countryId==31){
            return "(^[0-9]{5}$)";
        }else if(countryId==32){
            return"(^[0-9a-zA-Z]+$))";
        }else if(countryId==40){
            return "(^[0-9]{5}$)";
        }else if(countryId==41){
            return "(^[0-9]{6}$)";
        }else if(countryId==42){
            return"(^[0-9a-zA-Z]+$))";
        }else if(countryId==43){
            return "(^[0-9]{3,4}$)";
        }else if(countryId==44){
            return "(^[0-9]{4}$)";
        }else if(countryId==45){
            return "(^[0-9]{3,5}$)";
        }else if(countryId==46){
            return "(^[0-9]{4}$)";
        }else if(countryId==47){
            return "(^[0-9]{5}$)";
        }else if(countryId==48){
            return "(^[0-9]{5}$)";
        }else if(countryId==49){
            return "(^[0-9]{7}$)";
        }else if(countryId==50){
            return "(^[0-9]{5}$)";
        }else if(countryId==51){
            return "(^[0-9]{6}$)";
        }
        return null;
    }
}
