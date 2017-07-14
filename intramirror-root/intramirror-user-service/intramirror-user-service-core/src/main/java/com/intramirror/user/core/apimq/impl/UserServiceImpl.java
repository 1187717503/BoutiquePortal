package com.intramirror.user.core.apimq.impl;


import com.intramirror.user.api.model.User;
import com.intramirror.user.api.service.UserService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public class UserServiceImpl extends BaseDao implements UserService {

    private UserMapper userMapper;

    public void init() {
        userMapper = this.getSqlSession().getMapper(UserMapper.class);
    }

    public User getUserByEmail(String email, Boolean enabled) throws Exception {
        try {
            User user = userMapper.getUserByEmailAndEnabled(email, true);
            return user;
        } catch (Exception e) {
            throw e;
        }
    }
}
