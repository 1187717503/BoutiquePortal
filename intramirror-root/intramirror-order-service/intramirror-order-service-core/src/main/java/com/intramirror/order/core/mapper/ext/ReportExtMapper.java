package com.intramirror.order.core.mapper.ext;

import com.intramirror.order.api.vo.ReportRequestVO;
import com.intramirror.order.api.vo.ReportVO;
import com.intramirror.order.api.vo.SeasonVO;

import java.util.List;
import java.util.Map;


public interface ReportExtMapper {
    /* 报表统计查询*/
    List<ReportVO> searchByParam(ReportRequestVO requestVO);

    Long countByParam(ReportRequestVO requestVO);

    List<SeasonVO> queryAllSeason();

    Long queryVendorIdByUserId(Long userId);
}
