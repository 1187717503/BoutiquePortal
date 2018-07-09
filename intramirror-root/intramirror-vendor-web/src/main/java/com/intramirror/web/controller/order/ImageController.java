package com.intramirror.web.controller.order;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.intramirror.web.common.BarcodeUtil;
import com.intramirror.web.controller.BaseController;

@CrossOrigin
@Controller
@RequestMapping("/image")
public class ImageController extends BaseController{
	
	private static Logger logger = Logger.getLogger(ImageController.class);
	
	/**
	 * 获取条形码图片流
	 * @param message 
	 * @param httpRequest
	 * @return
	 */
    @RequestMapping(value = "/getImage", method = RequestMethod.GET)
	public void getOrderList(@RequestParam("message") String message,@RequestParam("type") String type,@RequestParam(value = "width", required = false) String width,
			HttpServletRequest httpRequest,HttpServletResponse response){
    	logger.info(MessageFormat.format("order getImage 生成条形码入参:{0}", new Gson().toJson(message)));
		
    	boolean show = true;
    	if(StringUtils.isNotBlank(type) && type.equals("0")){
    		show = false;
    	}
    	
    	//width 生成类型,宽度不同的条形码
    	byte[] data = null;
    	if(width != null && StringUtils.isNotBlank(width) && width.equals("2")){
    		data = BarcodeUtil.generate(message, show,2);
    	}else{
    		data = BarcodeUtil.generate(message, show,1);
    	}
    	

        response.setContentType("image/png");

        OutputStream stream;
		try {
			stream = response.getOutputStream();
	        stream.write(data);
	        stream.flush();
	        stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
    

}
