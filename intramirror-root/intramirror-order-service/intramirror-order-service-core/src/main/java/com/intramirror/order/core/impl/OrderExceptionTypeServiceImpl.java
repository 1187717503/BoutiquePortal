/**
 *
 */
package com.intramirror.order.core.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.intramirror.order.api.service.IOrderExceptionTypeService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderExceptionTypeMapper;

/**
 * @author yml
 */
@Service
public class OrderExceptionTypeServiceImpl extends BaseDao implements IOrderExceptionTypeService {

    private OrderExceptionTypeMapper orderExceptionTypeMapper;

    @Override
    public void init() {
        orderExceptionTypeMapper = this.getSqlSession().getMapper(OrderExceptionTypeMapper.class);
    }

    @Override
    public List<Map<String, Object>> getExceptionType() {
        List<Map<String, Object>> exceptionTypeList = orderExceptionTypeMapper.getExceptionType();
        List<Map<String, Object>> list = new LinkedList<>();
        if (exceptionTypeList != null && exceptionTypeList.size() > 0) {
            for (Map<String, Object> exceptionType : exceptionTypeList) {
                //过滤type=4的类型
                if (!"4".equals(exceptionType.get("order_exception_type_id").toString())) {
                    String description = exceptionType.get("description").toString();
                    String[] strings = description.split("-");
                    exceptionType.put("description", strings[0]);
                    if (strings.length > 1) {
                        exceptionType.put("description_detail", strings[1]);
                    }
                    list.add(exceptionType);
                }
            }
        }
        return list;
    }


}
