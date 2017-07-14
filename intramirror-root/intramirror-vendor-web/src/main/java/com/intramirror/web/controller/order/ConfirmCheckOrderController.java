package com.intramirror.web.controller.order;

import com.google.gson.Gson;
import com.intramirror.common.Helper;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.api.service.SkuService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/order")
public class ConfirmCheckOrderController {

    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";

    //@Resource(name = "userServiceImpl")
    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductPropertyService productPropertyService;

    /**
     * Wang
     *
     * @param brandId
     * @param colorCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/confirmCheckOrder", method = RequestMethod.POST)
    @ResponseBody
    public String confirmCheckOrder(String barCode, String brandId, String colorCode, String token) throws Exception {
        // 返回数据初始化
        int status = StatusType.FAILURE;
        Map<String, Object> result = new HashMap<String, Object>();

        String jwt = token;
        if (StringUtils.isEmpty(jwt)) {
            throw new JwtException("header not found,token is null");
        }
        //解析Jwt内容
        Claims claims = this.parseBody(jwt);

        Date expireation = claims.getIssuedAt();
        //如果信息过期则提示重新登录。
        if (System.currentTimeMillis() > expireation.getTime()) {
            result.put("userStatus", "1000002");
            return new Gson().toJson(result);
        }
        //获取用户详情
        Long userId = Long.valueOf(claims.getSubject());

        //如果匿名访问则跳过
        if (!Helper.checkNotNull(userId)) {
            result.put("userStatus", "1000003");
            return new Gson().toJson(result);
        }
        Sku sku = null;
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        if (barCode != null) {
            sku = skuService.getSkuBySkuCode(barCode);
            result.put("sku", sku);
        } else {
            mapList = productPropertyService.getProductPropertyByBrandIDAndColorCode(brandId, colorCode);
            result.put("mapList", mapList);
        }
        if (sku != null || mapList != null) {
            status = StatusType.SUCCESS;
        }
        result.put("status", status);
        return new Gson().toJson(result);
    }


    public String getJwtBase64Key() {
        return Base64Codec.BASE64.encode(jwtSecret);
    }

    public JwtParser parseToken() {
        return Jwts.parser().setSigningKey(getJwtBase64Key());
    }

    public Claims parseBody(String jwt) throws JwtException {
        return parseToken().setSigningKey(getJwtBase64Key()).parseClaimsJws(jwt).getBody();
    }
}
