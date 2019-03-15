package com.intramirror.order.core.impl;

import com.google.gson.Gson;
import com.intramirror.common.help.IPageService;
import com.intramirror.common.help.Page;
import com.intramirror.common.help.PageUtils;
import com.intramirror.order.api.model.*;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.vo.ReconciliationVO;
import com.intramirror.order.api.vo.ShippedParam;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.MemberPointsErrorLogMapper;
import com.intramirror.order.core.mapper.OrderMapper;

import com.intramirror.order.api.vo.PageListVO;
import com.intramirror.order.core.mapper.ProductPropertyMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl extends BaseDao implements IOrderService, IPageService {

    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private OrderMapper orderMapper;
    private ProductPropertyMapper productPropertyMapper;

    private MemberPointsErrorLogMapper memberPointsErrorLogMapper;



    public void init() {
        orderMapper = this.getSqlSession().getMapper(OrderMapper.class);
        productPropertyMapper = this.getSqlSession().getMapper(ProductPropertyMapper.class);
        memberPointsErrorLogMapper = this.getSqlSession().getMapper(MemberPointsErrorLogMapper.class);
    }

    public List<Map<String, Object>> getOrderList(int status) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", status);
        List<Map<String, Object>> result = orderMapper.getOrderList(param);
        logger.info("getOrderList result:{}", new Gson().toJson(result));
        return result;
    }


    public Map<String, Object> getOrderByOrderNumber(String number) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderNumber", number);
        Map<String, Object> result = orderMapper.getOrderListByOrderNumber(param);
        logger.info("getOrderListByOrderNumber result:{}", new Gson().toJson(result));
        return result;
    }


    public List<Map<String, Object>> getOrderPaymentByLogisProductId(
            Long logisticsProductId) {
        return orderMapper.getOrderPaymentByLogisProductId(logisticsProductId);
    }

    /**
     * 根据status统计各个状态的订单数量
     *
     * @param
     * @return Integer
     */
    public int getOrderByIsvalidCount(Map<String, Object> param) {
        Integer count = orderMapper.getOrderByIsvalidCount(param);
        return count==null?0:count;
    }

    /**
     * 根据订单号查询物流ID
     *
     * @param orderId
     * @return map
     */
    public Map<String, Object> getOrderPaymentInfoByOrderId(int orderId) {
        return orderMapper.getOrderPaymentInfoByOrderId(orderId);
    }

    @Override
    public List<Map<String, Object>> selectCreateThreeOrderInfo(Map<String, Object> params) {
        return orderMapper.selectCreateThreeOrderInfo(params);
    }

    /**
     * 根据订单号查询支付信息
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getPaymentInfoByOrderId(int orderId) {
        List<Map<String, Object>> orderMapList = orderMapper.getPaymentInfoByOrderId(orderId);
        if (orderMapList.size() > 0) {
            return orderMapList.get(0);
        }
        return null;
    }

    /**
     * 根据订单获取Shipment
     */
    public Shipment getOrderByShipment(int logisticsProductId) {
        return orderMapper.getOrderByShipment(logisticsProductId);
    }


    /**
     * 根据 订单状态获取子订单列表
     *
     * @param status
     * @return
     */
    @Override
    public List<Map<String, Object>> getOrderListByStatus(int status, Long vendorId, String sortByName) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("status", status);
        if(vendorId==null){
            vendorId=0l;
        }
        conditionMap.put("vendorIds", Arrays.asList(vendorId));
        if (sortByName != null && StringUtils.isNoneBlank(sortByName)) {
            conditionMap.put(sortByName, sortByName);
        }
        List<Map<String, Object>> mapList = orderMapper.getOrderListByStatus(conditionMap);
        //addProductPropertyMap(mapList);
        return mapList;
    }

    /**
     * 根据 订单状态获 和 container ID取子订单列表
     *
     * @param status
     * @return
     */
    @Override
    public List<Map<String, Object>> getOrderListByStatusAndContainerId(
            long containerId, int status, List<Long> vendorIds) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("status", status);
        conditionMap.put("vendorIds", vendorIds);
        conditionMap.put("containerId", containerId);
        List<Map<String, Object>> mapList = orderMapper.getOrderListByStatusAndContainerId(conditionMap);
        //addProductPropertyMap(mapList);
        return mapList;
    }

    @Override
    public Map<String, Object> getOrderInfoByCondition(
            Map<String, Object> map) {
        List<Map<String, Object>> list = orderMapper.getOrderInfoByCondition(map);
        if (list != null && list.size() > 0) {
            //addProductPropertyMap(list);
            return list.get(0);
        }
        return null;
    }

    @Override
    public PageUtils getShippedOrderListByStatus(Page page, List<Long> vendorIds, ShippedParam shippedParam) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("vendorIds", vendorIds);
        conditionMap.put("shippedParam", shippedParam);
        PageUtils pageUtils = new PageUtils(page, this, conditionMap);
        return pageUtils;
    }

    @Override
    public List<Map<String, Object>> getOrderListByShipmentId(
            Map<String, Object> conditionMap) {

        List<Map<String, Object>> mapList = orderMapper.getOrderListByShipmentId(conditionMap);
        //addProductPropertyMap(mapList);
        Set<Long> ids = new HashSet<>();
        for(Map<String, Object> vo :mapList){
            ids.add(Long.valueOf(vo.get("product_id").toString()));
        }

        if(ids.size() <= 0){
            return mapList;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("ids",ids);
        map.put("keyName","MadeIn");
        List<ProductPropertyVO> vos = productPropertyMapper.getProductProperty(map);
        if(vos!=null&&vos.size()>0){
            for(Map<String, Object> vo :mapList){
                for(ProductPropertyVO pp:vos){
                    if(Long.valueOf(vo.get("product_id").toString()).equals(pp.getProductId())){
                        vo.put("MadeIn",pp.getValue());
                        break;
                    }
                }
            }
        }
        Map<String,Object> map1 = new HashMap<>();
        map1.put("ids",ids);
        map1.put("keyName","Composition");
        List<ProductPropertyVO> vos1 = productPropertyMapper.getProductProperty(map1);
        if(vos1!=null&&vos1.size()>0){
            for(Map<String, Object> vo :mapList){
                for(ProductPropertyVO pp:vos1){
                    if(Long.valueOf(vo.get("product_id").toString()).equals(pp.getProductId())){
                        vo.put("Composition",pp.getValue());
                        break;
                    }
                }
            }
        }

        return mapList;
        /*for(Map<String, Object> vo :mapList){
            vo.put("spuComposition",productMapper.getCompositionByProductId(Long.valueOf(vo.get("product_id").toString())));
        }*/
    }

    @Override
    public Map<String, Object> getShipmentDetails(Map<String, Object> conditionMap) {
        List<Map<String, Object>> list = orderMapper.getShipmentDetails(conditionMap);
        if (list != null && list.size() > 0) {
            Map<String, Object> map = list.get(0);
            Object shipToCountry = map.get("consignee_country_id");
            if(shipToCountry!=null){
                Map<String, Object> addrCountry = orderMapper.selectAddrByCountry(Long.valueOf(shipToCountry.toString()));
                map.put("consignee_country_id",Integer.valueOf(addrCountry.get("address_country_id").toString()));
                map.put("countryCode",addrCountry.get("country_code").toString());
                return map;
            }
        }
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> getResult(Map<String, Object> params) {
        List<Map<String, Object>> mapList = orderMapper.getShippedOrderListByStatus(params);
       /* addProductPropertyMap(mapList);
        Object shippedParamObj = params.get("shippedParam");
        List<Map<String, Object>> list = new ArrayList<>();
        if(shippedParamObj != null){
            ShippedParam shippedParam = (ShippedParam)params.get("shippedParam");
            String brandID = shippedParam.getBrandID();
            String colorCode = shippedParam.getColorCode();
            if((brandID==null||"".equals(brandID.trim()))
                    &&(colorCode==null||"".equals(colorCode.trim()))){
                return mapList;
            }
            for (Map<String, Object> map : mapList){
                if (brandID !=null && (colorCode ==null||"".equals(colorCode.trim()))){
                    if(brandID.equals(map.get("brandID"))){
                        list.add(map);
                    }
                }
                if (brandID !=null && colorCode !=null){
                    if(brandID.equals(map.get("brandID"))
                            &&colorCode.equals(map.get("colorCode"))){
                        list.add(map);
                    }
                }

                if ((brandID ==null||"".equals(brandID.trim())) && colorCode !=null){
                    if(colorCode.equals(map.get("colorCode"))){
                        list.add(map);
                    }
                }

            }
        }else {
            return mapList;
        }*/
        return mapList;
    }

    @Override
    public Integer getShippedCount(Map<String, Object> map) {
        return orderMapper.getShippedCount(map);
    }

    @Override
    public Order createOrder(Order order) {
        orderMapper.createOrder(order);
        // 创建订单编号
        String orderNum = generateOrderNum(order.getUserId(), order.getOrderId());
        order.setOrderNum(orderNum);
        orderMapper.updateById(order);
        return order;
}

    @Override
    public void updateOrder(Order order) {
        orderMapper.updateById(order);
    }

    @Override
    public List<Map<String, Object>> atelierSelectOrder(Map<String, Object> conditionMap) {
        return orderMapper.atelierSelectOrder(conditionMap);
    }

    @Override
    public List<Map<String, Object>> selectOrderInfo(Map<String, Object> conditionMap) {
        return orderMapper.selectOrderInfo(conditionMap);
    }

    // 订单号生成规则：
    // 日期（8位）＋ 用户ID（末3位）＋ 订单order ID（末5位）
    //
    // 例如：2014092100400023

    /**
     * 订单号生成规则：
     *
     * @param userId
     * @param orderId
     * @return
     */
    private String generateOrderNum(Long userId, Long orderId) {
        String userStr = "000" + userId;
        String orderStr = "00000" + orderId;
        userStr = userStr.substring(userStr.length() - 3, userStr.length());
        orderStr = orderStr.substring(orderStr.length() - 5, orderStr.length());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(new Date()) + userStr + orderStr;
    }

	@Override
	public Map<String, Object> getOrderLogisticsInfoByIdWithSql(
			Long logisticsProductId) {
		return orderMapper.getOrderLogisticsInfoByIdWithSql(logisticsProductId);
	}

    @Override
    public PageListVO getOrderCancelList(Map<String, Object> params) {
        Integer pageNumber = Integer.valueOf(params.get("pageNumber")==null?"1":params.get("pageNumber").toString());
        Integer pageSize = Integer.valueOf(params.get("pageSize")==null?"10":params.get("pageSize").toString());
        params.put("pageNo",(pageNumber-1) * pageSize);
        params.put("pageSize",pageSize);
        if(params.get("orderNumber")!=null)
        params.put("orderNumber","%"+params.get("orderNumber").toString()+"%");
        List<CancelOrderVO> orderCancelList = orderMapper.getOrderCancelList(params);
        if(orderCancelList != null && orderCancelList.size()>0){
            PageListVO listVO = new PageListVO();
            listVO.setTotal(getOrderCancelCount(params));
            //addProductProperty(orderCancelList);
            listVO.setData(orderCancelList);
            listVO.setCurrPageNo(pageNumber);
            listVO.setPageSize(pageSize);
            listVO.calPageTotal();
            return listVO;
        }else {
            return new PageListVO();
        }

    }


    @Override
    public void addProductPropertyMap(List<Map<String, Object>> orderList){
        if(orderList == null || orderList.size() <= 0){
            return;
        }
        Set<Long> ids = new HashSet<>();
        for(Map<String, Object> vo :orderList){
            ids.add(Long.valueOf(vo.get("product_id").toString()));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("ids",ids);
        map.put("keyName","ColorCode");
        List<ProductPropertyVO> vos = productPropertyMapper.getProductProperty(map);
        if(vos!=null&&vos.size()>0){
            for(Map<String, Object> vo :orderList){
                for(ProductPropertyVO pp:vos){
                    if(Long.valueOf(vo.get("product_id").toString()).equals(pp.getProductId())){
                        vo.put("colorCode",pp.getValue());
                        break;
                    }
                }
            }
        }
        Map<String,Object> map1 = new HashMap<>();
        map1.put("ids",ids);
        map1.put("keyName","BrandID");
        List<ProductPropertyVO> vos1 = productPropertyMapper.getProductProperty(map1);
        if(vos1!=null&&vos1.size()>0){
            for(Map<String, Object> vo :orderList){
                for(ProductPropertyVO pp:vos1){
                    if(Long.valueOf(vo.get("product_id").toString()).equals(pp.getProductId())){
                        vo.put("brandID",pp.getValue());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int getOrderCancelCount(Map<String, Object> params){
        params.put("count",1);
        List<CancelOrderVO> count = orderMapper.getOrderCancelList(params);
        if(count!=null&&count.size()>0){
            return count.size();
        }
        return 0;
    }

    @Override
    public void updateOrderByOrderLogisticsId(Long orderLogisticsId, int status) {
        Map<String,Object> params = new HashMap<>();
        params.put("orderLogisticsId",orderLogisticsId);
        params.put("status",status);
        orderMapper.updateOrderByOrderLogisticsId(params);
    }

    @Override
    public List<Map<String, Object>> getOrderListByParams(Map<String, Object> params) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        if (paramCheck(params.get("status"))) {
            conditionMap.put("status", params.get("status"));
        }
        if (paramCheck(params.get("vendorIds"))) {
            conditionMap.put("vendorIds", params.get("vendorIds"));
        }
        if (paramCheck(params.get("sortByName"))) {
            conditionMap.put(params.get("sortByName").toString(), params.get("sortByName"));
        }
        if (params.get("categoryIds") != null) {
            conditionMap.put("categoryIds", params.get("categoryIds"));
        }
        if (paramCheck(params.get("brandId"))) {
            conditionMap.put("brandId", params.get("brandId"));
        }
        if (paramCheck(params.get("locationId"))) {
            conditionMap.put("locationId", params.get("locationId"));
        }
        if (params.get("logisticsProductIds") != null) {
            conditionMap.put("logisticsProductIds", params.get("logisticsProductIds"));
        }
        if(paramCheck(params.get("shippingMethod"))){
            conditionMap.put("shippingMethod", params.get("shippingMethod"));
        }
        List<Map<String, Object>> mapList = orderMapper.getOrderListByStatus(conditionMap);
        //addProductPropertyMap(mapList);
        return mapList;
    }

    @Override
    public List<String> getStyleroomOrder(List<String> orderLineNums) {
        return orderMapper.getStyleroomOrder(orderLineNums);
    }

    @Override
    public Map<String, Object> getUserGrowthInfo(String orderLineNum) {
        Map<String, Object> map = orderMapper.getUserGrowthInfo(orderLineNum);
        if (map != null){
            Map<String,Object> userGrowthInfo = new HashMap<>();
            String orderTotalRmb = map.get("orderTotalRmd") != null ? map.get("orderTotalRmd").toString() : "1";
            String orderPaymentAmt = map.get("orderPaymentAmt") != null ? map.get("orderPaymentAmt").toString() : "0";
            String totalRmb = map.get("total_rmb") != null ? map.get("total_rmb").toString() : "0";
            userGrowthInfo.put("userId",map.get("user_id"));
            if (map.get("geography_id")!=null&&"1".equals(map.get("geography_id").toString())){
                userGrowthInfo.put("isMainland",1);
            }else {
                userGrowthInfo.put("isMainland",0);
            }
            userGrowthInfo.put("orderAmt",totalRmb);
            BigDecimal orderTotalRmbDec = new BigDecimal(orderTotalRmb);
            BigDecimal orderPaymentAmtRmbDec = new BigDecimal(orderPaymentAmt);
            BigDecimal totalRmbRmbDec = new BigDecimal(totalRmb);
            BigDecimal paymentAmt = orderPaymentAmtRmbDec.multiply(totalRmbRmbDec).divide(orderTotalRmbDec, 2,BigDecimal.ROUND_HALF_UP);
            userGrowthInfo.put("paymentAmt",paymentAmt.toString());
            userGrowthInfo.put("orderLineNum",orderLineNum);
            userGrowthInfo.put("orderNum",map.get("order_num"));
            return userGrowthInfo;
        }
        return null;
    }

    @Override
    public void insertMemberPointsErrorLog(MemberPointsErrorLog errorLog) {
        memberPointsErrorLogMapper.insertSelective(errorLog);
    }

    @Override
    public MemberPointsErrorLog getMemberPointsErrorLog(String oderLineNum) {
        MemberPointsErrorLogExample example = new MemberPointsErrorLogExample();
        MemberPointsErrorLogExample.Criteria criteria = example.createCriteria();
        criteria.andOrderLineNumEqualTo(oderLineNum);
        criteria.andIsDeletedEqualTo(0);
        example.setLimit(1);
        List<MemberPointsErrorLog> logs = memberPointsErrorLogMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(logs)){
            return logs.get(0);
        }
        return null;
    }

    @Override
    public void updateMemberPointsErrorLog(MemberPointsErrorLog errorLog) {
        memberPointsErrorLogMapper.updateByPrimaryKeySelective(errorLog);
    }

    @Transactional
    @Override
    public List<ReconciliationVO> reconciliationExport(ReconciliationVO inputVO) {
        List<ReconciliationVO> list = orderMapper.reconciliationExport(inputVO);
        List<ReconciliationVO> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)){
            List<String> orders = new ArrayList<>();
            for (ReconciliationVO reconciliationVO:list){
                if (reconciliationVO.getReconciliationStatementId() == null){
                    reconciliationVO.setIsDownload(0);  //未下载
                    reconciliationVO.setSettlementStatus(1);  //未结算
                    //记录下载对账数据
                    orders.add(reconciliationVO.getOrderLineNum());
                }else {
                    reconciliationVO.setIsDownload(1);
                }
                //计算原始boutique price
                if (reconciliationVO.getOriginalBoutiquePrice() == null
                        || reconciliationVO.getOriginalBoutiquePrice().intValue() == 0){
                    double discountOff = reconciliationVO.getDiscountOff();
                    double originalPrice = reconciliationVO.getOriginalPrice() == null ? 0 : reconciliationVO.getOriginalPrice().doubleValue();
                    reconciliationVO.setOriginalPrice(new BigDecimal(originalPrice));
                    BigDecimal decimal = new BigDecimal(originalPrice).multiply(new BigDecimal((100 - discountOff)/100)).setScale(4, BigDecimal.ROUND_HALF_UP);
                    reconciliationVO.setOriginalBoutiquePrice(decimal);
                }
                Integer status = reconciliationVO.getSettlementStatus() == null ? 1 : reconciliationVO.getSettlementStatus();
                Integer settlementStatus = inputVO.getSettlementStatus();
                if (settlementStatus != null ){
                    if (settlementStatus.equals(status)){
                        voList.add(reconciliationVO);
                    }
                }else {
                    voList.add(reconciliationVO);
                }
            }
            if (CollectionUtils.isNotEmpty(orders)){
                orderMapper.saveReconciliation(orders);
            }
        }
        return voList;
    }

    private boolean paramCheck(Object obj) {
        return obj != null && StringUtils.isNoneBlank(obj.toString());
    }
}