package com.intramirror.user.api.service;

import com.intramirror.user.api.model.PasswordMail;

public interface PasswordMailService {

    void insert(PasswordMail passwordMail);

    void updateIschange(Long id);

    PasswordMail getNewPasswordMail(String email);
}
