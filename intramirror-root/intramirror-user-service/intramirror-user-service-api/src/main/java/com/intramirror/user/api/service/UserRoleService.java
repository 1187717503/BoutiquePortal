package com.intramirror.user.api.service;


import com.intramirror.user.api.model.UserRole;

public interface UserRoleService {

    UserRole getUserRoleByUserId(Long userId,Boolean enabled);
}
