package com.intramirror.web.controller.price;

import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.PriceChangeRuleExcelUtils;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.enums.CategoryTypeEnum;
import com.intramirror.product.api.model.*;
import com.intramirror.product.api.service.IPriceChangeRuleSeasonGroupService;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.product.api.service.rule.IRuleService;
import com.intramirror.web.common.CommonProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

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
    @Resource(name = "productPriceChangeRule")
    private IPriceChangeRule iPriceChangeRule;

    @Resource(name = "productCategoryServiceImpl")
    private ICategoryService iCategoryService;

    @Autowired
    private ITagService iTagService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IPriceChangeRuleSeasonGroupService iPriceChangeRuleSeasonGroupService;

    @RequestMapping("/download")
    public void download(@Param("price_change_rule_id") String price_change_rule_id,HttpServletResponse response) {
        try {
            PriceChangeRule priceChangeRule=iPriceChangeRule.selectByPrimaryKey(Long.valueOf(price_change_rule_id));
            String type = String.valueOf(priceChangeRule.getCategoryType());
            // 查询品牌目录映射
            Map<String, Object> params = new HashMap<>();
            params.put("exception_flag", 0);
            params.put("price_change_rule_id", price_change_rule_id);
            /*params.put("categoryType",Integer.valueOf(type));*/
            List<Map<String, Object>> dataMaps = iRuleService.queryRuleByBrand(params);
            List<Map<String, Object>> productGroupMaps = iRuleService.queryRuleByGroup(params);
            List<Map<String, Object>> productMaps = iRuleService.queryRuleByProduct(params);
            params.put("exception_flag", 1);
            List<Map<String, Object>> categoryBrandMaps = iRuleService.queryRuleByBrandOne(params);

            //查询所有类目
            List<Category> allCategories = this.getCategoryList(type);
            List<String> categoryNodeNameList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(allCategories)) {
                for (Category category_1 : allCategories) {
                    for (Category category_2 : category_1.getChildren()) {
                        List<Category> category_2Children = category_2.getChildren();
                        for (Category category_3 : category_2Children) {
                            categoryNodeNameList.add(category_3.getCategoryPath());
                        }
                    }
                }
            }

            //查询所有product group
            List<Tag> tags = this.getProductGroupList(priceChangeRule.getVendorId());
            List<String> productGroupList = new ArrayList<>();
            for (Tag tag : tags) {
                productGroupList.add(tag.getTagName());
            }

            // 查询所有品牌
            List<Map<String, Object>> brandMaps = getBrandList(params);
            List<String> brandList = new ArrayList<>();
            brandList.add("Default");
            for (Map<String, Object> brandMap : brandMaps) {
                brandList.add(brandMap.get("english_name").toString());
            }

            Map<String, List<String>> baseDataMap = new HashMap();
            baseDataMap.put("brandList", brandList);
            baseDataMap.put("categoryNodeNameList", categoryNodeNameList);
            baseDataMap.put("productGroupList", productGroupList);

            // 设置文件目录路径
            String dateStr = DateUtils.getStrDate(new Date(), "yyyyMMddHHmmss");
            String fileName = dateStr + ".xls";
            String filePath = commonProperties.getRuleExcelPath() + "download/" + fileName;

            logger.info("RulePoiController,download,filePath:" + filePath);
            PriceChangeRuleExcelUtils.genPriceExcel(type, baseDataMap, dataMaps, filePath, categoryBrandMaps, productGroupMaps, productMaps);
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

    private List<Map<String, Object>> getBrandList(Map<String, Object> params) throws Exception {
        return iRuleService.queryNotRuleByBrand(params);
    }

    private List<Category> getCategoryList(String type) throws Exception {
        List<Category> categories = new ArrayList<>();
        List<Category> allCategories = iCategoryService.queryActiveCategorys();
        if (CollectionUtils.isNotEmpty(allCategories)) {
            CategoryTypeEnum categoryType = null;
            if (type.equals(CategoryTypeEnum.ADULT.getCategoryType())) {
                categoryType = CategoryTypeEnum.ADULT;
            } else if (type.equals(CategoryTypeEnum.KIDS.getCategoryType())) {
                categoryType = CategoryTypeEnum.KIDS;
            }
            for (Category category_1 : allCategories) {
                List<Long> firstCategoryIds = categoryType.getFirstCategoryIds();
                if (firstCategoryIds.contains(category_1.getCategoryId())) {
                    if (category_1.getCategoryId().equals(Long.valueOf(1499))) { // Men
                        List<Category> childs = category_1.getChildren();
                        Category tmpB = null;
                        Category tmpS = null;
                        for (Category cate : childs) {
                            if (cate.getCategoryId().equals(Long.valueOf(1505))) { // Bags
                                tmpB = cate;
                            }
                            if (cate.getCategoryId().equals(Long.valueOf(1506))) { // Shoes
                                tmpS = cate;
                            }
                        }
                        if (tmpB != null && tmpS != null) {
                            childs.add(1, tmpS);
                            childs.add(2, tmpB);
                            childs.remove(3);
                            childs.remove(3);
                        }
                    }
                    categories.add(category_1);
                }
            }
        }
        return categories;
    }

    private List<Tag> getProductGroupList(Long vendorId) {
        Map<String, Object> param = new HashMap<>();
        param.put("vendorIds", Collections.singletonList(vendorId));
        param.put("tagTypes", Collections.singletonList(2));
        return iTagService.getTagsByParam(param);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    public Response upload(@RequestParam("file") MultipartFile file, @RequestParam("price_change_rule_id") String price_change_rule_id,
            HttpServletRequest request) throws Exception {

        PriceChangeRule priceChangeRule = iPriceChangeRule.selectByPrimaryKey(Long.valueOf(price_change_rule_id));

        String type = String.valueOf(priceChangeRule.getCategoryType());

        // 查询所有品牌
        List<Map<String, Object>> brandMaps = iRuleService.queryAllBrand();
        Map<String, Object> brand0 = new HashMap<>();
        brand0.put("english_name", "Default");
        brand0.put("brand_id", "0");
        brandMaps.add(0, brand0);

        //查询所有类目
        List<Category> allCategories = this.getCategoryList(type);
        List<Map<String, Object>> categoryMaps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allCategories)) {
            for (Category category_1 : allCategories) {
                for (Category category_2 : category_1.getChildren()) {
                    for (Category category_3 : category_2.getChildren()) {
                        Map<String, Object> categoryMap = new HashMap<>();
                        categoryMap.put("categoryId", category_3.getCategoryId());
                        categoryMap.put("categoryPath", category_3.getCategoryPath());
                        categoryMap.put("parentId", category_3.getParentId());
                        categoryMap.put("level", category_3.getLevel());
                        categoryMaps.add(categoryMap);
                    }
                }
            }
        }

        //查询所有product group
        List<Tag> tags = this.getProductGroupList(priceChangeRule.getVendorId());
        List<Map<String, Object>> productGroupMaps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tags)) {
            for (Tag tag : tags) {
                Map<String, Object> productGroupMap = new HashMap<>();
                productGroupMap.put("productGroupName", tag.getTagName());
                productGroupMap.put("productGroupId", tag.getTagId());
                productGroupMaps.add(productGroupMap);
            }
        }

        Map<String, List<Map<String, Object>>> baseDataMap = new HashMap<>();
        baseDataMap.put("brandMap", brandMaps);
        baseDataMap.put("categoryMap", categoryMaps);
        baseDataMap.put("productGroupMap", productGroupMaps);

        String filePath = commonProperties.getRuleExcelPath() + "upload/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        Map<String, List<Map<String, Object>>> sheetExcelData = PriceChangeRuleExcelUtils.readRuleExcel(filePath, baseDataMap, price_change_rule_id, type);
        //  检查Excel数据的可用性
        this.checkExcelData(sheetExcelData, type, priceChangeRule.getVendorId(), Long.valueOf(price_change_rule_id));

        iRuleService.changeRule(price_change_rule_id, sheetExcelData);

        return Response.success();
    }

    private void checkExcelData(Map<String, List<Map<String, Object>>> sheetExcelData, String type, Long vendorId, Long priceChangeRuleId) throws Exception {
        List<Map<String, Object>> categoryBrandMapList = sheetExcelData.get("priceRuleList");
        List<Map<String, Object>> categoryBrandExceptionMapList = sheetExcelData.get("categoryBrandList");
        List<Map<String, Object>> productGroupList = sheetExcelData.get("productGroupList");
        List<Map<String, Object>> productList = sheetExcelData.get("productList");
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
        int jsum = 0;
        if("1".equals(type)) {
            jsum = set.size() * 8; // men women 导出
        }else if("2".equals(type)){
            jsum = set.size() * 9; // kids 校验
        }
        if (sum != jsum) {
            throw new RuntimeException("Excel品牌数据存在重复。");
        }

        Set<String> categoryBrand3Set = new HashSet<>();
        for (Map<String, Object> map : categoryBrandExceptionMapList) {
            String value = map.get("brand_id").toString() + "_" + map.get("category_id").toString();
            categoryBrand3Set.add(value);
        }
        if (categoryBrand3Set.size() != categoryBrandExceptionMapList.size()) {
            throw new RuntimeException("Excel品牌数据存在重复。");
        }

        Set<String> productGroupSet = new HashSet<>();
        for (Map<String, Object> map : productGroupList) {
            String value = map.get("product_group_id").toString();
            productGroupSet.add(value);
        }
        if (productGroupSet.size() != productGroupList.size()) {
            throw new RuntimeException("Product Group数据存在重复。");
        }

        //查询priceChangeRule的seasonCode
//        List<PriceChangeRuleSeasonGroup> priceChangeRuleSeasonGroups = iPriceChangeRuleSeasonGroupService.getPriceChangeRuleGroupListByPriceChangeRuleId(priceChangeRuleId);
//        Set<String> seasonCodes = new HashSet<>();
//        for (PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroup : priceChangeRuleSeasonGroups) {
//            seasonCodes.add(priceChangeRuleSeasonGroup.getSeasonCode());
//        }

        Set<String> productSet = new HashSet<>();
        for (Map<String, Object> map : productList) {
            String designerId = map.get("designer_id").toString();
            String colorCode = map.get("color_code").toString();
            String value = designerId + "_" + colorCode;
            productSet.add(value);

            //查询是否存在该商品
            List<ProductWithBLOBs> productWithBLOBsList = null;
            if (StringUtils.isNotBlank(designerId) && StringUtils.isNotBlank(colorCode) ) {
                ProductWithBLOBs productWithBLOBs = new ProductWithBLOBs();
                productWithBLOBs.setDesignerId(designerId);
                productWithBLOBs.setColorCode(colorCode);
                productWithBLOBs.setEnabled(EnabledType.USED);
                productWithBLOBs.setVendorId(vendorId);
                productWithBLOBsList = productService.getProductByParameter(productWithBLOBs);
            }

            if (productWithBLOBsList == null) {
                throw new RuntimeException("Can't find the goods");
            } else if (productWithBLOBsList.size() > 1) {
                throw new RuntimeException("Duplicate products exist.");
            }
            ProductWithBLOBs productWithBLOBs = productWithBLOBsList.get(0);
            //商品的seasonCode要和priceChangeRule保持一致
//            if(!seasonCodes.contains(productWithBLOBs.getSeasonCode())) {
//                throw new RuntimeException("Wrong Season Code.");
//            }

            map.put("product_id", productWithBLOBs.getProductId());
            map.put("boutique_id", productWithBLOBs.getProductCode());
            map.put("product_name", productWithBLOBs.getName());
        }
        if (productSet.size() != productList.size()) {
            throw new RuntimeException("Product数据存在重复。");
        }

    }
}
