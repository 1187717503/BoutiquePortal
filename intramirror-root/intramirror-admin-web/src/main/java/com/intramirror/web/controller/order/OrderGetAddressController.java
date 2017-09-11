package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.service.AddressCityService;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.AddressDistrictService;
import com.intramirror.main.api.service.AddressProvinceService;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderGetAddressController extends BaseController {


    @Autowired
    private AddressCountryService addressCountryService;

    @Autowired
    private AddressProvinceService addressProvinceService;

    @Autowired
    private AddressDistrictService addressDistrictService;

    @Autowired
    private AddressCityService addressCityService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/get_address", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage orderGetAddress(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) throws Exception {
        ResultMessage result = new ResultMessage();
        Map<String, Object> results = new HashMap<>();
        result.errorStatus();

        User user = this.getUserInfo(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        String geographyId = null;
        String countryId = null;
        String provinceId = null;
        String cityId = null;
        String districtId = null;

        if (map.get("geography_id") != null && StringUtils.isNotBlank(map.get("geography_id").toString())) {
            geographyId = map.get("geography_id").toString();
        }

        if (map.get("country_id") != null && StringUtils.isNotBlank(map.get("country_id").toString())) {
            countryId = map.get("country_id").toString();
        }

        if (map.get("province_id") != null && StringUtils.isNotBlank(map.get("province_id").toString())) {
            provinceId = map.get("province_id").toString();
        }

        if (map.get("city_id") != null && StringUtils.isNotBlank(map.get("city_id").toString())) {
            cityId = map.get("city_id").toString();
        }

        if (map.get("district_id") != null && StringUtils.isNotBlank(map.get("district_id").toString())) {
            districtId = map.get("district_id").toString();
        }

        try {

            if (geographyId != "" && geographyId != null) {
                List<Map<String, Object>> countryMapList = addressCountryService.getAddressCountryByGeographyId(Long.valueOf(geographyId));
                results.put("countryMapList", countryMapList);
            }
            if (countryId != "" && countryId != null) {
                List<Map<String, Object>> provinceMapList = addressProvinceService.getAddressProvinceByCountryId(Long.valueOf(countryId));
                results.put("provinceMapList", provinceMapList);
            }
            if (provinceId != "" && provinceId != null) {
                List<Map<String, Object>> cityMapList = addressCityService.getAddressCityByProvinceId(Long.valueOf(provinceId));
                results.put("cityMapList", cityMapList);
            }
            if (cityId != "" && cityId != null) {
                List<Map<String, Object>> districtMapList = addressDistrictService.getAddressDistrictByCityId(Long.valueOf(cityId));
                results.put("districtMapList", districtMapList);
            }

            result.successStatus();
            result.setData(results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
