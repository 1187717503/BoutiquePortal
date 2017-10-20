package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.Product;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/10/20.
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/product")
public class ProductMgnt {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductMgnt.class);

    @RequestMapping(value = "/list/{status}", method = RequestMethod.GET)
    public List<Product> listProductByFilter(@PathVariable(value = "status") String status, @RequestParam("boutique") String boutique,
            @RequestParam("boutiqueid") String boutiqueid, @RequestParam("brand") String brand, @RequestParam("category") String category,
            @RequestParam("season") String season, @RequestParam("designerid_colorcode") String designerid_colorcode, @RequestParam("image") String image,
            @RequestParam("modelimage") String modelimage, @RequestParam("streetimage") String streetimage, @RequestParam("stock") String stock) {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setBoutique(boutique);
        searchCondition.setBoutiqueid(boutiqueid);
        searchCondition.setBrand(brand);
        searchCondition.setCategory(category);
        searchCondition.setDesignerid_colorcode(designerid_colorcode);
        searchCondition.setImage(image);
        searchCondition.setModelimage(modelimage);
        searchCondition.setSeason(season);
        searchCondition.setStatus(status);
        searchCondition.setStock(stock);
        searchCondition.setStreetimage(streetimage);
        LOGGER.info("{}", searchCondition);

        return null;
    }

    @RequestMapping(value = "/operate/{action}", method = RequestMethod.PUT)
    public Object operateProduct(@PathVariable(value = "action") String action) {
        return null;
    }

}
