package com.tranquility.common.user.model;

import com.tranquility.common.model.Role;

import java.util.UUID;

public interface UserPrincipal {
    UUID getId();
    String getEmail();
    String getPassword();
    Role getRole();
}