package com.intramirror.web.controller.file;

import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.service.IApiErrorProcessingService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dingyifan on 2017/12/13.
 */
@Controller
@RequestMapping("/delErrorMessage")
public class DelErrorMessageController {

    private static final Logger logger = Logger.getLogger(DelErrorMessageController.class);

    private static final String delShPath = "/opt/shell/delLog.sh";

    @Autowired
    private IApiErrorProcessingService iApiErrorProcessingService;

    @ResponseBody
    @RequestMapping("/execute")
    public ResultMessage execute(@Param("aFewDaysAgo")Integer day){
        try {
            if(day.intValue() > 3) {
                String createTime = DateUtils.getTimeByHour(day*24);
                iApiErrorProcessingService.deleteApiErrorProcessing(createTime);
                logger.info("DelErrorMessageController,execute,createTime:"+createTime);
                this.delLog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("DelErrorMessageController,execute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return ResultMessage.getInstance().successStatus();
    }

    private void delLog() throws Exception {
        Process ps = Runtime.getRuntime().exec(delShPath);
        ps.waitFor();

        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String result = sb.toString();
        logger.info("result:"+result);
        ps.destroy();
    }
}
