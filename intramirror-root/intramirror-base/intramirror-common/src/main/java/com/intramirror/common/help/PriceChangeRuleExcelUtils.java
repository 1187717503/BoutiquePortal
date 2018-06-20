package com.intramirror.common.help;

import com.intramirror.common.enums.CategoryTypeEnum;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> brandNames = new ArrayList<>();
        Map<String, Object> brand1 = new HashMap<>();
        brand1.put("english_name", "Default");
        brand1.put("brand_id", "0");
        brandNames.add(brand1);
        Map<String, Object> brand2 = new HashMap<>();
        brand2.put("english_name", "Free City Kids");
        brand2.put("brand_id", "3636");
        brandNames.add(brand2);
        Map<String, Object> brand3 = new HashMap<>();
        brand3.put("english_name", "Frency & Mercury Kids");
        brand3.put("brand_id", "3637");
        brandNames.add(brand3);
        Map<String, Object> brand4 = new HashMap<>();
        brand4.put("english_name", "Frescobol Carioca Kids");
        brand4.put("brand_id", "3638");
        brandNames.add(brand4);

        PriceChangeRuleExcelUtils.readRuleExcelKids("/Users/juzhongzheng/Desktop/20180323165033.xls", brandNames, "12");

    }

    private static final String[] categoryNames = new String[] { "Brand", "Clothing", "Shoes", "Bags", "Accessories", "Clothing", "Shoes", "Bags",
            "Accessories" };

    private static final String[] categoryIds = new String[] { "", "1504", "1506", "1505", "1507", "1569", "1584", "1598", "1608" };

    private static final String[] categoryKidsNames = new String[] { "Brand", "Clothing", "Shoes", "Accessories", "Clothing", "Shoes", "Accessories",
            "Clothing", "Shoes", "Accessories" };

    private static final String[] categoryKidsIds = new String[] { "", "1760", "1761", "1762", "1766", "1767", "1768", "1763", "1764", "1765" };

    private static final String HIDDEN_SHEET_NAME = "dropdownHidden";
    private static final String PRICE_RULE_SHEET_NAME = "Pricing Rule";
    private static final String CATEGORY_BRAND_SHEET_NAME = "Brand Category Rule";
    private static final String PRODUCT_GROUP_SHEET_NAME = "Product Group Rule";
    private static final String PRODUCT_SHEET_NAME = "Product Rule";
    private static final String HIDDEN_DROPDOWN_BRAND = "hiddenBrand";
    private static final String HIDDEN_DROPDOWN_BRAND_WITHOUT_DEFAULT = "hiddenBrandWithoutDefault";
    private static final String HIDDEN_DROPDOWN_CATEGORY = "hiddenCategory";
    private static final String HIDDEN_DROPDOWN_PRODUCT_GROUP = "hiddenProductGroup";

    public static String genPriceExcel(String type, Map<String, List<String>> baseDataMap, List<Map<String, Object>> datas, String filePath,
                                       List<Map<String, Object>> categoryBrandMaps, List<Map<String, Object>> productGroupMaps, List<Map<String, Object>> productMaps) throws Exception {
        List<String> brandList = baseDataMap.get("brandList");
        List<String> categoryNodeNameList = baseDataMap.get("categoryNodeNameList");
        List<String> productGroupList = baseDataMap.get("productGroupList");

        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createSheet(PRICE_RULE_SHEET_NAME);
        workbook.createSheet(CATEGORY_BRAND_SHEET_NAME);
        workbook.createSheet(PRODUCT_GROUP_SHEET_NAME);
        workbook.createSheet(PRODUCT_SHEET_NAME);
        workbook.createSheet(HIDDEN_SHEET_NAME);

        //设置品牌下拉菜单
        generateDropDown(workbook, brandList, HIDDEN_DROPDOWN_BRAND, 0);
        generateDropDown(workbook, categoryNodeNameList, HIDDEN_DROPDOWN_CATEGORY, 1);
        generateDropDown(workbook, productGroupList, HIDDEN_DROPDOWN_PRODUCT_GROUP, 2);

        if (CategoryTypeEnum.ADULT.getCategoryType().equals(type)) {
            generateAdultPriceRuleSheet(workbook, datas);
        } else if (CategoryTypeEnum.KIDS.getCategoryType().equals(type)) {
            generateKidsPriceRuleSheet(workbook, datas);
        }
        generateBrandCategoryRuleSheet(workbook, categoryBrandMaps);
        generateProductGroupRuleSheet(workbook, productGroupMaps);
        generateProductRuleSheet(workbook, productMaps);

        //隐藏下拉框内容sheet
        workbook.setSheetHidden(4, true);
        FileOutputStream fileOut;
        fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        return filePath;
    }

    private static void generateDropDown(HSSFWorkbook workbook, List<String> data, String dropdownName, int col) {
        String[] strs = data.toArray(new String[data.size()]);
        HSSFSheet sheet = workbook.getSheet(HIDDEN_SHEET_NAME);
        HSSFCell cell = null;
        for (int i = 0, length = strs.length; i < length; i++) {
            String name = strs[i];
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            cell = row.getCell(col);
            if (cell == null) {
                cell = row.createCell(col);
            }
            cell.setCellValue(name);
        }

        String startName = new CellReference(HIDDEN_SHEET_NAME, 0, col, true, true).formatAsString();
        String endName = new CellReference(data.size(), col, true, true).formatAsString();
        String refersToFormula = startName + ":" + endName;
        Name name = workbook.createName();
        name.setNameName(dropdownName);
        name.setRefersToFormula(refersToFormula);
        if (HIDDEN_DROPDOWN_BRAND.equals(dropdownName)) {
            String startName1 = new CellReference(HIDDEN_SHEET_NAME, 1, col, true, true).formatAsString();
            String refersToFormula1 = startName1 + ":" + endName;
            Name name1 = workbook.createName();
            name1.setNameName(HIDDEN_DROPDOWN_BRAND_WITHOUT_DEFAULT);
            name1.setRefersToFormula(refersToFormula1);
        }
    }

    private static void generateAdultPriceRuleSheet(HSSFWorkbook workbook, List<Map<String, Object>> datas) {
        // set sheet name
        int rowLength = 0;
        HSSFSheet sheet = workbook.getSheet(PRICE_RULE_SHEET_NAME);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
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

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 4));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 8));

        rowLength++;

        // 创建第二行数据
        HSSFRow row2 = sheet.createRow(rowLength);
        for (int i = 0, iLen = categoryNames.length; i < iLen; i++) {
            HSSFCell cell = row2.createCell(i);
            cell.setCellValue(categoryNames[i]);
            cell.setCellStyle(cellStyle);
        }
        rowLength++;

        // 创建折扣数据
        for (Map<String, Object> data : datas) {
            HSSFRow row = sheet.createRow(rowLength);

            for (int i = 0, iLen = categoryIds.length; i < iLen; i++) {
                String cId = categoryIds[i];
                String value = "";
                if (StringUtils.isBlank(cId)) {
                    value = data.get("english_name").toString();
                } else {
                    value = getDiscountForView(data.get(cId).toString());
                }
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(new HSSFRichTextString(value));
                cell.setCellStyle(cellStyle);
            }

            rowLength++;
        }

        //设置品牌下拉菜单
        DVConstraint brandDVConstraint = DVConstraint.createFormulaListConstraint(HIDDEN_DROPDOWN_BRAND);
        CellRangeAddressList brandAddressList = new CellRangeAddressList(2, 500, 0, 0);
        HSSFDataValidation brandValidation = new HSSFDataValidation(brandAddressList, brandDVConstraint);
        sheet.addValidationData(brandValidation);
    }

    private static String getDiscountForView(String discount) {
        return String.valueOf(100 - Integer.parseInt(discount == null ? "100" : discount));
    }

    private static void generateKidsPriceRuleSheet(HSSFWorkbook workbook, List<Map<String, Object>> datas) {
        // set sheet name
        int rowLength = 0;
        HSSFSheet sheet = workbook.getSheet(PRICE_RULE_SHEET_NAME);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
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

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 9));
        rowLength++;

        // 创建第二行数据
        HSSFRow row2 = sheet.createRow(rowLength);
        for (int i = 0, iLen = categoryKidsNames.length; i < iLen; i++) {
            HSSFCell cell = row2.createCell(i);
            cell.setCellValue(categoryKidsNames[i]);
            cell.setCellStyle(cellStyle);
        }
        rowLength++;

        // 创建折扣数据
        for (Map<String, Object> data : datas) {
            HSSFRow row = sheet.createRow(rowLength);

            for (int i = 0, iLen = categoryKidsIds.length; i < iLen; i++) {
                String cId = categoryKidsIds[i];
                String value = "";
                if (StringUtils.isBlank(cId)) {
                    value = data.get("english_name").toString();
                } else {
                    value = getDiscountForView(data.get(cId).toString());
                }
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(new HSSFRichTextString(value));
                cell.setCellStyle(cellStyle);
            }

            rowLength++;
        }

        //设置品牌下拉菜单
        DVConstraint brandDVConstraint = DVConstraint.createFormulaListConstraint(HIDDEN_DROPDOWN_BRAND);
        CellRangeAddressList brandAddressList = new CellRangeAddressList(2, 500, 0, 0);
        HSSFDataValidation brandValidation = new HSSFDataValidation(brandAddressList, brandDVConstraint);
        sheet.addValidationData(brandValidation);
    }

    private static void generateBrandCategoryRuleSheet(HSSFWorkbook workbook, List<Map<String, Object>> categoryBrandMaps) {
        int rowLength = 0;
        HSSFSheet sheet = workbook.getSheet(CATEGORY_BRAND_SHEET_NAME);
        sheet.setColumnWidth(1, 30 * 256);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        String[] title = new String[]{"Brand", "Category", "Discount"};

        HSSFRow row1 = sheet.createRow(rowLength);
        for (int i = 0; i < title.length; i++) {
            HSSFCell row1cell = row1.createCell(i);
            row1cell.setCellValue(title[i]);
            row1cell.setCellStyle(cellStyle);
        }
        rowLength++;

        // 创建折扣数据
        for (Map<String, Object> data : categoryBrandMaps) {
            HSSFRow row = sheet.createRow(rowLength);
            HSSFCell brandCell = row.createCell(0);
            brandCell.setCellValue(data.get("english_name").toString());
            brandCell.setCellStyle(cellStyle);
            HSSFCell categoryCell = row.createCell(1);
            String categoryPath = data.get("c1Name").toString() + "=>" + data.get("c2Name").toString() + "=>" + data.get("cName").toString();
            categoryCell.setCellValue(categoryPath);
            categoryCell.setCellStyle(cellStyle);
            HSSFCell discountCell = row.createCell(2);
            discountCell.setCellValue(new HSSFRichTextString(getDiscountForView(data.get("discount_percentage").toString())));
            discountCell.setCellStyle(cellStyle);
            rowLength++;
        }

        //设置品牌下拉菜单
        DVConstraint brandDVConstraint = DVConstraint.createFormulaListConstraint(HIDDEN_DROPDOWN_BRAND_WITHOUT_DEFAULT);
        CellRangeAddressList brandAddressList = new CellRangeAddressList(1, 500, 0, 0);
        HSSFDataValidation brandValidation = new HSSFDataValidation(brandAddressList, brandDVConstraint);
        sheet.addValidationData(brandValidation);

        //设置类目下拉菜单
        DVConstraint categoryDVConstraint = DVConstraint.createFormulaListConstraint(HIDDEN_DROPDOWN_CATEGORY);
        CellRangeAddressList categoryAddressList = new CellRangeAddressList(1, 500, 1, 1);
        HSSFDataValidation categoryValidation = new HSSFDataValidation(categoryAddressList, categoryDVConstraint);
        sheet.addValidationData(categoryValidation);
    }

    private static void generateProductGroupRuleSheet(HSSFWorkbook workbook, List<Map<String, Object>> productGroupMaps) {
        int rowLength = 0;
        HSSFSheet sheet = workbook.getSheet(PRODUCT_GROUP_SHEET_NAME);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        String[] title = new String[]{"Product Group", "Discount"};

        HSSFRow row1 = sheet.createRow(rowLength);
        for (int i = 0; i < title.length; i++) {
            HSSFCell row1cell = row1.createCell(i);
            row1cell.setCellValue(title[i]);
            row1cell.setCellStyle(cellStyle);
        }
        rowLength++;

        // 创建折扣数据
        for (Map<String, Object> data : productGroupMaps) {
            HSSFRow row = sheet.createRow(rowLength);
            HSSFCell productGroupCell = row.createCell(0);
            productGroupCell.setCellValue(data.get("name").toString());
            productGroupCell.setCellStyle(cellStyle);
            HSSFCell discountCell = row.createCell(1);
            discountCell.setCellValue(new HSSFRichTextString(getDiscountForView(data.get("discount_percentage").toString())));
            discountCell.setCellStyle(cellStyle);
            rowLength++;
        }

        //设置商品组下拉菜单
        DVConstraint productGroupDVConstraint = DVConstraint.createFormulaListConstraint(HIDDEN_DROPDOWN_PRODUCT_GROUP);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 500, 0, 0);
        HSSFDataValidation validation = new HSSFDataValidation(addressList, productGroupDVConstraint);
        sheet.addValidationData(validation);

        sheet.autoSizeColumn((short) 0); //调整第一列宽度
        sheet.autoSizeColumn((short) 1); //调整第二列宽度
    }

    private static void generateProductRuleSheet(HSSFWorkbook workbook, List<Map<String, Object>> productMaps) {
        int rowLength = 0;
        HSSFSheet sheet = workbook.getSheet(PRODUCT_SHEET_NAME);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        String[] title = new String[]{"Designer Id", "Color Code", "Discount"};

        HSSFRow row1 = sheet.createRow(rowLength);
        for (int i = 0; i < title.length; i++) {
            HSSFCell row1cell = row1.createCell(i);
            row1cell.setCellValue(title[i]);
            row1cell.setCellStyle(cellStyle);
        }
        rowLength++;

        // 创建折扣数据
        for (Map<String, Object> data : productMaps) {
            HSSFRow row = sheet.createRow(rowLength);
            HSSFCell designerIdCell = row.createCell(0);
            designerIdCell.setCellValue(data.get("designer_id").toString());
            designerIdCell.setCellStyle(cellStyle);
            HSSFCell colorCodeCell = row.createCell(1);
            colorCodeCell.setCellValue(data.get("color_code").toString());
            colorCodeCell.setCellStyle(cellStyle);
            HSSFCell discountCell = row.createCell(2);
            discountCell.setCellValue(new HSSFRichTextString(getDiscountForView(data.get("discount_percentage").toString())));
            discountCell.setCellStyle(cellStyle);
            rowLength++;
        }
        sheet.autoSizeColumn((short) 0); //调整第一列宽度
        sheet.autoSizeColumn((short) 1); //调整第二列宽度
        sheet.autoSizeColumn((short) 2); //调整第二列宽度
    }

    public static Map<String, List<Map<String, Object>>> readRuleExcel(String filePath, Map<String, List<Map<String, Object>>> baseDataMap,
                                                                       String price_change_rule_id, String type) throws Exception {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        List<Map<String, Object>> brandMapList = baseDataMap.get("brandMap");
        List<Map<String, Object>> categoryMapList = baseDataMap.get("categoryMap");
        List<Map<String, Object>> productGroupMapList = baseDataMap.get("productGroupMap");

        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
        HSSFSheet priceRuleSheet = workbook.getSheet(PRICE_RULE_SHEET_NAME);
        List<Map<String, Object>> priceRuleList = new ArrayList<>();
        if (CategoryTypeEnum.ADULT.getCategoryType().equals(type)) {
            priceRuleList = readAdultPriceRuleSheet(priceRuleSheet, brandMapList, price_change_rule_id);
        } else if (CategoryTypeEnum.KIDS.getCategoryType().equals(type)) {
            priceRuleList = readKidsPriceRuleSheet(priceRuleSheet, brandMapList, price_change_rule_id);
        }
        HSSFSheet categoryBrandSheet = workbook.getSheet(CATEGORY_BRAND_SHEET_NAME);
        List<Map<String, Object>> categoryBrandList = new ArrayList<>();
        if (categoryBrandSheet != null) {
            categoryBrandList = readCategoryBrandSheet(type, categoryBrandSheet, brandMapList, categoryMapList, price_change_rule_id);
        }
        HSSFSheet productGroupSheet = workbook.getSheet(PRODUCT_GROUP_SHEET_NAME);
        List<Map<String, Object>> productGroupList = new ArrayList<>();
        if (productGroupSheet != null) {
            productGroupList = readProductGroupSheet(productGroupSheet, productGroupMapList, price_change_rule_id);
        }
        HSSFSheet productSheet = workbook.getSheet(PRODUCT_SHEET_NAME);
        List<Map<String, Object>> productList = new ArrayList<>();
        if (productSheet != null) {
            productList = readProductSheet(productSheet, price_change_rule_id);
        }

        result.put("priceRuleList", priceRuleList);
        result.put("categoryBrandList", categoryBrandList);
        result.put("productGroupList", productGroupList);
        result.put("productList", productList);

        return result;
    }

    private static List<Map<String, Object>> readAdultPriceRuleSheet(HSSFSheet sheet, List<Map<String, Object>> brandMapList, String price_change_rule_id)
            throws Exception {
        List<Map<String, Object>> readExcelList = new ArrayList<>();
        List<String> defaultValues = new ArrayList<>(10);
        for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行

            HSSFRow row=sheet.getRow(j);

            if (row == null || row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).toString())) {
                break;
            }
            String brand_id = getBrandId(brandMapList, row.getCell(0).getStringCellValue());
            if(brand_id.equals("0")){
                defaultValues.add(0,"0");
                for(int i=1;i<9;i++){
                    if(row.getCell(i) != null && StringUtils.isNotBlank(row.getCell(i).toString())){
                        defaultValues.add(i,row.getCell(i).toString());
                    }else {
                        defaultValues.add(i,"0");
                    }
                }
            }
            String men_clothing;
            if (row.getCell(1) != null && StringUtils.isNotBlank(row.getCell(1).toString())) {
                men_clothing = row.getCell(1).toString();
            } else {
                men_clothing = defaultValues.get(1);
            }

            String men_shoes;
            if (row.getCell(2) != null && StringUtils.isNotBlank(row.getCell(2).toString())) {

                men_shoes = row.getCell(2).toString();
            } else {
                men_shoes = defaultValues.get(2);
            }
            String men_bags;
            if (row.getCell(3) != null && StringUtils.isNotBlank(row.getCell(3).toString())) {

                men_bags = row.getCell(3).toString();
            } else {
                men_bags = defaultValues.get(3);
            }
            String men_accessores;
            if (row.getCell(4) != null && StringUtils.isNotBlank(row.getCell(4).toString())) {
                men_accessores = row.getCell(4).toString();
            } else {
                men_accessores = defaultValues.get(4);
            }
            String women_clothing;
            if (row.getCell(5) != null && StringUtils.isNotBlank(row.getCell(5).toString())) {
                women_clothing = row.getCell(5).toString();
            } else {
                women_clothing = defaultValues.get(5);
            }
            String women_shoes;
            if (row.getCell(6) != null && StringUtils.isNotBlank(row.getCell(6).toString())) {

                women_shoes = row.getCell(6).toString();
            } else {
                women_shoes = defaultValues.get(6);
            }
            String women_bags;
            if (row.getCell(7) != null && StringUtils.isNotBlank(row.getCell(7).toString())) {
                women_bags = row.getCell(7).toString();
            } else {
                women_bags = defaultValues.get(7);
            }
            String women_accessores;
            if (row.getCell(8) != null && StringUtils.isNotBlank(row.getCell(8).toString())) {
                women_accessores = row.getCell(8).toString();
            } else {
                women_accessores = defaultValues.get(8);
            }
            /* Men */
            readExcelList.add(genMap(brand_id, men_clothing, 1, price_change_rule_id, "1"));
            readExcelList.add(genMap(brand_id, men_shoes, 2, price_change_rule_id, "1"));
            readExcelList.add(genMap(brand_id, men_bags, 3, price_change_rule_id, "1"));
            readExcelList.add(genMap(brand_id, men_accessores, 4, price_change_rule_id, "1"));
            /* Women */
            readExcelList.add(genMap(brand_id, women_clothing, 5, price_change_rule_id, "1"));
            readExcelList.add(genMap(brand_id, women_shoes, 6, price_change_rule_id, "1"));
            readExcelList.add(genMap(brand_id, women_bags, 7, price_change_rule_id, "1"));
            readExcelList.add(genMap(brand_id, women_accessores, 8, price_change_rule_id, "1"));
        }
        return readExcelList;
    }

    private static List<Map<String, Object>> readKidsPriceRuleSheet(HSSFSheet sheet, List<Map<String, Object>> brandMapList, String price_change_rule_id)
            throws Exception {
        List<Map<String, Object>> readExcelList = new ArrayList<>();
        List<String> defaultValues = new ArrayList<>(10);

        for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
            HSSFRow row = sheet.getRow(j);

            if (row == null || row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).toString())) {
                break;
            }

            String brand_id = getBrandId(brandMapList, row.getCell(0).getStringCellValue());
            if (brand_id.equals("0")) { //default brand
                defaultValues.add(0, "0");
                for (int i = 1; i < 10; i++) {
                    if (row.getCell(i) != null && StringUtils.isNotBlank(row.getCell(i).toString())) {
                        defaultValues.add(i, row.getCell(i).toString());// 设置默认
                    } else {
                        defaultValues.add(i, "0");
                    }
                }
            }
            String babies_clothing;
            if (row.getCell(1) != null && StringUtils.isNotBlank(row.getCell(1).toString())) {
                babies_clothing = row.getCell(1).toString();
            } else {
                babies_clothing = defaultValues.get(1);
            }

            String babies_shoes;
            if (row.getCell(2) != null && StringUtils.isNotBlank(row.getCell(2).toString())) {
                babies_shoes = row.getCell(2).toString();
            } else {
                babies_shoes = defaultValues.get(2);
            }
            String babies_accessores;
            if (row.getCell(3) != null && StringUtils.isNotBlank(row.getCell(3).toString())) {
                babies_accessores = row.getCell(3).toString();
            } else {
                babies_accessores = defaultValues.get(3);
            }

            String boys_clothing;
            if (row.getCell(4) != null && StringUtils.isNotBlank(row.getCell(4).toString())) {
                boys_clothing = row.getCell(4).toString();
            } else {
                boys_clothing = defaultValues.get(4);
            }

            String boys_shoes;
            if (row.getCell(5) != null && StringUtils.isNotBlank(row.getCell(5).toString())) {
                boys_shoes = row.getCell(5).toString();
            } else {
                boys_shoes = defaultValues.get(5);
            }

            String boys_accessores;
            if (row.getCell(6) != null && StringUtils.isNotBlank(row.getCell(6).toString())) {
                boys_accessores = row.getCell(6).toString();
            } else {
                boys_accessores = defaultValues.get(6);
            }

            String girls_clothing;
            if (row.getCell(7) != null && StringUtils.isNotBlank(row.getCell(7).toString())) {
                girls_clothing = row.getCell(7).toString();
            } else {
                girls_clothing = defaultValues.get(7);
            }

            String girls_shoes;
            if (row.getCell(8) != null && StringUtils.isNotBlank(row.getCell(8).toString())) {
                girls_shoes = row.getCell(8).toString();
            } else {
                girls_shoes = defaultValues.get(8);
            }

            String girls_accessores;
            if (row.getCell(9) != null && StringUtils.isNotBlank(row.getCell(9).toString())) {
                girls_accessores = row.getCell(9).toString();
            } else {
                girls_accessores = defaultValues.get(9);
            }

            /* Babies */
            readExcelList.add(genMap(brand_id, babies_clothing, 1, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, babies_shoes, 2, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, babies_accessores, 3, price_change_rule_id, "2"));
            /*boys*/
            readExcelList.add(genMap(brand_id, boys_clothing, 4, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, boys_shoes, 5, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, boys_accessores, 6, price_change_rule_id, "2"));

            /*girls*/
            readExcelList.add(genMap(brand_id, girls_clothing, 7, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, girls_shoes, 8, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, girls_accessores, 9, price_change_rule_id, "2"));

        }
        return readExcelList;
    }

    private static List<Map<String, Object>> readCategoryBrandSheet(String type, HSSFSheet sheet, List<Map<String, Object>> brandMapList,
                                                                    List<Map<String, Object>> categoryMapList, String price_change_rule_id) throws Exception {
        List<Map<String, Object>> readExcelList = new ArrayList<>();
        Map<String, Map<String, Object>> categoryPathToCategoryMap = new HashMap<>();
        for (Map<String, Object> categoryMap : categoryMapList) {
            categoryPathToCategoryMap.put(categoryMap.get("categoryPath").toString(), categoryMap);
        }

        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
            HSSFRow row = sheet.getRow(j);
            if (row == null || row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).toString())
                    || row.getCell(1) == null || StringUtils.isBlank(row.getCell(1).toString())
                    || row.getCell(2) == null || StringUtils.isBlank(row.getCell(2).toString())) {
                break;
            }
            String brandName = row.getCell(0).getStringCellValue();
            String categoryPath = row.getCell(1).getStringCellValue();
            String discount = row.getCell(2).toString();

            String brand_id = getBrandId(brandMapList, brandName);

            Map<String, Object> category = categoryPathToCategoryMap.get(categoryPath);
            if (category == null) {
                throw new RuntimeException("类目不在限定范围内：" + categoryPath);
            }
            Long categoryId = Long.parseLong(category.get("categoryId").toString());

            int dis = 100 - (int) Double.parseDouble(StringUtils.trim(discount));
            if (dis < 0 || dis > 100) {
                throw new RuntimeException("折扣设置错误");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("price_change_rule_id", price_change_rule_id);
            map.put("category_id", categoryId);
            map.put("level", "3");
            map.put("brand_id", brand_id);
            map.put("discount_percentage", dis);
            map.put("exception_flag", "1");

            readExcelList.add(map);
        }
        return readExcelList;
    }

    private static List<Map<String, Object>> readProductGroupSheet(HSSFSheet sheet, List<Map<String, Object>> productGroupList, String price_change_rule_id)
            throws Exception {
        List<Map<String, Object>> readExcelList = new ArrayList<>();
        Map<String, Map<String, Object>> productGroupNameToProductGroupMap = new HashMap<>();
        for (Map<String, Object> productGroupMap : productGroupList) {
            productGroupNameToProductGroupMap.put(productGroupMap.get("productGroupName").toString(), productGroupMap);
        }
        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
            HSSFRow row = sheet.getRow(j);
            if (row == null || row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).toString())
                    || row.getCell(1) == null || StringUtils.isBlank(row.getCell(1).toString())) {
                break;
            }
            String productGroupName = row.getCell(0).getStringCellValue();
            String discount = row.getCell(1).toString();
            Map<String, Object> productGroup = productGroupNameToProductGroupMap.get(productGroupName);
            if (productGroup == null) {
                throw new RuntimeException("Product Group不存在：" + productGroupName);
            }

            int dis = 100 - (int) Double.parseDouble(StringUtils.trim(discount));
            if (dis < 0 || dis > 100) {
                throw new RuntimeException("折扣设置错误");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("price_change_rule_id", price_change_rule_id);
            map.put("product_group_id", productGroup.get("productGroupId"));
            map.put("discount_percentage", dis);

            readExcelList.add(map);
        }
        return readExcelList;
    }

    private static List<Map<String, Object>> readProductSheet(HSSFSheet sheet, String price_change_rule_id) throws Exception {

        List<Map<String, Object>> readExcelList = new ArrayList<>();
        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
            HSSFRow row = sheet.getRow(j);
            if (row == null || row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).toString())
                    || row.getCell(1) == null || StringUtils.isBlank(row.getCell(1).toString())
                    || row.getCell(2) == null || StringUtils.isBlank(row.getCell(2).toString())) {
                break;
            }
            String designerId = row.getCell(0).getStringCellValue();
            String colorCode = row.getCell(1).getStringCellValue();
            String discount = row.getCell(2).toString();

            int dis = 100 - (int) Double.parseDouble(StringUtils.trim(discount));
            if (dis < 0 || dis > 100) {
                throw new RuntimeException("折扣设置错误");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("price_change_rule_id", price_change_rule_id);
            map.put("designer_id", designerId);
            map.put("color_code", colorCode);
            map.put("discount_percentage", dis);

            readExcelList.add(map);
        }
        return readExcelList;
    }

    public static String genPriceExcelKids(String excelName, List<Map<String, Object>> brands, List<Map<String, Object>> datas, String filePath)
            throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();

        // set sheet name
        int rowLength = 0;
        HSSFSheet sheet = workbook.createSheet(excelName);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
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

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 9));
        rowLength++;

        // 创建第二行数据
        HSSFRow row2 = sheet.createRow(rowLength);
        for (int i = 0, iLen = categoryKidsNames.length; i < iLen; i++) {
            HSSFCell cell = row2.createCell(i);
            cell.setCellValue(categoryKidsNames[i]);
            cell.setCellStyle(cellStyle);
        }
        rowLength++;

        // 创建折扣数据
        for (Map<String, Object> data : datas) {
            HSSFRow row = sheet.createRow(rowLength);

            for (int i = 0, iLen = categoryKidsIds.length; i < iLen; i++) {
                String cId = categoryKidsIds[i];
                String value = "";
                if (StringUtils.isBlank(cId)) {
                    value = data.get("english_name").toString();
                } else {
                    if (data.get(cId) == null) {
                        value = "100"; // 如果不存在  折扣为100 不打折
                    } else {
                        value = data.get(cId).toString();
                    }

                    value = "" + (100 - Integer.parseInt(value));
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
        for (int i = 0, length = strs.length; i < length; i++) {
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

    public static List<Map<String, Object>> readRuleExcelKids(String filePath, List<Map<String, Object>> brandNames, String price_change_rule_id)
            throws Exception {

        List<Map<String, Object>> readExcelList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
        HSSFSheet sheet = workbook.getSheetAt(0);
        List<String> defaultValues = new ArrayList<>(10);

        for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
            HSSFRow row = sheet.getRow(j);


            if(row == null || row.getCell(0) == null) {
                break;
            }

            if(row == null || row.getCell(0) == null) {
                break;
            }

            String brand_id = getBrandId(brandNames,row.getCell(0).getStringCellValue());
            if(brand_id.equals("0")){ //default brand
                defaultValues.add(0,"0");
                for(int i = 1;i<10;i++) {
                    if(row.getCell(i) != null && StringUtils.isNotBlank(row.getCell(i).toString())) {
                        defaultValues.add(i, row.getCell(i).toString());// 设置默认
                    } else {
                        defaultValues.add(i, "0");
                    }
                }
            }
            String babies_clothing;
            if (row.getCell(1) != null && StringUtils.isNotBlank(row.getCell(1).toString())) {
                babies_clothing = row.getCell(1).toString();
            } else {
                babies_clothing = defaultValues.get(1);
            }

            String babies_shoes;
            if (row.getCell(2) != null && StringUtils.isNotBlank(row.getCell(2).toString())) {
                babies_shoes = row.getCell(2).toString();
            } else {
                babies_shoes = defaultValues.get(2);
            }
            String babies_accessores;
            if (row.getCell(3) != null && StringUtils.isNotBlank(row.getCell(3).toString())) {
                babies_accessores = row.getCell(3).toString();
            } else {
                babies_accessores = defaultValues.get(3);
            }

            String boys_clothing;
            if (row.getCell(4) != null && StringUtils.isNotBlank(row.getCell(4).toString())) {
                boys_clothing = row.getCell(4).toString();
            } else {
                boys_clothing = defaultValues.get(4);
            }

            String boys_shoes;
            if (row.getCell(5) != null && StringUtils.isNotBlank(row.getCell(5).toString())) {
                boys_shoes = row.getCell(5).toString();
            } else {
                boys_shoes = defaultValues.get(5);
            }

            String boys_accessores;
            if (row.getCell(6) != null && StringUtils.isNotBlank(row.getCell(6).toString())) {
                boys_accessores = row.getCell(6).toString();
            } else {
                boys_accessores = defaultValues.get(6);
            }

            String girls_clothing;
            if (row.getCell(7) != null && StringUtils.isNotBlank(row.getCell(7).toString())) {
                girls_clothing = row.getCell(7).toString();
            } else {
                girls_clothing = defaultValues.get(7);
            }

            String girls_shoes;
            if (row.getCell(8) != null && StringUtils.isNotBlank(row.getCell(8).toString())) {
                girls_shoes = row.getCell(8).toString();
            } else {
                girls_shoes = defaultValues.get(8);
            }

            String girls_accessores;
            if (row.getCell(9) != null && StringUtils.isNotBlank(row.getCell(9).toString())) {
                girls_accessores = row.getCell(9).toString();
            } else {
                girls_accessores = defaultValues.get(9);
            }

            /* Babies */
            readExcelList.add(genMap(brand_id, babies_clothing, 1, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, babies_shoes, 2, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, babies_accessores, 3, price_change_rule_id, "2"));
            /*boys*/
            readExcelList.add(genMap(brand_id, boys_clothing, 4, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, boys_shoes, 5, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, boys_accessores, 6, price_change_rule_id, "2"));

            /*girls*/
            readExcelList.add(genMap(brand_id, girls_clothing, 7, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, girls_shoes, 8, price_change_rule_id, "2"));
            readExcelList.add(genMap(brand_id, girls_accessores, 9, price_change_rule_id, "2"));

        }
        return readExcelList;
    }

    private static Map<String, Object> genMap(String brand_id, String discount, int i, String price_change_rule_id, String type) {
        int dis = 100 - (int) Double.parseDouble(StringUtils.trim(discount));
        if (dis < 0 || dis > 100) {
            throw new RuntimeException("折扣设置错误");
        }
        Map<String, Object> map = new HashMap<>();
        if ("1".equals(type)) {
            map.put("category_id", categoryIds[i]);
        } else if ("2".equals(type)) {
            map.put("category_id", categoryKidsIds[i]);
        }
        map.put("brand_id", brand_id);
        map.put("discount_percentage", dis);
        map.put("exception_flag", "0");
        map.put("level", "2");
        map.put("price_change_rule_id", price_change_rule_id);
        return map;
    }

    private static String getBrandId(List<Map<String, Object>> brands, String name) throws Exception {
        for (Map<String, Object> map : brands) {
            String eName = map.get("english_name").toString();

            if (StringUtils.trim(eName).toLowerCase().equals(StringUtils.trim(name).toLowerCase())) {
                return map.get("brand_id").toString();
            }
        }
        throw new RuntimeException("找不到这个品牌:" + name);
    }

    private static String[] getBrands(List<Map<String, Object>> brands) throws Exception {
        String[] strings = new String[brands.size()];
        for (int i = 0, iLen = brands.size(); i < iLen; i++) {
            strings[i] = brands.get(i).get("english_name").toString();
        }
        return strings;
    }

}
