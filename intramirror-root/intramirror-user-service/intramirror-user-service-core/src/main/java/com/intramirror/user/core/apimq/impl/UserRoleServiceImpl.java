package com.intramirror.user.core.apimq.impl;


import com.intramirror.user.api.model.UserRole;
import com.intramirror.user.api.service.UserRoleService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 */
@Service
public class UserRoleServiceImpl extends BaseDao implements UserRoleService {

    private UserRoleMapper userRoleMapper;

    public void init() {
        userRoleMapper = this.getSqlSession().getMapper(UserRoleMapper.class);
    }


    @Override
    public UserRole getUserRoleByUserId(Long userId, Boolean enabled) {
        UserRole userRole = null;
        List<UserRole> userRoleList = userRoleMapper.getUserRoleByUserIdAndEnabled(userId, enabled);
        for (UserRole temp : userRoleList) {
            if (temp != null) {
                userRole = temp;
                break;
            }
        }
        return userRole;
    }
}
