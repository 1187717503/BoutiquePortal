package com.intramirror.web.controller.apimq;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.PostCodeService;
import com.intramirror.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/address")
public class AddressController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressCountryService addressCountryService;
    @Autowired
    private PostCodeService postCodeService;

    @RequestMapping(value = "/getCountry", method = RequestMethod.GET)
    @ResponseBody
    public Map getCountry(HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<AddressCountry> list = addressCountryService.getList();
            result.put("status", StatusType.SUCCESS);
            result.put("data",list);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("查询国家列表异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/getCityName", method = RequestMethod.GET)
    @ResponseBody
    public Map getCityName(HttpServletRequest httpRequest, Long countryId,String cityName) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String,String>> list = postCodeService.getCityNameByCountryIdAndCityName(countryId,cityName);
            result.put("status", StatusType.SUCCESS);
            result.put("data",list);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("查询城市列表异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/getCityNameByCode", method = RequestMethod.GET)
    @ResponseBody
    public Map getCityName(HttpServletRequest httpRequest, String code,String cityName) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String,String>> list = postCodeService.getCityNameByCountryCodeAndCityName(code,cityName);
            result.put("status", StatusType.SUCCESS);
            result.put("data",list);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("查询城市列表异常："+e.getMessage(),e);
        }
        return result;
    }

}
