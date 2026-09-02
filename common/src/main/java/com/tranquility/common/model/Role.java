package com.tranquility.common.model;

public enum Role {
    ADMIN,
    Moderator,
    USER;

    public String getAuthority() {
        return this.name();
    }
}