package com.intramirror.web.controller.apimq;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.model.StockLocationDto;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.StockLocationService;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@Controller
@RequestMapping("/stockLocation")
public class StockLocationController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(StockLocationController.class);

    @Autowired
    private StockLocationService stockLocationService;
    @Autowired
    private AddressCountryService addressCountryService;
    @Autowired
    private VendorService vendorService;

    @RequestMapping(value = "/getStockLocationList", method = RequestMethod.GET)
    @ResponseBody
    public Map getCityName(HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = super.getUser(httpRequest);
            Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
            List<StockLocation> list = stockLocationService.getStockLocations(vendor.getVendorId());
            for(StockLocation stockLocation:list){
                StockLocation stockLocationFrom = stockLocationService.getStockLocation(stockLocation.getShipFromLocationId());
                if(stockLocationFrom!=null){
                    stockLocation.setShipFromLocationName(stockLocationFrom.getStockLocation());
                }
                stockLocation.setContactPhoneNumber((stockLocation.getAreaCode()==null?"":stockLocation.getAreaCode())+stockLocation.getContactPhoneNumber());
                String fullAddress = (stockLocation.getAddressStreetlines()==null?"":stockLocation.getAddressStreetlines())+(stockLocation.getAddressStreetlines2()==null?"":stockLocation.getAddressStreetlines2())
                        +(stockLocation.getAddressStreetlines3()==null?"":stockLocation.getAddressStreetlines3());
                stockLocation.setFullAddress(fullAddress);
                AddressCountry addressCountry = addressCountryService.getAddressCountryByCountryCode(stockLocation.getAddressCountryCode());
                if(addressCountry!=null){
                    stockLocation.setCountryName(addressCountry.getEnglishName());
                }
            }
            result.put("status", StatusType.SUCCESS);
            result.put("data",list);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("查询stockLocation列表异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/createStockLocation", method = RequestMethod.POST)
    @ResponseBody
    public Map createStockLocation(HttpServletRequest httpRequest, @RequestBody StockLocation stockLocation) {
        Map<String, Object> result = new HashMap<>();
        try {
            if(StringUtil.isNotEmpty(stockLocation.getAddressPostalCode())&&stockLocation.getAddressCountryCode()!=null){
                String pattent = getPattent(stockLocation.getAddressCountryCode());
                if(pattent!=null){
                    Pattern r = Pattern.compile(pattent);
                    Matcher m = r.matcher(stockLocation.getAddressPostalCode());
                    if (!m.find()) {
                        result.put("status", StatusType.FAILURE);
                        result.put("message","postalcode error");
                        return result;
                    }
                }
            }
            User user = super.getUser(httpRequest);
            Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
            stockLocation.setEnabled(true);
            stockLocation.setCreateAt(new Date());
            stockLocation.setUpdateAt(new Date());
            stockLocation.setVendorId(vendor.getVendorId());
            stockLocationService.createStockLocation(stockLocation);
            if(stockLocation.getShipFromLocationId()==-1){
                stockLocation.setShipFromLocationId(stockLocation.getLocationId());
                stockLocationService.updateStockLocation(stockLocation);
            }
            result.put("status", StatusType.SUCCESS);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("创建stockLocation列表异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/updateStockLocation", method = RequestMethod.POST)
    @ResponseBody
    public Map updateStockLocation(HttpServletRequest httpRequest, @RequestBody StockLocation stockLocation) {
        Map<String, Object> result = new HashMap<>();
        try {
            if(StringUtil.isNotEmpty(stockLocation.getAddressPostalCode())&&stockLocation.getAddressCountryCode()!=null){
                String pattent = getPattent(stockLocation.getAddressCountryCode());
                if(pattent!=null){
                    Pattern r = Pattern.compile(pattent);
                    Matcher m = r.matcher(stockLocation.getAddressPostalCode());
                    if (!m.find()) {
                        result.put("status", StatusType.FAILURE);
                        result.put("message","postalcode error");
                        return result;
                    }
                }
            }
            stockLocation.setUpdateAt(new Date());
            stockLocationService.updateStockLocation(stockLocation);
            result.put("status", StatusType.SUCCESS);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("更新stockLocation列表异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/deleteStockLocation", method = RequestMethod.GET)
    @ResponseBody
    public Map deleteStockLocation(HttpServletRequest httpRequest, Long stockLocationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            stockLocationService.deleteStockLocation(stockLocationId);
            result.put("status", StatusType.SUCCESS);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("删除stockLocation列表异常："+e.getMessage(),e);
        }
        return result;
    }

    private String getPattent(String code){
        if("PK".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("CN".equals(code)){
            return "(^[0-9]{6}$)";
        }else if("HK".equals(code)){
            return null;
        }else if("MO".equals(code)){
            return null;
        }else if("AT".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("BE".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("BG".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("HR".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("CY".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("CZ".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("DK".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("EE".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("FI".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("FR".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("DE".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("GR".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("HU".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("IE".equals(code)){
            return null;
        }else if("IT".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("LV".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("LT".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("LU".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("MT".equals(code)){
            return null;
        }else if("NL".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("PL".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("PT".equals(code)){
            return "(^[0-9]{7}$)";
        }else if("RO".equals(code)){
            return "(^[0-9]{6}$)";
        }else if("SK".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("SI".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("ES".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("SE".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("GB".equals(code)){
            return"(^[0-9a-zA-Z]+$))";
        }else if("US".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("RU".equals(code)){
            return "(^[0-9]{6}$)";
        }else if("CA".equals(code)){
            return"(^[0-9a-zA-Z]+$))";
        }else if("AU".equals(code)){
            return "(^[0-9]{3,4}$)";
        }else if("NZ".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("TW".equals(code)){
            return "(^[0-9]{3,5}$)";
        }else if("PH".equals(code)){
            return "(^[0-9]{4}$)";
        }else if("KR".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("MY".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("JP".equals(code)){
            return "(^[0-9]{7}$)";
        }else if("TH".equals(code)){
            return "(^[0-9]{5}$)";
        }else if("SG".equals(code)){
            return "(^[0-9]{6}$)";
        }
        return null;
    }
}
