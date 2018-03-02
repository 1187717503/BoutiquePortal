package com.intramirror.web.controller.order;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.logistics.api.model.Invoice;
import com.intramirror.logistics.api.service.IInvoiceService;
import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.model.Geography;
import com.intramirror.main.api.model.Tax;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.GeographyService;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.model.ShippingProvider;
import com.intramirror.order.api.model.SubShipment;
import com.intramirror.order.api.service.*;
import com.intramirror.product.api.model.Shop;
import com.intramirror.product.api.service.IShopService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.common.BarcodeUtil;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.LogisticsProductService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/orderShip")
public class OrderShipController extends BaseController {

    private static Logger logger = Logger.getLogger(OrderShipController.class);

    @Autowired
    private IOrderService orderService;

    @Autowired
    private LogisticsProductService logisticsProductService;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ISkuStoreService skuStoreService;

    @Autowired
    private ILogisticsProductService iLogisticsProductService;

    @Autowired
    private IShipmentService iShipmentService;

    @Autowired
    private IContainerService containerService;

    @Autowired
    private GeographyService geographyService;

    @Autowired
    private IInvoiceService invoiceService;

    @Autowired
    private IShopService shopService;

    @Autowired
    private IShippingProviderService shippingProviderService;

    @Autowired
    private ISubShipmentService subShipmentService;

    @Autowired
    private TaxService taxService;

    @Autowired
    private AddressCountryService addressCountryService;

