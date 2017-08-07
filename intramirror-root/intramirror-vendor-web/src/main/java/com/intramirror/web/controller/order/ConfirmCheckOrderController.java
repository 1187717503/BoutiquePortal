package com.intramirror.web.controller.order;

import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

@CrossOrigin
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
    public ResultMessage confirmCheckOrder(@RequestBody Map<String,Object> map, HttpServletRequest httpRequest) throws Exception {
        // 返回数据初始化
        int status = StatusType.FAILURE;
		ResultMessage result= new ResultMessage();
		result.errorStatus();

        String jwt = httpRequest.getHeader("token");
        if (StringUtils.isEmpty(jwt)) {
            throw new JwtException("header not found,token is null");
        }
        //解析Jwt内容
        Claims claims = this.parseBody(jwt);

        Date expireation = claims.getIssuedAt();
        //如果信息过期则提示重新登录。
        if (System.currentTimeMillis() > expireation.getTime()) {
        	result.addMsg("1000002");
            return result;
        }
        //获取用户详情
        Long userId = Long.valueOf(claims.getSubject());

        //如果匿名访问则跳过
        if (!Helper.checkNotNull(userId)) {
        	result.addMsg("1000003");
            return result;
        }
        
        String barCode = map.get("barCode").toString(); 
        String brandId = map.get("brandId").toString(); 
        String colorCode = map.get("colorCode").toString(); 
        Sku sku = null;
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        if (barCode != null) {
            sku = skuService.getSkuBySkuCode(barCode);
//            result.put("sku", sku);
        } else {
            mapList = productPropertyService.getProductPropertyByBrandIDAndColorCode(brandId, colorCode);
//            result.put("mapList", mapList);
        }
        if (sku != null || mapList != null) {
        	result.successStatus();
        }
        
        return result;
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
