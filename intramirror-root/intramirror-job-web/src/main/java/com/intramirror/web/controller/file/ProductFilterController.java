package com.intramirror.web.controller.file;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.User;

/**
 * Created by dingyifan on 2017/12/15.
 */
@Controller
@RequestMapping("/product/filter")
public class ProductFilterController {

    @RequestMapping(method= RequestMethod.GET,value="/index")
    public ModelAndView index(@Param("vendor_id")Long vendor_id){
        ModelAndView modelAndView = new ModelAndView("filter/index.ftl");
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.open();
            EntityDao<User> userDao = new EntityDao<>(conn);
            String selVendorSQL = "select * from vendor where enabled = 1";
            List<Map<String,Object>> vendorList = userDao.executeBySql(selVendorSQL,null);
            modelAndView.addObject("vendorList",vendorList);

            String selSeasonSQL = "select * from season ";
            List<Map<String,Object>> seasonList = userDao.executeBySql(selSeasonSQL,null);
            modelAndView.addObject("seasonList",seasonList);

            /*String selBrandSQL = "select distinct b.* from `product` p \n"
                    + "inner join `brand`  b on(p.`brand_id` = b.`brand_id`  and p.`enabled`  = 1 and b.`enabled`  = 1)  order by b.`english_name`  asc  ";*/
            String selBrandSQL = "select b.* from `brand` b where b.enabled = 1 order by b.`english_name`  asc ";
            List<Map<String,Object>> brandList = userDao.executeBySql(selBrandSQL,null);
            modelAndView.addObject("brandList",brandList);

            List<Map<String,Object>> filterSeasonList = new ArrayList<>();
            List<Map<String,Object>> filterBrandList = new ArrayList<>();
            if(vendor_id != null && vendor_id != -1) {
                String selFilterSeasonSQL = "select * from season_filter sf where sf.enabled = 1 and sf.vendor_id ="+vendor_id;
                String selFilterBrandSQL = "select * from brand_filter bf where bf.enabled = 1 and bf.vendor_id ="+vendor_id;

                filterSeasonList = userDao.executeBySql(selFilterSeasonSQL,null);
                filterBrandList = userDao.executeBySql(selFilterBrandSQL,null);
            }
            modelAndView.addObject("filterSeasonList",filterSeasonList);
            modelAndView.addObject("filterBrandList",filterBrandList);
            modelAndView.addObject("vendor_id",vendor_id == null?"-1":vendor_id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return modelAndView;
    }

    @RequestMapping(method= RequestMethod.POST,value="/submit")
    @ResponseBody
    public ResultMessage submit(@Param("vendor_id")String vendor_id,@Param("seasons")String seasons,@Param("brands")String brands){
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();
            EntityDao<User> userDao = new EntityDao<>(conn);

            if(StringUtils.isNotBlank(vendor_id)) {

                if(StringUtils.isNotBlank(seasons)) {
                    String[] seasonsArr = seasons.split(",");
                    String delSeasonFilterSQL = "delete from season_filter where vendor_id = "+ vendor_id;
                    userDao.updateBySQL(delSeasonFilterSQL,null);
                    if(seasons.contains("-1")) {
                        String insertSeasonFilterSQL = "insert into season_filter(vendor_id,season_code,enabled,created_at) values("+vendor_id+",-1,1,now())";
                        userDao.updateBySQL(insertSeasonFilterSQL,null);
                    } else {
                        for(String season : seasonsArr) {
                            String insertSeasonFilterSQL = "insert into season_filter(vendor_id,season_code,enabled,created_at) values("+vendor_id+",\""+season+"\",1,now())";
                            userDao.updateBySQL(insertSeasonFilterSQL,null);
                        }
                    }
                } else {
                    String delSeasonFilterSQL = "delete from season_filter where vendor_id = "+ vendor_id;
                    userDao.updateBySQL(delSeasonFilterSQL,null);
                }

                if(StringUtils.isNotBlank(brands)) {
                    brands= brands.replaceAll(",","");
                    String[] brandsArr = brands.split("###");
                    String delBrandFilterSQL = "delete from brand_filter where vendor_id = "+vendor_id;
                    userDao.updateBySQL(delBrandFilterSQL,null);
                    if(brands.contains("-1")) {
                        String insertBrandFilterSQL = "insert into brand_filter(vendor_id,brand_id,enabled,created_at) values("+vendor_id+",-1,1,now())";
                        userDao.updateBySQL(insertBrandFilterSQL,null);
                    } else {
                        for(String brand : brandsArr) {
                            String insertBrandFilterSQL = "insert into brand_filter(vendor_id,brand_id,enabled,created_at) values("+vendor_id+",\""+brand+"\",1,now())";
                            userDao.updateBySQL(insertBrandFilterSQL,null);
                        }
                    }

                } else {
                    String delBrandFilterSQL = "delete from brand_filter where vendor_id = "+vendor_id;
                    userDao.updateBySQL(delBrandFilterSQL,null);
                }

            }
            if(conn != null) {conn.commit();}
        } catch (Exception e) {
            e.printStackTrace();
            if(conn != null) {conn.rollback();}
        } finally {
            if(conn != null) {conn.close();}
        }
        return ResultMessage.getInstance().successStatus();
    }
}
