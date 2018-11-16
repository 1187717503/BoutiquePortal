package com.intramirror.order.core.utils;

import com.google.gson.Gson;
import com.intramirror.common.help.StringUtils;
import com.intramirror.main.api.model.Tax;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.order.api.service.IVendorShipmentService;
import com.intramirror.order.api.service.IViewOrderLinesService;
import com.intramirror.order.api.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by 123 on 2018/5/22.
 */
public class ShipMailSendThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ShipMailSendThread.class);

    private MailSendManageService mailSendManageService;

    private ShipmentSendMailVO shipment;

    private TaxService taxService;

    public ShipMailSendThread(ShipmentSendMailVO shipment, MailSendManageService mailSendManageService,TaxService taxService) {
        this.shipment = shipment;
        this.mailSendManageService = mailSendManageService;
        this.taxService = taxService;

    }

    @Override
    public void run() {
        logger.info("----------Start to send ship mail.----------");
        logger.info("ShipMailSendThread shipmentNo={}", shipment.getShipmentNo());

        //根据AWB单号查询视图order_line_view获取数据
        logger.info("ShipMailSendThread 查询shipment下的订单列表");
        IViewOrderLinesService viewOrderLinesService = mailSendManageService.getViewOrderLinesService();
        List<ViewOrderLinesVO> shipmentList = viewOrderLinesService.getShipmentListByShipmentNo(shipment.getShipmentNo());
        if (shipment.getDestination() == null) {
            shipment.setDestination(shipmentList.get(0).getShip_to_country());
        }
        // 判断一下是否收件人名字、收件人手机是否重复超过3个，把这些订单显示在email的正文里
        Map<String, List<ViewOrderLinesVO>> consigneeOrderListMap = new HashMap<>();
        Map<String, List<ViewOrderLinesVO>> buyerContactOrderListMap = new HashMap<>();
        Set<String> orderLineNums = new HashSet<>();
        List<ViewOrderLinesVO> orders = new ArrayList<>();
        String awbNo = "";
        for (ViewOrderLinesVO viewOrderLinesVO : shipmentList) {
            if (StringUtils.isBlank(awbNo)) {
                awbNo = viewOrderLinesVO.getAwb_nbr();
            }
            String consignee = viewOrderLinesVO.getConsignee();
            List<ViewOrderLinesVO> nameOrderList = null;
            if(consigneeOrderListMap.containsKey(consignee)) {
                nameOrderList = consigneeOrderListMap.get(consignee);
            } else {
                nameOrderList = new ArrayList<>();
                consigneeOrderListMap.put(consignee, nameOrderList);
            }
            nameOrderList.add(viewOrderLinesVO);

            String consignee_mobile = viewOrderLinesVO.getConsignee_mobile();
            List<ViewOrderLinesVO> contactOrderList = null;
            if(buyerContactOrderListMap.containsKey(consignee_mobile)) {
                contactOrderList = buyerContactOrderListMap.get(consignee_mobile);
            } else {
                contactOrderList = new ArrayList<>();
                buyerContactOrderListMap.put(consignee_mobile, contactOrderList);
            }
            contactOrderList.add(viewOrderLinesVO);
        }
        for (Map.Entry<String, List<ViewOrderLinesVO>> entry : consigneeOrderListMap.entrySet()) {
            if (entry.getValue().size() >= 3) {
                List<ViewOrderLinesVO> vos = entry.getValue();
                for (ViewOrderLinesVO vo : vos) {
                    if (!orderLineNums.contains(vo.getOrder_line_num())) {
                        orderLineNums.add(vo.getOrder_line_num());
                        orders.add(vo);
                    }
                }
            }
        }
        for (Map.Entry<String, List<ViewOrderLinesVO>> entry : buyerContactOrderListMap.entrySet()) {
            if (entry.getValue().size() >= 3) {
                List<ViewOrderLinesVO> vos = entry.getValue();
                for (ViewOrderLinesVO vo : vos) {
                    if (!orderLineNums.contains(vo.getOrder_line_num())) {
                        orderLineNums.add(vo.getOrder_line_num());
                        orders.add(vo);
                    }
                }
            }
        }

        MailModelVO mailContent = new MailModelVO();
        //设置邮件标题，邮件标题为Shipment No. XXXXX + 【国家】/【Transit warehouse】
        mailContent.setSubject("Shipment No. " + shipment.getShipmentNo() +" AWB No. " + awbNo + "【" + shipment.getDestination() + "】");
        //设置邮件正文
        if (!orders.isEmpty()) {
            mailContent.setContent(getContent(orders));
        } else {
            mailContent.setContent("");
        }

        List<MailAttachmentVO> mailAttachmentVOs = generateMailAttachmentList(shipmentList);
        mailContent.setAttachments(mailAttachmentVOs);

        //设置发送对象
        //如果是发给质检仓的邮件
        if ("Transit Warehouse".equals(shipment.getDestination())) {
            mailContent.setToEmails(MailConfig.emailToWarehouse);
        } else {
            mailContent.setToEmails(MailConfig.emailTo);
        }

        try {
            logger.info("ShipMailSendThread 开始发送邮件 content={}", new Gson().toJson(mailContent));
            MailSendUtil.sendMail(mailContent);
            logger.info("ShipMailSendThread 邮件发送完成");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ShipMailSendThread 邮件发送失败", e);
        } finally {
            //删除附件文件
            List<MailAttachmentVO> attachments = mailContent.getAttachments();
            for (MailAttachmentVO attachment : attachments) {
                File file = new File(attachment.getFileUrl());
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
        }
        logger.info("----------Send mail finished.----------");
    }

    /**
     * 生成附件列表
     * @param shipmentList
     * @return
     */
    private List<MailAttachmentVO> generateMailAttachmentList(List<ViewOrderLinesVO> shipmentList) {
        logger.info("ShipMailSendThread 开始生成附件");
        //设置附件
        List<MailAttachmentVO> mailAttachmentVOs = new ArrayList<>();

        String path = "/opt/data/ship_excel/download/";
        String suffix = String.valueOf(System.currentTimeMillis()) + String.valueOf(Math.random() * 100);
        String orderLinesFileName = "OrderLines_"+ suffix + ".xls";
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String orderLinesFilePath = path + orderLinesFileName;

        logger.info("ShipMailSendThread 生成orderLines文件");
        generateShipmentExcel(shipmentList, orderLinesFilePath);
        mailAttachmentVOs.add(new MailAttachmentVO(orderLinesFileName, orderLinesFilePath));

        logger.info("获取boutique invoicing文件");
        List<MailAttachmentVO> boutiqueInvoiceList = getBoutiqueInvoicing(path, suffix);
        if (CollectionUtils.isNotEmpty(boutiqueInvoiceList)) {
            mailAttachmentVOs.addAll(boutiqueInvoiceList);
        }
        return mailAttachmentVOs;
    }

    /**
     * 获取boutique发送的invoicing文件
     */
    private List<MailAttachmentVO> getBoutiqueInvoicing(String path, String suffix) {
        List<MailAttachmentVO> result = new ArrayList<>();

        IVendorShipmentService vendorShipmentService = mailSendManageService.getVendorShipmentService();
        List<VendorInvoiceVO> vendorInvoicingList = vendorShipmentService.getVendorInvoicingList(shipment.getShipmentId());
        for (VendorInvoiceVO vendorInvoiceVO : vendorInvoicingList) {
            String invoiceUrl = vendorInvoiceVO.getInvoiceUrl();
            String boutiqueShipmentId = vendorInvoiceVO.getBoutiqueShipmentId() == null ? "" : "_" + vendorInvoiceVO.getBoutiqueShipmentId().toString();
            String fileSuffix = invoiceUrl.substring(invoiceUrl.lastIndexOf("."));
            String fileName = vendorInvoiceVO.getVendorName() + boutiqueShipmentId + "_" + suffix + fileSuffix;
            String targetPath = path + fileName;
            //将文件下载到服务器
            downloadFile(invoiceUrl, targetPath);
            result.add(new MailAttachmentVO(fileName, targetPath));
        }
        return result;
    }

    private void downloadFile(String sourceUrl, String targetPath) {
        OutputStream os = null;
        InputStream is = null;
        try {
            //获取网络资源
            URL url = new URL(sourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);

            File file = new File(targetPath);
            is = conn.getInputStream();
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            logger.error("File download failed!", e);
            throw new RuntimeException("File download failed!");
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != os) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("文件下载失败！", e);
            }
        }
    }

    private void generateShipmentExcel(List<ViewOrderLinesVO> shipmentList, String filePath) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        int rowLength = 0;
        HSSFSheet sheet = workbook.createSheet("导出文件");

        String[] excelHeaders = new String[]{"awb_num", "vendor_name", "stock_location", "order_line_num","order_num","shipping_fee", "designer_id", "brand", "l1_category", "l2_category", "l3_category", "color_code", "size", "product_name", "buyer_name", "buyer_contact", "ship_to_address", "ship_to_province", "ship_to_city", "ship_to_area", "ship_to_country", "zip_code", "consignee", "consignee_mobile", "container_nr", "height", "length", "width", "weight", "shipment_nr", "shipment_status", "created_at_datetime", "confirmed_at_datetime", "packed_at_datetime", "shipped_at(day)", "qty", "retail_price", "boutique_discount_off", "boutique_price"};

        // 创建表头
        HSSFRow row1 = sheet.createRow(rowLength);
        for (int i = 0, iLen = excelHeaders.length; i < iLen; i++) {
            HSSFCell cell = row1.createCell(i);
            cell.setCellValue(excelHeaders[i]);
        }

        rowLength++;
        HSSFRow row = null;
        HSSFCell cell = null;
        for (ViewOrderLinesVO order : shipmentList) {
            row = sheet.createRow(rowLength);
            Tax tax = taxService.calculateDiscountTax(order.getOrder_line_num());
            double discountTax;
            if (tax != null){
                discountTax = tax.getTaxRate().doubleValue() * 100 + 100;
            }else {
                discountTax = 100;
            }
            String boutiqueDiscountOff = new BigDecimal(100).subtract(order.getBoutique_price().multiply(new BigDecimal(discountTax)).divide(order.getRetail_price(), 4, BigDecimal.ROUND_HALF_DOWN)).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
            String[] values = {
                    transforNullValue(order.getAwb_nbr()),
                    transforNullValue(order.getVendor_name()),
                    transforNullValue(order.getStock_location()),
                    transforNullValue(order.getOrder_line_num()),
                    transforNullValue(order.getOrder_num()),
                    transforNullValue((order.getShipping_fee().multiply(order.getCurrent_rate())).setScale(2,BigDecimal.ROUND_HALF_UP)),
                    transforNullValue(order.getDesigner_id()),
                    transforNullValue(order.getBrand()),
                    transforNullValue(order.getL1_category()),
                    transforNullValue(order.getL2_category()),
                    transforNullValue(order.getL3_category()),
                    transforNullValue(order.getColor_code()),
                    transforNullValue(order.getSize()),
                    transforNullValue(order.getProduct_name()),
                    transforNullValue(order.getBuyer_name()),
                    transforNullValue(order.getBuyer_contact()),
                    transforNullValue(order.getShip_to_address()),
                    transforNullValue(order.getShip_to_province()),
                    transforNullValue(order.getShip_to_city()),
                    transforNullValue(order.getShip_to_area()),
                    transforNullValue(order.getShip_to_country()),
                    transforNullValue(order.getZip_code()),
                    transforNullValue(order.getConsignee()),
                    transforNullValue(order.getConsignee_mobile()),
                    transforNullValue(order.getContainer_nr()),
                    transforNullValue(order.getHeight()),
                    transforNullValue(order.getLength()),
                    transforNullValue(order.getWidth()),
                    transforNullValue(order.getWeight()),
                    transforNullValue(order.getShipment_nr()),
                    transforNullValue(order.getShipment_status()),
                    transforNullValue(order.getCreated_at_datetime()),
                    transforNullValue(order.getConfirmed_at_datetime()),
                    transforNullValue(order.getPacked_at_datetime()),
                    transforNullValue(order.getShipped_at_datetime()),
                    transforNullValue(order.getQty()),
                    transforNullValue(order.getRetail_price() == null ? null :order.getRetail_price().setScale(2, BigDecimal.ROUND_HALF_UP)),
                    transforNullValue(boutiqueDiscountOff),
                    transforNullValue(order.getBoutique_price() == null ? null :order.getBoutique_price().setScale(2, BigDecimal.ROUND_HALF_UP))
            };
            //将数据放到excel中
            for (int i = 0; i < excelHeaders.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(values[i]);
            }
            rowLength++;
        }
        //宽度自适应
        for (int i = 0; i < excelHeaders.length; i++) {
            sheet.autoSizeColumn(i, true);
        }

        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String transforNullValue(Object obj) {
        if(obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

    private static String getContent(List<ViewOrderLinesVO> orders) {
        StringBuilder content = new StringBuilder();

        content.append("<div style='margin: 0 auto;width:400px;background: #fff;font-family:Roboto'>");
        content.append("<table width='100%' cellpadding='0' cellspacing='0' border='0'>");
        for (ViewOrderLinesVO order : orders) {
            content.append("<tr>");
            content.append("<td align='left'>").append(order.getOrder_line_num()).append("</td>");
            content.append("<td align='left'>").append(order.getBuyer_name()).append("</td>");
            content.append("<td align='left'>").append(order.getBuyer_contact()).append("</td>");
            content.append("<td align='left'>").append(order.getShip_to_address()).append("</td>");
            content.append("<td align='left'>").append(order.getShip_to_province()).append("</td>");
            content.append("<td align='left'>").append(order.getShip_to_city()).append("</td>");
            content.append("<td align='left'>").append(order.getShip_to_area()).append("</td>");
            content.append("<td align='left'>").append(order.getShip_to_country()).append("</td>");
            content.append("<td align='left'>").append(order.getZip_code()).append("</td>");
            content.append("<td align='left'>").append(order.getConsignee()).append("</td>");
            content.append("<td align='left'>").append(order.getConsignee_mobile()).append("</td>");
            content.append("</tr>");
        }
        content.append("</table>");
        content.append("</div>");
        return content.toString();
    }
}
