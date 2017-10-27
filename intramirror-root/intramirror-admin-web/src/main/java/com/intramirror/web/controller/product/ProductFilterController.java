package com.intramirror.web.controller.product;

import com.intramirror.product.api.service.brand.IBrandService;
import com.intramirror.product.api.service.season.SeasonService;
import com.intramirror.web.common.Response;
import com.intramirror.web.common.StatusCode;
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
    @Autowired
    private SeasonService seasonService;

    @Autowired
    private IBrandService brandService;

    @GetMapping(value = "/season/list")
    public Response listSeasonCode() {
        return Response.status(StatusCode.SUCCESS).data(seasonService.listAllSeasonCode());
    }

    @GetMapping(value = "/brand/list")
    public Response listCategory() {
        return Response.status(StatusCode.SUCCESS).data(brandService.listActiveBrand());
    }
}
