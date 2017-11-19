package com.intramirror.web.controller.content;

import com.intramirror.product.api.service.BlockService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.web.Exception.ErrorResponse;
import com.intramirror.web.Exception.ValidateException;
import com.intramirror.web.common.response.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Created on 2017/11/17.
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/ContentMgnt")
public class ContentMgntController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContentMgntController.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private ITagService iTagService;

    @PostMapping(value = "/saveTagProductRel", consumes = "application/json")
    public Response saveTagProductRel(@SessionAttribute(value = "sessionStorage", required = false) Long userId, @RequestBody Map<String, Object> body) {
        Long tagId = body.get("tagId") == null ? null : Long.parseLong(body.get("tagId").toString());
        List<String> productIdList = (List<String>) body.get("productIdList");

        if (productIdList.size() <= 0 || null == tagId) {
            throw new ValidateException(new ErrorResponse("Parameter could not be null!"));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("productIdList", productIdList);
        map.put("tagId", tagId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        map.put("created_at", sdf.format(new Date()));

        try {
            iTagService.saveTagProductRel(map);
        } catch (Exception e) {
            throw new ValidateException(new ErrorResponse("Failed to add tag to product! ErrorMsg: " + e.getMessage()));
        }

        return Response.success();
    }

}
