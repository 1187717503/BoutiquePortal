package com.intramirror.web.controller.content;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.service.BlockService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.web.Exception.ErrorResponse;
import com.intramirror.web.Exception.ValidateException;
import com.intramirror.web.common.response.Response;
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

/**
 * Created on 2017/11/17.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/content")
public class ContentMgntController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContentMgntController.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private ITagService iTagService;

    @PostMapping(value = "/savetagproductrel", consumes = "application/json")
    public Response saveTagProductRel(@RequestBody Map<String, Object> body) {
        Long tagId = body.get("tagId") == null ? null : Long.parseLong(body.get("tagId").toString());
        Long sortNum = body.get("sortNum") == null ? 999 : Long.parseLong(body.get("sortNum").toString());
        List<String> productIdList = (List<String>) body.get("productIdList");

        if (productIdList.size() <= 0 || null == tagId) {
            throw new ValidateException(new ErrorResponse("Parameter could not be null!"));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("productIdList", productIdList);
        map.put("tagId", tagId);
        map.put("sort_num", sortNum);
        iTagService.saveTagProductRel(map);
        return Response.success();
    }

    @PostMapping(value = "/list/tag", consumes = "application/json")
    public Response listTags() {
        List<Tag> listTags = iTagService.getTags();
        return Response.status(StatusType.SUCCESS).data(listTags);
    }

}
