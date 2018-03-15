package com.intramirror.web.util;

import com.intramirror.order.api.model.ShippingProvider;
import com.intramirror.order.api.model.SubShipment;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;
import java.util.Map;

/**
 * Created by caowei on 2018/3/14.
 */
public class ExcelUtil {
    public static Workbook createWorkBook(Map<String, Object> resultMap) {
        // 创建excel工作簿
        Workbook wb = new HSSFWorkbook();
        // 创建第一个sheet（页），并命名
        Sheet sheet = wb.createSheet();
        // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
        for (int i = 0; i < 5; i++) {
            sheet.setColumnWidth((short) i, (short) (35.7 * 150));
        }
        sheet.setColumnWidth((short) 1, (short) (65 * 150));

        // 创建两种单元格格式
        CellStyle cs = wb.createCellStyle();
        CellStyle cs2 = wb.createCellStyle();
        CellStyle cs3 = wb.createCellStyle();

        // 创建两种字体
        Font f = wb.createFont();
        Font f2 = wb.createFont();

        // 创建第一种字体样式（用于列名）
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // 创建第二种字体样式（用于值）
        f2.setFontHeightInPoints((short) 10);
        f2.setColor(IndexedColors.BLACK.getIndex());

        // 设置第一种单元格的样式（用于列名）
        cs.setFont(f);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setAlignment(CellStyle.ALIGN_LEFT);

        // 设置第二种单元格的样式（用于值）
        cs2.setFont(f2);
        cs2.setBorderLeft(CellStyle.BORDER_THIN);
        cs2.setBorderRight(CellStyle.BORDER_THIN);
        cs2.setBorderTop(CellStyle.BORDER_THIN);
        cs2.setBorderBottom(CellStyle.BORDER_THIN);
        cs2.setAlignment(CellStyle.ALIGN_LEFT);

        cs3.setFont(f);
        cs3.setBorderLeft(CellStyle.BORDER_THIN);
        cs3.setBorderRight(CellStyle.BORDER_THIN);
        cs3.setBorderTop(CellStyle.BORDER_THIN);
        cs3.setBorderBottom(CellStyle.BORDER_THIN);
        cs3.setAlignment(CellStyle.ALIGN_RIGHT);

        //设置列名
        // 创建第一行
        Row row0 = sheet.createRow(0);
        Cell cell01 = row0.createCell(0);
        cell01.setCellValue("Invoice From");
        cell01.setCellStyle(cs);
        Cell cell02 = row0.createCell(2);
        cell02.setCellValue("Shipping From");
        cell02.setCellStyle(cs);

        //第二行
        Row row1 = sheet.createRow(1);
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue(resultMap.get("ShipCompanyName")!=null?resultMap.get("ShipCompanyName").toString():"");
        cell11.setCellStyle(cs2);
        Cell cell12 = row1.createCell(2);
        cell12.setCellValue(resultMap.get("ShipVendorName")!=null?resultMap.get("ShipVendorName").toString():"");
        cell12.setCellStyle(cs2);

        //第三行
        Row row2 = sheet.createRow(2);
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue(resultMap.get("ShipCompanyName")!=null?resultMap.get("ShipCompanyName").toString():"");
        cell21.setCellStyle(cs2);
        Cell cell22 = row2.createCell(2);
        cell22.setCellValue(resultMap.get("ShipVendorName")!=null?resultMap.get("ShipVendorName").toString():"");
        cell22.setCellStyle(cs2);

        //第四行
        Row row3 = sheet.createRow(3);
        Cell cell31 = row3.createCell(0);
        cell31.setCellValue("VAT Number: "+(resultMap.get("VATNumber")!=null?resultMap.get("VATNumber").toString():""));
        cell31.setCellStyle(cs2);

        //第五行
        Row row4 = sheet.createRow(4);
        Cell cell41 = row4.createCell(0);
        cell41.setCellValue("");
        cell41.setCellStyle(cs2);

        //第六行
        Row row5 = sheet.createRow(5);
        Cell cell51 = row5.createCell(0);
        cell51.setCellValue("Date of Invoice: "+(resultMap.get("InvoiceDate")!=null?resultMap.get("InvoiceDate").toString():""));
        cell51.setCellStyle(cs2);

        //第七行
        Row row6 = sheet.createRow(6);
        Cell cell61 = row6.createCell(0);
        cell61.setCellValue("Invoice Number: "+(resultMap.get("InvoiceNumber")!=null?resultMap.get("InvoiceNumber").toString():""));
        cell61.setCellStyle(cs2);

        //第八行
        Row row7 = sheet.createRow(7);
        Cell cell71 = row7.createCell(0);
        cell71.setCellValue("");
        cell71.setCellStyle(cs2);

        // 创建第九行
        Row row8 = sheet.createRow(8);
        Cell cell81 = row8.createCell(0);
        cell81.setCellValue("Invoice To");
        cell81.setCellStyle(cs);
        Cell cell82 = row8.createCell(2);
        cell82.setCellValue("Deliver To");
        cell82.setCellStyle(cs);

        //第十行
        Row row9 = sheet.createRow(9);
        Cell cell91 = row9.createCell(0);
        cell91.setCellValue(resultMap.get("InvoiceTo")!=null?resultMap.get("InvoiceTo").toString():"");
        cell91.setCellStyle(cs2);
        Cell cell92 = row9.createCell(2);
        String name = "";
        Object o = resultMap.get("DeliverTo") != null ? resultMap.get("DeliverTo") : null;
        if(o!=null){
            if (o instanceof ShippingProvider){
                name = ((ShippingProvider) o).getName();
            }else if (o instanceof SubShipment){
                name = ((SubShipment) o).getConsignee();
            }
        }
        cell92.setCellValue(name);
        cell92.setCellStyle(cs2);

        //第十一行
        Row row10 = sheet.createRow(10);
        Cell cell101 = row10.createCell(0);
        cell101.setCellValue("");
        cell101.setCellStyle(cs2);

        //第十二行
        Row row11 = sheet.createRow(11);
        Cell cell111 = row11.createCell(0);
        cell111.setCellValue("Order No.");
        cell111.setCellStyle(cs);
        Cell cell112 = row11.createCell(1);
        cell112.setCellValue("Product Description");
        cell112.setCellStyle(cs);
        Cell cell113 = row11.createCell(2);
        cell113.setCellValue("Composition");
        cell113.setCellStyle(cs);
        Cell cell114 = row11.createCell(3);
        cell114.setCellValue("Made In");
        cell114.setCellStyle(cs);
        Cell cell115 = row11.createCell(4);
        cell115.setCellValue("Purchase Price");
        cell115.setCellStyle(cs);

        int i = 11;
        Object cartonList = resultMap.get("cartonList");
        if(cartonList!=null){
            List<Map<String, Object>> containerList = (List<Map<String, Object>>)cartonList;
            if(containerList.size()>0){
                for (Map<String,Object> map : containerList){
                    Row row = sheet.createRow(++i);
                    String brandName = map.get("brandName").toString();
                    String categoryName = map.get("categoryName").toString();
                    String orderLineNum = map.get("order_line_num").toString();
                    String brandID = map.get("brandID").toString();
                    String colorCode = map.get("colorCode").toString();
                    String size = map.get("size").toString();
                    String inPrice = map.get("in_price").toString();
                    Cell cell1 = row.createCell(0);
                    cell1.setCellValue(orderLineNum);
                    cell1.setCellStyle(cs2);
                    Cell cell2 = row.createCell(1);
                    cell2.setCellValue(brandName+" "+categoryName+"   "+brandID+"/"+colorCode+"/"+size);
                    cell2.setCellStyle(cs2);
                    Cell cell3 = row.createCell(2);
                    cell3.setCellValue("");
                    cell3.setCellStyle(cs2);
                    Cell cell4 = row.createCell(3);
                    cell4.setCellValue("");
                    cell4.setCellStyle(cs2);
                    Cell cell5 = row.createCell(4);
                    cell5.setCellValue("€"+inPrice);
                    cell5.setCellStyle(cs2);

                }
            }
        }

        Row rowi1 = sheet.createRow(i+1);
        Cell celli3 = rowi1.createCell(3);
        celli3.setCellValue("Total:"+resultMap.get("all_qty").toString());
        celli3.setCellStyle(cs3);
        Cell celli4 = rowi1.createCell(4);
        celli4.setCellValue("€"+resultMap.get("allTotal").toString());
        celli4.setCellStyle(cs3);

        Row rowi2 = sheet.createRow(i+2);
        Cell celli23 = rowi2.createCell(3);
        celli23.setCellValue("VAT:");
        celli23.setCellStyle(cs3);
        Cell celli24 = rowi2.createCell(4);
        celli24.setCellValue("€"+resultMap.get("VAT").toString());
        celli24.setCellStyle(cs3);

        Row rowi3 = sheet.createRow(i+3);
        Cell celli33 = rowi3.createCell(3);
        celli33.setCellValue("Grand Total:");
        celli33.setCellStyle(cs3);
        Cell celli34 = rowi3.createCell(4);
        celli34.setCellValue("€"+resultMap.get("GrandTotal").toString());
        celli34.setCellStyle(cs3);

        // 单元格合并
        // 四个参数分别是：起始行，起始列，结束行，结束列
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
        sheet.addMergedRegion(new CellRangeAddress(0,0,2,4));
        sheet.addMergedRegion(new CellRangeAddress(1,1,0,1));
        sheet.addMergedRegion(new CellRangeAddress(1,1,2,4));
        sheet.addMergedRegion(new CellRangeAddress(2,2,0,1));
        sheet.addMergedRegion(new CellRangeAddress(2,2,2,4));
        sheet.addMergedRegion(new CellRangeAddress(3,3,0,4));
        sheet.addMergedRegion(new CellRangeAddress(4,4,0,4));
        sheet.addMergedRegion(new CellRangeAddress(5,5,0,4));
        sheet.addMergedRegion(new CellRangeAddress(6,6,0,4));
        sheet.addMergedRegion(new CellRangeAddress(7,7,0,4));
        sheet.addMergedRegion(new CellRangeAddress(8,8,0,1));
        sheet.addMergedRegion(new CellRangeAddress(8,8,2,4));
        sheet.addMergedRegion(new CellRangeAddress(9,9,0,1));
        sheet.addMergedRegion(new CellRangeAddress(9,9,2,4));
        sheet.addMergedRegion(new CellRangeAddress(10,10,0,4));
        //sheet.getRow()
        return wb;
    }
}
