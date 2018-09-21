package com.intramirror.user.core.apimq.impl;

import com.intramirror.user.api.model.PasswordMail;
import com.intramirror.user.api.service.PasswordMailService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.PasswordMailMapper;
import org.springframework.stereotype.Service;

@Service
public class PasswordMailServiceImpl extends BaseDao implements PasswordMailService {

    private PasswordMailMapper passwordMailMapper;

    @Override
    public void init() {
        passwordMailMapper = this.getSqlSession().getMapper(PasswordMailMapper.class);
    }
    @Override
    public void insert(PasswordMail passwordMail) {
        passwordMailMapper.insert(passwordMail);
    }

    @Override
    public void updateIschange(Long id) {
        passwordMailMapper.updateIschange(id);
    }

    @Override
    public PasswordMail getNewPasswordMail(String email) {
        return passwordMailMapper.getNewPasswordMail(email);
    }

}
