package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper {

    Role getRoleByIdAndEnabled(@Param("roleId") Long roleId);

}