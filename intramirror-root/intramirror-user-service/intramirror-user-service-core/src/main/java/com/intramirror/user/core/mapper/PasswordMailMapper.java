package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.PasswordMail;
import org.apache.ibatis.annotations.Param;

public interface PasswordMailMapper {

    void insert(PasswordMail passwordMail);

    void updateIschange(@Param("id") Long id);

    PasswordMail getNewPasswordMail(@Param("email") String email);

}
