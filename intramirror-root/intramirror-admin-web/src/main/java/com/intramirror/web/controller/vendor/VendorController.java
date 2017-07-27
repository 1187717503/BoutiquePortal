package com.intramirror.web.controller.vendor;

import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.user.api.service.vendor.IQueryVendorService;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/20.
 */
@CrossOrigin
@Controller
@RequestMapping("/vendor")
public class VendorController {
    private static Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Resource(name = "userQueryVendorServiceImpl")
    private IQueryVendorService iQueryVendorService;

    @RequestMapping("/select/queryAllVendor")
    @ResponseBody
    public ResultMessage queryAllVendor(){
        ResultMessage resultMessage = new ResultMessage();
        try {
            List<Map<String,Object>> allVendorMaps = iQueryVendorService.queryAllVendor(null);
            resultMessage.successStatus().putMsg("info","success").setData(allVendorMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/select/queryRuleVendor")
    @ResponseBody
    public ResultMessage queryRuleVendor(@Param("status") String status,@Param("price_type")String price_type){
        ResultMessage resultMessage = new ResultMessage();
        try {
            if(StringUtils.isBlank(status)) {
                return resultMessage.errorStatus().putMsg("info"," status is nulll !!!");
            }

            Map<String,Object> params = new HashMap<>();
//            params.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
            params.put("price_type", Integer.parseInt(price_type));
            params.put("status",status);
            List<Map<String,Object>> allVendorMaps = iQueryVendorService.queryRuleVendor(params);
            if(allVendorMaps != null && allVendorMaps.size() > 0) {
                resultMessage.successStatus().putMsg("info","success").setData(allVendorMaps);
            } else {
                resultMessage.errorStatus().putMsg("info","empty !!!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }


}
