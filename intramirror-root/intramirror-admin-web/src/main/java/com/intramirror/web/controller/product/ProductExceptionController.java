package com.intramirror.web.controller.product;

import com.intramirror.product.api.service.IProductExceptionService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.web.Exception.ErrorResponse;
import com.intramirror.web.Exception.ValidateException;
import com.intramirror.web.common.response.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Created on 2017/11/15.
 * @author Shang Jian
 */
@RestController
@RequestMapping("/product/exception")
public class ProductExceptionController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductExceptionController.class);

    @Autowired
    private ISkuStoreService iSkuStoreService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private IProductExceptionService productExceptionService;

    @PostMapping(value = "/saveProductException", consumes = "application/json")
    public Response saveProductException(@SessionAttribute(value = "sessionStorage", required = false) Long userId, @RequestBody Map<String, Object> body) {
        Long productId = body.get("productId") == null ? null : Long.parseLong(body.get("productId").toString());
        Long skuId = body.get("skuId") == null ? null : Long.parseLong(body.get("skuId").toString());

        if (null == productId || null == skuId) {
            throw new ValidateException(new ErrorResponse("productId, skuId 参数为空！"));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("product_id", productId);
        map.put("sku_id", skuId);
        LOGGER.info("查询参数: [{}]", map);
        List<Map<String, Object>> list = productExceptionService.selectByProductAndSkuId(map);
        if (list.size() >= 1) {
            throw new ValidateException(
                    new ErrorResponse("productId: [" + body.get("productId").toString() + "], skuId: [" + body.get("skuId").toString() + "] 记录已存在！"));
        } else {
            //新增
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                map.put("type_id", 1);
                map.put("note", "人工处理，库存不匹配，强制清零库存，Job不再同步此库存.");
                map.put("status", 1);
                map.put("created_at", sdf.format(new Date()));
                map.put("created_by_user_id", userId);

                productExceptionService.saveProductException(map);
                LOGGER.info("成功增加商品异常记录: [{}]", map);

                //库存清零
                String[] skuIds = new String[1];
                skuIds[0] = body.get("skuId").toString();
                List<Map<String, Object>> listSkuStore = iSkuStoreService.getSkuStoreBySkuId(skuIds);
                if (listSkuStore.size() <= 0) {
                    throw new ValidateException(new ErrorResponse("skuId: [" + body.get("skuId").toString() + "] 不存在！"));
                } else {
                    int nCount = iSkuStoreService.updateBySkuId(6, skuId);
                    LOGGER.info("skuId: [{}], 由 [{}] 成功清零库存: [{}]", skuId, userId, nCount);
                }
            } catch (Exception e) {
                throw new ValidateException(new ErrorResponse(
                        "productId: [" + body.get("productId").toString() + "], skuId: [" + body.get("skuId").toString() + "] 处理失败！ ErrorMsg: [" + e
                                .getMessage() + "]"));
            }
        }
        return Response.success();
    }

    @PutMapping(value = "/updateProductException", consumes = "application/json")
    public Response updateProductException(@SessionAttribute(value = "sessionStorage", required = false) Long userId, List<String> skuIdList) {
        if (skuIdList.size() <= 0) {
            throw new ValidateException(new ErrorResponse("入参为空不能操作！"));
        }

        String skuIds = "";
        for (int i = 0; i < skuIdList.size(); i++) {
            if (i == 0) {
                skuIds = skuIdList.get(0);
            } else {
                skuIds += "," + skuIdList.get(i);
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        map.put("skuIds", skuIds);
        map.put("status", 0);
        map.put("modified_at", sdf.format(new Date()));
        map.put("modified_by_user_id", userId);
        LOGGER.info("更新条件: [{}]", map);
        try {
            int nCount = productExceptionService.updateProductException(map);
            LOGGER.info("更新条件: [{}], 更新行数: [{}]", map, nCount);
        } catch (Exception e) {
            throw new ValidateException(new ErrorResponse("批量resolve商品异常失败！ ErrorMsg: [" + e.getMessage() + "]"));
        }

        return Response.success();
    }
}
