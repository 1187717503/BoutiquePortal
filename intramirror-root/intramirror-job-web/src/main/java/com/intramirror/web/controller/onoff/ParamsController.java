package com.intramirror.web.controller.onoff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.main.api.service.ApiParameterService;
import com.intramirror.product.api.service.IApiMqService;
import com.intramirror.web.vo.ParamsVo;

/**
 * Created by dingyifan on 2017/8/31.
 */
@Controller
@RequestMapping("/params")
public class ParamsController {

    private static Logger logger = Logger.getLogger(ParamsController.class);

    @Autowired
    private IApiMqService iApiMqService;

    @Autowired
    private ApiParameterService apiParameterService;

    private final static String[] dateParams = new String[]{"datetime","DateStart","DateEnd"};

    @RequestMapping("/change")
    @ResponseBody
    public ResultMessage changeParams(@ModelAttribute ParamsVo paramsVo){
        ResultMessage resultMessage = new ResultMessage();
        try {
            // checked
            if(paramsVo == null
                    || StringUtils.isBlank(paramsVo.getMqName())
                    || StringUtils.isBlank(paramsVo.getParamKey())
                    || StringUtils.isBlank(paramsVo.getParamValue())) {
                logger.info("ParamsControllerChangeParams,params:"+new Gson().toJson(paramsVo));
                return resultMessage.errorStatus().putMsg("info","paramsVo is null !!!");
            }

            // select
            List<Map<String,Object>> apiMqList = iApiMqService.selectMqByName(paramsVo.getMqName());
            logger.info("ParamsControllerChangeParams,apiMqList:"+new Gson().toJson(apiMqList));

            if(apiMqList == null || apiMqList.size() == 0) {
                logger.info("ParamsControllerChangeParams,apiMqList is null:"+new Gson().toJson(paramsVo));
                return resultMessage.errorStatus().putMsg("info","apiMqList is null !!!");
            }

            // update
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("paramValue",paramsVo.getParamValue());
            paramMap.put("paramKey",paramsVo.getParamKey());
            paramMap.put("apiEndPointId",apiMqList.get(0).get("api_end_point_id"));
            logger.info("ParamsControllerChangeParams,paramMap:"+new Gson().toJson(paramMap));
            int rs = apiParameterService.updateApiParameterByKey(paramMap);
            logger.info("ParamsControllerChangeParams,rs"+rs);

            String currentDate = DateUtils.getTimeByHour(6);

            for(String dateParam : dateParams) {
                paramMap.put("paramValue",currentDate);
                paramMap.put("paramKey",dateParam);
                paramMap.put("apiEndPointId",apiMqList.get(0).get("api_end_point_id"));
                logger.info("ParamsControllerChangeParams,"+dateParam+":"+new Gson().toJson(paramMap));
                rs = apiParameterService.updateApiParameterByKey(paramMap);
                logger.info("ParamsControllerChangeParams,"+dateParam+",rs:"+new Gson().toJson(paramMap));
            }
            resultMessage.successStatus().putMsg("info","success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ParamsControllerChangeParams,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().putMsg("info","errorMessage:"+e.getMessage());
        }
        return resultMessage;
    }
}
