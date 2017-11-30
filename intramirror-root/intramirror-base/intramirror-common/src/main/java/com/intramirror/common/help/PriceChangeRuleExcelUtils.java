package com.intramirror.common.help;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * Created by dingyifan on 2017/11/30.
 */
public class PriceChangeRuleExcelUtils {
    public static void main(String[] args) throws Exception {

        List<Map<String,Object>> brandNames = new ArrayList<>();
        List<Map<String,Object>> datas = new ArrayList<>();

        Map<String,Object> brand1 = new HashMap<>();
        brand1.put("english_name","032C");
        brand1.put("brand_id","944");
        brandNames.add(brand1);

        Map<String,Object> map1 = new HashMap<>();
        map1.put("1504",1);
        map1.put("1506",1);
        map1.put("1505",1);
        map1.put("1507",1);
        map1.put("1569",3);
        map1.put("1584",1);
        map1.put("1598",1);
        map1.put("1608",1);
        map1.put("english_name","Default");
        map1.put("brand_id","0");

        Map<String,Object> map2 = new HashMap<>();
        map2.put("1504",1);
        map2.put("1506",1);
        map2.put("1505",1);
        map2.put("1507",1);
        map2.put("1569",1);
        map2.put("1584",1);
        map2.put("1598",1);
        map2.put("1608",1);
        map2.put("english_name","Default");
        map2.put("brand_id","0");
        datas.add(map1);
        datas.add(map2);

        PriceChangeRuleExcelUtils.genPriceExcel("IM Pricing Rule",brandNames,datas);

    }

    private static final String[] categoryNames = new String[]{"Brand","Clothing","Shoes","Bags","Accessories","Clothing","Shoes","Bags","Accessories"};

    private static final String[] categoryIds = new String[]{"","1504","1506","1505","1507","1569","1584","1598","1608"};

    public static boolean genPriceExcel(String excelName,List<Map<String,Object>> brands,List<Map<String, Object>> datas){
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            // set sheet name
            int rowLength = 0;
            HSSFSheet sheet = workbook.createSheet(excelName);
            HSSFCellStyle cellStyle =workbook.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            // 创建第一行数据
            HSSFRow row1 = sheet.createRow(rowLength);
            HSSFCell row1cell0 = row1.createCell(0);
            row1cell0.setCellValue("");

            HSSFCell row1cell1 = row1.createCell(1);
            row1cell1.setCellValue("Men");
            row1cell1.setCellStyle(cellStyle);

            HSSFCell row1cell5 = row1.createCell(5);
            row1cell5.setCellValue("Women");
            row1cell5.setCellStyle(cellStyle);

            sheet.addMergedRegion(new CellRangeAddress(0,0,1,4));
            sheet.addMergedRegion(new CellRangeAddress(0,0,5,8));
            rowLength++;

            // 创建第二行数据
            HSSFRow row2 = sheet.createRow(rowLength);
            for(int i =0,iLen=categoryNames.length;i<iLen;i++){
                HSSFCell cell = row2.createCell(i);
                cell.setCellValue(categoryNames[i]);
                cell.setCellStyle(cellStyle);
            }
            rowLength++;

            // 创建折扣数据
            for(Map<String,Object> data : datas) {
                HSSFRow row = sheet.createRow(rowLength);

                for(int i=0,iLen=categoryIds.length;i<iLen;i++){
                    String cId = categoryIds[i];
                    String value = "";
                    if(StringUtils.isBlank(cId)) {
                        value = data.get("english_name").toString();
                    } else {
                        value = data.get(cId).toString();

                        value = ""+(100 - Integer.parseInt(value) );
                    }
                    HSSFCell cell = row.createCell(i);
                    cell.setCellValue(new HSSFRichTextString(value));
                    cell.setCellStyle(cellStyle);
                }

                rowLength++;
            }

            //设置品牌下拉菜单
            /*CellRangeAddressList regions = new CellRangeAddressList(2,500,0,0);
            String[] strs = getBrands(brands);
            DVConstraint constraint = DVConstraint.createExplicitListConstraint(strs);
            HSSFDataValidation data_validation = new HSSFDataValidation(regions,constraint);
            sheet.addValidationData(data_validation);*/

            //设置品牌下拉菜单
            String[] strs = getBrands(brands);
            HSSFSheet hidden = workbook.createSheet("hidden");
            HSSFCell cell = null;
            for (int i = 0, length= strs.length; i < length; i++) {
                String name = strs[i];
                HSSFRow row = hidden.createRow(i);
                cell = row.createCell(0);
                cell.setCellValue(name);
            }
            Name namedCell = workbook.createName();
            namedCell.setNameName("hidden");
            namedCell.setRefersToFormula("hidden!A1:A" + strs.length);
            DVConstraint constraint = DVConstraint.createFormulaListConstraint("hidden");
            CellRangeAddressList addressList = new CellRangeAddressList(2, 500, 0, 0);
            HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
            workbook.setSheetHidden(1, true);
            sheet.addValidationData(validation);

            FileOutputStream fileOut;
            fileOut = new FileOutputStream("/Users/dingyifan/Downloads/workbook.xls");
            workbook.write(fileOut);
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String[] getBrands(List<Map<String,Object>> brands) throws Exception {
        Map<String,Object> brand0 = new HashMap<>();
        brand0.put("english_name","Default");
        brand0.put("brand_id","0");
        brands.add(0,brand0);

        String[] strings = new String[brands.size()];
        for(int i =0,iLen=brands.size();i<iLen;i++) {
            strings[i] = brands.get(i).get("english_name").toString();
        }
        return strings;
    }

}
