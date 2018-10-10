package com.intramirror.web.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.MemberPointsErrorLog;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.util.HttpClientUtil;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.web.service.LogisticsProductService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pk.shoplus.common.utils.StringUtil;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class ConfirmCheckOrderController {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmCheckOrderController.class);

    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";

    @Autowired
    private SkuService skuService;
    @Autowired
    private IProductService productService;

    @Autowired
    IOrderService orderService;
    @Autowired
    private LogisticsProductService logisticsProductService;
    @Autowired
    private ILogisticsProductService iLogisticsProductService;

    /**
     * Wang
     * @param map
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/confirmCheckOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage confirmCheckOrder(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) throws Exception {
        ResultMessage result = new ResultMessage();
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

        String barCode = null;
        String brandId = null;
        String colorCode = null;
        String estShipDate = null;
        String logisticsProductId = null;
        String stockLocation = null;
        Long stockLocationId = null;

        if (map.get("barCode") != null && StringUtils.isNotBlank(map.get("barCode").toString()) && !map.get("barCode").toString().equals("#")) {
            barCode = map.get("barCode").toString();
        }

        if (map.get("brandId") != null && StringUtils.isNotBlank(map.get("brandId").toString())) {
            brandId = map.get("brandId").toString();
        }

        if (map.get("colorCode") != null && StringUtils.isNotBlank(map.get("colorCode").toString())) {
            colorCode = map.get("colorCode").toString();
        }

        if (map.get("estShipDate") != null && StringUtils.isNotBlank(map.get("estShipDate").toString())) {
            estShipDate = map.get("estShipDate").toString();
        }

        if (map.get("logisticsProductId") != null && StringUtils.isNotBlank(map.get("logisticsProductId").toString())) {
            logisticsProductId = map.get("logisticsProductId").toString();
        }

        if (map.get("stockLocation") != null && StringUtils.isNotBlank(map.get("stockLocation").toString())) {
            stockLocation = map.get("stockLocation").toString();
        }
        if (map.get("stockLocationId") != null && StringUtils.isNotBlank(map.get("stockLocationId").toString())) {
            stockLocationId = Long.valueOf(map.get("stockLocationId").toString());
        }

        Sku sku = null;
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (barCode != null) {
            sku = skuService.getSkuBySkuCode(barCode);
            //            result.put("sku", sku);
        } else {
            mapList = productService.getProductByBrandIDAndColorCode(map);
            //            result.put("mapList", mapList);
        }

        if (sku != null || (mapList != null && mapList.size() > 0)) {
            result.successStatus();

            try{
                if (logisticsProductId != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    //LogisticsProduct logis = logisticsProductServiceImpl.selectById(Long.parseLong(logisticsProductId));
                    LogisticsProduct logis = logisticsProductService.selectById(Long.parseLong(logisticsProductId));
                    Map<String, Object> maps = orderService.getOrderLogisticsInfoByIdWithSql(Long.parseLong(logisticsProductId));
                    String mapBrandId = maps.get("brandID") == null ? "" : maps.get("brandID").toString();
                    String mapColorCode = maps.get("colorCode") == null ? "" : maps.get("colorCode").toString();

                    //如果是当前自己的brandId和colorCode保存
                    if (!mapBrandId.equals(brandId)){
                        result.errorStatus().setMsg("Designer-ID is incorrect");
                        return result;
                    }
                    if (!mapColorCode.equals(colorCode)){
                        result.errorStatus().setMsg("Color-Code is incorrect");
                        return result;
                    }
                    if (logis != null) {
                        LogisticsProduct upLogis = new LogisticsProduct();
                        upLogis.setLogistics_product_id(logis.getLogistics_product_id());
                        if (stockLocation != null) {
                            upLogis.setStock_location(stockLocation);
                        }
                        if (estShipDate != null) {
                            upLogis.setEst_ship_date(sdf.parse(estShipDate));
                        }
                        if (stockLocationId !=null ){
                            upLogis.setStock_location_id(stockLocationId);
                        }
                        upLogis.setConfirmed_at(Helper.getCurrentUTCTime());

                        //logisticsProductServiceImpl.updateByLogisticsProduct(upLogis);
                        upLogis.setOrder_line_num(logis.getOrder_line_num());
                        //确认订单
                        LogisticsProduct old = logisticsProductService.selectById(upLogis.getLogistics_product_id());
                        Integer oldStatus = old==null?null:old.getStatus();
                        logisticsProductService.confirmOrder(upLogis);

                        iLogisticsProductService.updateByLogisticsProduct4Jpush(oldStatus,upLogis);

                        //会员系统积分
                        logisticsProductService.updateMemberCredits(logis.getOrder_line_num());
                    } else {
                        result.errorStatus().setMsg("Order does not exist,logisticsProductId:" + logisticsProductId);
                    }
                }
            }catch (Exception e){
                result.errorStatus().setMsg(e.getMessage());
                return result;
            }
        }else {
            result.setMsg("Designer-ID or Color-Code is incorrect");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/memberPoints", method = RequestMethod.POST)
    public ResultMessage memberPoints(@RequestParam String orderLineNum){
        ResultMessage result = new ResultMessage();
        result.errorStatus();
        MemberPointsErrorLog log = orderService.getMemberPointsErrorLog(orderLineNum);
        if (log != null){
            result.successStatus();
            String res = HttpClientUtil.httpPost(log.getRequestBody(), HttpClientUtil.appMemberPointsUrl);
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (StringUtil.isEmpty(res)
                    ||jsonObject.getInteger("status") != 1){
                result.setMsg(res);
                return result;
            }else {
                log.setIsDeleted(1);
                orderService.updateMemberPointsErrorLog(log);
                result.successStatus();
                return result;
            }
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
