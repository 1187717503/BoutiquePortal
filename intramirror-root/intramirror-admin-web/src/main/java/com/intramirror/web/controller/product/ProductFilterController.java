package com.intramirror.web.controller.product;

import com.intramirror.product.api.service.season.SeasonService;
import java.util.List;
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

    @GetMapping(value = "/season/listcode")
    public List<String> listSeasonCode() {
        return seasonService.listAllSeasonCode();
    }
}
