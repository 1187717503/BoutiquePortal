package com.intramirror.web.common;


import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.intramirror.common.help.SendEmailVo;
import com.intramirror.common.help.SendMailUtil;

public class OrderMail {

    public static final String EMAIL_CONFIG_FILE = "/email.properties";

    private static String host;

    private static String userName;

    private static String passWord;

    static {
        // 初始化email 信息
        InputStream in = OrderMail.class.getResourceAsStream(EMAIL_CONFIG_FILE);
        Properties props = new Properties();
        try {
            props.load(in);

            host = props.getProperty("host");
            userName = props.getProperty("userName");
            passWord = props.getProperty("passWord");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static void main(String[] args) {

//			// 打开数据库
//		try (Connection conn = DBConnector.sql2o.open()) {
//			
//			/**------------------------start send mail-------------------------------**/
//			OrderLogisticsService orderLogisticsService = new OrderLogisticsService(conn);
//			try{
//				Map<String, Object> map = orderLogisticsService.getOrderLogisticsInfoByIdWithSql(131l);
//				OrderMail.sendOrderMail(map);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//
//			/**------------------------end send mail-------------------------------**/
//			
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//		}

    }


    public static void sendOrderMail(Map<String, Object> orderInfo) {
        System.out.println("------------------------map----------------------------");
        System.out.println(new Gson().toJson(orderInfo));

        //汇率
        Double rate = Double.parseDouble(orderInfo.get("current_rate").toString());

        //按汇率计算人民币价钱
        Double sale_price2 = Double.parseDouble(orderInfo.get("sale_price").toString()) * rate;
        orderInfo.put("sale_price2", sale_price2);
        //计算利润
        Double profit = Double.parseDouble(orderInfo.get("sale_price").toString()) - Double.parseDouble(orderInfo.get("in_price").toString());
        BigDecimal b = new BigDecimal(profit);
        profit = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        orderInfo.put("profit", profit * rate);

        //计算折扣
        Double salePrice = Double.parseDouble(orderInfo.get("sale_price").toString());
        Double price = Double.parseDouble(orderInfo.get("price").toString());
        Double inPrice = Double.parseDouble(orderInfo.get("in_price").toString());


        BigDecimal sale_price_discount = new BigDecimal((salePrice / price) * 100);
//			info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
        orderInfo.put("sale_price_discount", (100 - sale_price_discount.intValue()) + " %");

        BigDecimal supply_price_discount = new BigDecimal((inPrice * (1 + 0.22) / price) * 100);
        orderInfo.put("supply_price_discount", (100 - supply_price_discount.setScale(0, BigDecimal.ROUND_HALF_UP).intValue()) + " %");

        orderInfo.put("sale_price", new java.text.DecimalFormat("#.00").format(salePrice));
        orderInfo.put("price", new java.text.DecimalFormat("#.00").format(price));
        orderInfo.put("in_price", new java.text.DecimalFormat("#.00").format(inPrice));

        if (orderInfo.get("cover_img") != null) {
            try {
                JsonArray coverImgJsonArray = new JsonParser().parse(orderInfo.get("cover_img").toString()).getAsJsonArray();
                if (coverImgJsonArray.size() > 0) {
                    orderInfo.put("cover_img", coverImgJsonArray.get(0).getAsString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(orderInfo.get("cover_img").toString());
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy HH:mm", Locale.UK);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String created_at = orderInfo.get("created_at").toString();
        try {
            System.out.println(sdf2.parse(created_at));
            created_at = sdf.format(sdf2.parse(created_at));
            orderInfo.put("created_at", created_at);
            System.out.println(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (orderInfo.get("contact") != null && StringUtils.isNotBlank(orderInfo.get("contact").toString())) {
            String[] addressList = orderInfo.get("contact").toString().split(",");

            try {
                for (int i = 0; i < addressList.length; i++) {
                    String address = addressList[i];
                    orderInfo.put("contact", address);
                    
                    SendEmailVo vo = new SendEmailVo();
                    vo.setHost(host);
                    vo.setUserName(userName);
                    vo.setPassWord(passWord);
                    vo.setTitle("IntraMirror- Order information");
                    vo.setContent(getContent(orderInfo));
                    vo.setAddressee(orderInfo.get("contact").toString());
                    SendMailUtil.sendMail(vo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String getContent(Map<String, Object> map) {
        StringBuffer content = new StringBuffer();

//		String orderNumberUrl = "Barcode-"+map.get("order_line_num").toString()+".png";
//		String skuBarcodeUrl = "Barcode-"+map.get("sku_code").toString()+".png";

        content.append("<div style='margin: 0 auto;width:400px;background: #fff;font-family:Roboto'>");

        content.append("<table width='100%' cellpadding='0' cellspacing='0' border='0'>");
        content.append("<tr style='height:64px'>");
        content.append("<th align='left'>");
        content.append("<img src='http://static-front.oss-cn-shanghai.aliyuncs.com/logo/284198222588302119.jpg?x-oss-process=image/resize,w_230' alt='logo'>");
        content.append("</th></tr>");
        content.append("<tr style=''>");
        content.append("<th align='left' style='padding:0 0 12px 24px;font-size: 18px;	font-weight: bold;	letter-spacing: 2px;'>" + map.get("vendor_name").toString() + "</th>");
        content.append("</tr>");
        content.append("</table>");
        content.append("<table width='100%' style='border-collapse:collapse;border-top:1px solid #d3d3d3;border-bottom:1px solid #d3d3d3;font-size:12px;'>");
        content.append("<tr>");
        content.append("<td colspan='2' style='padding-left:24px;padding-top:11px'>");
        content.append("Order Number</td></tr>");
        content.append("<tr><td style='padding-left:24px;font-size:18px;letter-spacing: 1px;'>" + map.get("order_num").toString());
        content.append("</td>");
        content.append("<td style='	font-size:18px;	letter-spacing:1px;text-align:right;padding-right:20px;'>" + map.get("geography_name").toString());
        content.append("</td></tr>");
        content.append("<tr><td style='padding-left:24px;padding-top:16px'>Order Date");
        content.append("</td></tr>");
        content.append("<tr><td colspan='2' style='padding-left:24px;font-size:18px;letter-spacing: 1px;padding-bottom:17px;'>" + map.get("created_at").toString());
        content.append("</td></tr>");
        content.append("</table>");


        content.append("<table width='100%' style='border-collapse:collapse;font-size:12px;'>");
        content.append("<tr><td colspan='3' style='padding:10px 0 19px 26px;'>");
        System.out.println("cover_img:" + map.get("cover_img").toString());
        content.append("<img src='" + map.get("cover_img").toString() + "?x-oss-process=image/resize,m_fill,w_125,h_159' alt=''>");
        content.append("</td></tr>");
        content.append("<tr>");
        content.append("<td style='padding-left:26px;width:120px'>Product ID</td>");
        content.append("<td style='width:80px'>Color</td>");
        content.append("<td style='width:100px'>Size</td>");
        content.append("</tr>");
        content.append("<tr>");
        content.append("<td style='padding-left:26px;font-size:18px;'>" + map.get("brandID").toString() + "</td>");
        content.append("<td style='font-size:18px'>" + map.get("colorCode").toString() + "</td>");
        content.append("<td style='font-size:18px'>" + map.get("size").toString() + "</td>");
        content.append("</tr>");
        content.append("<tr>");
        content.append("<td style='padding-left:26px;padding-top:16px;'>Brand</td>");
        content.append("<td colspan='2' style='padding-top:16px;'>Boutique ID</td>");
        content.append("</tr>");
        content.append("<tr>");
        content.append("<td style='padding-left:26px;font-size:18px'>" + map.get("brandName").toString() + "</td>");
        content.append("<td colspan='2' style='font-size:18px'>" + map.get("product_code").toString() + "</td>");
        content.append("</tr>");
        content.append("<tr><td colspan='3' style='padding-left:26px;padding-top:16px;'>Name</td></tr>");
        content.append("<tr><td colspan='3' style='padding-left:26px;font-size:18px;'>" + map.get("name").toString() + "</td></tr>");
        content.append("</table>");
        content.append("<table width='100%' style='border-collapse:collapse;border-bottom:1px solid #d3d3d3;font-size:12px;'>");
        content.append("<tr>");
        content.append("<td width='110px;' style='padding-left:26px;padding-top:16px;'>Retail Price</td>");
        content.append("<td width='120px' style='padding-top:16px'>Purchase Price</td>");
        content.append("<td width='150px' style='padding-top:16px'>Discount</td>");
        content.append("</tr>");
        content.append("<tr>");
        content.append("<td width='90px;' style='padding-left:26px;font-size:18px;padding-bottom:19px;'>€ " + map.get("price").toString() + "</td>");
        content.append("<td style='font-size:18px;padding-bottom:19px;'>€ " + map.get("in_price").toString() + "</td>");
        content.append("<td style='font-size:18px;padding-bottom:19px;'>" + map.get("supply_price_discount").toString() + " off</td>");
        content.append("</tr>");
        content.append("</table>");

        content.append("</div>");


//		        
//					content.append("<td style='padding:16px 0 0 0;width:160px'><span");
//					content.append(" style='transform:scale(0.833333);display:inline-block;margin-left:-6px'>Order Date</span></td>");
//					content.append("</tr>");
//					content.append("<tr style='font-size:14px;letter-spacing: 1.09px;'>");
//					content.append("<td style='padding:0 0 0 24px'>"+map.get("order_num").toString()+"</td>");
//					content.append("<td>"+map.get("created_at").toString()+"</td>");
//					content.append("</tr>");
//					content.append("<tr>");
//					content.append("<td style='width:400px'>");
//					content.append("<img src='"+BarcodeUtil.generateFile(map.get("order_line_num").toString(),orderNumberUrl,false)+"' id='img1' style='width:200px;margin:10px 0 16px 15px'>");
//					content.append("</td>");
//					content.append("<td style='	font-size: 20px;'>");
//					content.append(map.get("geography_name").toString());
//					content.append("</td>");
//					content.append("</tr>");
//					content.append("</table>");
//					content.append("<table width='100%' style='border-collapse:collapse;border-bottom:1px solid #d3d3d3;font-size:12px'>");
//					content.append("<tr>");
//					content.append("<td rowspan='3' style='width:146px'>");
//					content.append("<img src="+map.get("cover_img").toString()+"?x-oss-process=image/resize,w_125,h_159");
//					content.append(" style='padding:20px'>");
//					content.append("</td>");
//					content.append("<td style='width:90px'>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block;margin-left:-4px;margin-top:6px'>Brand</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>Gucci</p>");
//					content.append("</td>");
//					content.append("<td colspan='3'>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block'>Name</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>"+map.get("name").toString()+"</p>");
//					content.append("</td>");
//					content.append("</tr>");
//					content.append("<tr>");
//					content.append("<td style='width:120px'>");
//					content.append("<span");
//					content.append(" style='transform:scale(0.833333);display:inline-block;margin-left:-6px;margin-top:6px'>Product ID</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>"+map.get("product_id").toString()+"</p>");
//					content.append("</td>");
//					content.append("<td>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block;'>Color</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>"+map.get("colorCode").toString()+"</p>");
//					content.append("</td>");
//					content.append("<td>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block;'>Size</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>"+map.get("size").toString()+"</p>");
//					content.append("</td>");
//					content.append("<td>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block;margin-left:-6px'>Boutique ID</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>"+map.get("vendor_id").toString()+"</p>");
//					content.append("</td>");
//					content.append("</tr>");
//					content.append("<tr>");
//					content.append("<td>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block;margin-left:-6px'>Retail Price</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>€ "+map.get("price").toString()+"</p>");
//					content.append("</td>");
//					content.append("<td>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block;margin-left:-6px'>Purchase Price</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>€ "+map.get("sale_price").toString()+"</p>");
//					content.append("</td>");
//					content.append("<td>");
//					content.append("<span style='transform:scale(0.833333);display:inline-block;margin-left:-6px'>Discount</span>");
//					content.append("<p style='font-size:14px;margin:4px 0 0 0'>"+map.get("sale_price_discount").toString()+" off</p>");
//					content.append("</td>");
//					content.append(" </tr>");
//					content.append("<tr>");
//					content.append("<td colspan='5'>");	
//					content.append("<img src='"+BarcodeUtil.generateFile(map.get("sku_code").toString(),skuBarcodeUrl,true)+"' alt='' style='width:167px;margin:10px 0 16px 24px'>");
//					content.append("</td>");
//					content.append("</tr>");
//					content.append("</table>");
//					content.append("</div>");

//					content.append("<script type='text/javascript'>");  
//					content.append("function showPreview(source) {  ");  
//					content.append("var file = "+"data:"+new String(BarcodeUtil.generateFile("123456789", true))+ "; ");  
//					content.append("if(window.FileReader) {  ");  
//					content.append("var fr = new FileReader();  ");  
//					content.append("fr.onloadend = function(e) {  ");  
//					content.append("document.getElementById('img1').src = e.target.result;");    
//					content.append("};  ");  
//					content.append("fr.readAsDataURL(file);  ");  
//					content.append("}  ");  
//					content.append("}  ");  
//					content.append("</script>  ");  

        return content.toString();
    }

}
