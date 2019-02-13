package com.intramirror.web.controller.vendor;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on 2019/2/13.
 *
 * @author yfding
 */
@Controller
@RequestMapping("/vendor/job/config")
public class VendorJobConfigController extends BaseController {

    @Autowired
    private VendorService vendorService;

    @RequestMapping(value = "/allow_import_product", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getAllowImportProductFlag(HttpServletRequest httpRequest) {
        try {
            User user = super.getUser(httpRequest);
            Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
            Long allowImportProduct = vendorService.selectAllowImportProductByVendorId(vendor.getVendorId());
            if (allowImportProduct == null) {
                allowImportProduct = 0L;
            }
            return ResultMessage.getInstance().successStatus().putMsg("allowImportProduct", allowImportProduct.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultMessage.getInstance().errorStatus();
    }
}
