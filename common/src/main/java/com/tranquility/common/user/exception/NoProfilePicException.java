package com.tranquility.common.user.exception;

public class NoProfilePicException extends RuntimeException {
    public NoProfilePicException() {
        super("No profile pic found. Please set a profile picture.");
    }
}
