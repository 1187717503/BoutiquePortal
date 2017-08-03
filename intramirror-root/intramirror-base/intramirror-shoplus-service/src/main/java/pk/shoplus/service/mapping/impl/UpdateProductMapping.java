package pk.shoplus.service.mapping.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import pk.shoplus.model.ProductAtelierMapping;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;


/**
 * @Author: DingYiFan
 * @Description: Atelier调用IM接口修改商品映射处理
 * @Date: Create in 13:31 2017/5/25
 * @Modified By:
 */
public class UpdateProductMapping implements IMapping{
    private static Logger logger = Logger.getLogger(UpdateProductMapping.class);

    private static IProductService productServie = new ProductServiceImpl();

    private static ProductAtelierMapping productAtelierMapping = new ProductAtelierMapping();

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData) {
        logger.info("------------------------------------------start UpdateProductMapping.handleMappingAndExecuteCreate,mqData:" + mqData);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try {
            Map<String,Object> convertMap = productAtelierMapping.mapping(mqData);
            if(convertMap.get("status").toString().equals(StatusType.SUCCESS+"")) {
                ProductEDSManagement.ProductOptions productOptions = (ProductEDSManagement.ProductOptions)convertMap.get("productOptions");
                ProductEDSManagement.VendorOptions vendorOptions = (ProductEDSManagement.VendorOptions)convertMap.get("vendorOptions");

                logger.info("开始调用商品修改Service by atelier,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                Map<String,Object> serviceProductMap = productServie.updateProduct(productOptions,vendorOptions);
                logger.info("结束调用商品修改Service by atelier,serviceProductMap:" + new Gson().toJson(serviceProductMap));

                // 返回结果
                String updateStockResult = serviceProductMap.get("status") == null ? "" : serviceProductMap.get("status").toString();
                resultMap.put("status",updateStockResult);
                resultMap.put("info", "serviceResultMap:" + new Gson().toJson(serviceProductMap));
            } else {
                return convertMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("info", "系统异常! errorMsg:" + e.getMessage());
            logger.error("ERROR:"+new Gson().toJson(resultMap));
        }
        logger.info("------------------------------------------end UpdateProductMapping.handleMappingAndExecuteCreate,resultMap:" + new Gson().toJson(resultMap));
        return resultMap;
    }
}
