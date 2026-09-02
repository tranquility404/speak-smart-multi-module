package com.tranquility.common.auth;

import com.tranquility.common.user.model.UserPrincipal;

public interface UserAuthPort {
    UserPrincipal findUserByEmail(String email);
    void createUserForExternalAuth(String name, String email, String password, String picture);
    void createUserForEmailAuth(String name, String email, String password);
}