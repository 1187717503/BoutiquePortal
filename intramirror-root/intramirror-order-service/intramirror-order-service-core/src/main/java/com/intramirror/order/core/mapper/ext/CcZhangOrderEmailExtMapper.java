package com.intramirror.order.core.mapper.ext;

import com.intramirror.order.api.dto.CcZhangOrderEmailDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Created on 2019/1/20.
 *
 * @author yfding
 */
public interface CcZhangOrderEmailExtMapper {

    List<CcZhangOrderEmailDTO> selectOrderByActionType(@Param("actionType") Long actionType);

    int updateSendEmailFlag(@Param("ccZhangOrderEmailId") Long ccZhangOrderEmailId);
}
