package com.intramirror.web.controller.price;

import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.PriceChangeRuleExcelUtils;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.service.rule.IRuleService;
import com.intramirror.web.common.CommonProperties;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
    public void download(@Param("price_change_rule_id") String price_change_rule_id, HttpServletResponse response) {
        try {
            // 查询品牌目录映射
            Map<String, Object> params = new HashMap<>();
            params.put("exception_flag", 0);
            params.put("price_change_rule_id", price_change_rule_id);
            List<Map<String, Object>> dataMaps = iRuleService.queryRuleByBrand(params);

            // 查询所有品牌
            List<Map<String, Object>> brandMaps = iRuleService.queryNotRuleByBrand(params);
            Map<String, Object> brand0 = new HashMap<>();
            brand0.put("english_name", "Default");
            brand0.put("brand_id", "0");
            brandMaps.add(0, brand0);

            // 设置文件目录路径
            String dateStr = DateUtils.getStrDate(new Date(), "yyyyMMddHHmmss");
            String fileName = dateStr + ".xls";
            String filePath = commonProperties.getRuleExcelPath() + "download/" + fileName;

            logger.info("RulePoiController,download,filePath:" + filePath);
            PriceChangeRuleExcelUtils.genPriceExcel("Pricing Rule", brandMaps, dataMaps, filePath);
            File file = new File(filePath);
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);

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
            logger.info("RulePoiController,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    public Response upload(@RequestParam("file") MultipartFile file, @RequestParam("price_change_rule_id") String price_change_rule_id,
            HttpServletRequest request) throws Exception {

        // 查询所有品牌
        List<Map<String, Object>> brandMaps = iRuleService.queryAllBrand();
        Map<String, Object> brand0 = new HashMap<>();
        brand0.put("english_name", "Default");
        brand0.put("brand_id", "0");
        brandMaps.add(0, brand0);

        String filePath = commonProperties.getRuleExcelPath() + "upload/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        List<Map<String, Object>> categoryBrandMapList = PriceChangeRuleExcelUtils.readRuleExcel(filePath, brandMaps, price_change_rule_id);

        //  检查Excel数据的可用性
        this.checkExcelData(categoryBrandMapList);

        iRuleService.changeRule(price_change_rule_id, categoryBrandMapList);

        return Response.success();
    }

    private void checkExcelData(List<Map<String, Object>> categoryBrandMapList) throws Exception {
        if (categoryBrandMapList == null || categoryBrandMapList.size() == 0) {
            throw new RuntimeException("没有读取到Excel的数据。");
        }

        boolean defaultBrand = false;
        HashSet set = new HashSet();
        for (Map<String, Object> map : categoryBrandMapList) {
            if (map.get("brand_id").toString().equals("0")) {
                defaultBrand = true;
            }
            set.add(map.get("brand_id").toString());
        }

        if (!defaultBrand) {
            throw new RuntimeException("没有读取到Excel的默认的品牌数据。");
        }

        int sum = categoryBrandMapList.size();
        int jsum = set.size() * 8;
        if (sum != jsum) {
            throw new RuntimeException("Excel品牌数据存在重复。");
        }
    }
}
