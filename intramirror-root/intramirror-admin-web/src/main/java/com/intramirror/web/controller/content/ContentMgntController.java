package com.intramirror.web.controller.content;

import com.intramirror.common.help.StringUtils;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.model.*;
import com.intramirror.product.api.service.BlockService;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.product.api.vo.tag.ProductGroupVO;
import com.intramirror.product.api.vo.tag.TagRequestVO;
import com.intramirror.product.api.vo.tag.VendorTagVO;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.cache.CategoryCache;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/11/17.
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

    @Autowired
    private ContentManagementService contentManagementService;

    @Autowired
    private ISkuStoreService skuStoreService;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private VendorService vendorService;
    @Autowired
    private IPriceChangeRuleGroupService priceChangeRuleGroupService;

    /**
     * Return block info with bind tag.
     * @param blockName
     * @param status
     * @param tagId
     * @param modifiedAtFrom
     * @param modifiedAtTo
     * @param pageSize
     * @param pageNo
     * @return
     */
    @GetMapping(value = "/blocks", produces = "application/json")
    // @formatter:off
    public Response listBlockTag(
            @RequestParam(value = "blockName", required = false) String blockName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "tagId", required = false) Long tagId,
            @RequestParam(value = "modifiedAtFrom", required = false) Long modifiedAtFrom,
            @RequestParam(value = "modifiedAtTo", required = false) Long modifiedAtTo,
            @RequestParam(value = "pageSize",required = false) Integer pageSize,
            @RequestParam(value = "pageNo",required = false) Integer pageNo,
            @RequestParam(value = "desc",required = false) Integer desc)
    // @formatter:on
    {
        int start = ((pageNo == null || pageNo < 0) ? 0 : (pageNo - 1) * pageSize);
        int limit = ((pageSize == null || pageSize < 0) ? 25 : pageSize);

        return Response.status(StatusType.SUCCESS).data(
                contentManagementService.listBlockWithTag(escapeLikeParams(blockName), status, tagId, modifiedAtFrom, modifiedAtTo, start, limit, desc));
    }

    // @formatter:off
    @GetMapping(value = "/blocks/count", produces = "application/json")
    public Response getBlocksCount(
            @RequestParam(value = "blockName", required = false) String blockName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "tagId", required = false) Long tagId,
            @RequestParam(value = "modifiedAtFrom", required = false) Long modifiedAtFrom,
            @RequestParam(value = "modifiedAtTo", required = false) Long modifiedAtTo) {
        // @formatter:on
        return Response.status(StatusType.SUCCESS).data(
                contentManagementService.getBlockSize(escapeLikeParams(blockName), status, tagId, modifiedAtFrom, modifiedAtTo));
    }

    private String escapeLikeParams(String input) {
        if (input == null) {
            return input;
        }
        return input.replace("_", "\\_").replace("%", "\\%");
    }

    /**
     * Create an new block only.
     * @param block
     * @return
     */
    @PostMapping(value = "/blocks", consumes = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Response createBlock(@RequestBody Block block) throws Exception {
        contentManagementService.createBlockWithDefaultTag(block);
        return Response.success();
    }

    /**
     * Update block info by block id.
     * @param blockId
     * @param block
     * @return
     */
    @PutMapping(value = "/blocks/{blockId}", consumes = "application/json")
    public Response updateBlock(@PathVariable Long blockId, @RequestBody Block block) {
        block.setBlockId(blockId);
        contentManagementService.updateBlockByBlockId(block);
        return Response.success();
    }

    /**
     * @param blocks
     * @return
     */
    @PutMapping(value = "/blocks", consumes = "application/json")
    public Response updateBlock(@RequestBody List<Block> blocks) {
        return Response.status(StatusType.SUCCESS).data(contentManagementService.batchUpdateBlock(blocks));
    }

    /**
     * @param tag
     * @return
     */
    @PostMapping(value = "/tags", consumes = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Response createTag(@RequestBody Tag tag) {
        contentManagementService.createTag(tag);
        return Response.success();
    }

    @GetMapping(value = "/tags", produces = "application/json")
    public Response listTags(@RequestParam(value = "orderBy", required = false) String orderBy) {
        return Response.status(StatusType.SUCCESS).data(iTagService.getTags(orderBy));
    }

    @PostMapping(value = "/tags/list", produces = "application/json")
    public Response getTags(@RequestBody TagRequestVO vo) {
        Map<String,Object> param = new HashMap<>();
        List<Long> vendors = vo.getVendorIds();
        if(CollectionUtils.isEmpty(vendors)){
            if(vo.getVendorId()!=null){
                vendors = new ArrayList<>();
                vendors.add(vo.getVendorId());
            }
        }else {
            vendors.add(vo.getVendorId());
        }
        List<Integer> types = vo.getTagTypes();
        if(CollectionUtils.isEmpty(types)){
            if(vo.getTagType() !=null){
                types = new ArrayList<>();
                types.add(vo.getTagType());
            }

        }else {
            types.add(vo.getTagType());
        }
        param.put("tagId",vo.getTagId());
        param.put("vendorIds",vendors);
        param.put("tagTypes",types);
        param.put("tagName",vo.getTagName());
        param.put("orderBy",vo.getOrderBy());
        List<Tag> tags = iTagService.getTagsByParam(param);
        return Response.status(StatusType.SUCCESS).data(tags);
    }
    @PostMapping(value = "/vendor/productGroup/list", produces = "application/json")
    public Response getVendorTags(@RequestBody TagRequestVO vo) {
        ProductGroupVO resultVo = new ProductGroupVO();

        List<Tag> tags = null;
        Response response = getTags(vo);
        if(response != null){
            tags = (List<Tag>)response.getData();
        }
        if(CollectionUtils.isNotEmpty(tags)){
            Map<Long,List<Tag>> venTagMap = new HashMap<>();
            Map<Long,List<Long>> ventTadIdMap = new HashMap<>();
            for(Tag tag : tags){
                if(tag.getTagType() == 3){ // 爆款
                    resultVo.setHot(tag);
                    continue;
                }
                if(tag.getVendorId()==null) continue;
                List<Tag> list = venTagMap.get(tag.getVendorId());
                List<Long> tagIds = ventTadIdMap.get(tag.getVendorId());
                if(list == null){
                    list = new ArrayList<>();
                    tagIds = new ArrayList<>();
                    venTagMap.put(tag.getVendorId(),list);
                    ventTadIdMap.put(tag.getVendorId(),tagIds);
                }
                list.add(tag);
                tagIds.add(tag.getTagId());
            }
            List<Vendor> vendors = null;
            if(venTagMap.size()>0){
                vendors = vendorService.getVendorByIds(new ArrayList<Long>(venTagMap.keySet()));
            }
            if(CollectionUtils.isNotEmpty(vendors)){
                List<VendorTagVO> vendorTagVOS = new ArrayList<>();
                resultVo.setVendorTagVOs(vendorTagVOS);
                for(Vendor vendor : vendors){
                    VendorTagVO tagVO = new VendorTagVO();
                    tagVO.setVendorId(vendor.getVendorId());
                    tagVO.setVendorName(vendor.getVendorName());
                    tagVO.setTags(venTagMap.get(vendor.getVendorId()));
                    tagVO.setTagIds(ventTadIdMap.get(vendor.getVendorId()));
                    vendorTagVOS.add(tagVO);
                }
            }

        }
        return Response.status(StatusType.SUCCESS).data(resultVo);
    }

    @DeleteMapping(value = "/tags/{tagId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Response deleteTag(@PathVariable Long tagId) {
        if(tagId == null){
            throw new ValidateException(new ErrorResponse("The tagId can not be null !"));
        }
        Tag tag = iTagService.selectTagByTagId(tagId);
        if(tag.getTagType()==1){
            List<BlockTagRel> blockTagRelList = blockService.getBlockTagRelByTagId(tagId);
            if (blockTagRelList.size() >= 1) {
                throw new ValidateException(new ErrorResponse("The tag has been bound with block and cannot be removed!"));
            }
        }else {
            List<PriceChangeRuleGroup> ruleLisr = priceChangeRuleGroupService.getChangeRulesByTagId(tagId);
            if(CollectionUtils.isNotEmpty(ruleLisr)){
                throw new ValidateException(new ErrorResponse("The product group has been bound with product and cannot be removed!"));
            }

        }

        contentManagementService.deleteTag(tagId);
        return Response.success();
    }

    @GetMapping(value = "/blocks/{blockId}", produces = "application/json")
    public Response getBlocksDetail(@PathVariable Long blockId) {
        return Response.status(StatusType.SUCCESS).data(contentManagementService.getBlockWithTagByBlockId(blockId));
    }

    @GetMapping(value = "/blocks/simple", produces = "application/json")
    public Response listBlockSimple() {
        return Response.status(StatusType.SUCCESS).data(blockService.listSimpleBlock());
    }

    @GetMapping(value = "/tags/{tagId}/products", produces = "application/json")
    public Response listProductByTag(@PathVariable(value = "tagId") Long tagId) {

        List<Map<String, Object>> productList = contentManagementService.listTagProductInfo(tagId);
        if (productList.size() > 0) {
            appendInfo(productList);
        }
        return Response.status(StatusType.SUCCESS).data(productList);
    }

    // 删除tag关联
    @DeleteMapping(value = "/tags/{tagId}/products/{productId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Response removeTagProduct(@PathVariable(value = "tagId") Long tagId, @PathVariable(value = "productId") Long productId) {
        contentManagementService.deleteByTagIdAndProductId(tagId, productId);
        return Response.success();
    }
    // 删除product的所有tag
    @DeleteMapping(value = "/tags/{tagId}/products")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Response removeTagProduct(@RequestBody List<TagProductRel> tagProductRelList) {
        contentManagementService.batchDeleteByTagIdAndProductId(tagProductRelList);
        return Response.success();
    }

    @PostMapping(value = "/tags/{tagId}/products", consumes = "application/json")
    public Response saveTagProductRel(@PathVariable(value = "tagId") Long tagId, @RequestBody Map<String, Object> body) {
        Long sortNum = body.get("sortNum") == null ? -1 : Long.parseLong(body.get("sortNum").toString());
        Integer tagType = body.get("tagType") == null ? 1 : Integer.valueOf(body.get("tagType").toString());
        List<Long> productIdList = (List<Long>) body.get("productIdList");

        if (productIdList.size() <= 0 || null == tagId) {
            throw new ValidateException(new ErrorResponse("Parameter could not be null!"));
        }

        Map<String, Object> map = new HashMap<>();
        Map<String,String> response = new HashMap<>();
        map.put("productIdList", productIdList);
        map.put("tag_id", tagId);
        map.put("sort_num", sortNum);
        map.put("tagType",tagType);
        iTagService.saveTagProductRel(map,response);
        return Response.success();
    }

    @GetMapping(value = "/tags/unbind", produces = "application/json")
    public Response listUnbindTags(@RequestParam(value = "blockId", required = false) Long blockId) {
        return Response.status(StatusType.SUCCESS).data(contentManagementService.listUnbindTag(blockId));
    }

    private void appendInfo(List<Map<String, Object>> productList) {

        List<Long> idList = extractProductIdList(productList);
        List<Map<String, Object>> storeList = skuStoreService.listTotalStockByProductIds(idList);
        Map<Long, Long> productStock = mapProductStock(storeList);
        //        List<Map<String, Object>> priceList = productManagementService.listPriceByProductList(productList);
        for (Map<String, Object> product : productList) {
            product.put("totalStock", productStock.get(Long.parseLong(product.get("product_id").toString())));
            Category rootCategory = categoryCache.getRootCategory(Long.parseLong(product.get("category_id").toString()));
            if (rootCategory != null) {
                if (rootCategory.getName().equals("Baby") || rootCategory.getName().equals("Boy")) {
                    product.put("gender", rootCategory.getName());
                } else {
                    product.put("gender", rootCategory.getName().substring(0, 1));
                }
            }

            //            setPriceDiscount(product, priceList);
            //如果关联spu有图片，则展示spu的图片
            if (product.get("spu_cover_img") != null && StringUtils.isNotBlank(product.get("spu_cover_img").toString())) {
                product.put("cover_img", product.get("spu_cover_img"));
            }
        }

    }

    private void setPriceDiscount(Map<String, Object> product, List<Map<String, Object>> priceList) {
        for (Map<String, Object> price : priceList) {
            if (product.get("product_id").equals(price.get("product_id"))) {
                product.put("im_price", price.get("im_price"));
                Double discount = 0D;
                Double retailPrice = Double.parseDouble(price.get("retail_price").toString());
                if (retailPrice > 0 && price.get("im_price") != null) {
                    Double boutique_price = Double.parseDouble(price.get("im_price").toString());
                    BigDecimal b = new BigDecimal(1 - boutique_price / retailPrice);
                    discount = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                product.put("im_discount", discount);
                break;
            }
        }
    }

    private List<Long> extractProductIdList(List<Map<String, Object>> productList) {
        List<Long> idList = new ArrayList<>();
        for (Map<String, Object> product : productList) {
            idList.add(Long.parseLong(product.get("product_id").toString()));
        }
        return idList;
    }

    private Map<Long, Long> mapProductStock(List<Map<String, Object>> storeList) {
        Map<Long, Long> productStockMap = new HashMap<>();
        for (Map<String, Object> store : storeList) {
            productStockMap.put(Long.parseLong(store.get("product_id").toString()), Long.parseLong(store.get("total_stock").toString()));
        }
        return productStockMap;
    }

}
