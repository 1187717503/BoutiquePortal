package com.intramirror.web.controller.order;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.logistics.api.model.Invoice;
import com.intramirror.logistics.api.model.VendorShipment;
import com.intramirror.logistics.api.service.IInvoiceService;
import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.model.Geography;
import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.model.Tax;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.GeographyService;
import com.intramirror.main.api.service.StockLocationService;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.model.ShippingProvider;
import com.intramirror.order.api.model.SubShipment;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.order.api.service.IShippingProviderService;
import com.intramirror.order.api.service.ISubShipmentService;
import com.intramirror.order.api.util.HttpClientUtil;
import com.intramirror.order.api.vo.LogisticsProductVO;
import com.intramirror.product.api.model.Shop;
import com.intramirror.product.api.service.IShopService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.VO.DHLInputVO;
import com.intramirror.web.VO.InvoiceVO;
import com.intramirror.web.VO.RecipientVO;
import com.intramirror.web.VO.ShipperVO;
import com.intramirror.web.VO.TransitWarehouseInvoiceVO;
import com.intramirror.web.common.BarcodeUtil;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.util.ExcelUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.common.utils.StringUtil;

@CrossOrigin
@Controller
@RequestMapping("/orderShip")
public class OrderShipController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(OrderShipController.class);

    @Autowired
    private IOrderService orderService;

    @Autowired
    private VendorService vendorService;

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

    @Autowired
    private StockLocationService stockLocationService;

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

        List<Vendor> vendors = null;
        try {
            vendors = vendorService.getVendorsByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendors == null) {
            result.setMsg("Please log in again");
            return result;
        }
        List<Long> vendorIds = vendors.stream().map(Vendor::getVendorId).collect(Collectors.toList());

        try {
            Map<String, Object> paramtMap = new HashMap<String, Object>();

            if (map.get("sortByName") != null && StringUtils.isNoneBlank(map.get("sortByName").toString())) {
                paramtMap.put("sortByName", map.get("sortByName").toString());
            }

            if (map.get("shipmentStatus") != null && StringUtils.isNoneBlank(map.get("shipmentStatus").toString())) {
                paramtMap.put("shipmentStatus", Integer.parseInt(map.get("shipmentStatus").toString()));
            }

            if (map.get("locationId") != null && StringUtils.isNoneBlank(map.get("locationId").toString())) {
                paramtMap.put("locationId", Integer.parseInt(map.get("locationId").toString()));
            }

            if (map.get("ship_to_geography") != null && StringUtils.isNoneBlank(map.get("ship_to_geography").toString())) {
                paramtMap.put("ship_to_geography", map.get("ship_to_geography").toString());
            }

            if (map.get("locationId") != null && StringUtils.isNoneBlank(map.get("locationId").toString())) {
                paramtMap.put("locationId", map.get("locationId").toString());
            }

            if (map.get("vendorId") != null && StringUtils.isNoneBlank(map.get("vendorId").toString())) {
                vendorIds= Arrays.asList(Long.parseLong(map.get("vendorId").toString()));
            }

            paramtMap.put("vendorIds", vendorIds);

            //获取箱子列表信息
            logger.info("order getReadyToShipCartonList 获取container 列表信息   调用接口containerService.getContainerList 入参:" + new Gson().toJson(paramtMap));
            List<Map<String, Object>> containerList = containerService.getContainerList(paramtMap);
            List<Shipment> shipmentListSort = null;
            if(paramtMap.get("sortByName")!=null){
                //查询出有序的shipment的列表
                shipmentListSort = iShipmentService.getShipmentList(paramtMap);
            }

            Map<String, List<Map<String, Object>>> shipMentCartonList = new HashMap<String, List<Map<String, Object>>>();
            List<Map<String, Object>> shipMentList = new ArrayList<Map<String, Object>>();

            if (containerList != null && containerList.size() > 0) {
                logger.info("order getReadyToShipCartonList 解析container 列表信息 ");

                //获取product列表
                Set<Long> shipmentIds =  new HashSet<>();
                for (Map<String, Object> container:containerList){
                    shipmentIds.add(Long.parseLong(container.get("shipment_id").toString()));
                }
                paramtMap.put("shipmentIds",shipmentIds);
                paramtMap.put("status", OrderStatusType.READYTOSHIP);
                List<Map<String, Object>> orderList = orderService.getOrderListByShipmentId(paramtMap);

                for (Map<String, Object> container : containerList) {
                    List<Map<String,Object>> orderListContain = new LinkedList<>();
                    String containerId = container.get("container_id").toString();
                    if(orderList!=null&&orderList.size()>0){
                        for (Map<String, Object> order :orderList){
                            String price = order.get("price")!=null?order.get("price").toString():"0";
                            order.put("price",(new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP)).toString());
                            String in_price = order.get("in_price")!=null?order.get("in_price").toString():"0";
                            order.put("in_price",(new BigDecimal(in_price).setScale(2, BigDecimal.ROUND_HALF_UP)).toString());
                            if(containerId.equals(order.get("container_id").toString())){
                                orderListContain.add(order);
                            }
                        }
                    }
                    container.put("orderList",orderListContain);

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
                        String shipToGeography = container.get("ship_to_geography").toString();
                        shipMent.put("ship_to_geography", shipToGeography);
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
            //shipment排序
            List<Map<String, Object>> shipmentListMapSort = new ArrayList<>();
            if(shipmentListSort!=null&&shipmentListSort.size()>0){
                for (Shipment shipment:shipmentListSort){
                    Long shipmentId = shipment.getShipmentId();
                    for (Map<String, Object> shipmentMap:shipMentList){
                        if(shipmentId.equals(Long.parseLong(shipmentMap.get("shipment_id").toString()))){
                            shipmentMap.put("stockLocation",shipment.getStockLocation());
                            shipmentMap.put("vendorName",shipment.getVendorName());
                            shipmentListMapSort.add(shipmentMap);
                            break;
                        }
                    }
                }
            }else {
                shipmentListMapSort = shipMentList;
            }

            result.successStatus();
            result.setData(shipmentListMapSort);

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
            //map.put("vendorId", vendor.getVendorId());
            map.put("vendorIds", Arrays.asList(vendor.getVendorId()));

            Map<String, Object> getShipment = new HashMap<>();
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
                shipmentMap.put("invoice_num",shipmentMap.get("invoiceNum"));
            }

            //获取carton列表
            Set<Long> shipmentIds =  new HashSet<>();
            shipmentIds.add(Long.parseLong(map.get("shipment_id").toString()));
            map.put("shipmentIds", shipmentIds);
            List<Map<String, Object>> containerList = orderService.getOrderListByShipmentId(map);
            Map<String, Object> resultMap = new HashMap<String, Object>();

            Map<String, List<Map<String, Object>>> orderList = new HashMap<String, List<Map<String, Object>>>();
            List<Map<String, Object>> shipMentCartonList = new ArrayList<Map<String, Object>>();

            if (containerList != null && containerList.size() > 0) {

                for (Map<String, Object> container : containerList) {
                    String priceStr = container.get("price").toString();
                    String inPriceStr = container.get("in_price").toString();
                    //四舍五入金额
                    container.put("price",new BigDecimal(priceStr).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                    container.put("in_price",new BigDecimal(inPriceStr).setScale(2,BigDecimal.ROUND_HALF_UP).toString());

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
                        String weight = container.get("weight").toString();
                        BigDecimal decimal = new BigDecimal(weight);
                        cartonInfo.put("weight",decimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
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

            VendorShipment vendorShipment = invoiceService.queryVendorShipmentByShipmentId(Long.parseLong(map.get("shipment_id").toString()));
            shipmentMap.put("carton_qty", shipMentCartonList == null ? 0 : shipMentCartonList.size());
            resultMap.put("cartonList", shipMentCartonList);
            resultMap.put("shipmentInfo", shipmentMap);
            if(vendorShipment!=null){
                resultMap.put("vendorShipment",vendorShipment);
            }

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
            List<Geography> geographyList = geographyService.getGeographyGroupList();

            result.successStatus();
            result.setData(geographyList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query geography list fail,Check parameters, please ");
            return result;
        }

        return result;
    }

    @RequestMapping(value = "/getStockLocation", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getStockLocation(Long userId,Long vendorId) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();
        if ((userId == null || userId == 0)&&(vendorId == null || vendorId == 0)){
            result.setMsg("parameters vendorId is null ");
            return result;
        }
        try {
            if (userId != null) {
                Vendor vendor = vendorService.getVendorByUserId(userId);
                if (vendor != null){
                    List<StockLocation> stockLocationList = stockLocationService.getStockLocation(vendor.getVendorId());
                    result.successStatus();
                    result.setData(stockLocationList);
                }else {
                    result.setMsg("此用户未注册买手店");
                    return result;
                }
            }else if(vendorId>0){
                List<StockLocation> stockLocationList = stockLocationService.getStockLocation(vendorId);
                result.successStatus();
                result.setData(stockLocationList);
            }else {
                result.setMsg("Parameter error.");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query StockLocation list fail,Check parameters, please ");
            return result;
        }
        return result;
    }

    @RequestMapping(value = "/getVendors", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getVendors(Long userId) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();
        if (userId == null || userId == 0){
            result.setMsg("parameters vendorId is null ");
            return result;
        }
        try {
            List<Vendor> vendors=vendorService.getVendorsByUserId(userId);
            result.successStatus();
            result.setData(vendors);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query StockLocation list fail,Check parameters, please ");
            return result;
        }
        return result;
    }

    private ResultMessage printExcelShipmentInfo(HttpServletResponse response,Map<String, Object> resultMap) throws IOException {
        String fileName = new Date().getTime() + ".xls";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ExcelUtil.createWorkBook(resultMap).write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;
    }

    private ResultMessage printExcelShipmentInfo(HttpServletResponse response, TransitWarehouseInvoiceVO invoiceVO) throws IOException {
        String fileName = new Date().getTime() + ".xls";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ExcelUtil.createWorkBook(invoiceVO).write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;
    }

    /***
     * 打印shipment 详细信息  以及关联的carton 及关联的订单信息
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/printShipmentInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage printShipmentInfo(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest,HttpServletResponse response) {
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

        //获取ddt number
        long shipment_id = Long.parseLong(map.get("shipment_id").toString());
        //获取Invoice 信息
        logger.info("打印Invoice----获取Invoice信息");
        Invoice invoice = invoiceService.getInvoiceByShipmentId(shipment_id);
        if(map.get("shipmentCategory")!=null&&"1".equals(map.get("shipmentCategory").toString())){
            if (invoice==null){
                Map<String, Object> ddtNo = invoiceService.getMaxDdtNo();
                int maxDdtNo = ddtNo.get("maxddtNum")!=null?Integer.parseInt(ddtNo.get("maxddtNum").toString()):0;
                long s = ddtNo.get("shipment_id") != null ? Long.parseLong(ddtNo.get("shipment_id").toString()) : 0;
                if (s!=shipment_id){
                    Invoice newInvoice = new Invoice();
                    if(maxDdtNo<10000){
                        maxDdtNo = 10000;
                    }else {
                        maxDdtNo++;
                    }
                    newInvoice.setEnabled(true);
                    newInvoice.setDdtNum(maxDdtNo);
                    newInvoice.setInvoiceNum("");
                    newInvoice.setShipmentId(shipment_id);
                    newInvoice.setVendorId(vendor.getVendorId());
                    newInvoice.setInvoiceDate(new Date());
                    newInvoice.setVatNum("");
                    invoiceService.insertSelective(newInvoice);
                    invoice = newInvoice;
                }
            }
        }else {
            //获取Invoice To信息
            logger.info("打印Invoice----获取Invoice To信息");
            Shop shop = shopService.selectByPrimaryKey(65l);
            resultMap.put("InvoiceTo", shop.getBusinessLicenseLocation());
            resultMap.put("InvoiceName", shop.getShopName());
        }

        try {
            map.put("vendorId", vendor.getVendorId());
            Map<String, Object> getShipment = new HashMap<>();
            getShipment.put("shipmentId", shipment_id);

            //根据shipmentId 获取shipment 相关信息及物流第一段类型
            logger.info("打印Invoice----根据shipmentId 获取shipment 相关信息及物流第一段类型,开始获取");
            Map<String, Object> shipmentMap = iShipmentService.getShipmentTypeById(getShipment);
            if (shipmentMap == null || shipmentMap.size() == 0) {
                logger.info("获取失败");
                result.setMsg("Query Shipment fail,Check parameters, please ");
                return result;
            }
            resultMap.put("shipmentNo",shipmentMap.get("shipment_no"));
            logger.info("打印Invoice----获取shipment相关信息及物流第一段类型成功");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            if(map.get("shipmentCategory")!=null&&"1".equals(map.get("shipmentCategory").toString())){
                resultMap.put("InvoiceNumber", invoice.getDdtNum());
            }else {
                resultMap.put("InvoiceNumber", invoice.getInvoiceNum());
            }
            if (invoice.getInvoiceDate() != null) {
                String invoiceDate = sdf.format(invoice.getInvoiceDate());
                resultMap.put("InvoiceDate", sdf2.format(sdf.parse(invoiceDate)));
            } else {
                result.setMsg("invoiceDate is null ");
                return result;
            }
            resultMap.put("VATNumber", invoice.getVatNum());

            //获取Ship From信息
            StockLocation location = stockLocationService.getShipFromLocation(shipment_id);
            // 获取invoice from
            resultMap.put("companyName",location.getContactCompanyName());
            resultMap.put("personName",location.getContactPersonName());
            resultMap.put("contact",location.getContactPhoneNumber());
            resultMap.put("address",location.getAddressStreetlines());
            resultMap.put("city",location.getAddressCity());
            resultMap.put("country","Italy");

            resultMap.put("invoiceCompanyName",vendor.getCompanyName());
            resultMap.put("invoicePersonName",vendor.getRegisteredPerson());
            resultMap.put("invoiceAddress",vendor.getBusinessLicenseLocation());
            //resultMap.put("invoiceCity",location.getAddressCity());
            resultMap.put("invoiceCountry","Italy");

            logger.info("打印Invoice----获取Deliver To信息");
            List<SubShipment> subShipmentList = subShipmentService.getSubShipmentByShipmentId(shipment_id);
            if (subShipmentList!=null&&subShipmentList.size()>0){
                resultMap.put("DeliverTo", subShipmentList.get(0));
                //发往质检仓的取DHL税号
                if(map.get("shipmentCategory")!=null&&"1".equals(map.get("shipmentCategory").toString())){
                    resultMap.put("VATNumber",subShipmentList.get(0).getPiva());
                }
            }else {
                result.setMsg("DeliverTo is null ");
                return result;
            }
            /*if (subShipmentList.size() > 1) {
                ShippingProvider shippingProvider = shippingProviderService.getShippingProviderByShipmentId(shipment_id);
                resultMap.put("DeliverTo", shippingProvider);
            } else if (subShipmentList.size() == 1) {
                resultMap.put("DeliverTo", subShipmentList.get(0));
                //发往质检仓的取DHL税号
                if(map.get("shipmentCategory")!=null&&"1".equals(map.get("shipmentCategory").toString())){
                    resultMap.put("VATNumber",subShipmentList.get(0).getPiva());
                }
            } else {
                result.setMsg("DeliverTo is null ");
                return result;
            }*/
            //获取carton列表
            Set<Long> shipmentIds =  new HashSet<>();
            shipmentIds.add(shipment_id);
            map.put("shipmentIds",shipmentIds);
            List<Map<String, Object>> containerList = orderService.getOrderListByShipmentId(map);

            BigDecimal allTotal = new BigDecimal(0);
            BigDecimal VAT = new BigDecimal(0);
            BigDecimal totalWeight = new BigDecimal(0);
            Map<String,BigDecimal> decimalMap = new HashMap<>();
            if (containerList != null && containerList.size() > 0) {
                for (Map<String, Object> container : containerList) {
                    decimalMap.put(container.get("container_id").toString(),new BigDecimal(container.get("weight").toString()));
                    BigDecimal total = new BigDecimal(Double.parseDouble(container.get("in_price").toString()) * Double.parseDouble(container.get("amount").toString())).setScale(2,BigDecimal.ROUND_HALF_UP);
                    container.put("in_price",new BigDecimal(container.get("in_price").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                    //获取欧盟的信息
                    Geography geography = geographyService.getGeographyById(3l);
                    logger.info("打印Invoice----获取VAT信息");
                    //获取当前shipment的信息
                    Map<String, Object> shipmentPramMap = new HashMap<>();
                    shipmentPramMap.put("shipmentId", shipment_id);
                    Shipment shipment = iShipmentService.selectShipmentById(shipmentPramMap);
                    if (geography != null && shipment != null && geography.getGeographyId().toString().equals(container.get("geography_id").toString())) {
                        if (geography.getEnglishName().equals(shipment.getShipToGeography())) {
                            logger.info("打印Invoice----查询当前订单的到货国家");
                            //查询当前订单的到货国家
                            AddressCountry addressCountry = addressCountryService.getAddressCountryByName(container.get("countryName").toString());
                            logger.info("打印Invoice----查询当前到货国家的tax_rate");
                            //查询当前到货国家的tax_rate
                            Tax tax = taxService.getTaxByAddressCountryId(addressCountry.getAddressCountryId());
                            if (tax.getTaxRate() != null) {
                                logger.info("打印Invoice----计算VAT的值");
                                VAT = VAT.add(new BigDecimal(Double.parseDouble(container.get("in_price").toString())).multiply(tax.getTaxRate())).setScale(2,BigDecimal.ROUND_HALF_UP);
                            }
                        }
                    }
                    allTotal = allTotal.add(total);
                    container.put("Total", total);
                }
            }
            if(decimalMap.size()>0){
                Set<String> strings = decimalMap.keySet();
                for (String key:strings){
                    totalWeight = totalWeight.add(decimalMap.get(key));
                }
            }
            resultMap.put("totalWeight",totalWeight.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            resultMap.put("cartonQty",decimalMap.size());
            shipmentMap.put("all_qty", containerList == null ? 0 : containerList.size());
            resultMap.put("all_qty", containerList == null ? 0 : containerList.size());
            resultMap.put("cartonList", containerList);
            resultMap.put("shipmentInfo", shipmentMap);
            //金额四舍五入
            resultMap.put("allTotal", allTotal.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            resultMap.put("VAT", VAT.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            resultMap.put("GrandTotal", (VAT.add(allTotal)).setScale(2,BigDecimal.ROUND_HALF_UP).toString());



            String isExcel = map.get("isExcel")!=null?map.get("isExcel").toString():null;
            if(isExcel!=null&&"1".equals(isExcel)){
                printExcelShipmentInfo(response,resultMap);
            }
            result.successStatus();
            result.setData(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query container list fail,Check parameters, please ");
            return result;
        }

        return result;
    }

    /***
     * 打印去transit warehouse的发票
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/printInvoice", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage printInvoice(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest,HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
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

        TransitWarehouseInvoiceVO transitWarehouseInvoiceVO = new TransitWarehouseInvoiceVO();
        long shipment_id = Long.parseLong(map.get("shipment_id").toString());

        //获取Ship From信息
        StockLocation location = stockLocationService.getShipFromLocation(shipment_id);
        ShipperVO shipperVO = new ShipperVO();
        shipperVO.setCompanyName(location.getContactCompanyName());
        shipperVO.setPersonName(location.getContactPersonName());
        shipperVO.setPhoneNumber(location.getContactPhoneNumber());
        shipperVO.setStreetLines(location.getAddressStreetlines());
        shipperVO.setStreetLines2(location.getAddressStreetlines2());
        shipperVO.setStreetLines3(location.getAddressStreetlines3());
        shipperVO.setCity(location.getAddressCity());
        shipperVO.setCountry("Italy");
        ShipperVO invoiceForm = new ShipperVO();
        invoiceForm.setCompanyName(vendor.getCompanyName());
        invoiceForm.setPersonName(vendor.getRegisteredPerson());
        invoiceForm.setStreetLines(vendor.getBusinessLicenseLocation());
        invoiceForm.setCountry("Italy");

        boolean isDDt = false;
        if(map.get("ddtFlag")!=null&&"1".equals(map.get("ddtFlag").toString())){
            isDDt = true;
        }

        //获取Invoice 信息
        logger.info("打印Invoice----获取Invoice信息");
        Map<String, Object> invoiceMap = new HashMap<>();
        if(map.get("ddtFlag")!=null&&"1".equals(map.get("ddtFlag").toString())){
            invoiceMap.put("type", 2);
        }else{
            invoiceMap.put("type", 1);
        }
        invoiceMap.put("shipmentId", shipment_id);
        Invoice invoice = invoiceService.getInvoiceByMap(invoiceMap);
        if (invoice==null){
            Map<String, Object> ddtNo = invoiceService.getMaxDdtNo();
            int maxDdtNo = ddtNo.get("maxddtNum")!=null?Integer.parseInt(ddtNo.get("maxddtNum").toString()):0;

            Invoice newInvoice = new Invoice();
            if(maxDdtNo<10000){
                maxDdtNo = 10000;
            }else {
                maxDdtNo++;
            }
            newInvoice.setEnabled(true);
            newInvoice.setDdtNum(maxDdtNo);
            newInvoice.setInvoiceNum("");
            newInvoice.setShipmentId(shipment_id);
            newInvoice.setVendorId(vendor.getVendorId());
            newInvoice.setInvoiceDate(new Date());
            newInvoice.setVatNum("");
            newInvoice.setType(2);
            invoiceService.insertSelective(newInvoice);
            invoice = newInvoice;
        }else {
            if(map.get("ddtFlag")!=null&&"1".equals(map.get("ddtFlag").toString())){
                if(invoice.getDdtNum() == null){
                    Map<String, Object> ddtNo = invoiceService.getMaxDdtNo();
                    int maxDdtNo = ddtNo.get("maxddtNum")!=null?Integer.parseInt(ddtNo.get("maxddtNum").toString()):0;
                    if(maxDdtNo<10000){
                        maxDdtNo = 10000;
                    }else {
                        maxDdtNo++;
                    }
                    invoice.setDdtNum(maxDdtNo);

                    invoiceService.updateByPrimaryKey(invoice);
                }
            }
            //获取Invoice To信息
            logger.info("打印Invoice----获取Invoice To信息");
            Shop shop = shopService.selectByPrimaryKey(65l);
            resultMap.put("InvoiceTo", shop.getBusinessLicenseLocation());
            resultMap.put("InvoiceName", shop.getShopName());
            resultMap.put("contactPersonName", shop.getContactPersonName());
        }

        map.put("vendorId", vendor.getVendorId());
        Map<String, Object> getShipment = new HashMap<>();
        getShipment.put("shipmentId", shipment_id);

        //根据shipmentId 获取shipment 相关信息及物流第一段类型
        logger.info("打印Invoice----根据shipmentId 获取shipment 相关信息及物流第一段类型,开始获取");
        Map<String, Object> shipmentMap = iShipmentService.getShipmentTypeById(getShipment);
        if (shipmentMap == null || shipmentMap.size() == 0) {
            logger.info("获取失败");
            result.setMsg("Query Shipment fail,Check parameters, please ");
            return result;
        }
        resultMap.put("shipmentNo",shipmentMap.get("shipment_no"));
        logger.info("打印Invoice----获取shipment相关信息及物流第一段类型成功");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        if(map.get("ddtFlag")!=null&&"1".equals(map.get("ddtFlag").toString())){
            resultMap.put("InvoiceNumber", String.valueOf(invoice.getDdtNum()));
        }else {
            resultMap.put("InvoiceNumber", invoice.getInvoiceNum());
        }
        if (invoice.getInvoiceDate() != null) {
            String invoiceDate = sdf.format(invoice.getInvoiceDate());
            try {
                resultMap.put("InvoiceDate", sdf2.format(sdf.parse(invoiceDate)));
            } catch (ParseException e) {
                logger.error("Query container list fail invoiceDate="+invoiceDate, e);
                result.setMsg("Query container list fail,Check parameters, please ");
                return result;
            }
        } else {
            result.setMsg("invoiceDate is null ");
            return result;
        }
        resultMap.put("VATNumber", invoice.getVatNum());
        //ddt的invoice
        InvoiceVO ddtInvoice = new InvoiceVO();
        List<Map<String,Object>> ddtOrderList = new ArrayList<>();
        BigDecimal ddtVAT = new BigDecimal(0);
        BigDecimal ddtAllTotal = new BigDecimal(0);
        Integer ddtAllQty = 0;
        if(isDDt){
            ddtInvoice.setShipperVO(shipperVO);
            RecipientVO recipientVO = new RecipientVO();
            ShippingProvider shippingProvider = shippingProviderService.getShippingProviderById(5L);// 质检仓地址
            if(shippingProvider == null){
                shippingProvider = new ShippingProvider();
            }
            recipientVO.setCity(shippingProvider.getAddrCity());
            recipientVO.setCompanyName(shippingProvider.getTransferConsignee());
            recipientVO.setPersonName(shippingProvider.getPersonName());
            recipientVO.setStreetLines(shippingProvider.getTransferAddr());
            recipientVO.setStreetLines2(shippingProvider.getTransferAddr2());
            recipientVO.setStreetLines3(shippingProvider.getTransferAddr3());
            recipientVO.setPostalCode(shippingProvider.getZipCode());
            recipientVO.setPhoneNumber(shippingProvider.getTransferContact());
            recipientVO.setCountry(shippingProvider.getAddrCountry());
            ddtInvoice.setRecipientVO(recipientVO);
            ddtInvoice.setList(ddtOrderList);
            ddtInvoice.setInvoiceTo((String) resultMap.get("InvoiceTo"));
            ddtInvoice.setInvoiceName((String) resultMap.get("InvoiceName"));
            ddtInvoice.setInvoicePersonName((String)resultMap.get("contactPersonName"));
            ddtInvoice.setVatNum((String) resultMap.get("VATNumber"));
            ddtInvoice.setInvoiceDate((String) resultMap.get("InvoiceDate"));
            ddtInvoice.setInvoiceNum((String) resultMap.get("InvoiceNumber"));
            ddtInvoice.setShipmentNo((String) resultMap.get("shipmentNo"));
        }

        //获取发往中国大陆，香港，澳门的订单列表
        Set<Long> shipmentIds =  new HashSet<>();
        shipmentIds.add(shipment_id);
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("status", map.get("status"));
        conditionMap.put("vendorId", vendor.getVendorId());
        conditionMap.put("shipmentIds", shipmentIds);
        conditionMap.put("packGroup", 1);
        List<Map<String, Object>> chinaList = orderService.getOrderListByShipmentId(conditionMap);
        if( chinaList != null && chinaList.size() > 0 ){
            InvoiceVO chinaInovice = new InvoiceVO();
            RecipientVO recipientVO = new RecipientVO();
            if(!isDDt){
                transitWarehouseInvoiceVO.setChinaInvoice(chinaInovice);
            }
            //2018-6-28改回ZSY地址
            ShippingProvider shippingProvider = shippingProviderService.getShippingProviderById(4L);//zsy地址
            //2018-6-26修改发票ship to地址为物流第一段供应商地址
            //ShippingProvider shippingProvider = shippingProviderService.getShippingProviderByVendorId(vendor.getVendorId());
            if(shippingProvider == null){
                shippingProvider = new ShippingProvider();
            }
            recipientVO.setCity(shippingProvider.getAddrCity());
            recipientVO.setCompanyName(shippingProvider.getTransferConsignee());
            recipientVO.setPersonName(shippingProvider.getPersonName());
            recipientVO.setStreetLines(shippingProvider.getTransferAddr());
            recipientVO.setStreetLines2(shippingProvider.getTransferAddr2());
            recipientVO.setStreetLines3(shippingProvider.getTransferAddr3());
            recipientVO.setPostalCode(shippingProvider.getZipCode());
            recipientVO.setPhoneNumber(shippingProvider.getTransferContact());
            recipientVO.setCountry(shippingProvider.getAddrCountry());
            //获取shipTo地址信息
            BigDecimal allTotal = new BigDecimal("0");
            BigDecimal VAT = new BigDecimal("0");
            for(Map<String, Object> chinaOrder : chinaList){
                Object countryName = chinaOrder.get("countryName").equals("中国大陆") ? "中国" : chinaOrder.get("countryName");
                AddressCountry addressCountry = addressCountryService.getAddressCountryByName(countryName.toString());
                Tax tax = taxService.getTaxByAddressCountryId(addressCountry.getAddressCountryId());
                BigDecimal taxRate = tax.getTaxRate() == null ? new BigDecimal("0") : tax.getTaxRate();
                BigDecimal total = new BigDecimal(Double.parseDouble(chinaOrder.get("in_price").toString()) * Double.parseDouble(chinaOrder.get("amount").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                VAT = VAT.add(total.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP));
                allTotal = allTotal.add(total);

                String inPrice = chinaOrder.get("in_price")!=null?chinaOrder.get("in_price").toString():"";
                String retailPrice = chinaOrder.get("price")!=null?chinaOrder.get("price").toString():"";
                BigDecimal discount = (new BigDecimal(1)).subtract((new BigDecimal(inPrice)).multiply(new BigDecimal("1.22")).divide(new BigDecimal(retailPrice), 2, RoundingMode.HALF_UP));
                chinaOrder.put("discount", discount.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%");
                chinaOrder.put("in_price", (new BigDecimal(inPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                chinaOrder.put("price", (new BigDecimal(retailPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
            }

            BigDecimal grandTotal = (VAT.add(allTotal)).setScale(2,BigDecimal.ROUND_HALF_UP);
            if(isDDt){
                if(CollectionUtils.isNotEmpty(chinaList)){
                    ddtInvoice.getList().addAll(chinaList);
                }
                ddtAllTotal = ddtAllTotal.add(grandTotal);
                ddtVAT = ddtVAT.add(VAT);
                ddtAllQty  = chinaList.size()+ddtAllQty;

            }
            chinaInovice.setList(chinaList);
            chinaInovice.setRecipientVO(recipientVO);
            chinaInovice.setShipperVO(shipperVO);
            chinaInovice.setInvoiceFromVO(invoiceForm);
            chinaInovice.setInvoiceTo((String) resultMap.get("InvoiceTo"));
            chinaInovice.setInvoiceName((String) resultMap.get("InvoiceName"));
            chinaInovice.setInvoicePersonName((String)resultMap.get("contactPersonName"));
            chinaInovice.setVatNum((String) resultMap.get("VATNumber"));
            chinaInovice.setInvoiceDate((String) resultMap.get("InvoiceDate"));
            chinaInovice.setInvoiceNum((String) resultMap.get("InvoiceNumber"));
            chinaInovice.setShipmentNo((String) resultMap.get("shipmentNo"));
            chinaInovice.setGrandTotal(grandTotal.toString());
            chinaInovice.setAllTotal(allTotal.toString());
            chinaInovice.setVat(VAT.toString());
            chinaInovice.setAllQty(String.valueOf(chinaList.size()));
        }

        //获取欧盟国家的订单
        conditionMap.put("shipmentIds",shipmentIds);
        conditionMap.put("packGroup",2);
        List<Map<String, Object>> UNlist = orderService.getOrderListByShipmentId(conditionMap);
        if (UNlist != null && UNlist.size() > 0){
            List<InvoiceVO> UNInvoiceList = new ArrayList<>();
            if(!isDDt){
                transitWarehouseInvoiceVO.setUNInvoices(UNInvoiceList);
            }
            for (Map<String,Object> UNOrder: UNlist){
                RecipientVO recipientVO = new RecipientVO();
                InvoiceVO UNInvoiceVO = new InvoiceVO();
                //欧盟的不显示remake
                UNInvoiceVO.setRemark(null);
                String country = UNOrder.get("user_rec_country") != null ? UNOrder.get("user_rec_country").toString() : "";
                recipientVO.setCountry(country);
                String personName = UNOrder.get("user_rec_name") != null ? UNOrder.get("user_rec_name").toString() : "";
                recipientVO.setPersonName(personName);
                String province = UNOrder.get("user_rec_province") != null ? UNOrder.get("user_rec_province").toString() : "";
                recipientVO.setProvince(province);
                String city = UNOrder.get("user_rec_city") != null ? UNOrder.get("user_rec_city").toString() : "";
                recipientVO.setCity(city);
                String addr = UNOrder.get("user_rec_addr") != null ? UNOrder.get("user_rec_addr").toString() : "";
                recipientVO.setStreetLines(addr);
                String mobile = UNOrder.get("user_rec_mobile") != null ? UNOrder.get("user_rec_mobile").toString() : "";
                recipientVO.setPhoneNumber(mobile);
                String postalCode = UNOrder.get("user_rec_code") != null ? UNOrder.get("user_rec_code").toString() : "";
                recipientVO.setPostalCode(postalCode);
                String area = UNOrder.get("user_rec_area") != null ? UNOrder.get("user_rec_area").toString() : "";
                recipientVO.setArea(area);

                String inPrice = UNOrder.get("in_price")!=null?UNOrder.get("in_price").toString():"";
                String retailPrice = UNOrder.get("price")!=null?UNOrder.get("price").toString():"";
                BigDecimal discount = (new BigDecimal(1)).subtract((new BigDecimal(inPrice)).multiply(new BigDecimal("1.22")).divide(new BigDecimal(retailPrice), 2, RoundingMode.HALF_UP));
                UNOrder.put("discount", discount.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%");
                UNOrder.put("in_price", (new BigDecimal(inPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                UNOrder.put("price", (new BigDecimal(retailPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));

                AddressCountry addressCountry = addressCountryService.getAddressCountryByName(UNOrder.get("countryName").toString());
                Tax tax = taxService.getTaxByAddressCountryId(addressCountry.getAddressCountryId());
                BigDecimal taxRate = tax.getTaxRate() == null ? new BigDecimal("0") : tax.getTaxRate();
                BigDecimal total = new BigDecimal(Double.parseDouble(UNOrder.get("in_price").toString()) * Double.parseDouble(UNOrder.get("amount").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal VAT = total.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal grandTotal = (VAT.add(total)).setScale(2,BigDecimal.ROUND_HALF_UP);


                List<Map<String, Object>> list = new ArrayList<>();
                list.add(UNOrder);
                if(isDDt){
                    ddtInvoice.getList().addAll(list);
                    ddtAllTotal = ddtAllTotal.add(grandTotal);
                    ddtVAT = ddtVAT.add(VAT);
                    ddtAllQty  = chinaList.size()+ddtAllQty;
                }
                UNInvoiceVO.setList(list);
                UNInvoiceVO.setShipperVO(shipperVO);
                UNInvoiceVO.setInvoiceFromVO(invoiceForm);
                UNInvoiceVO.setRecipientVO(recipientVO);
                UNInvoiceVO.setInvoiceTo((String) resultMap.get("InvoiceTo"));
                UNInvoiceVO.setInvoiceName((String) resultMap.get("InvoiceName"));
                UNInvoiceVO.setInvoicePersonName((String) resultMap.get("contactPersonName"));
                UNInvoiceVO.setVatNum((String) resultMap.get("VATNumber"));
                UNInvoiceVO.setInvoiceDate((String) resultMap.get("InvoiceDate"));
                UNInvoiceVO.setInvoiceNum((String) resultMap.get("InvoiceNumber"));
                UNInvoiceVO.setShipmentNo((String) resultMap.get("shipmentNo"));
                UNInvoiceVO.setGrandTotal(grandTotal.toString());
                UNInvoiceVO.setAllTotal(total.toString());
                UNInvoiceVO.setVat(VAT.toString());
                UNInvoiceVO.setAllQty(String.valueOf(list.size()));

                UNInvoiceList.add(UNInvoiceVO);
            }
        }

        // 获取到其他国家的数据
        conditionMap.put("shipmentIds",shipmentIds);
        conditionMap.put("packGroup", 4);
        List<Map<String, Object>> elselist = orderService.getOrderListByShipmentId(conditionMap);
        if (elselist != null && elselist.size() > 0) {
            List<InvoiceVO> elseInvoiceList = new ArrayList<>();
            if(!isDDt){
                transitWarehouseInvoiceVO.setOtherInvoices(elseInvoiceList);
            }
            for (Map<String,Object> elseOrder: elselist){
                InvoiceVO elseInvoiceVO = new InvoiceVO();
                RecipientVO recipientVO = new RecipientVO();
                String country = elseOrder.get("user_rec_country") != null ? elseOrder.get("user_rec_country").toString() : "";
                recipientVO.setCountry(country);
                String personName = elseOrder.get("user_rec_name") != null ? elseOrder.get("user_rec_name").toString() : "";
                recipientVO.setPersonName(personName);
                String province = elseOrder.get("user_rec_province") != null ? elseOrder.get("user_rec_province").toString() : "";
                recipientVO.setProvince(province);
                String city = elseOrder.get("user_rec_city") != null ? elseOrder.get("user_rec_city").toString() : "";
                recipientVO.setCity(city);
                String addr = elseOrder.get("user_rec_addr") != null ? elseOrder.get("user_rec_addr").toString() : "";
                recipientVO.setStreetLines(addr);
                String mobile = elseOrder.get("user_rec_mobile") != null ? elseOrder.get("user_rec_mobile").toString() : "";
                recipientVO.setPhoneNumber(mobile);
                String postalCode = elseOrder.get("user_rec_code") != null ? elseOrder.get("user_rec_code").toString() : "";
                recipientVO.setPostalCode(postalCode);
                String area = elseOrder.get("user_rec_area") != null ? elseOrder.get("user_rec_area").toString() : "";
                recipientVO.setArea(area);

                String inPrice = elseOrder.get("in_price")!=null?elseOrder.get("in_price").toString():"";
                String retailPrice = elseOrder.get("price")!=null?elseOrder.get("price").toString():"";
                BigDecimal discount = (new BigDecimal(1)).subtract((new BigDecimal(inPrice)).multiply(new BigDecimal("1.22")).divide(new BigDecimal(retailPrice), 2, RoundingMode.HALF_UP));
                elseOrder.put("discount", discount.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%");
                elseOrder.put("in_price", (new BigDecimal(inPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                elseOrder.put("price", (new BigDecimal(retailPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));

                AddressCountry addressCountry = addressCountryService.getAddressCountryByName(elseOrder.get("countryName").toString());
                Tax tax = taxService.getTaxByAddressCountryId(addressCountry.getAddressCountryId());
                BigDecimal taxRate = tax.getTaxRate() == null ? new BigDecimal("0") : tax.getTaxRate();
                BigDecimal total = new BigDecimal(Double.parseDouble(elseOrder.get("in_price").toString()) * Double.parseDouble(elseOrder.get("amount").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal VAT = total.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal grandTotal = (VAT.add(total)).setScale(2,BigDecimal.ROUND_HALF_UP);

                List<Map<String, Object>> list = new ArrayList<>();
                list.add(elseOrder);
                if(isDDt){
                    ddtInvoice.getList().addAll(list);
                    ddtAllTotal = ddtAllTotal.add(grandTotal);
                    ddtVAT = ddtVAT.add(VAT);
                    ddtAllQty  = chinaList.size()+ddtAllQty;
                }
                elseInvoiceVO.setList(list);
                elseInvoiceVO.setShipperVO(shipperVO);
                elseInvoiceVO.setInvoiceFromVO(invoiceForm);
                elseInvoiceVO.setRecipientVO(recipientVO);
                elseInvoiceVO.setInvoiceTo((String) resultMap.get("InvoiceTo"));
                elseInvoiceVO.setInvoiceName((String) resultMap.get("InvoiceName"));
                elseInvoiceVO.setInvoicePersonName((String) resultMap.get("contactPersonName"));
                elseInvoiceVO.setVatNum((String) resultMap.get("VATNumber"));
                elseInvoiceVO.setInvoiceDate((String) resultMap.get("InvoiceDate"));
                elseInvoiceVO.setInvoiceNum((String) resultMap.get("InvoiceNumber"));
                elseInvoiceVO.setShipmentNo((String) resultMap.get("shipmentNo"));
                elseInvoiceVO.setGrandTotal(grandTotal.toString());
                elseInvoiceVO.setAllTotal(total.toString());
                elseInvoiceVO.setVat(VAT.toString());
                elseInvoiceVO.setAllQty(String.valueOf(elseOrder.size()));

                elseInvoiceList.add(elseInvoiceVO);
            }

        }
        if(isDDt){
            List<InvoiceVO> ddtInvoices = new ArrayList<>();
            ddtInvoice.setGrandTotal(ddtAllTotal.toString());
            ddtInvoice.setVat(ddtVAT.toString());
            ddtInvoice.setAllQty(ddtAllQty.toString());
            ddtInvoices.add(ddtInvoice);
            transitWarehouseInvoiceVO.setUNInvoices(ddtInvoices);
        }

        try {
            String isExcel = map.get("isExcel")!=null?map.get("isExcel").toString():null;
            if(isExcel!=null&&"1".equals(isExcel)){
                printExcelShipmentInfo(response, transitWarehouseInvoiceVO);
            }
            result.successStatus();
            result.setData(transitWarehouseInvoiceVO);

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
            Set<Long> shipmentIds =  new HashSet<>();
            shipmentIds.add(Long.parseLong(map.get("shipment_id").toString()));
            map.put("shipmentIds",shipmentIds);
            List<Map<String, Object>> containerList = orderService.getOrderListByShipmentId(map);
            Map<String, Object> resultMap = new HashMap<String, Object>();

            Map<String, List<Map<String, Object>>> orderList = new HashMap<>();
            List<Map<String, Object>> shipMentCartonList = new ArrayList<>();

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
                        String weight = container.get("weight").toString();
                        BigDecimal decimal = new BigDecimal(weight);
                        cartonInfo.put("weight",decimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
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

    @RequestMapping(value = "/printAWBNum", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage printAWBNum(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest){
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.get("shipment_id") == null) {
            result.setMsg("shipment_id cannot be empty");
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
        DHLInputVO inputVO = new DHLInputVO();
        Long shipmentId = Long.parseLong(map.get("shipment_id").toString());
        // 校验invoice
        /*VendorShipment vendorShipment = invoiceService.queryVendorShipmentByShipmentId(shipmentId);
        if(vendorShipment == null){
            result.setMsg("Please upload invoice");
            return result;
        }*/
        if (map.get("shipmentDate")!=null){
            Long shipmentDate = Long.parseLong(map.get("shipmentDate").toString());
            inputVO.setShipmentDate(shipmentDate);
        }
        if (map.get("specialPickupInstruction")!=null){
            inputVO.setSpecialPickupInstruction(map.get("specialPickupInstruction").toString());
        }
        if (map.get("pickupLocation")!=null){
            inputVO.setPickupLocation(map.get("pickupLocation").toString());
        }
        Map<String,Object> params = new HashMap<>();
        //查询close状态纸箱
        //params.put("status",2);
        params.put("shipmentId",shipmentId);
        //查询第一段
        params.put("sequence",1);
        SubShipment dhlShipment = subShipmentService.getDHLShipment(params);
        List<Map<String, Object>> containerList = containerService.getListByShipmentId(params);
        Shipment shipment = iShipmentService.selectShipmentById(params);

        String oldAwbNo = null;
        if(dhlShipment!=null){
            if(StringUtil.isNotEmpty(dhlShipment.getAwbNum())){
                /*String s;
                try {
                    //获取awb文档
                    s = HttpClientUtil.httpGet(HttpClientUtil.queryAWBUrl + dhlShipment.getAwbNum());
                }catch (Exception e){
                    result.addMsg("DHL service invocation failed");
                    logger.error("request fail,params={},url={}",JsonTransformUtil.toJson(inputVO),HttpClientUtil.createAWBUrl);
                    return result;
                }
                if(StringUtil.isNotEmpty(s)){
                    JSONObject jo = JSONObject.fromObject(s);
                    result.successStatus();
                    jo.put("shipmentNo",shipment.getShipmentNo());
                    result.setData(jo);
                    return result;
                }*/
                //删除awb
                /*Map<String,Object> deleteParams = new HashMap<>();
                deleteParams.put("awbNo",dhlShipment.getAwbNum());
                deleteParams.put("requestorName",user.getUsername());
                deleteParams.put("reason","001");
                String s;
                try{
                    s = HttpClientUtil.httpPost(JsonTransformUtil.toJson(deleteParams), HttpClientUtil.deleteAWBUrl);
                }catch (Exception e){
                    logger.error("request fail,params={},url={}",JsonTransformUtil.toJson(deleteParams),HttpClientUtil.deleteAWBUrl);
                    result.setMsg("DHL service invocation failed");
                    return result;
                }
                if (StringUtil.isEmpty(s)){
                    logger.error("deleteAWB fail");
                    result.errorStatus().putMsg("Info", "deleteAWB fail");
                    return result;
                }*/
                //deleteParams.put("shipmentId",map.get("shipmentId"));
                //deleteParams.put("awbNo","");
                //subShipmentService.updateSubShipment(deleteParams);
                oldAwbNo = dhlShipment.getAwbNum();
            }

            if (shipment!=null){
                String shipToGeography = shipment.getShipToGeography();
                if ("European Union".equals(shipToGeography)){
                    if ("IT".equalsIgnoreCase(dhlShipment.getShipToCountryCode())){
                        inputVO.setServiceType("N");
                    }else {
                        inputVO.setServiceType("U");
                    }
                    //查询第三段
                    /*params.put("sequence",3);
                    dhlShipment = subShipmentService.getDHLShipment(params);
                    if (dhlShipment==null){
                        //查询第二段
                        params.put("sequence",2);
                        dhlShipment = subShipmentService.getDHLShipment(params);
                    }*/
                }else if ("Transit Warehouse".equals(shipToGeography)){
                    inputVO.setServiceType("N");
                }else {
                    inputVO.setServiceType("P");
                }
            }
            StockLocation fromLocation = stockLocationService.getShipFromLocation(shipmentId);

            if ("P".equalsIgnoreCase(inputVO.getServiceType())){
                //只有类型ServiceType=P时才需要customsValue
                BigDecimal customValue = iShipmentService.getCustomValue(params);
                if (customValue!=null){
                    inputVO.setCustomsValue(customValue.setScale(2,BigDecimal.ROUND_HALF_UP));
                }
            }

            addParams(inputVO,dhlShipment,fromLocation,containerList,shipment);
            String resultStr;
            try{
                logger.info("shipmentRequest,params={},url={}",JsonTransformUtil.toJson(inputVO), HttpClientUtil.createAWBUrl);
                resultStr = HttpClientUtil.httpPost(JsonTransformUtil.toJson(inputVO), HttpClientUtil.createAWBUrl);
            }catch (Exception e){
                result.addMsg("DHL service invocation failed");
                logger.error("request fail,params={},url={}",JsonTransformUtil.toJson(inputVO), HttpClientUtil.createAWBUrl);
                return result;
            }
            if(StringUtil.isNotEmpty(resultStr)){
                JSONObject object = JSONObject.fromObject(resultStr);
                if(object.optInt("status")!=1) {
                    //获取DHL报错信息
                    String msg = object.optString("msg");
                    JSONObject jsonObject = JSONObject.fromObject(msg);
                    JSONObject shipmentResponse = jsonObject.optJSONObject("ShipmentResponse");
                    JSONArray notification = shipmentResponse.optJSONArray("Notification");
                    msg = notification.get(0).toString();
                    String message = JSONObject.fromObject(msg).optString("Message");
                    if (message.contains("[")&&message.contains("]")&&message.contains(":")){
                        message = convertMsg(message);
                    }
                    message = message + ". Please contact customer to adjust! ";
                    result.addMsg(message);
                    return result;
                }
                JSONObject jo = JSONObject.fromObject(resultStr);
                String awbNo = jo.optString("awbNo");
                //保存到subShipment
                params.put("awbNo",awbNo);
                subShipmentService.updateSubShipment(params);
                result.successStatus();
                jo.put("shipmentNo",shipment.getShipmentNo());
                jo.remove("status");
                result.setData(jo);

                //保存awb单号
                List<LogisticsProductVO> productVOList = iShipmentService.getlogisticsMilestone(shipmentId);
                if (productVOList!=null&&productVOList.size()>0){
                    for (LogisticsProductVO vo:productVOList){
                        vo.setAwb(awbNo);
                        vo.setIsComplete(0);
                        vo.setShipmentType(3);
                        vo.setType(4);
                        iShipmentService.saveMilestone(vo);
                    }
                }
                logger.info("shipmentNo:{},新生成awb为{},并同步到物流数据中",shipment.getShipmentNo(),awbNo);

                if (oldAwbNo != null){
                    //删除物流表中旧的awb单号
                    iShipmentService.deleteMilestone(oldAwbNo);
                    logger.info("awb:{}已从物流数据中删除",oldAwbNo);
                }
                return result;
            }

        }
        return result;
    }

    public static String convertMsg(String message) {
        String msg = "";
        if (StringUtil.isNotEmpty(message)){
            int i1 = message.indexOf("]");
            int i2 = message.indexOf(":");
            int i = message.indexOf("-");
            boolean maximum = message.contains("maximum");
            String substring = message.substring(i1 + 1, i);
            String s = message.substring(i2+1);
            String[] split = s.split("/");
            String filed = "";
            if (split.length>=3){
                filed = split[split.length-3]+"."+split[split.length-2]+"."+split[split.length-1];
            }else {
                filed = s;
            }
            if (maximum){
                msg = substring.trim() + "(35 characters) ";
            }else {
                msg = substring.trim() + " ";
            }
            msg += "Filed: "+filed;
        }
        return msg;
    }

    private void addParams(DHLInputVO inputVO, SubShipment dhlShipment,StockLocation fromLocation, List<Map<String, Object>> containerList,Shipment shipment) {
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("HH:mm:ssz");
        //Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_YEAR, 1);
        //Date currentTime = calendar.getTime();
        Date currentTime = new Date(inputVO.getShipmentDate());
        inputVO.setShipmentDate(null);
        try {
            //System.out.println(f1.parse("2015-12-12"));
            f1.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));
            f2.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //更换账号2018-08-02
        inputVO.setAccount("106841512");
        inputVO.setShipmentTime(f1.format(currentTime)+"T"+f2.format(currentTime));
        inputVO.setDescription("clothing");
        inputVO.setLabelType("PDF");
        inputVO.setLabelTemplate("ECOM26_A4_001");
        if (fromLocation!=null){
            ShipperVO shipperVO = new ShipperVO();
            shipperVO.setCity(fromLocation.getAddressCity());
            shipperVO.setCompanyName(fromLocation.getContactCompanyName());
            shipperVO.setPersonName(fromLocation.getContactPersonName());
            shipperVO.setPhoneNumber(fromLocation.getContactPhoneNumber());
            shipperVO.setEmailAddress(fromLocation.getContactEmailAddress());
            shipperVO.setStreetLines(fromLocation.getAddressStreetlines());
            if (StringUtil.isNotEmpty(fromLocation.getAddressStreetlines2())){
                shipperVO.setStreetLines2(fromLocation.getAddressStreetlines2());
            }
            if (StringUtil.isNotEmpty(fromLocation.getAddressStreetlines3())){
                shipperVO.setStreetLines3(fromLocation.getAddressStreetlines3());
            }
            shipperVO.setPostalCode(fromLocation.getAddressPostalCode());
            shipperVO.setCountryCode(fromLocation.getAddressCountryCode());
            inputVO.setShipper(shipperVO);
        }
        if (dhlShipment!=null){
            RecipientVO recipientVO = new RecipientVO();
            recipientVO.setCity(dhlShipment.getShipToCity());
            recipientVO.setCompanyName(dhlShipment.getConsignee());
            recipientVO.setPersonName(dhlShipment.getPersonName());
            recipientVO.setPhoneNumber(dhlShipment.getContact());
            recipientVO.setEmailAddress(dhlShipment.getShipToEamilAddr());
            String shipToAddr = dhlShipment.getShipToAddr();
            if (shipToAddr.length()>35){
                shipToAddr = shipToAddr.substring(0,34);
            }
            recipientVO.setStreetLines(shipToAddr);
            if (StringUtil.isNotEmpty(dhlShipment.getShipToAddr2())
                    &&!"0".equals(dhlShipment.getShipToAddr2())){
                recipientVO.setStreetLines2(dhlShipment.getShipToAddr2());
            }
            if (StringUtil.isNotEmpty(dhlShipment.getShipToAddr3())
                    &&!"0".equals(dhlShipment.getShipToAddr3())){
                recipientVO.setStreetLines3(dhlShipment.getShipToAddr3());
            }
            if(dhlShipment.getPostalCode()!=null){
                recipientVO.setPostalCode(dhlShipment.getPostalCode());
            }else {
                recipientVO.setPostalCode("101731");
            }
            recipientVO.setCountryCode(dhlShipment.getShipToCountryCode());
            inputVO.setRecipient(recipientVO);
        }
        if(containerList!=null&&containerList.size()>0){
            List<Map<String,Object>> list = new ArrayList<>();
            for (Map<String,Object> map:containerList){
                Map<String,Object> container = new HashMap<>();
                container.put("barcode",shipment.getShipmentNo()+"/"+map.get("barcode"));
                container.put("length",map.get("length"));
                container.put("width",map.get("width"));
                container.put("height",map.get("height"));
                container.put("weight",new BigDecimal(map.get("weight").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                list.add(container);
            }
            inputVO.setPackageInfos(list);
        }
    }
}
