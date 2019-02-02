package com.intramirror.order.core.impl;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.email.EmailUtils;
import com.intramirror.common.excel.EasyPoiUtil;
import com.intramirror.order.api.common.enums.CcZhangOrderEmailActionTypeEnum;
import com.intramirror.order.api.dto.CcZhangOrderEmailDTO;
import com.intramirror.order.api.service.ICcZhangOrderEmailService;
import com.intramirror.order.api.vo.ConfirmedExcelVO;
import com.intramirror.order.api.vo.ShippedExcelVO;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.ext.CcZhangOrderEmailExtMapper;
import com.intramirror.order.core.properties.CcZhangEmailProperties;
import com.intramirror.utils.LogUtil.LogContext;
import com.intramirror.utils.LogUtil.LogUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2019/1/20.
 *
 * @author yfding
 */
@Service
public class CcZhangOrderEmailServiceImpl extends BaseDao implements ICcZhangOrderEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CcZhangOrderEmailServiceImpl.class);

    private CcZhangOrderEmailExtMapper ccZhangOrderEmailExtMapper;

    @Override
    public void init() {
        ccZhangOrderEmailExtMapper = this.getSqlSession().getMapper(CcZhangOrderEmailExtMapper.class);
    }

    @Autowired
    private EasyPoiUtil easyPoiUtil;

    @Autowired
    private CcZhangEmailProperties ccZhangEmailProperties;

    private static final String confirmedTitle = "香港店张公司客人已确认订单列表";

    private static final String shippedTitle = "香港店张公司客人已发货订单列表(含E特快快递单号)";

    private static final List<String> confirmedEmailTitle = new ArrayList<>();

    private static final List<String> shippedEmailTitle = new ArrayList<>();

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

        shippedEmailTitle.add("Order Number");
        shippedEmailTitle.add("Order Line Number");
        shippedEmailTitle.add("EMS");
        shippedEmailTitle.add("Designer Id");
        shippedEmailTitle.add("Color Code");
        shippedEmailTitle.add("Brand");
        shippedEmailTitle.add("Season");
        shippedEmailTitle.add("Product Name");
    }

    @Override
    public void confirmedOrderToEmail() {
        handle(confirmedTitle, CcZhangOrderEmailActionTypeEnum.confirmed, confirmedEmailTitle, ccZhangEmailProperties.getConfirmedFrom(),
                ccZhangEmailProperties.getConfirmedPassword(), ccZhangEmailProperties.getConfirmedTo());
    }

    @Override
    public void shippedOrderToEmail() {
        handle(shippedTitle, CcZhangOrderEmailActionTypeEnum.shipped, shippedEmailTitle, ccZhangEmailProperties.getShippedFrom(),
                ccZhangEmailProperties.getShippedPassword(), ccZhangEmailProperties.getShippedTo());
    }

    private void handle(String title, CcZhangOrderEmailActionTypeEnum ccZhangOrderEmailActionTypeEnum, List<String> titles, String from, String password,
            String to) {
        try {
            LogContext context = new LogContext(ccZhangOrderEmailActionTypeEnum.getActionType(), "CcZhangOrderEmail");
            String path = ccZhangEmailProperties.getPath() + ccZhangOrderEmailActionTypeEnum.getActiveTypeValue() + "_" + DateUtils.formatDate(new Date(),
                    "yyyyMMdd") + ".xls";
            List<CcZhangOrderEmailDTO> ccZhangOrderEmailDTOList = ccZhangOrderEmailExtMapper.selectOrderByActionType(
                    ccZhangOrderEmailActionTypeEnum.getActionType());
            if (CollectionUtils.isEmpty(ccZhangOrderEmailDTOList)) {
                LogUtil.info(context, "No mail needs to be sent.");
                return;
            }

            List<Object> objects;
            if (ccZhangOrderEmailActionTypeEnum.getActionType().intValue() == CcZhangOrderEmailActionTypeEnum.confirmed.getActionType().intValue()) {
                List<ConfirmedExcelVO> list = new ArrayList<>();
                for (CcZhangOrderEmailDTO ccZhangOrderEmailDTO : ccZhangOrderEmailDTOList) {
                    ConfirmedExcelVO confirmedExcelVO = new ConfirmedExcelVO();
                    BeanUtils.copyProperties(ccZhangOrderEmailDTO, confirmedExcelVO);
                    list.add(confirmedExcelVO);
                }
                objects = (List<Object>) (Object) list;
            } else {
                List<ShippedExcelVO> list = new ArrayList<>();
                for (CcZhangOrderEmailDTO ccZhangOrderEmailDTO : ccZhangOrderEmailDTOList) {
                    ShippedExcelVO shippedExcelVO = new ShippedExcelVO();
                    BeanUtils.copyProperties(ccZhangOrderEmailDTO, shippedExcelVO);
                    list.add(shippedExcelVO);
                }
                objects = (List<Object>) (Object) list;
            }

            easyPoiUtil.createSimpleExcel(titles, objects, path);
            EmailUtils.sendAttachment(from, password, title, to, path);
            LogUtil.info(context, "Send mail successfully. ccZhangOrderEmailDTOList:{}", JSONObject.toJSONString(ccZhangOrderEmailDTOList));

            for (CcZhangOrderEmailDTO ccZhangOrderEmailDTO : ccZhangOrderEmailDTOList) {
                ccZhangOrderEmailExtMapper.updateSendEmailFlag(Long.parseLong(ccZhangOrderEmailDTO.getIgnoreCcZhangOrderEmailId()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(" ERROR ", e);
        }
    }

}
