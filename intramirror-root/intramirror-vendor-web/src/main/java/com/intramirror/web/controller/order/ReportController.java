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
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

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
        /*requestVO.setPageSize(10000);
        requestVO.setPageNum(1);*/
        /*int pageNum = requestVO.getPageNum();
        List<ReportVO> reportVOS = new ArrayList<>();
        while (true){
            try{
                ReportResponseVO vo= reportExtService.search(requestVO);
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(vo.getReportVOS())){
                    reportVOS.addAll(vo.getReportVOS());
                }else {
                    break;
                }
                pageNum++;
                requestVO.setPageNum(pageNum);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
                break;
            }

        }*/
        ReportResponseVO vo= reportExtService.search(requestVO);

        logger.info("exportOrderList 生成订单文件");
        //generateReportExcel("product",reportVOS, filePath);
        String dateStr = DateUtils.getStrDate(new Date(), "yyyyMMddHHmmss");
        String name = "Product_" + dateStr + ".xls";
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            generateReportExcel("product",vo.getReportVOS()).write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            httpResponse.reset();
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
            httpResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes(), "iso-8859-1"));
            ServletOutputStream out = httpResponse.getOutputStream();
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
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*File newFile = new File(filePath);
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
        }*/
        resultMessage.successStatus();
        return resultMessage;
    }

    private void checkRequestVo(ReportRequestVO requestVO) {
        if(StringUtils.isBlank(requestVO.getColorCode())){
            requestVO.setColorCode(null);
        }else {
            requestVO.setColorCode(requestVO.getColorCode().trim());
        }
        if(StringUtils.isBlank(requestVO.getDesignerId())){
            requestVO.setDesignerId(null);
        }else {
            requestVO.setDesignerId(requestVO.getDesignerId().trim());
        }
        if (StringUtils.isBlank(requestVO.getSeasonCode())){
            requestVO.setSeasonCode(null);
        }else {
            requestVO.setSeasonCode(requestVO.getSeasonCode().trim());
        }
        if(StringUtils.isBlank(requestVO.getBoutiqueId())){
            requestVO.setBoutiqueId(null);
        }else {
            requestVO.setBoutiqueId(requestVO.getBoutiqueId().trim());
        }
    }

    private Workbook generateReportExcel(String excelName, List<ReportVO> reportVOS) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        int rowLength = 0;


        String[] excelHeaders = null;
        excelHeaders=new String[]{"boutique_id","designer_id", "color_code", "size", "retail_price", "boutique_price", "category", "brand_name", "season_code", "stock"};

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
                cell.setCellValue(reportVO.getBoutiqueId());

                cell = row.createCell(1);
                cell.setCellValue(reportVO.getDesignerId());

                cell = row.createCell(2);
                cell.setCellValue(reportVO.getColorCode());

                cell = row.createCell(3);
                cell.setCellValue(reportVO.getSize());

                cell = row.createCell(4);
                cell.setCellValue(reportVO.getRetailPrice()!=null?reportVO.getRetailPrice().setScale(4, RoundingMode.HALF_UP).toString():"");

                cell = row.createCell(5);
                cell.setCellValue(reportVO.getBoutiquePrice()!=null?reportVO.getBoutiquePrice().setScale(4, RoundingMode.HALF_UP).toString():"");

                cell = row.createCell(6);

//                cell.setCellStyle();
                cell.setCellValue(reportVO.getCategoryName());

                cell = row.createCell(7);
                cell.setCellValue(reportVO.getBrandName());

                cell = row.createCell(8);
                cell.setCellValue(reportVO.getSeasonCode());

                cell = row.createCell(9);
                cell.setCellValue(reportVO.getStock()!=null?reportVO.getStock():0);
                rowLength ++;
            }
        }
        return workbook;
    }

}
