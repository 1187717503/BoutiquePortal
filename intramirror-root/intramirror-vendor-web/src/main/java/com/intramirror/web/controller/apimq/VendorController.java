package com.intramirror.web.controller.apimq;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.service.PostCodeService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pk.shoplus.common.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@Controller
@RequestMapping("/vendor")
public class VendorController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    private VendorService vendorService;
    @Autowired
    private PostCodeService postCodeService;

    @RequestMapping(value = "/getVendor", method = RequestMethod.GET)
    @ResponseBody
    public Map getCountry(HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        User user = super.getUser(httpRequest);
        if (user == null) {
            result.put("status", StatusType.FAILURE);
            return result;
        }
        try {
            Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
            result.put("status", StatusType.SUCCESS);
            result.put("data",vendor);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("查询vendor异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Map getCountry(HttpServletRequest httpRequest,@RequestBody Vendor vendor) {
        Map<String, Object> result = new HashMap<>();
        try {
            if(StringUtil.isNotEmpty(vendor.getPostalCode())&&vendor.getAddressCountryId()!=null){
                String pattent = getPattent(vendor.getAddressCountryId());
                if(pattent!=null){
                    Pattern r = Pattern.compile(pattent);
                    Matcher m = r.matcher(vendor.getPostalCode());
                    if (!m.find()) {
                        result.put("status", StatusType.FAILURE);
                        result.put("message","postalcode error");
                        return result;
                    }
                }
            }
            if(postCodeService.getByCountryId(vendor.getAddressCountryId(),vendor.getCity())==null){
                result.put("status", StatusType.FAILURE);
                result.put("message","city error");
                return result;
            }
            vendorService.updateByPrimaryKeySelective(vendor);
            result.put("status", StatusType.SUCCESS);
            result.put("data",vendor);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("更新vendor异常："+e.getMessage(),e);
        }
        return result;
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
