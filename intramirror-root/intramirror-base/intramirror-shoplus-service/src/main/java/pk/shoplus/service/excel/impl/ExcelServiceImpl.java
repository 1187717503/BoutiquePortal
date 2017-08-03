package pk.shoplus.service.excel.impl;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import pk.shoplus.common.Contants;
import pk.shoplus.service.excel.api.IExcelService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/3.
 */
public class ExcelServiceImpl implements IExcelService {

    private static Logger logger = Logger.getLogger(ExcelServiceImpl.class);

    @Override
    public String genExcel(List<List<String>> rows) throws IOException {
        FileOutputStream output = null;
        try {
            Date date = new Date();
            DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = format1.format(date);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet sheetOrder = hssfWorkbook.createSheet("order");


            for (int i = 0, iLen = rows.size(); i < iLen; i++) {
                List<String> cells = rows.get(i);
                HSSFRow row = sheetOrder.createRow(i);

                for (int j = 0, jLen = cells.size(); j < jLen; j++) {
                    row.createCell(j).setCellValue(cells.get(j));
                }
            }

            output = new FileOutputStream(Contants.download_excel_path + fileName + Contants.excel_suffix);
            hssfWorkbook.write(output);
            output.flush();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("gen excel file !!! error message : " + e.getMessage());
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return null;
    }

    @Override
    public String converMap(List<Map<String, Object>> mapList) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        // start init first row
        List<String> stringList = new ArrayList<>();
        //list
        stringList.add("Order Number");
        stringList.add("Status");
        stringList.add("Created Date");
        stringList.add("Total Qty");
        stringList.add("Sale Price Total");
        stringList.add("Shipping Total");
        stringList.add("Tax Total");
        stringList.add("Total Rmb");
        stringList.add("Consignee Address");
        stringList.add("Consignee Province");
        stringList.add("Consignee City");
        stringList.add("Consignee Area");
        stringList.add("Sender Name");
        stringList.add("Sender Phone");
        stringList.add("Consignee Name");
        stringList.add("Consignee Phone");
//        stringList.add("vendor_id");
        stringList.add("Geography");
        stringList.add("Buyer Name");
        stringList.add("Buyer Telephone");
        stringList.add("ContactI Info");
        //orderLineList
        stringList.add("Order Line Nbr");
        stringList.add("Product Name");
        stringList.add("Product Images");
        stringList.add("Category1");
        stringList.add("Category2");
        stringList.add("Category3");
        stringList.add("BrandID");
        stringList.add("ColorCode");
        stringList.add("Size");
        stringList.add("Boutique Sale Price");
        stringList.add("Supply Price");
        stringList.add("Sale Price Discount");
        stringList.add("Supply Price Discount");
        stringList.add("Sale Price");
        stringList.add("Sale Price(RMB)");
        stringList.add("Profit (RMB)");
        stringList.add("Shipping Fee");
        stringList.add("Tax");
        stringList.add("Boutique");
        stringList.add("Status");

        rows.add(stringList);
        // end init first row


        for (Map<String, Object> map : mapList) {
            List<Map<String, Object>> orderLineList = (List<Map<String, Object>>) map.get("orderLineList");
            for (Map<String, Object> orderLineMap : orderLineList) {
                stringList = new ArrayList<>();
                //list
                String order_num = map.get("order_num") == null ? "" : map.get("order_num").toString();

                String listStatus = map.get("status") == null ? "" : map.get("status").toString();
                if ("1".equals(listStatus)) {
                    listStatus = "PENDING";
                } else if ("2".equals(listStatus)) {
                    listStatus = "CONFIRMED";
                } else if ("3".equals(listStatus)) {
                    listStatus = "SHIPPED";
                } else if ("4".equals(listStatus)) {
                    listStatus = "DELIVERED";
                } else if ("5".equals(listStatus)) {
                    listStatus = "CLOSED";
                } else if ("6".equals(listStatus)) {
                    listStatus = "CANCELED";
                } else if ("-1".equals(listStatus)) {
                    listStatus = "UNPAYED";
                } else if ("-2".equals(listStatus)) {
                    listStatus = "INVALID";
                } else if ("-3".equals(listStatus)) {
                    listStatus = "PAYING";
                } else if ("-4".equals(listStatus)) {
                    listStatus = "REFUNDING";
                } else if ("-5".equals(listStatus)) {
                    listStatus = "PAYERROR";
                } else if ("MULTIPLE".equalsIgnoreCase(listStatus)) {
                    listStatus = "MULTIPLE";
                } else {
                    listStatus = "unknown";
                }
                String created_at = map.get("created_at") == null ? "" : map.get("created_at").toString();
                String total_sale_price_rmb = map.get("total_sale_price_rmb") == null ? "0" : map.get("total_sale_price_rmb").toString();
                total_sale_price_rmb = String.format("%.2f", Double.parseDouble(total_sale_price_rmb));

                String total_shipping_fee_rmb = map.get("total_shipping_fee_rmb") == null ? "0" : map.get("total_shipping_fee_rmb").toString();
                total_shipping_fee_rmb = String.format("%.2f", Double.parseDouble(total_shipping_fee_rmb));

                String total_qty = map.get("total_qty") == null ? "0" : map.get("total_qty").toString();
                total_qty = String.format("%.2f", Double.parseDouble(total_qty));

                String total_tax_rmb = map.get("total_tax_rmb") == null ? "0" : map.get("total_tax_rmb").toString();
                total_tax_rmb = String.format("%.2f", Double.parseDouble(total_tax_rmb));

                String total_rmb = map.get("total_rmb") == null ? "0" : map.get("total_rmb").toString();
                total_rmb = String.format("%.2f", Double.parseDouble(total_rmb));

                String user_rec_addr = map.get("user_rec_addr") == null ? "" : map.get("user_rec_addr").toString();
                String user_rec_province = map.get("user_rec_province") == null ? "" : map.get("user_rec_province").toString();
                String user_rec_city = map.get("user_rec_city") == null ? "" : map.get("user_rec_city").toString();
                String user_rec_area = map.get("user_rec_area") == null ? "" : map.get("user_rec_area").toString();
                String rec_name = map.get("rec_name") == null ? "" : map.get("rec_name").toString();
                String rec_mobile = map.get("rec_mobile") == null ? "" : map.get("rec_mobile").toString();
                String user_rec_name = map.get("user_rec_name") == null ? "" : map.get("user_rec_name").toString();
                String user_rec_mobile = map.get("user_rec_mobile") == null ? "" : map.get("user_rec_mobile").toString();
//                String vendor_id = map.get("vendor_id") == null ? "" : map.get("vendor_id").toString();
                String geography_name = map.get("geography_name") == null ? "" : map.get("geography_name").toString();
                String contact_person_name = map.get("contact_person_name") == null ? "" : map.get("contact_person_name").toString();
                String telephone = map.get("telephone") == null ? "" : map.get("telephone").toString();
                String contact_info = map.get("contact_info") == null ? "" : map.get("contact_info").toString();

                //orderLineList
                String order_line_num = orderLineMap.get("order_line_num") == null ? "" : orderLineMap.get("order_line_num").toString();
                String name = orderLineMap.get("name") == null ? "" : orderLineMap.get("name").toString();
                String cover_img = orderLineMap.get("cover_img") == null ? "" : orderLineMap.get("cover_img").toString();
                String categoryName = orderLineMap.get("categoryName") == null ? "" : orderLineMap.get("categoryName").toString();
                String[] category = categoryName.split(">");
                String brandID = orderLineMap.get("brandID") == null ? "" : orderLineMap.get("brandID").toString();
                String colorCode = orderLineMap.get("colorCode") == null ? "" : orderLineMap.get("colorCode").toString();
                String size = orderLineMap.get("size") == null ? "" : orderLineMap.get("size").toString();

                String price = orderLineMap.get("price") == null ? "0" : orderLineMap.get("price").toString();
                price = String.format("%.2f", Double.parseDouble(price));

                String in_price = orderLineMap.get("in_price") == null ? "0" : orderLineMap.get("in_price").toString();
                in_price = String.format("%.2f", Double.parseDouble(in_price));

                String sale_price_discount = orderLineMap.get("sale_price_discount") == null ? "0" : orderLineMap.get("sale_price_discount").toString();
//                sale_price_discount = String.format("%.0f", Double.parseDouble(sale_price_discount));

                String supply_price_discount = orderLineMap.get("supply_price_discount") == null ? "0" : orderLineMap.get("supply_price_discount").toString();
//                supply_price_discount = String.format("%.0f", Double.parseDouble(supply_price_discount));

                String sale_price = orderLineMap.get("sale_price") == null ? "0" : orderLineMap.get("sale_price").toString();
                sale_price = String.format("%.2f", Double.parseDouble(sale_price));

                String sale_price2 = orderLineMap.get("sale_price2") == null ? "0" : orderLineMap.get("sale_price2").toString();
                sale_price2 = String.format("%.2f", Double.parseDouble(sale_price2));

                String profit = orderLineMap.get("profit") == null ? "0" : orderLineMap.get("profit").toString();
                profit = String.format("%.2f", Double.parseDouble(profit));

                String shipping_fee = orderLineMap.get("shipping_fee") == null ? "0" : orderLineMap.get("shipping_fee").toString();
                shipping_fee = String.format("%.2f", Double.parseDouble(shipping_fee));

                String tax_fee = orderLineMap.get("tax_fee") == null ? "0" : orderLineMap.get("tax_fee").toString();
                tax_fee = String.format("%.2f", Double.parseDouble(tax_fee));

                String vendor_name = orderLineMap.get("vendor_name") == null ? "" : orderLineMap.get("vendor_name").toString();
                String orderLineListStatus = orderLineMap.get("status") == null ? "" : orderLineMap.get("status").toString();
                if ("1".equals(orderLineListStatus)) {
                    orderLineListStatus = "PENDING";
                } else if ("2".equals(orderLineListStatus)) {
                    orderLineListStatus = "CONFIRMED";
                } else if ("3".equals(orderLineListStatus)) {
                    orderLineListStatus = "SHIPPED";
                } else if ("4".equals(orderLineListStatus)) {
                    orderLineListStatus = "DELIVERED";
                } else if ("5".equals(orderLineListStatus)) {
                    orderLineListStatus = "CLOSED";
                } else if ("6".equals(orderLineListStatus)) {
                    orderLineListStatus = "CANCELED";
                } else if ("-1".equals(orderLineListStatus)) {
                    orderLineListStatus = "UNPAYED";
                } else if ("-2".equals(orderLineListStatus)) {
                    orderLineListStatus = "INVALID";
                } else if ("-3".equals(orderLineListStatus)) {
                    orderLineListStatus = "PAYING";
                } else if ("-4".equals(orderLineListStatus)) {
                    orderLineListStatus = "REFUNDING";
                } else if ("-5".equals(orderLineListStatus)) {
                    orderLineListStatus = "PAYERROR";
                } else if ("MULTIPLE".equalsIgnoreCase(listStatus)) {
                    listStatus = "MULTIPLE";
                } else {
                    listStatus = "unknown";
                }

                //list
                stringList.add(order_num);
                stringList.add(listStatus);
                stringList.add(created_at);
                stringList.add(total_qty);
                stringList.add(total_sale_price_rmb);
                stringList.add(total_shipping_fee_rmb);
                stringList.add(total_tax_rmb);
                stringList.add(total_rmb);
                stringList.add(user_rec_addr);
                stringList.add(user_rec_province);
                stringList.add(user_rec_city);
                stringList.add(user_rec_area);
                stringList.add(rec_name);
                stringList.add(rec_mobile);
                stringList.add(user_rec_name);
                stringList.add(user_rec_mobile);
//                stringList.add(vendor_id);
                stringList.add(geography_name);
                stringList.add(contact_person_name);
                stringList.add(telephone);
                stringList.add(contact_info);

                //orderLineList
                stringList.add(order_line_num);
                stringList.add(name);
                stringList.add(cover_img);
                for (int i = 0; i < 3; i++) {
                    stringList.add(category[i].toString());
                }
                stringList.add(brandID);
                stringList.add(colorCode);
                stringList.add(size);
                stringList.add(price);
                stringList.add(in_price);
                stringList.add(sale_price_discount);
                stringList.add(supply_price_discount);
                stringList.add(sale_price);
                stringList.add(sale_price2);
                stringList.add(profit);
                stringList.add(shipping_fee);
                stringList.add(tax_fee);
                stringList.add(vendor_name);
                stringList.add(orderLineListStatus);

                rows.add(stringList);
            }

        }
        return this.genExcel(rows);
    }

}
