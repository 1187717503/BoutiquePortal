package com.intramirror.order.core.impl.ext;

import com.intramirror.order.api.vo.ReportRequestVO;
import com.intramirror.order.api.vo.ReportResponseVO;
import com.intramirror.order.api.vo.ReportVO;
import com.intramirror.order.api.vo.SeasonVO;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.ext.ReportExtMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportExtServiceImpl extends BaseDao{
    private ReportExtMapper reportExtMapper;

    @Override
    public void init() {
        reportExtMapper = this.getSqlSession().getMapper(ReportExtMapper.class);
    }

    public ReportResponseVO search(ReportRequestVO vo){
        ReportResponseVO reportResponseVO = new ReportResponseVO();
        reportResponseVO.setPageNum(vo.getPageNum());
        reportResponseVO.setPageSize(vo.getPageSize());
        vo.setStart((vo.getPageNum()-1)* vo.getPageSize());
        List<ReportVO> reportVOS = reportExtMapper.searchByParam(vo);
        reportResponseVO.setReportVOS(reportVOS);
        return reportResponseVO;
    }

    public Integer count(ReportRequestVO vo){
        Long count = reportExtMapper.countByParam(vo);
        return count.intValue();
    }

    public List<SeasonVO> queryAllSeason(){
        return reportExtMapper.queryAllSeason();
    }

    public Long queryVendorIdByUserId(Long userId){
        Long vendorId = reportExtMapper.queryVendorIdByUserId(userId);
        return vendorId;
    }
}
