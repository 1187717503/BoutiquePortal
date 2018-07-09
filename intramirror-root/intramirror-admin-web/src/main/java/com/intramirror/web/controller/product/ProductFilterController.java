package com.intramirror.web.controller.product;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.service.brand.IBrandService;
import com.intramirror.product.api.service.season.SeasonService;
import com.intramirror.core.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/product/filter")
public class ProductFilterController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductFilterController.class);
    @Autowired
    private SeasonService seasonService;

    @Autowired
    private IBrandService brandService;

    @GetMapping(value = "/season/list")
    public Response listSeasonCode() {
        return Response.status(StatusType.SUCCESS).data(seasonService.listAllSeasonCode());
    }

    @GetMapping(value = "/brand/list")
    public Response listCategory() {
        return Response.status(StatusType.SUCCESS).data(brandService.listActiveBrand());
    }
}
