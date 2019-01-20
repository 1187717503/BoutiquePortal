package com.intramirror.order.core.impl;

import com.intramirror.common.email.EmailUtils;
import com.intramirror.common.excel.EasyPoiUtil;
import com.intramirror.order.api.common.enums.CcZhangOrderEmailActionTypeEnum;
import com.intramirror.order.api.dto.CcZhangOrderEmailDTO;
import com.intramirror.order.api.service.ICcZhangOrderEmailService;
import com.intramirror.order.api.vo.ConfirmedExcelVO;
import com.intramirror.order.core.mapper.ext.CcZhangOrderEmailExtMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2019/1/20.
 *
 * @author yfding
 */
@Service
public class CcZhangOrderEmailServiceImpl implements ICcZhangOrderEmailService {

    @Autowired
    private EasyPoiUtil easyPoiUtil;

    @Resource
    private CcZhangOrderEmailExtMapper ccZhangOrderEmailExtMapper;

    private static final String confirmedTitle = "香港店张公司客人已确认订单列表";

    private static final String shippedTitle = "香港店张公司客人已发货订单列表(含E特快快递单号)";

    private static final String confirmedPath = "/Users/yfding/Downloads/orderExcel";

    private static final List<String> confirmedEmailTitle = new ArrayList<>();

    private static final List<String> reservedEmailTitle = new ArrayList<>();

    {
        confirmedEmailTitle.add("Order Number");
        confirmedEmailTitle.add("Order Line Number");
        confirmedEmailTitle.add("Designer Id");
        confirmedEmailTitle.add("Color Code");
        confirmedEmailTitle.add("Brand");
        confirmedEmailTitle.add("Season");
        confirmedEmailTitle.add("Size");
        confirmedEmailTitle.add("Product Name");
        confirmedEmailTitle.add("Shipping Fee");
        confirmedEmailTitle.add("L3 Category");
        confirmedEmailTitle.add("Buyer Name");
        confirmedEmailTitle.add("Buyer Contact");
        confirmedEmailTitle.add(" Sale Price");

        reservedEmailTitle.add("Order Number");
        reservedEmailTitle.add("Order Line Number");
        reservedEmailTitle.add("EMS");
        reservedEmailTitle.add("Designer Id");
        reservedEmailTitle.add("Color Code");
        reservedEmailTitle.add("Brand");
        reservedEmailTitle.add("Season");
        reservedEmailTitle.add("Product Name");
    }

    @Override
    public void confirmedOrderToEmail() {
        String path = confirmedPath + confirmedEmailTitle + "_" + DateUtils.formatDate(new Date(), "yyyyMMdd") + ".xls";
        List<CcZhangOrderEmailDTO> ccZhangOrderEmailDTOList = ccZhangOrderEmailExtMapper.selectOrderByActionType(
                CcZhangOrderEmailActionTypeEnum.confirmed.getActionType());
        List<ConfirmedExcelVO> confirmedExcelVOS = new ArrayList<>();
        BeanUtils.copyProperties(ccZhangOrderEmailDTOList, confirmedExcelVOS, List.class);
        try {
            easyPoiUtil.createSimpleExcel(confirmedEmailTitle, (List<Object>) (Object) confirmedExcelVOS, path);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shippedOrderToEmail() {
        ccZhangOrderEmailExtMapper.selectOrderByActionType(CcZhangOrderEmailActionTypeEnum.shipped.getActionType());

    }
}
