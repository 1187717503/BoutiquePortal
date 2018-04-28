package com.intramirror.web.controller.price;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.enums.SystemPropertyEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.StatusCode;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;
import com.intramirror.product.api.model.PriceChangeRuleGroup;
import com.intramirror.product.api.model.PriceChangeRuleProduct;
import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.api.service.IPriceChangeRuleProductService;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.ISystemPropertyService;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.PriceChangeRuleService;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wzh
 */

@Controller
@RequestMapping("/priceChangeRule")
public class PriceChangeRuleController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleController.class);

    @Autowired
    private IPriceChangeRuleCategoryBrandService priceChangeRuleCategoryBrandService;

    @Autowired
    private IPriceChangeRule priceChangeRule;

    @Autowired
    private IPriceChangeRuleProductService priceChangeRuleProductService;

    @Autowired
    private IPriceChangeRuleGroupService priceChangeRuleGroupService;

    @Autowired
    private PriceChangeRuleService priceChangeRuleService;

    @Autowired
    private IProductService productService;

    @Autowired
    private ISystemPropertyService iSystemPropertyService;

    @Autowired
    private PriceTaskController priceTaskController;

    @RequestMapping(value = "/del/{price_change_rule_id}", method = RequestMethod.POST)
    @ResponseBody
    public Response delActive(@PathVariable(name = "price_change_rule_id") Long price_change_rule_id) throws Exception {
        logger.info("Start del discounts,price_change_rule_id:" + price_change_rule_id);
        PriceChangeRule pcrModel = priceChangeRule.selectByPrimaryKey(price_change_rule_id);

        PriceChangeRuleEnum.PriceType priceType = null;
        if (pcrModel.getPriceType().intValue() == PriceChangeRuleEnum.PriceType.IM_PRICE.getCode().intValue()) {
            priceType = PriceChangeRuleEnum.PriceType.IM_PRICE;
        } else {
            priceType = PriceChangeRuleEnum.PriceType.SUPPLY_PRICE;
        }
        priceChangeRule.deleteByPrimaryKey(pcrModel.getPriceChangeRuleId());

        Map<String, Object> params = new HashMap<>();
        params.put("category_type", pcrModel.getCategoryType());
        params.put("price_type", pcrModel.getPriceType());
        priceChangeRule.updateDefaultPrice(priceType, params);
        priceTaskController.syncAllRedundantTable();
        return Response.success();
    }

    @RequestMapping(value = "/run/im",method = RequestMethod.GET)
    @ResponseBody
    public Response runIm(@Param("price_change_rule_id") String price_change_rule_id, @Param("flag") String flag) throws Exception {
        logger.info("Start running IM discounts,price_change_rule_id:" + price_change_rule_id);
        PriceChangeRule pcrModel = priceChangeRule.selectByPrimaryKey(Long.parseLong(price_change_rule_id));

        if(pcrModel.getStatus().intValue() != 1) {
            logger.info("Start running IM discounts Error,price_change_rule_id:" + price_change_rule_id);
            return Response.status(StatusCode.FAILURE).data("不允许执行非Pending的IM折扣");
        }

        logger.info("Begin to refresh the discounts of IM goods,pcrModel:" + JSONObject.toJSONString(pcrModel));
        if (StringUtils.isNotBlank(flag) && flag.equalsIgnoreCase("all")) {
            pcrModel.setPriceChangeRuleId(null);
        }
        priceChangeRule.updateAdminPrice(pcrModel.getVendorId(), pcrModel.getCategoryType().intValue(), pcrModel.getPriceChangeRuleId());
        logger.info("End the discounts of IM goods:" + JSONObject.toJSONString(pcrModel));

        /*// sku.im_price -> shop_product_sku.sale_price
        priceChangeRule.updateShopPrice();

        // shop_product_sku.sale_price -> shop_product.min_sale_price,shop_product.max_sale_price
        priceChangeRule.updateShopProductSalePrice();

        // sku.price -> product.min_retail_price,product.max_retail_price
        priceChangeRule.updateProductRetailPrice();

        // sku.in_price -> product.min_boutique_price,product.max_boutique_price
        priceChangeRule.updateProductBoutiquePrice();

        // sku.im_price -> product.min_im_price,product.max_im_price
        priceChangeRule.updateProductImPrice();*/
        priceTaskController.syncAllRedundantTable();

        logger.info("End running IM discounts,price_change_rule_id:" + price_change_rule_id);
        return Response.success();
    }

    @RequestMapping(value = "/run/boutique",method = RequestMethod.GET)
    @ResponseBody
    public Response runBoutique(@Param("price_change_rule_id") String price_change_rule_id, @Param("flag") String flag) throws Exception {

        logger.info("Start running BOUTIQUE discounts,price_change_rule_id:" + price_change_rule_id);
        PriceChangeRule pcrModel = priceChangeRule.selectByPrimaryKey(Long.parseLong(price_change_rule_id));

        if(pcrModel.getStatus().intValue() != 1) {
            logger.info("Start running IM discounts Error,price_change_rule_id:" + price_change_rule_id);
            return Response.status(StatusCode.FAILURE).data("不允许执行非Pending的Boutique折扣");
        }

        logger.info("Begin to refresh the discounts of BOUTIQUE goods,pcrModel:" + JSONObject.toJSONString(pcrModel));
        if (StringUtils.isNotBlank(flag) && flag.equalsIgnoreCase("all")) {
            pcrModel.setPriceChangeRuleId(null);
        }
        priceChangeRule.updateVendorPrice(pcrModel.getVendorId(), pcrModel.getCategoryType(), pcrModel.getPriceChangeRuleId());
        logger.info("End the discounts of BOUTIQUE goods:" + JSONObject.toJSONString(pcrModel));

        /*// sku.im_price -> shop_product_sku.sale_price
        priceChangeRule.updateShopPrice();

        // shop_product_sku.sale_price -> shop_product.min_sale_price,shop_product.max_sale_price
        priceChangeRule.updateShopProductSalePrice();

        // sku.price -> product.min_retail_price,product.max_retail_price
        priceChangeRule.updateProductRetailPrice();

        // sku.in_price -> product.min_boutique_price,product.max_boutique_price
        priceChangeRule.updateProductBoutiquePrice();

        // sku.im_price -> product.min_im_price,product.max_im_price
        priceChangeRule.updateProductImPrice();*/
        priceTaskController.syncAllRedundantTable();
        logger.info("End running BOUTIQUE discounts,price_change_rule_id:" + price_change_rule_id);

        return Response.success();
    }

    @RequestMapping("select")
    @ResponseBody
    public ResultMessage queryPriceChangeRuleById(@Param("price_change_rule_id") String price_change_rule_id) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if (StringUtils.isBlank(price_change_rule_id))
                return resultMessage.errorStatus().putMsg("info", "params is null !!!");
            PriceChangeRule pcrModel = priceChangeRule.selectByPrimaryKey(Long.parseLong(price_change_rule_id));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if (pcrModel != null && pcrModel.getValidFrom() != null) {
                pcrModel.setValidFromStr(simpleDateFormat.format(pcrModel.getValidFrom()));
            }

            resultMessage.successStatus().putMsg("info", "success !!!").setData(pcrModel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : " + e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/changepreview")
    @ResponseBody

    public ResultMessage changePreview(@Param("price_change_rule_id") Long price_change_rule_id, @Param("preview_status") Long preview_status) {
        logger.info("PriceChangeRuleController,changePreview,inputParams,price_change_rule_id:{},preview_status:{} ", price_change_rule_id, preview_status);

        ResultMessage resultMessage = ResultMessage.getInstance();

        try {
            PriceChangeRule pcrModel = priceChangeRule.selectByPrimaryKey(price_change_rule_id);
            priceChangeRule.updatePreviewPrice(pcrModel.getVendorId(), preview_status, pcrModel.getCategoryType().intValue());

            resultMessage.successStatus().putMsg("info", "success !!!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : " + e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }

        logger.info("PriceChangeRuleController,changePreview,inputParams,price_change_rule_id:{},preview_status:{}, resultMsg:{}.", price_change_rule_id,
                preview_status, JSONObject.toJSONString(resultMessage));

        return resultMessage;
    }

    @RequestMapping("selectDefaultDiscount")
    @ResponseBody
    public ResultMessage queryDefaultDiscount() {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            List<Map<String, Object>> systemMaps = iSystemPropertyService.selectSystemProperty();
            Map<String, Object> mapDiscounts = new HashMap<>();
            for (Map<String, Object> map : systemMaps) {

                String bd = "";
                String id = "";
                if (map.get("system_property_name").toString().equals(SystemPropertyEnum.propertyName.BOUTIQUE_DISCOUNT_DEFAULT.getCode())) {
                    bd = map.get("system_property_value").toString();
                    mapDiscounts.put("bDiscount", bd);
                }

                if (map.get("system_property_name").toString().equals(SystemPropertyEnum.propertyName.IM_DISCOUNT_DEFAULT.getCode())) {
                    id = map.get("system_property_value").toString();
                    mapDiscounts.put("iDiscount", id);
                }
            }
            resultMessage.successStatus().putMsg("info", "success !!!").setData(mapDiscounts);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : " + e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/initPriceChangeRule", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> initPriceChangeRule(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info("initPriceChangeRule param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验登录
        Map<String, Object> userMap = new HashMap<String, Object>();
        User user = null;
        try {
            userMap = super.baseController(httpRequest);
            result.put("userStatus", userMap.get("userStatus"));

            if (Integer.parseInt(userMap.get("status").toString()) == StatusType.SUCCESS) {
                user = (User) userMap.get("user");
                if (user == null) {
                    result.put("info", "User not logged in");
                    return result;
                }
                map.put("userId", user.getUserId());
            } else {
                result.put("info", "User not logged in");
                return result;
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            result.put("info", "User not logged in");
            return result;
        }

        //校验参数
        if (!initCheckParams(map)) {
            result.put("info", "parameter is incorrect");
            return result;
        }
        String checkSeasonExists = priceChangeRule.checkSeasonExists(map, map.get("name").toString());
        if (!checkSeasonExists.equalsIgnoreCase("SUCCESS")) {
            result.put("info", checkSeasonExists);
            return result;
        }

        //调用service 创建 事物管理
        try {
            result = priceChangeRuleService.initPriceChangeRule(map);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "create priceChangeRule fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 修改PriceChangeRuleCategoryBrand
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/updatePriceChangeRuleCategoryBrand", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> priceChangeRuleCategoryBrandUpdate(@RequestBody Map<String, Object> map) {
        logger.info("priceChangeRuleCategoryBrandUpdate param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        if (map.get("price_change_rule_category_brand") == null || StringUtils.isBlank(map.get("price_change_rule_category_brand").toString())) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        try {

            JsonObject priceChangeRuleCategory = new JsonParser().parse(map.get("price_change_rule_category_brand").toString()).getAsJsonObject();

            PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand = new PriceChangeRuleCategoryBrand();
            priceChangeRuleCategoryBrand.setPriceChangeRuleId(priceChangeRuleCategory.get("price_change_rule_id").getAsLong());
            priceChangeRuleCategoryBrand.setCategoryId(priceChangeRuleCategory.get("category_id").getAsLong());
            priceChangeRuleCategoryBrand.setLevel(priceChangeRuleCategory.get("level").getAsByte());
            priceChangeRuleCategoryBrand.setBrandId(priceChangeRuleCategory.get("brand_id").getAsLong());
            priceChangeRuleCategoryBrand.setExceptionFlag(0);
            priceChangeRuleCategoryBrand.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleCategory.get("discount_percentage").getAsLong());

            /** start checked 数据 */
            Map<String, Object> paramMaps = new HashMap<>();
            paramMaps.put("catgeory_id", priceChangeRuleCategory.get("category_id").getAsLong());
            paramMaps.put("level", priceChangeRuleCategory.get("level").getAsByte());
            paramMaps.put("brand_id", priceChangeRuleCategory.get("brand_id").getAsLong());
            paramMaps.put("exceptionFlag", 0);
            paramMaps.put("price_change_rule_id", priceChangeRuleCategory.get("price_change_rule_id").toString());
            List<Map<String, Object>> brandCategoryMaps = priceChangeRuleCategoryBrandService.selectPriceChangeRuleCategoryBrandExists(paramMaps);
            /** end checked 数据 */

            //如果不存在则创建(主要针对老数据可能会有不存在的可能)
            if (brandCategoryMaps == null || brandCategoryMaps.size() == 0) {
                logger.info("Record does not exist ! start createPriceChangeRuleCategoryBrand ");
                priceChangeRuleCategoryBrandService.createPriceChangeRuleCategoryBrand(priceChangeRuleCategoryBrand);

                if (priceChangeRuleCategoryBrand.getPriceChangeRuleCategoryBrandId() == null) {
                    result.put("info", "parameter is incorrect");
                    return result;
                }

                //修改
            } else {
                int row = priceChangeRuleCategoryBrandService.updateDiscountPercentageBySelective(priceChangeRuleCategoryBrand);

                if (row == 0) {
                    //		            result.put("status", StatusType.PARAMETEREXIST);
                    result.put("info", "parameter is incorrect");
                    return result;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "update priceChangeRuleCategoryBrand fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 根据品牌ID添加下面所有2级类目的PriceChangeRuleCategoryBrand
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/createPriceChangeRuleCategoryBrandBatch", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> priceChangeRuleCategoryBrandBatchCreate(@RequestBody Map<String, Object> map) {
        logger.info("createPriceChangeRuleCategoryBrand param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (map.get("price_change_rule_id") == null || StringUtils.isBlank(map.get("price_change_rule_id").toString()) || map.get(
                "price_change_rule_category_brand_list") == null || StringUtils.isBlank(map.get("price_change_rule_category_brand_list").toString())) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        try {
            //创建 PriceChangeRuleCategoryBrand
            result = priceChangeRuleService.createPriceChangeRuleCategoryBrandBatch(map);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "create PriceChangeRuleCategoryBrand fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 根据品牌ID删除下面所有2级类目的PriceChangeRuleCategoryBrand
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/deletePriceChangeRuleCategoryBrandBatch", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> priceChangeRuleCategoryBrandBatchDelete(@RequestBody Map<String, Object> map) {
        logger.info("deletePriceChangeRuleCategoryBrand param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (map.get("price_change_rule_id") == null || StringUtils.isBlank(map.get("price_change_rule_id").toString()) || map.get(
                "price_change_rule_category_brand_list") == null || StringUtils.isBlank(map.get("price_change_rule_category_brand_list").toString())) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        try {
            //删除 PriceChangeRuleCategoryBrand
            result = priceChangeRuleService.deletePriceChangeRuleCategoryBrandBatch(map);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "delete PriceChangeRuleCategoryBrand fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    //	@RequestMapping("/createPriceChangeRule")
    //	@ResponseBody
    //	public Map<String, Object> priceChangeRuleCreate(@RequestBody Map<String, Object> map){
    //		logger.info("priceChangeRuleCreate param:"+new Gson().toJson(map));
    //		Map<String, Object> result = new HashMap<String, Object>();
    //		result.put("status", StatusType.FAILURE);
    //
    //		if(!checkParams(map)){
    //			result.put("info","parameter is incorrect");
    //			return result;
    //		}
    //
    //		//调用service 创建 事物管理
    //		try {
    //			result = priceChangeRuleService.createPriceChangeRule(map);
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //			result.put("info","create priceChangeRule fail ");
    //			return result;
    //		}
    //
    //
    //        result.put("status", StatusType.SUCCESS);
    //		return result;
    //	}
    //
    //
    //
    //	@RequestMapping("/updatePriceChangeRule")
    //	@ResponseBody
    //	public Map<String, Object> priceChangeRuleUpdate(@RequestBody Map<String, Object> map){
    //		logger.info("priceChangeRuleCreate param:"+new Gson().toJson(map));
    //		Map<String, Object> result = new HashMap<String, Object>();
    //		result.put("status", StatusType.FAILURE);
    //
    //		if(!checkUpdateParams(map)){
    //			result.put("info","parameter is incorrect");
    //			return result;
    //		}
    //
    //		//调用service 创建 事物管理
    //		try {
    //			result = priceChangeRuleService.updatePriceChangeRule(map);
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //			result.put("info","update priceChangeRule fail ");
    //			return result;
    //		}
    //
    //
    //        result.put("status", StatusType.SUCCESS);
    //		return result;
    //	}

    /**
     * 单个添加PriceChangeRuleCategoryBrand
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/createPriceChangeRuleCategoryBrand", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> priceChangeRuleCategoryBrandCreate(@RequestBody Map<String, Object> map) {
        logger.info("priceChangeRuleCategoryBrandCreate param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        if (!checkCreatePriceChangeRuleCategoryBrandParams(map)) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        try {

            JsonObject priceChangeRuleCategory = new JsonParser().parse(map.get("price_change_rule_category_brand").toString()).getAsJsonObject();

            /** start checked 重复数据 */
            Map<String, Object> paramMaps = new HashMap<>();
            paramMaps.put("catgeory_id", priceChangeRuleCategory.get("category_id").getAsLong());
            paramMaps.put("level", priceChangeRuleCategory.get("level").getAsByte());
            paramMaps.put("brand_id", priceChangeRuleCategory.get("brand_id").getAsLong());
            paramMaps.put("exceptionFlag", 1);
            paramMaps.put("price_change_rule_id", priceChangeRuleCategory.get("price_change_rule_id").toString());
            List<Map<String, Object>> brandCategoryMaps = priceChangeRuleCategoryBrandService.selectPriceChangeRuleCategoryBrandExists(paramMaps);
            if (brandCategoryMaps != null && brandCategoryMaps.size() > 0) {
                result.put("info", "Data already exists !!!");
                return result;
            }
            /** end checked 重复数据 */

            PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand = new PriceChangeRuleCategoryBrand();
            priceChangeRuleCategoryBrand.setPriceChangeRuleId(priceChangeRuleCategory.get("price_change_rule_id").getAsLong());
            priceChangeRuleCategoryBrand.setCategoryId(priceChangeRuleCategory.get("category_id").getAsLong());
            priceChangeRuleCategoryBrand.setLevel(priceChangeRuleCategory.get("level").getAsByte());
            priceChangeRuleCategoryBrand.setBrandId(priceChangeRuleCategory.get("brand_id").getAsLong());
            priceChangeRuleCategoryBrand.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleCategory.get("discount_percentage").getAsLong());
            priceChangeRuleCategoryBrand.setExceptionFlag(1);

            priceChangeRuleCategoryBrandService.createPriceChangeRuleCategoryBrand(priceChangeRuleCategoryBrand);

            if (priceChangeRuleCategoryBrand.getPriceChangeRuleCategoryBrandId() == null) {
                result.put("info", "parameter is incorrect");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "create priceChangeRuleCategoryBrand fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 根据price_change_rule_category_brand_id 删除PriceChangeRuleCategoryBrand
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/deletePriceChangeRuleCategoryBrand", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deletePriceChangeRuleCategoryBrand(@RequestBody Map<String, Object> map) {
        logger.info("deletePriceChangeRuleCategoryBrand param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (map.get("price_change_rule_category_brand_id") == null || StringUtils.isBlank(map.get("price_change_rule_category_brand_id").toString())) {
            result.put("info", "Parameter cannot be null");
            return result;
        }

        try {
            //根据ID删除
            String priceChangeRuleCategoryBrandId = map.get("price_change_rule_category_brand_id").toString();
            int row = priceChangeRuleCategoryBrandService.deletePriceChangeRuleCategoryBrand(Long.parseLong(priceChangeRuleCategoryBrandId));

            //判断是否成功
            if (row > 0) {
                result.put("status", StatusType.SUCCESS);
            } else {
                result.put("info", "delete priceChangeRuleCategoryBrand fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "delete priceChangeRuleCategoryBrand fail ");
            return result;
        }

        return result;
    }

    /**
     * 添加priceChangeRuleProductGroup
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/createPriceChangeRuleProductGroup", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> priceChangeRuleProductGroupCreate(@RequestBody Map<String, Object> map) {
        logger.info("priceChangeRuleProductGroupCreate param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        if (!checkCreatePriceChangeRuleProductGroupParams(map)) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        try {
            //获取对象值
            JsonObject priceChangeRuleGroupjson = new JsonParser().parse(map.get("price_change_rule_product_group").toString()).getAsJsonObject();

            //判断是否已经存在规则
            PriceChangeRuleGroup priceChangeRuleGroupParam = new PriceChangeRuleGroup();
            priceChangeRuleGroupParam.setPriceChangeRuleId(priceChangeRuleGroupjson.get("price_change_rule_id").getAsLong());
            priceChangeRuleGroupParam.setProductGroupId(priceChangeRuleGroupjson.get("product_group_id").getAsLong());
            List<PriceChangeRuleGroup> list = priceChangeRuleGroupService.selectByParameter(priceChangeRuleGroupParam);
            if (list != null && list.size() > 0) {
                result.put("info", "Records already exist");
                return result;
            }

            PriceChangeRuleGroup priceChangeRuleGroup = new PriceChangeRuleGroup();
            priceChangeRuleGroup.setPriceChangeRuleId(priceChangeRuleGroupjson.get("price_change_rule_id").getAsLong());
            priceChangeRuleGroup.setProductGroupId(priceChangeRuleGroupjson.get("product_group_id").getAsLong());
            priceChangeRuleGroup.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleGroupjson.get("discount_percentage").getAsLong());

            //创建
            priceChangeRuleGroupService.insertSelective(priceChangeRuleGroup);

            if (priceChangeRuleGroup.getPriceChangeRuleGroupId() == null) {
                result.put("info", "parameter is incorrect");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "create priceChangeRuleGroup fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 根据price_change_rule_group_id 删除PriceChangeRuleGroup
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/deletePriceChangeRuleGroup", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deletePriceChangeRuleGroup(@RequestBody Map<String, Object> map) {
        logger.info("deletePriceChangeRuleGroup param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (map.get("price_change_rule_group_id") == null || StringUtils.isBlank(map.get("price_change_rule_group_id").toString())) {
            result.put("info", "Parameter cannot be null");
            return result;
        }

        try {
            //根据ID删除
            String priceChangeRuleGroupId = map.get("price_change_rule_group_id").toString();
            int row = priceChangeRuleGroupService.deleteByPrimaryKey(Long.parseLong(priceChangeRuleGroupId));

            //判断是否成功
            if (row > 0) {
                result.put("status", StatusType.SUCCESS);
            } else {
                result.put("info", "delete priceChangeRuleGroup fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "delete priceChangeRuleGroup fail ");
            return result;
        }

        return result;
    }

    /**
     * 添加PriceChangeRuleProduct
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/createPriceChangeRuleProduct", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> priceChangeRuleProductCreate(@RequestBody Map<String, Object> map) {
        logger.info("createPriceChangeRuleProduct param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (!checkCreatePriceChangeRuleProductParams(map)) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        try {
            String productCode = map.get("product_code").toString();
            String price_change_rule_id = map.get("price_change_rule_id").toString();

            PriceChangeRule pcr = priceChangeRule.selectByPrimaryKey(Long.parseLong(price_change_rule_id));

            //查询是否存在该商品
            ProductWithBLOBs nproductWithBLOBs = null;
            if (StringUtils.isNotBlank(productCode)) {
                ProductWithBLOBs productWithBLOBs = new ProductWithBLOBs();
                productWithBLOBs.setProductCode(productCode);
                productWithBLOBs.setEnabled(EnabledType.USED);
                productWithBLOBs.setVendorId(pcr.getVendorId());
                nproductWithBLOBs = productService.selectByParameter(productWithBLOBs);
            }

            if (nproductWithBLOBs == null) {
                result.put("info", "Can't find the goods");
                return result;

            }

            //判断是否已经存在规则
            PriceChangeRuleProduct priceChangeRuleProductParam = new PriceChangeRuleProduct();
            priceChangeRuleProductParam.setPriceChangeRuleId(Long.valueOf(price_change_rule_id));
            priceChangeRuleProductParam.setBoutiqueId(productCode);
            List<PriceChangeRuleProduct> list = priceChangeRuleProductService.selectByParameter(priceChangeRuleProductParam);
            if (list != null && list.size() > 0) {
                result.put("info", "Records already exist");
                return result;
            }

            //添加 priceChangeRuleProduct
            PriceChangeRuleProduct priceChangeRuleProduct = new PriceChangeRuleProduct();
            priceChangeRuleProduct.setPriceChangeRuleId(Long.valueOf(price_change_rule_id));
            priceChangeRuleProduct.setProductId(nproductWithBLOBs.getProductId());
            priceChangeRuleProduct.setBoutiqueId(nproductWithBLOBs.getProductCode());
            priceChangeRuleProduct.setProductName(nproductWithBLOBs.getName());
            priceChangeRuleProduct.setDiscountPercentage(Long.valueOf("100") - Long.valueOf(map.get("discount_percentage").toString()));
            priceChangeRuleProductService.insertSelective(priceChangeRuleProduct);

            if (priceChangeRuleProduct.getPriceChangeRuleProductId() == null) {
                result.put("info", "parameter is incorrect");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "create PriceChangeRuleProduct fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 根据price_change_rule_product_id 删除PriceChangeRuleProduct
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/deletePriceChangeRuleProduct", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deletePriceChangeRuleProduct(@RequestBody Map<String, Object> map) {
        logger.info("deletePriceChangeRuleGroup param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (map.get("price_change_rule_product_id") == null || StringUtils.isBlank(map.get("price_change_rule_product_id").toString())) {
            result.put("info", "Parameter cannot be null");
            return result;
        }

        try {
            //根据ID删除
            String priceChangeRuleProductId = map.get("price_change_rule_product_id").toString();
            int row = priceChangeRuleProductService.deleteByPrimaryKey(Long.parseLong(priceChangeRuleProductId));

            //判断是否成功
            if (row > 0) {
                result.put("status", StatusType.SUCCESS);
            } else {
                result.put("info", "delete priceChangeRuleProduct fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "delete priceChangeRuleProduct fail ");
            return result;
        }

        return result;
    }

    /**
     * 修改 SystemProperty 全局默认折扣
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/updateDefaultDiscount", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> systemPropertyByNameUpdate(@RequestBody Map<String, Object> map) {
        logger.info("updateSystemProperty param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (map.get("boutique_discount_default") == null || StringUtils.isBlank(map.get("boutique_discount_default").toString()) || map.get(
                "im_discount_default") == null || StringUtils.isBlank(map.get("im_discount_default").toString())) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        try {
            //修改 SystemProperty 全局默认折扣
            result = priceChangeRuleService.updateSystemPropertyByName(map);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "update SystemProperty fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 修改PriceChangeRul 修改有效日期
     *
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updatePriceChangeRule", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updatePriceChangeRule(@RequestBody Map<String, Object> map) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        try {
            result = priceChangeRuleService.updatePriceChangeRuleDate(map);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "update PriceChangeRule fail ");
            return result;
        }

        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 根据price_change_rule_id 删除PriceChangeRule
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/deletePriceChangeRule", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deletePriceChangeRule(@RequestBody Map<String, Object> map) {
        logger.info("deletePriceChangeRule param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        //校验
        if (map.get("price_change_rule_id") == null || StringUtils.isBlank(map.get("price_change_rule_id").toString())) {
            result.put("info", "Parameter cannot be null");
            return result;
        }

        try {

            //调用service 创建 事物管理
            try {
                //根据ID删除
                result = priceChangeRuleService.deletePriceChangeRule(map);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("info", "delete priceChangeRule fail ");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "delete priceChangeRule fail ");
            return result;
        }

        return result;
    }

    /**
     * 检查参数
     */
    public static boolean initCheckParams(Map<String, Object> params) {

        if (params.get("name") == null || StringUtils.isBlank(params.get("name").toString())) {
            return false;
        }

        if (params.get("vendorId") == null || StringUtils.isBlank(params.get("vendorId").toString())) {
            return false;
        }

        if (params.get("price_type") == null || StringUtils.isBlank(params.get("price_type").toString()) || checkIntegerNumber(
                params.get("price_type").toString())) {
            return false;
        }

        if (params.get("status") == null || StringUtils.isBlank(params.get("status").toString()) || checkIntegerNumber(params.get("status").toString())) {
            return false;
        }

        if (params.get("price_change_rule_season_group_List") == null || StringUtils.isBlank(params.get("price_change_rule_season_group_List").toString())) {
            return false;
        }

        if (params.get("categoryType") == null || StringUtils.isBlank(params.get("categoryType").toString())) {
            return false;
        }

        return true;
    }

    /**
     * 检查参数
     */
    public static boolean checkParams(Map<String, Object> params) {

        if (params.get("name") == null || StringUtils.isBlank(params.get("name").toString())) {
            return false;
        }

        if (params.get("price_type") == null || StringUtils.isBlank(params.get("price_type").toString()) || checkIntegerNumber(
                params.get("price_type").toString())) {
            return false;
        }

        if (params.get("valid_from") == null || StringUtils.isBlank(params.get("valid_from").toString())) {
            return false;
        }

        if (params.get("status") == null || StringUtils.isBlank(params.get("status").toString()) || checkIntegerNumber(params.get("status").toString())) {
            return false;
        }

        if (params.get("price_change_rule_category_brand_list") == null || StringUtils.isBlank(
                params.get("price_change_rule_category_brand_list").toString())) {
            return false;
        }

        if (params.get("price_change_rule_all_brand_list") == null || StringUtils.isBlank(params.get("price_change_rule_all_brand_list").toString())) {
            return false;
        }

        if (params.get("price_change_rule_product_list") == null || StringUtils.isBlank(params.get("price_change_rule_product_list").toString())) {
            return false;
        }

        if (params.get("price_change_rule_group_list") == null || StringUtils.isBlank(params.get("price_change_rule_group_list").toString())) {
            return false;
        }

        return true;
    }

    /**
     * 检查参数
     */
    public static boolean checkCreatePriceChangeRuleProductParams(Map<String, Object> params) {

        if (params.get("product_code") == null || StringUtils.isBlank(params.get("product_code").toString())) {
            return false;
        }

        if (params.get("price_change_rule_id") == null || StringUtils.isBlank(params.get("price_change_rule_id").toString()) || checkNumber(
                params.get("price_change_rule_id").toString())) {
            return false;
        }

        if (params.get("discount_percentage") == null || StringUtils.isBlank(params.get("discount_percentage").toString()) || checkNumber(
                params.get("discount_percentage").toString())) {
            return false;
        }

        return true;
    }

    /**
     * 检查参数
     */
    public static boolean checkCreatePriceChangeRuleCategoryBrandParams(Map<String, Object> map) {

        if (map.get("price_change_rule_category_brand") == null || StringUtils.isBlank(map.get("price_change_rule_category_brand").toString())) {
            return false;
        }

        JsonObject priceChangeRuleCategory = new JsonParser().parse(map.get("price_change_rule_category_brand").toString()).getAsJsonObject();

        if (priceChangeRuleCategory.get("price_change_rule_id").isJsonNull() || StringUtils.isBlank(
                priceChangeRuleCategory.get("price_change_rule_id").getAsString()) || checkNumber(
                priceChangeRuleCategory.get("price_change_rule_id").getAsString())) {
            return false;
        }

        if (priceChangeRuleCategory.get("category_id").isJsonNull() || StringUtils.isBlank(priceChangeRuleCategory.get("category_id").getAsString())
                || checkNumber(priceChangeRuleCategory.get("category_id").getAsString())) {
            return false;
        }

        if (priceChangeRuleCategory.get("level").isJsonNull() || StringUtils.isBlank(priceChangeRuleCategory.get("level").getAsString())) {
            return false;
        }

        if (priceChangeRuleCategory.get("brand_id").isJsonNull() || StringUtils.isBlank(priceChangeRuleCategory.get("brand_id").getAsString()) || checkNumber(
                priceChangeRuleCategory.get("brand_id").getAsString())) {
            return false;
        }

        if (priceChangeRuleCategory.get("discount_percentage").isJsonNull() || StringUtils.isBlank(
                priceChangeRuleCategory.get("discount_percentage").getAsString()) || checkNumber(
                priceChangeRuleCategory.get("discount_percentage").getAsString())) {
            return false;
        }
        return true;
    }

    /**
     * 检查参数
     */
    public static boolean checkCreatePriceChangeRuleProductGroupParams(Map<String, Object> map) {

        if (map.get("price_change_rule_product_group") == null || StringUtils.isBlank(map.get("price_change_rule_product_group").toString())) {
            return false;
        }

        //获取对象值
        JsonObject priceChangeRuleGroupjson = new JsonParser().parse(map.get("price_change_rule_product_group").toString()).getAsJsonObject();

        if (priceChangeRuleGroupjson.get("price_change_rule_id").isJsonNull() || StringUtils.isBlank(
                priceChangeRuleGroupjson.get("price_change_rule_id").getAsString()) || checkNumber(
                priceChangeRuleGroupjson.get("price_change_rule_id").getAsString())) {
            return false;
        }

        if (priceChangeRuleGroupjson.get("product_group_id").isJsonNull() || StringUtils.isBlank(priceChangeRuleGroupjson.get("product_group_id").getAsString())
                || checkNumber(priceChangeRuleGroupjson.get("product_group_id").getAsString())) {
            return false;
        }

        if (priceChangeRuleGroupjson.get("discount_percentage").isJsonNull() || StringUtils.isBlank(
                priceChangeRuleGroupjson.get("discount_percentage").getAsString()) || checkNumber(
                priceChangeRuleGroupjson.get("discount_percentage").getAsString())) {
            return false;
        }

        return true;
    }

    private static boolean checkIntegerNumber(String vue) {
        if (StringUtils.isNotBlank(vue)) {
            try {
                Integer.parseInt(vue);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkNumber(String vue) {
        if (StringUtils.isNotBlank(vue)) {
            try {
                Long number = Long.parseLong(vue);
                if (number == null || number < 0) {
                    return true;
                }
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

}
