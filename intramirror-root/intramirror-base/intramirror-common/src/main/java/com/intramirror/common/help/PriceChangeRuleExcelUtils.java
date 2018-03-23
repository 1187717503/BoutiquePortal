package com.intramirror.common.help;

import java.io.File;
import java.io.FileInputStream;
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

       /* List<Map<String,Object>> brandNames = new ArrayList<>();
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

        PriceChangeRuleExcelUtils.genPriceExcel("IM Pricing Rule",brandNames,datas);*/
        List<Map<String,Object>> brandNames = new ArrayList<>();
        Map<String,Object> brand1 = new HashMap<>();
        brand1.put("english_name","032C");
        brand1.put("brand_id","944");
        brandNames.add(brand1);

       PriceChangeRuleExcelUtils.readRuleExcel("/Users/dingyifan/Downloads/workbook.xls",brandNames,"12");

    }

    private static final String[] categoryNames = new String[]{"Brand","Clothing","Shoes","Bags","Accessories","Clothing","Shoes","Bags","Accessories"};

    private static final String[] categoryIds = new String[]{"","1504","1506","1505","1507","1569","1584","1598","1608"};


    private static final String[] categoryKidsNames = new String[]{"Brand","Clothing","Shoes","Accessories","Clothing","Shoes","Accessories","Clothing","Shoes","Accessories"};

    private static final String[] categoryKidsIds = new String[]{"","1760","1761","1762","1766","1767","1768","1763","1764","1765"};

    public static String genPriceExcel(String excelName,List<Map<String,Object>> brands,List<Map<String, Object>> datas,String filePath) throws Exception {
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
                    if(data.get(cId) == null){
                        value = "100"; // 如果不存在  折扣为100 不打折
                    }else {
                        value = data.get(cId).toString();
                    }

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
        fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        return filePath;
    }

    public static List<Map<String,Object>> readRuleExcel(String filePath,List<Map<String,Object>> brandNames,String price_change_rule_id) throws Exception {

        List<Map<String,Object>> readExcelList = new ArrayList<>();
        HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(new File(filePath)));
        HSSFSheet sheet=workbook.getSheetAt(0);
        List<String> defaultValues = new ArrayList<>();
        for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
            HSSFRow row=sheet.getRow(j);

            String brand_id = getBrandId(brandNames,row.getCell(0).getStringCellValue());
            if(brand_id.equals("0")){
                for(int i=1;i<9;i++){
                    defaultValues.add(i,row.getCell(i).toString());
                }
            }
            String men_clothing = row.getCell(1).toString();
            if(StringUtils.isBlank(men_clothing)){
                men_clothing = defaultValues.get(1);
            }
            String men_shoes = row.getCell(2).toString();
            if(StringUtils.isBlank(men_shoes)){
                men_shoes = defaultValues.get(2);
            }
            String men_bags = row.getCell(3).toString();
            if(StringUtils.isBlank(men_bags)){
                men_bags = defaultValues.get(3);
            }
            String men_accessores = row.getCell(4).toString();
            if(StringUtils.isBlank(men_accessores)){
                men_accessores = defaultValues.get(4);
            }
            String women_clothing = row.getCell(5).toString();
            if(StringUtils.isBlank(women_clothing)){
                women_clothing = defaultValues.get(5);
            }
            String women_shoes = row.getCell(6).toString();
            if(StringUtils.isBlank(women_shoes)){
                women_shoes = defaultValues.get(6);
            }
            String women_bags = row.getCell(7).toString();
            if(StringUtils.isBlank(women_bags)){
                women_bags = defaultValues.get(7);
            }
            String women_accessores = row.getCell(8).toString();
            if(StringUtils.isBlank(women_accessores)){
                women_accessores = defaultValues.get(8);
            }
            /* Men */
            readExcelList.add(genMap(brand_id,men_clothing,1,price_change_rule_id,"1"));
            readExcelList.add(genMap(brand_id,men_shoes,2,price_change_rule_id,"1"));
            readExcelList.add(genMap(brand_id,men_bags,3,price_change_rule_id,"1"));
            readExcelList.add(genMap(brand_id,men_accessores,4,price_change_rule_id,"1"));
            /* Women */
            readExcelList.add(genMap(brand_id,women_clothing,5,price_change_rule_id,"1"));
            readExcelList.add(genMap(brand_id,women_shoes,6,price_change_rule_id,"1"));
            readExcelList.add(genMap(brand_id,women_bags,7,price_change_rule_id,"1"));
            readExcelList.add(genMap(brand_id,women_accessores,8,price_change_rule_id,"1"));
        }
        return readExcelList;
    }


    public static String genPriceExcelKids(String excelName,List<Map<String,Object>> brands,List<Map<String, Object>> datas,String filePath) throws Exception {
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
        row1cell1.setCellValue("Babies");
        row1cell1.setCellStyle(cellStyle);

        HSSFCell row1cell4 = row1.createCell(4);
        row1cell4.setCellValue("Boys");
        row1cell4.setCellStyle(cellStyle);

        HSSFCell row1cell7 = row1.createCell(7);
        row1cell7.setCellValue("Girls");
        row1cell7.setCellStyle(cellStyle);



        sheet.addMergedRegion(new CellRangeAddress(0,0,1,3));
        sheet.addMergedRegion(new CellRangeAddress(0,0,4,6));
        sheet.addMergedRegion(new CellRangeAddress(0,0,7,9));
        rowLength++;

        // 创建第二行数据
        HSSFRow row2 = sheet.createRow(rowLength);
        for(int i =0,iLen=categoryKidsNames.length;i<iLen;i++){
            HSSFCell cell = row2.createCell(i);
            cell.setCellValue(categoryKidsNames[i]);
            cell.setCellStyle(cellStyle);
        }
        rowLength++;

        // 创建折扣数据
        for(Map<String,Object> data : datas) {
            HSSFRow row = sheet.createRow(rowLength);

            for(int i=0,iLen=categoryKidsIds.length;i<iLen;i++){
                String cId = categoryKidsIds[i];
                String value = "";
                if(StringUtils.isBlank(cId)) {
                    value = data.get("english_name").toString();
                } else {
                    if(data.get(cId) == null){
                        value = "100"; // 如果不存在  折扣为100 不打折
                    }else {
                        value = data.get(cId).toString();
                    }

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
        fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        return filePath;
    }

    public static List<Map<String,Object>> readRuleExcelKids(String filePath,List<Map<String,Object>> brandNames,String price_change_rule_id) throws Exception {

        List<Map<String,Object>> readExcelList = new ArrayList<>();
        HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(new File(filePath)));
        HSSFSheet sheet=workbook.getSheetAt(0);
        List<String> defaultValues = new ArrayList<>();
        for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
            HSSFRow row=sheet.getRow(j);

            String brand_id = getBrandId(brandNames,row.getCell(0).getStringCellValue());
            if(brand_id.equals("0")){ //default brand
                for(int i = 1;i<10;i++)
                    defaultValues.add(i,row.getCell(i).toString());// 设置默认
            }

            String babies_clothing = row.getCell(1).toString();
            if(StringUtils.isBlank(babies_clothing)){
                babies_clothing = defaultValues.get(1);
            }
            String babies_shoes = row.getCell(2).toString();
            if(StringUtils.isBlank(babies_shoes)){
                babies_shoes = defaultValues.get(2);
            }
            String babies_accessores = row.getCell(3).toString();
            if(StringUtils.isBlank(babies_accessores)){
                babies_accessores = defaultValues.get(3);
            }
            String boys_clothing = row.getCell(4).toString();
            if(StringUtils.isBlank(boys_clothing)){
                boys_clothing = defaultValues.get(4);
            }
            String boys_shoes = row.getCell(5).toString();
            if(StringUtils.isBlank(boys_shoes)){
                boys_shoes = defaultValues.get(5);
            }
            String boys_accessores = row.getCell(6).toString();
            if(StringUtils.isBlank(boys_accessores)){
                boys_accessores = defaultValues.get(6);
            }
            String girls_clothing = row.getCell(7).toString();
            if(StringUtils.isBlank(girls_clothing)){
                girls_clothing = defaultValues.get(7);
            }
            String girls_shoes = row.getCell(8).toString();
            if(StringUtils.isBlank(girls_shoes)){
                girls_shoes = defaultValues.get(8);
            }
            String girls_accessores = row.getCell(9).toString();
            if(StringUtils.isBlank(girls_accessores)){
                girls_accessores = defaultValues.get(9);
            }

            /* Babies */
            readExcelList.add(genMap(brand_id,babies_clothing,1,price_change_rule_id,"2"));
            readExcelList.add(genMap(brand_id,babies_shoes,2,price_change_rule_id,"2"));
            readExcelList.add(genMap(brand_id,babies_accessores,3,price_change_rule_id,"2"));
            /*boys*/
            readExcelList.add(genMap(brand_id,boys_clothing,4,price_change_rule_id,"2"));
            readExcelList.add(genMap(brand_id,boys_shoes,5,price_change_rule_id,"2"));
            readExcelList.add(genMap(brand_id,boys_accessores,6,price_change_rule_id,"2"));

            /*girls*/
            readExcelList.add(genMap(brand_id,girls_clothing,7,price_change_rule_id,"2"));
            readExcelList.add(genMap(brand_id,girls_shoes,8,price_change_rule_id,"2"));
            readExcelList.add(genMap(brand_id,girls_accessores,9,price_change_rule_id,"2"));


        }
        return readExcelList;
    }

    private static Map<String,Object> genMap(String brand_id,String discount,int i,String price_change_rule_id,String type) {
        int dis = 100-(int)Double.parseDouble(discount);
        if(dis < 0 || dis > 100) {
            throw new RuntimeException("折扣设置错误");
        }
        Map<String,Object> map = new HashMap<>();
        if("1".equals(type)) {
            map.put("category_id", categoryIds[i]);
        }else if("2".equals(type)){
            map.put("category_id", categoryKidsIds[i]);
        }
        map.put("brand_id",brand_id);
        map.put("discount_percentage",dis);
        map.put("exception_flag","0");
        map.put("level","2");
        map.put("price_change_rule_id",price_change_rule_id);
        return map;
    }

    private static String getBrandId(List<Map<String,Object>> brands,String name) throws Exception {
        for(Map<String,Object> map : brands) {
            String eName = map.get("english_name").toString();
            if(StringUtils.trim(eName).equals(StringUtils.trim(name))) {
                return map.get("brand_id").toString();
            }
        }
        throw new RuntimeException("找不到这个品牌:" + name);
    }

    private static String[] getBrands(List<Map<String,Object>> brands) throws Exception {
        String[] strings = new String[brands.size()];
        for(int i =0,iLen=brands.size();i<iLen;i++) {
            strings[i] = brands.get(i).get("english_name").toString();
        }
        return strings;
    }

}
