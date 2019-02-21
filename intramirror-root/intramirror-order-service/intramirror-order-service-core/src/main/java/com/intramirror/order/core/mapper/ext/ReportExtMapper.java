package com.intramirror.order.core.mapper.ext;

import com.intramirror.order.api.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface ReportExtMapper {
    /* 报表统计查询*/
    List<ReportVO> searchByParam(ReportRequestVO requestVO);

    Long countByParam(ReportRequestVO requestVO);

    List<SeasonVO> queryAllSeason();

    Long queryVendorIdByUserId(Long userId);

    List<BrandVO> queryVendorBrand(@Param("vendorIds") List<Long> vendorIds);

    List<Long> queryVendorIdsByParentId(Long vendorId);

    List<VendorVO> queryVendorsByUserId(Long userId);

    List<VendorVO> queryVendorByUserId(Long userId);
}
