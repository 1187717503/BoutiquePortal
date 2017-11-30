package com.intramirror.web.controller.price;

import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.PriceChangeRuleExcelUtils;
import com.intramirror.product.api.service.rule.IRuleService;
import com.intramirror.web.common.CommonProperties;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dingyifan on 2017/11/30.
 */
@Controller
@RequestMapping("/rule")
public class RulePoiController {

    private static Logger logger = Logger.getLogger(RulePoiController.class);

    @Autowired
    private CommonProperties commonProperties;

    @Resource(name = "productRuleServiceImpl")
    private IRuleService iRuleService;

    @RequestMapping("/download")
    public void downloads(@Param("price_change_rule_id")String price_change_rule_id,HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String,Object> params = new HashMap<>();
            params.put("exception_flag", 0);
            params.put("price_change_rule_id",price_change_rule_id);
            List<Map<String,Object>> dataMaps =  iRuleService.queryRuleByBrand(params);
            List<Map<String,Object>> brandMaps =  iRuleService.queryNotRuleByBrand(params);

            PriceChangeRuleExcelUtils.genPriceExcel("Pricing Rule",brandMaps,dataMaps);
            File file = new File("/Users/dingyifan/Downloads/workbook.xls");
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition",
                    "attachment;fileName=workbook.xls");

            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("RulePoiController,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }
}