    /**
     * 获取所有箱子信息
     *
     * @param map
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/getReadyToShipCartonList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getPackOrderList(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info("order getReadyToShipCartonList 入参:" + new Gson().toJson(map));
        ResultMessage result = new ResultMessage();
        result.errorStatus();

//		if(map == null || map.size() == 0 || map.get("status") == null){
//			result.setMsg("Parameter cannot be empty");
//			return result;
//		}

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        Vendor vendor = null;
        try {
            vendor = vendorService.getVendorByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendor == null) {
            result.setMsg("Please log in again");
            return result;
        }


        try {
            Map<String, Object> paramtMap = new HashMap<String, Object>();
            paramtMap.put("vendorId", vendor.getVendorId());

            if (map.get("sortByName") != null && StringUtils.isNoneBlank(map.get("sortByName").toString())) {
                paramtMap.put("sortByName", map.get("sortByName").toString());
            }

            if (map.get("shipmentStatus") != null && StringUtils.isNoneBlank(map.get("shipmentStatus").toString())) {
                paramtMap.put("shipmentStatus", Integer.parseInt(map.get("shipmentStatus").toString()));
            }

            if (map.get("ship_to_geography") != null && StringUtils.isNoneBlank(map.get("ship_to_geography").toString())) {
                paramtMap.put("ship_to_geography", map.get("ship_to_geography").toString());
            }

            //获取箱子列表信息
            logger.info("order getReadyToShipCartonList 获取container 列表信息   调用接口containerService.getContainerList 入参:" + new Gson().toJson(paramtMap));
            List<Map<String, Object>> containerList = containerService.getContainerList(paramtMap);

            Map<String, List<Map<String, Object>>> shipMentCartonList = new HashMap<String, List<Map<String, Object>>>();
            List<Map<String, Object>> shipMentList = new ArrayList<Map<String, Object>>();

            if (containerList != null && containerList.size() > 0) {
                logger.info("order getReadyToShipCartonList 解析container 列表信息 ");
//				for(Map<String, Object> container : containerList){
//					
//					//根据shipment_id 分组
//					if(!shipMentCartonList.containsKey(container.get("shipment_id").toString())){
//						//获取shipMent信息
//						Map<String, Object> shipMent = new HashMap<String, Object>();
//						shipMent.put("shipment_id", container.get("shipment_id").toString());
//						shipMent.put("shipment_no", container.get("shipment_no").toString());
//						shipMent.put("shipment_type", container.get("shipment_type").toString());
//						shipMent.put("product_qty", container.get("product_qty").toString());
//						shipMent.put("ship_to_geography", container.get("ship_to_geography").toString());
////						shipMent.put("cartonList", );
//						shipMentList.add(shipMent);
//					}
//				}


                for (Map<String, Object> container : containerList) {

                    //根据shipment_id 分组
                    if (shipMentCartonList.containsKey(container.get("shipment_id").toString())) {
                        List<Map<String, Object>> cons = shipMentCartonList.get(container.get("shipment_id").toString());
                        cons.add(container);
                    } else {
                        List<Map<String, Object>> cons = new ArrayList<Map<String, Object>>();
                        cons.add(container);
                        shipMentCartonList.put(container.get("shipment_id").toString(), cons);

                        //获取shipMent信息
                        Map<String, Object> shipMent = new HashMap<String, Object>();
                        shipMent.put("shipment_id", container.get("shipment_id").toString());
                        shipMent.put("shipment_no", container.get("shipment_no").toString());
                        shipMent.put("shipment_status", container.get("shipment_status").toString());

                        shipMent.put("ship_to_geography", container.get("ship_to_geography").toString());
                        shipMentList.add(shipMent);
                    }
                }

                if (shipMentList != null && shipMentList.size() > 0) {
                    //将cartonList 存入shipMent详情
                    for (Map<String, Object> shipMent : shipMentList) {
                        shipMent.put("cartonList", shipMentCartonList.get(shipMent.get("shipment_id").toString()));
                        shipMent.put("carton_qty", shipMentCartonList.get(shipMent.get("shipment_id").toString()).size());
                    }
                }

                for (Map<String, Object> shipItem : shipMentList) {
                    List<Map<String, Object>> cartonList = (List<Map<String, Object>>)shipItem.get("cartonList");
                    int product_qty_total = 0;
                    for(Map<String,Object> cartonItem: cartonList){
                        if(cartonItem.get("product_qty") != null){
                            product_qty_total = product_qty_total + Integer.parseInt(cartonItem.get("product_qty").toString());
                        }
                    }
                    shipItem.put("product_qty", product_qty_total);
                }

            }


            result.successStatus();
            result.setData(shipMentList);

        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query container list fail,Check parameters, please ");
            return result;
        }


        return result;
    }


    /**
     * 获取shipment 详细信息  以及关联的carton 及关联的订单信息
     *
     * @param map
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/getShipmentInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getShipmentInfo(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info("order getShipmentInfo 入参:" + new Gson().toJson(map));
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.size() == 0 || map.get("status") == null || map.get("shipment_id") == null) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        Vendor vendor = null;
        try {
            vendor = vendorService.getVendorByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendor == null) {
            result.setMsg("Please log in again");
            return result;
        }


        try {
            map.put("vendorId", vendor.getVendorId());

            Map<String, Object> getShipment = new HashMap<String, Object>();
            getShipment.put("shipmentId", Long.parseLong(map.get("shipment_id").toString()));

            //根据shipmentId 获取shipment 相关信息及物流第一段类型
            Map<String, Object> shipmentMap = iShipmentService.getShipmentInfoById(getShipment);
            if (shipmentMap == null || shipmentMap.size() == 0) {
                result.setMsg("Query Shipment fail,Check parameters, please ");
                return result;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            if (shipmentMap.get("invoice_date") != null && StringUtils.isNotBlank(shipmentMap.get("invoice_date").toString())) {
                shipmentMap.put("invoice_date", sdf2.format(sdf.parse(shipmentMap.get("invoice_date").toString())));
            }


            //获取carton列表
            List<Map<String, Object>> containerList = orderService.getOrderListByShipmentId(map);
            Map<String, Object> resultMap = new HashMap<String, Object>();

            Map<String, List<Map<String, Object>>> orderList = new HashMap<String, List<Map<String, Object>>>();
            List<Map<String, Object>> shipMentCartonList = new ArrayList<Map<String, Object>>();

            if (containerList != null && containerList.size() > 0) {

                for (Map<String, Object> container : containerList) {

                    //根据shipment_id 分组
                    if (orderList.containsKey(container.get("container_id").toString())) {
                        List<Map<String, Object>> cons = orderList.get(container.get("container_id").toString());
                        cons.add(container);
                    } else {
                        List<Map<String, Object>> cons = new ArrayList<Map<String, Object>>();
                        cons.add(container);
                        orderList.put(container.get("container_id").toString(), cons);


                        //获取container信息
                        Map<String, Object> cartonInfo = new HashMap<String, Object>();
                        cartonInfo.put("container_id", container.get("container_id").toString());
                        cartonInfo.put("barcode", container.get("barcode").toString());
                        cartonInfo.put("height", container.get("height").toString());
                        cartonInfo.put("width", container.get("width").toString());
                        cartonInfo.put("length", container.get("length").toString());
                        shipMentCartonList.add(cartonInfo);

                    }


                }

                if (shipMentCartonList != null && shipMentCartonList.size() > 0) {
                    //将orderList 存入container详情
                    for (Map<String, Object> carton : shipMentCartonList) {
                        carton.put("orderList", orderList.get(carton.get("container_id").toString()));
                        carton.put("order_qty", orderList.get(carton.get("container_id").toString()).size());
                    }
                }


            }

            shipmentMap.put("carton_qty", shipMentCartonList == null ? 0 : shipMentCartonList.size());
            resultMap.put("cartonList", shipMentCartonList);
            resultMap.put("shipmentInfo", shipmentMap);


            result.successStatus();
            result.setData(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query container list fail,Check parameters, please ");
            return result;
        }


        return result;
    }

    @RequestMapping(value = "/getGeography", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getGeography() {
        ResultMessage result = new ResultMessage();
        result.errorStatus();
        try {
            List<Map<String, Object>> geographyList = geographyService.getGeographyList();

            result.successStatus();
            result.setData(geographyList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query container list fail,Check parameters, please ");
            return result;
        }

        return result;
    }

    /***
     * 打印shipment 详细信息  以及关联的carton 及关联的订单信息
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/printShipmentInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage printShipmentInfo(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.size() == 0 || map.get("status") == null || map.get("shipment_id") == null) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        Vendor vendor = null;
        try {
            vendor = vendorService.getVendorByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendor == null) {
            result.setMsg("Please log in again");
            return result;
        }

        try {
            map.put("vendorId", vendor.getVendorId());
            Map<String, Object> getShipment = new HashMap<String, Object>();
            getShipment.put("shipmentId", Long.parseLong(map.get("shipment_id").toString()));

            //根据shipmentId 获取shipment 相关信息及物流第一段类型
            logger.info("打印Invoice----根据shipmentId 获取shipment 相关信息及物流第一段类型,开始获取");
            Map<String, Object> shipmentMap = iShipmentService.getShipmentTypeById(getShipment);
            if (shipmentMap == null || shipmentMap.size() == 0) {
                logger.info("获取失败");
                result.setMsg("Query Shipment fail,Check parameters, please ");
                return result;
            }
            logger.info("打印Invoice----获取shipment相关信息及物流第一段类型成功");
            //获取Invoice 信息
            logger.info("打印Invoice----获取Invoice信息");
            Invoice invoice = invoiceService.getInvoiceByShipmentId(Long.parseLong(map.get("shipment_id").toString()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            resultMap.put("InvoiceNumber", invoice.getInvoiceNum());
            if (invoice.getInvoiceDate() != null) {
                String invoiceDate = sdf.format(invoice.getInvoiceDate());
                resultMap.put("InvoiceDate", sdf2.format(sdf.parse(invoiceDate)));
            } else {
                result.setMsg("invoiceDate is null ");
                return result;
            }
            resultMap.put("VATNumber", invoice.getVatNum());

            //获取Ship From信息
            logger.info("打印Invoice----获取Ship From信息");
            Map<String, Object> vendorParams = new HashMap<>();
            vendorParams.put("vendor_id", shipmentMap.get("vendor_id"));
            Vendor shipVendor = vendorService.getVendorByVendorId(vendorParams);
            resultMap.put("ShipFrom", shipVendor.getBusinessLicenseLocation());
            resultMap.put("ShipCompanyName", shipVendor.getCompanyName());
            resultMap.put("ShipVendorName", shipVendor.getVendorName());

            //获取Invoice To信息
            logger.info("打印Invoice----获取Invoice To信息");
            Shop shop = shopService.selectByPrimaryKey(65l);
            resultMap.put("InvoiceTo", shop.getBusinessLicenseLocation());
            resultMap.put("InvoiceName", shop.getShopName());

            logger.info("打印Invoice----获取Deliver To信息");
            List<SubShipment> subShipmentList = subShipmentService.getSubShipmentByShipmentId(Long.parseLong(map.get("shipment_id").toString()));
            if (subShipmentList.size() > 1) {
                ShippingProvider shippingProvider = shippingProviderService.getShippingProviderByShipmentId(Long.parseLong(map.get("shipment_id").toString()));
                resultMap.put("DeliverTo", shippingProvider);
            } else if (subShipmentList.size() == 1) {
                resultMap.put("DeliverTo", subShipmentList.get(0));
            } else {
                result.setMsg("DeliverTo is null ");
                return result;
            }
            //获取carton列表
            List<Map<String, Object>> containerList = orderService.getOrderListByShipmentId(map);

            double allTotal = 0;
            double VAT = 0;

            if (containerList != null && containerList.size() > 0) {
                for (Map<String, Object> container : containerList) {
                    double total = Double.parseDouble(container.get("in_price").toString()) * Double.parseDouble(container.get("amount").toString());
                    //获取欧盟的信息
                    Geography geography = geographyService.getGeographyById(3l);
                    logger.info("打印Invoice----获取VAT信息");
                    //获取当前shipment的信息
                    Map<String, Object> shipmentPramMap = new HashMap<>();
                    shipmentPramMap.put("shipmentId", Long.parseLong(map.get("shipment_id").toString()));
                    Shipment shipment = iShipmentService.selectShipmentById(shipmentPramMap);
                    if (geography != null && shipment != null && geography.getGeographyId().toString().equals(container.get("geography_id").toString())) {
                        if (geography.getEnglishName().equals(shipment.getShipToGeography())) {
                            logger.info("打印Invoice----查询当前订单的到货国家");
                            //查询当前订单的到货国家
                            AddressCountry addressCountry = addressCountryService.getAddressCountryByName(container.get("user_rec_country").toString());
                            logger.info("打印Invoice----查询当前到货国家的tax_rate");
                            //查询当前到货国家的tax_rate
                            Tax tax = taxService.getTaxByAddressCountryId(addressCountry.getAddressCountryId());
                            if (tax.getTaxRate() != null) {
                                logger.info("打印Invoice----计算VAT的值");
                                VAT += Double.parseDouble(container.get("in_price").toString()) * tax.getTaxRate().doubleValue();
                            }

                        }
                    }
                    allTotal = allTotal + total;
                    container.put("Total", total);
                }
            }


            shipmentMap.put("all_qty", containerList == null ? 0 : containerList.size());
            resultMap.put("all_qty", containerList == null ? 0 : containerList.size());
            resultMap.put("cartonList", containerList);
            resultMap.put("shipmentInfo", shipmentMap);
            resultMap.put("allTotal", allTotal);
            resultMap.put("VAT", VAT);
            resultMap.put("GrandTotal", VAT + allTotal);


            result.successStatus();
            result.setData(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query container list fail,Check parameters, please ");
            return result;
        }

        return result;
    }


    /**
     * packingList
     *
     * @param map
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/printPackingList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage printPackingList(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.size() == 0 || map.get("status") == null || map.get("shipment_id") == null) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        Vendor vendor = null;
        try {
            vendor = vendorService.getVendorByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendor == null) {
            result.setMsg("Please log in again");
            return result;
        }

        try {
            map.put("vendorId", vendor.getVendorId());

            Map<String, Object> getShipment = new HashMap<String, Object>();
            getShipment.put("shipmentId", Long.parseLong(map.get("shipment_id").toString()));

            logger.info("打印PackList----根据shipmentId 获取shipment相关信息及物流第一段类型");
            //根据shipmentId 获取shipment 相关信息及物流第一段类型
            Map<String, Object> shipmentMap = iShipmentService.getShipmentTypeById(getShipment);
            if (shipmentMap == null || shipmentMap.size() == 0) {
                result.setMsg("Query Shipment fail,Check parameters, please ");
                return result;
            }

            //获取carton列表
            logger.info("打印PackList----获取carton列表");
            List<Map<String, Object>> containerList = orderService.getOrderListByShipmentId(map);
            Map<String, Object> resultMap = new HashMap<String, Object>();

            Map<String, List<Map<String, Object>>> orderList = new HashMap<String, List<Map<String, Object>>>();
            List<Map<String, Object>> shipMentCartonList = new ArrayList<Map<String, Object>>();


            if (containerList != null && containerList.size() > 0) {

                for (Map<String, Object> container : containerList) {

                    //根据shipment_id 分组
                    if (orderList.containsKey(container.get("container_id").toString())) {
                        List<Map<String, Object>> cons = orderList.get(container.get("container_id").toString());
                        cons.add(container);
                    } else {
                        List<Map<String, Object>> cons = new ArrayList<Map<String, Object>>();
                        cons.add(container);
                        orderList.put(container.get("container_id").toString(), cons);

                        //获取container信息
                        Map<String, Object> cartonInfo = new HashMap<String, Object>();
                        cartonInfo.put("container_id", container.get("container_id").toString());
                        cartonInfo.put("ship_to_geography", container.get("ship_to_geography").toString());
                        cartonInfo.put("barcode", container.get("barcode").toString());

                        //生成条形码
                        String cartonNumUrl = "Barcode-" + container.get("barcode").toString() + ".png";
                        cartonNumUrl = BarcodeUtil.generateFile(container.get("barcode").toString(), cartonNumUrl, false);
                        cartonInfo.put("cartonNumUrl", cartonNumUrl);

                        cartonInfo.put("height", container.get("height").toString());
                        cartonInfo.put("width", container.get("width").toString());
                        cartonInfo.put("length", container.get("length").toString());
                        shipMentCartonList.add(cartonInfo);

                    }

                }

                if (shipMentCartonList != null && shipMentCartonList.size() > 0) {
                    //将orderList 存入container详情
                    for (Map<String, Object> carton : shipMentCartonList) {
                        List<Map<String, Object>> list = orderList.get(carton.get("container_id").toString());
                        carton.put("orderList", list);
                        double allTotal = 0;
                        for (Map<String, Object> conInfo : list) {
                            double total = Double.parseDouble(conInfo.get("in_price").toString()) * Double.parseDouble(conInfo.get("amount").toString());
                            allTotal = allTotal + total;
                            conInfo.put("Total", total);

                            carton.put("allTotal", list == null ? 0 : list.size());
                            carton.put("allTotalNum", allTotal);
                        }


                        carton.put("order_qty", orderList.get(carton.get("container_id").toString()).size());
                    }
                }


            }

            shipmentMap.put("carton_qty", shipMentCartonList == null ? 0 : shipMentCartonList.size());
            resultMap.put("cartonList", shipMentCartonList);
            resultMap.put("shipmentInfo", shipmentMap);


            result.successStatus();
            result.setData(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query container list fail,Check parameters, please ");
            return result;
        }


        return result;
    }
}
