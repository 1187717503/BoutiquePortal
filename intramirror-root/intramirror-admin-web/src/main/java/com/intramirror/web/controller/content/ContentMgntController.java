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
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Created on 2017/11/17.
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/contentmgnt")
public class ContentMgntController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContentMgntController.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private ITagService iTagService;

    @PostMapping(value = "/savetagproductrel", consumes = "application/json")
    public Response saveTagProductRel(@SessionAttribute(value = "sessionStorage", required = false) Long userId, @RequestBody Map<String, Object> body) {
        Long tagId = body.get("tagId") == null ? null : Long.parseLong(body.get("tagId").toString());
        Long sortNum = body.get("sortNum") == null ? 999 : Long.parseLong(body.get("sortNum").toString());
        List<String> productIdList = (List<String>) body.get("productIdList");

        if (productIdList.size() <= 0 || null == tagId) {
            throw new ValidateException(new ErrorResponse("Parameter could not be null!"));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("productIdList", productIdList);
        map.put("tagId", tagId);
        map.put("sort_num", sortNum);

        try {
            iTagService.saveTagProductRel(map);
        } catch (Exception e) {
            throw new ValidateException(new ErrorResponse("Failed to add tag to product! ErrorMsg: " + e.getMessage()));
        }

        return Response.success();
    }

    @PostMapping(value = "/gettags", consumes = "application/json")
    public Response getTags(@SessionAttribute(value = "sessionStorage", required = false) Long userId) {
        List<Tag> listTags = null;
        try {
            listTags = iTagService.getTags();
        } catch (Exception e) {
            throw new ValidateException(new ErrorResponse("Failed to get all tags! ErrorMsg: " + e.getMessage()));
        }
        return Response.status(StatusType.SUCCESS).data(listTags);
    }

    // For test, not used by web page
    @PostMapping(value = "/gettagbyid", consumes = "application/json")
    public Response getTagById(@SessionAttribute(value = "sessionStorage", required = false) Long userId, @RequestBody Map<String, Object> body) {
        Long tagId = body.get("tagId") == null ? 1 : Long.parseLong(body.get("tagId").toString());
        Tag tag = null;
        try {
            tag = iTagService.selectByPrimaryKey(tagId);
        } catch (Exception e) {
            throw new ValidateException(new ErrorResponse("Failed to get all tags! ErrorMsg: " + e.getMessage()));
        }
        return Response.status(StatusType.SUCCESS).data(tag);
    }

}
