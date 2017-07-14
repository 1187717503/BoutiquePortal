package com.intramirror.user.api.service;


import com.intramirror.user.api.model.User;

public interface UserService {

    User getUserByEmail(String email, Boolean enabled) throws Exception;
}
