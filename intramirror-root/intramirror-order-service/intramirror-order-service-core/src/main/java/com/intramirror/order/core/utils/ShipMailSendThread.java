package com.intramirror.order.core.utils;

import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.order.api.service.IViewOrderLinesService;
import com.intramirror.order.api.vo.MailAttachmentVO;
import com.intramirror.order.api.vo.MailModelVO;
import com.intramirror.order.api.vo.ShipmentSendMailVO;
import com.intramirror.order.api.vo.ViewOrderLinesVO;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by 123 on 2018/5/22.
 */
public class ShipMailSendThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ShipMailSendThread.class);

    private IViewOrderLinesService viewOrderLinesService;

    private ShipmentSendMailVO shipment;

    public ShipMailSendThread(ShipmentSendMailVO shipment, IViewOrderLinesService viewOrderLinesService) {
        this.shipment = shipment;
        this.viewOrderLinesService = viewOrderLinesService;

    }

    @Override
    public void run() {
        logger.info("----------Start to send ship mail.----------");
        logger.info("ShipMailSendThread shipmentNo={}", shipment.getShipmentNo());

        //根据AWB单号查询视图order_line_view获取数据
        logger.info("ShipMailSendThread 查询shipment下的订单列表");
        List<ViewOrderLinesVO> shipmentList = viewOrderLinesService.getShipmentListByShipmentNo(shipment.getShipmentNo());
        if (shipment.getDestination() == null) {
            shipment.setDestination(shipmentList.get(0).getShip_to_country());
        }
        // 判断一下是否收件人名字、收件人手机是否重复超过3个，把这些订单显示在email的正文里
        Map<String, List<ViewOrderLinesVO>> consigneeOrderListMap = new HashMap<>();
        Map<String, List<ViewOrderLinesVO>> buyerContactOrderListMap = new HashMap<>();
        Set<String> orderLineNums = new HashSet<>();
        List<ViewOrderLinesVO> orders = new ArrayList<>();
        for (ViewOrderLinesVO viewOrderLinesVO : shipmentList) {
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

        logger.info("ShipMailSendThread 设置文件目录路径");
        String dateStr = DateUtils.getStrDate(new Date(), "yyyyMMddHHmmss");
        String fileName = dateStr + ".xls";
        String path = "/opt/data/ship_excel/download/";
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String filePath = path + fileName;

        logger.info("ShipMailSendThread 生成shipment文件");
        generateShipmentExcel("导出文件", shipmentList, filePath);

        MailModelVO mailContent = new MailModelVO();
        //设置邮件标题，邮件标题为Shipment No. XXXXX + 【国家】/【Transit warehouse】
        mailContent.setSubject("Shipment No. " + shipment.getShipmentNo() + "【" + shipment.getDestination() + "】");
        //设置邮件正文
        if (!orders.isEmpty()) {
            mailContent.setContent(getContent(orders));
        } else {
            mailContent.setContent("");
        }
        //设置附件
        List<MailAttachmentVO> mailAttachmentVOs = new ArrayList<>();
        MailAttachmentVO attachmentVO = new MailAttachmentVO();
        attachmentVO.setFileName(fileName);
        attachmentVO.setFileUrl(filePath);
        mailAttachmentVOs.add(attachmentVO);
        mailContent.setAttachments(mailAttachmentVOs);

        try {
            logger.info("ShipMailSendThread 开始发送邮件 content={}", new Gson().toJson(mailContent));
            MailSendUtil.sendMail(mailContent);
            logger.info("ShipMailSendThread 邮件发送完成");
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("ShipMailSendThread 邮件发送失败", e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("ShipMailSendThread 邮件发送失败", e);
        }
        logger.info("----------Send mail finished.----------");
    }

    private String generateShipmentExcel(String excelName, List<ViewOrderLinesVO> shipmentList, String filePath) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        int rowLength = 0;
        HSSFSheet sheet = workbook.createSheet(excelName);

        String[] excelHeaders = new String[]{"vendor_name", "stock_location", "order_line_num", "designer_id", "brand", "l1_category", "l2_category", "l3_category", "color_code", "size", "product_name", "buyer_name", "buyer_contact", "ship_to_address", "ship_to_province", "ship_to_city", "ship_to_area", "ship_to_country", "zip_code", "consignee", "consignee_mobile", "container_nr", "height", "length", "width", "weight", "shipment_nr", "shipment_status", "created_at_datetime", "confirmed_at_datetime", "packed_at_datetime", "shipped_at(day)", "qty", "retail_price", "boutique_discount_off", "boutique_price"};

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
            BigDecimal boutiquePrice = new BigDecimal(order.getBoutique_price());
            BigDecimal retailPrice = new BigDecimal(order.getRetail_price());
            String boutiqueDiscountOff = new BigDecimal(100).subtract(boutiquePrice.multiply(new BigDecimal(122)).divide(retailPrice, 4, BigDecimal.ROUND_HALF_DOWN)).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
            String[] values = {
                    transforNullValue(order.getVendor_name()),
                    transforNullValue(order.getStock_location()),
                    transforNullValue(order.getOrder_line_num()),
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
                    transforNullValue(order.getRetail_price()),
                    transforNullValue(boutiqueDiscountOff),
                    transforNullValue(order.getBoutique_price())
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
        return filePath;
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
