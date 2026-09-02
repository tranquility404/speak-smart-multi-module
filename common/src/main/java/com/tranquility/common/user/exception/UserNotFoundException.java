package com.tranquility.common.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User Not Found!");
    }
}
