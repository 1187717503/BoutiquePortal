package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.UserRole;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper {

    UserRole getUserRoleByUserIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);
}