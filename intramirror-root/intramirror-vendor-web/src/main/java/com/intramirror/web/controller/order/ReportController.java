package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.order.api.vo.ReportRequestVO;
import com.intramirror.order.api.vo.ReportResponseVO;
import com.intramirror.order.api.vo.ReportVO;
import com.intramirror.order.api.vo.SeasonVO;
import com.intramirror.order.core.impl.ext.ReportExtServiceImpl;
import com.intramirror.user.api.model.User;
import com.intramirror.web.common.CommonsProperties;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/report")
public class ReportController extends BaseController{

    Logger logger = LoggerFactory.getLogger(ReportController.class);
    @Autowired
    private ReportExtServiceImpl reportExtService;
    @Autowired
    private CommonsProperties commonsProperties;
    @PostMapping(value = "/search")
    @ResponseBody
    public Object search(@RequestBody ReportRequestVO requestVO,HttpServletRequest httpRequest){
        ResultMessage resultMessage = ResultMessage.getInstance();
        checkRequestVo(requestVO);
        User user = this.getUser(httpRequest);
        Long vendorId = reportExtService.queryVendorIdByUserId(user.getUserId());
        requestVO.setVendorId(vendorId);
        ReportResponseVO vo = reportExtService.search(requestVO);
        resultMessage.setData(vo);
        resultMessage.successStatus();
        return resultMessage;
    }


    @PostMapping(value = "/count")
    @ResponseBody
    public Object count(@RequestBody ReportRequestVO requestVO,HttpServletRequest httpRequest){
        ResultMessage resultMessage = ResultMessage.getInstance();
        checkRequestVo(requestVO);
        User user = this.getUser(httpRequest);
        Long vendorId = reportExtService.queryVendorIdByUserId(user.getUserId());
        requestVO.setVendorId(vendorId);
        Integer count = reportExtService.count(requestVO);
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put("count",count);
        resultMessage.setData(countMap);
        resultMessage.successStatus();
        return resultMessage;
    }

    @GetMapping(value = "/queryAllSeason")
    @ResponseBody
    public Object queryAllSeason(){
        ResultMessage resultMessage = ResultMessage.getInstance();
        List<SeasonVO> seasonVOS = reportExtService.queryAllSeason();
        resultMessage.setData(seasonVOS);
        resultMessage.successStatus();
        return resultMessage;
    }

    @PostMapping(value = "/excel/download")
    @ResponseBody
    public Object download(@RequestBody ReportRequestVO requestVO, HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        ResultMessage resultMessage = ResultMessage.getInstance();
        checkRequestVo(requestVO);
        User user = this.getUser(httpRequest);
        Long vendorId = reportExtService.queryVendorIdByUserId(user.getUserId());
        requestVO.setVendorId(vendorId);
        requestVO.setStart(0);
        requestVO.setPageSize(Integer.MAX_VALUE);
        ReportResponseVO vo= reportExtService.search(requestVO);
        String dateStr = DateUtils.getStrDate(new Date(), "yyyyMMddHHmmss");
        String name = "Product_" + dateStr + ".xls";
        String path = commonsProperties.getOrderPath() + "download/";
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String filePath = path + name;

        logger.info("exportOrderList 生成订单文件");
        generateReportExcel("product",vo, filePath);

        File newFile = new File(filePath);
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
        httpResponse.setHeader("Content-Disposition", "attachment;filename=" + name);
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(newFile);
            bis = new BufferedInputStream(fis);
            OutputStream os = httpResponse.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        resultMessage.successStatus();
        return resultMessage;
    }

    private void checkRequestVo(ReportRequestVO requestVO) {
        if(StringUtils.isBlank(requestVO.getColorCode())){
            requestVO.setColorCode(null);
        }
        if(StringUtils.isBlank(requestVO.getDesignerId())){
            requestVO.setDesignerId(null);
        }
        if (StringUtils.isBlank(requestVO.getSeasonCode())){
            requestVO.setSeasonCode(null);
        }
    }

    private String generateReportExcel(String excelName, ReportResponseVO vo, String filePath) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        int rowLength = 0;


        String[] excelHeaders = null;
        excelHeaders=new String[]{"designer_id", "color_code", "size", "retail_price", "boutique_price", "category", "brand_name", "season_code", "stock"};

        // 创建表头
        /*HSSFRow row1 = sheet.createRow(rowLength);
        for (int i = 0, iLen = excelHeaders.length; i < iLen; i++) {
            HSSFCell cell = row1.createCell(i);
            cell.setCellValue(excelHeaders[i]);
        }*/

        //rowLength++;
        HSSFRow row = null;
        HSSFCell cell = null;
        HSSFSheet sheet = null;
        FileOutputStream fileOut = null;
        List<ReportVO> reportVOS = vo.getReportVOS();
        int ro = 0;
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(reportVOS)){
            for(ReportVO reportVO : reportVOS){
                if(ro % 60000 == 0){
                    rowLength = 0;
                    sheet = workbook.createSheet(excelName+ (ro / 60000));
                    HSSFRow row1 = sheet.createRow(rowLength);
                    for (int i = 0, iLen = excelHeaders.length; i < iLen; i++) {
                        HSSFCell cellHe = row1.createCell(i);
                        cellHe.setCellValue(excelHeaders[i]);
                        if (i != 2) {
                            sheet.autoSizeColumn(i, true);
                        } else {
                            sheet.setColumnWidth(i, 16 * 256);
                        }
                    }
                    rowLength ++;
                }
                ro ++;
                row = sheet.createRow(rowLength);
                cell = row.createCell(0);
                cell.setCellValue(reportVO.getDesignerId());

                cell = row.createCell(1);
                cell.setCellValue(reportVO.getColorCode());

                cell = row.createCell(2);
                cell.setCellValue(reportVO.getSize());

                cell = row.createCell(3);
                cell.setCellValue(reportVO.getRetailPrice()!=null?reportVO.getRetailPrice().setScale(4, RoundingMode.HALF_UP).toString():"");

                cell = row.createCell(4);
                cell.setCellValue(reportVO.getBoutiquePrice()!=null?reportVO.getBoutiquePrice().setScale(4, RoundingMode.HALF_UP).toString():"");

                cell = row.createCell(5);

//                cell.setCellStyle();
                cell.setCellValue(reportVO.getCategoryName());

                cell = row.createCell(6);
                cell.setCellValue(reportVO.getBrandName());

                cell = row.createCell(7);
                cell.setCellValue(reportVO.getSeasonCode());

                cell = row.createCell(8);
                cell.setCellValue(reportVO.getStock()!=null?reportVO.getStock():0);
                rowLength ++;
            }
        }
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

}
