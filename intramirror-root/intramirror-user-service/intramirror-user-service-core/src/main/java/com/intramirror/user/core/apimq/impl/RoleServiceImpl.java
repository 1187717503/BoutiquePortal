package com.intramirror.user.core.apimq.impl;


import com.intramirror.user.api.model.Role;
import com.intramirror.user.api.service.RoleService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public class RoleServiceImpl extends BaseDao implements RoleService {

    private RoleMapper roleMapper;

    public void init() {
        roleMapper = this.getSqlSession().getMapper(RoleMapper.class);
    }


    @Override
    public Role getRoleById(Long roleId) {
        return roleMapper.getRoleByIdAndEnabled(roleId);
    }
}
