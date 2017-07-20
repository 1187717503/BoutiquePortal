package com.intramirror.user.core.impl.vendor;

import com.intramirror.user.api.service.vendor.IQueryVendorService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.VendorMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/20.
 */
@Service(value = "userQueryVendorServiceImpl")
public class QueryVendorServiceImpl extends BaseDao implements IQueryVendorService{

    private VendorMapper vendorMapper;

    @Override
    public List<Map<String, Object>> queryAllVendor(Map<String, Object> params) throws Exception {
        return vendorMapper.queryAllVendor(params);
    }

    @Override
    public List<Map<String, Object>> queryRuleVendor(Map<String, Object> params) throws Exception {
        return vendorMapper.queryRuleVendor(params);
    }

    @Override
    public void init() {
        vendorMapper = this.getSqlSession().getMapper(VendorMapper.class);
    }
}
