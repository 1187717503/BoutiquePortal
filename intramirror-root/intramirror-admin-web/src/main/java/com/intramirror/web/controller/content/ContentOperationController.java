package com.intramirror.web.controller.content;

import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.model.BlockTagRel;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.service.BlockService;
import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.web.common.request.Content;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/11/21.
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/content/operation")
public class ContentOperationController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContentOperationController.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private ContentManagementService contentManagementService;

    @PutMapping(value = "/save", consumes = "application/json")
    public Response saveContent(@RequestBody Content content) {
        validate(content);
        if (content.getIsNew().equals("1")) {
            BlockTagRel btRel = contentManagementService.createBlockWithDefaultTag(content.getBlock());
            content.getBlock().setBlockId(btRel.getBlockId());
            content.getTag().setTagId(btRel.getTagId());
        } else {
            contentManagementService.updateContent(content.getBlock(), content.getTag(), content.getSort());
        }
        return Response.success();
    }

    private void validate(Content content) {
        checkParameter(content);
        if (content.getIsNew().equals("1")) {
            return;
        }

        Block block = content.getBlock();
        Tag tag = content.getTag();
        if (block == null || block.getBlockId() == null || tag == null || tag.getTagId() == null) {
            throw new ValidateException(new ErrorResponse("Parameter missing."));
        }
        Block targetBlock = blockService.getBlockById(block.getBlockId());
        if (targetBlock == null) {
            throw new ValidateException(new ErrorResponse("The block does not exist."));
        }

        if (tag.getTagId() != -1) {
            Map<String, Object> tagRelInfo = contentManagementService.getTagAndBlockRelByTagId(tag.getTagId());
            if (tagRelInfo == null) {
                throw new ValidateException(new ErrorResponse("The tag does not exist."));
            }
            if (tagRelInfo.get("block_tag_id") != null && Long.parseLong(tagRelInfo.get("block_id").toString()) != block.getBlockId()) {
                throw new ValidateException(new ErrorResponse("The tag has bind to other block [" + tagRelInfo.get("block_id") + "]."));
            }
        }

    }

    private void checkParameter(Content content) {
        if (StringUtils.isEmpty(content.getIsNew())) {
            throw new ValidateException(new ErrorResponse("The action type is missing"));
        }
        if (StringUtils.isEmpty(content.getBlock().getTitle())) {
            throw new ValidateException(new ErrorResponse("Block title is missing"));
        }
        if (StringUtils.isEmpty(content.getBlock().getSubtitle())) {
            throw new ValidateException(new ErrorResponse("Block subtitle is missing"));
        }
        //        if (StringUtils.isEmpty(content.getBlock().getTitleEnglish())) {
        //            throw new ValidateException(new ErrorResponse("Block title english is missing"));
        //        }
        if (StringUtils.isEmpty(content.getBlock().getCoverImg())) {
            throw new ValidateException(new ErrorResponse("Block cover image is missing"));
        }
        if (StringUtils.isEmpty(content.getBlock().getBgColor())) {
            throw new ValidateException(new ErrorResponse("Block background color is missing"));
        }
        if (content.getBlock().getSortOrder() == null) {
            throw new ValidateException(new ErrorResponse("Block sort order is missing"));
        }

    }

    //    @GetMapping(value = "/temp")
    //    public Response temp() throws IOException {
    //        List<Block> list = blockService.listAllBlock();
    //        for (Block block : list) {
    //            if (block.getStatus() == 0 || block.getContent() == null) {
    //                continue;
    //            }
    //
    //            FileOutputStream out = new FileOutputStream("C:\\Users\\YouFeng.Zhu\\Desktop\\html\\" + block.getTitle() + ".html");
    //            out.write(block.getContent());
    //            out.close();
    //        }
    //
    //        return Response.success();
    //    }
}
