package com.intramirror.web.controller.promotion;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.enums.SortColumn;
import com.intramirror.product.api.exception.BusinessException;
import com.intramirror.product.api.service.promotion.IPromotionService;
import com.intramirror.product.api.entity.promotion.SortPromotion;
import com.intramirror.web.controller.cache.CategoryCache;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/1/12.
 * @author 123
 */
@RestController
@RequestMapping("/sort/promotion")
public class PromotionSortManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionSortManageController.class);

    @Autowired
    IPromotionService promotionService;

    @Autowired
    CategoryCache categoryCache;

    @GetMapping(value = "/{promotionId}")
    public Response listSortColumn(@PathVariable(value = "promotionId") Long promotionId) {
        LOGGER.info("Start to get sort column with promotion id {}.", promotionId);

        List<Map<String, Object>> data = promotionService.listSortColumn(promotionId);

        promotionService.updatePromotionSaveTime(promotionId);
        return Response.status(StatusType.SUCCESS).data(data);
    }

    @PutMapping(value = "/{promotionId}", consumes = "application/json")
    public Response setSortColumn(@PathVariable(value = "promotionId") Long promotionId, @RequestBody List<SortPromotion> body) throws BusinessException {
        if (promotionId == null) {
            LOGGER.error("Promotion id is empty.");
            throw new ValidateException(new ErrorResponse("Promotion id is empty."));
        }

        if (body == null || body.size() == 0) {
            LOGGER.error("The request column is empty, {}.", body);
            throw new ValidateException(new ErrorResponse("The request column is empty."));
        }

        for (SortPromotion sortPromotion : body) {
            if (sortPromotion.getPromotionSeqId() == null || sortPromotion.getSort() == null || sortPromotion.getSeqType() == null) {
                throw new ValidateException(new ErrorResponse("Parameter is missing."));
            }
        }
        promotionService.updatePromotionSaveTime(promotionId);
        return Response.status(StatusType.SUCCESS).data(promotionService.updateSortPromotion(body));
    }

    @GetMapping(value = "/{promotionId}/{columnName}")
    public Response listSortItemsByType(@PathVariable(value = "promotionId") Long promotionId, @PathVariable(value = "columnName") String columnName) {

        SortColumn sortColumn = SortColumn.fromString(columnName);
        if (sortColumn == null) {
            throw new ValidateException(new ErrorResponse("Column name is not correct."));
        }
        List<Map<String, Object>> data = promotionService.listSortItemByColumn(promotionId, sortColumn);
        if (SortColumn.CATEGORY == sortColumn) {
            for (Map<String, Object> categorySort : data) {
                String absCategoryName = categoryCache.getAbsolutelyCategoryPath(Long.parseLong(categorySort.get("categoryId").toString()));
                categorySort.put("name", absCategoryName);
            }
        }
        promotionService.updatePromotionSaveTime(promotionId);
        return Response.status(StatusType.SUCCESS).data(data);
    }

    @PutMapping(value = "/{promotionId}/{columnName}", consumes = "application/json")
    public Response setSortItems(@PathVariable(value = "promotionId") Long promotionId, @PathVariable(value = "columnName") String columnName,
            @RequestBody List<Map<String, Object>> body) throws BusinessException {
        SortColumn sortColumn = SortColumn.fromString(columnName);
        if (sortColumn == null) {
            throw new ValidateException(new ErrorResponse("Column name is not correct."));
        }
        promotionService.updatePromotionSaveTime(promotionId);
        return Response.status(StatusType.SUCCESS).data(promotionService.updateItemsSort(promotionId, sortColumn, body));
    }
}
