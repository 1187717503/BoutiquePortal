package pk.shoplus.service.mapping.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import pk.shoplus.model.ProductAtelierMapping;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.ExceptionUtils;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;


/**
 * @Author: DingYiFan
 * @Description: Atelier调用IM接口修改商品映射处理
 * @Date: Create in 13:31 2017/5/25
 * @Modified By:
 */
public class UpdateProductMapping implements IMapping{
    private static Logger logger = Logger.getLogger(UpdateProductMapping.class);

    private static IProductService productServie = new ProductServiceImpl();
    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();
    private static ProductAtelierMapping productAtelierMapping = new ProductAtelierMapping();

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData,String queueNameEnum) {
        logger.info("------------------------------------------start UpdateProductMapping.handleMappingAndExecuteCreate,mqData:" + mqData);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try {
            Map<String,Object> convertMap = productAtelierMapping.mapping(mqData);
            if(convertMap.get("status").toString().equals(StatusType.SUCCESS+"")) {
                ProductEDSManagement.ProductOptions productOptions = (ProductEDSManagement.ProductOptions)convertMap.get("productOptions");
                ProductEDSManagement.VendorOptions vendorOptions = (ProductEDSManagement.VendorOptions)convertMap.get("vendorOptions");

                logger.info("Atelier开始调用商品创建Service by atelier,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                Map<String,Object> serviceProductMap = productEDSManagement.createProduct(productOptions,vendorOptions);
                logger.info("Ateler结束调用商品创建Service by atelier,serviceProductMap:" + new Gson().toJson(serviceProductMap)+",productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));

                if(serviceProductMap != null && serviceProductMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {
                    logger.info("调用Atelier商品修改Service by atelier,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                    serviceProductMap = productServie.updateProduct(productOptions,vendorOptions);
                    logger.info("调用Atelier商品修改Service by atelier,serviceProductMap:" + JSON.toJSONString(serviceProductMap)+",productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                }

                serviceProductMap.put("product_code",productOptions.getCode());
                serviceProductMap.put("color_code",productOptions.getColorCode());
                serviceProductMap.put("brand_id",productOptions.getBrandCode());

                resultMap.put("product_code",productOptions.getCode());
                resultMap.put("color_code",productOptions.getColorCode());
                resultMap.put("brand_id",productOptions.getBrandCode());
                return serviceProductMap;
            } else {
                return convertMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status",StatusType.FAILURE);
            resultMap.put("key","exception");
            resultMap.put("value", ExceptionUtils.getExceptionDetail(e));
            resultMap.put("info", "UpdateProductMapping - " + Runtime_exception.getDesc() + "error message : " + e.getMessage());
            resultMap.put("error_enum", Runtime_exception);
            logger.error("ERROR:"+new Gson().toJson(resultMap));
        }
        logger.info("------------------------------------------end UpdateProductMapping.handleMappingAndExecuteCreate,resultMap:" + new Gson().toJson(resultMap));
        return resultMap;
    }
}
