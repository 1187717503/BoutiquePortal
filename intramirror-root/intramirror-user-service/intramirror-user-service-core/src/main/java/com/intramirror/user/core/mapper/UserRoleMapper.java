package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleMapper {

    List<UserRole> getUserRoleByUserIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);
}