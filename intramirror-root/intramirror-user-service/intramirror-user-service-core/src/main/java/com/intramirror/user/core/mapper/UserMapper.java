package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.User;
import org.apache.ibatis.annotations.Param;


public interface UserMapper {
    User getUserByEmailAndEnabled(@Param("email") String email, @Param("enabled") Boolean enabled);

    User getUserByIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);

    void updatePwd(@Param("userId") Long userId, @Param("pwd") String pwd);
}