package com.tranquility.common.utils;

public class CursorCodecException extends RuntimeException {
    public Exception srcExc;
    public CursorCodecException(Exception srcExc) {
        super();
        this.srcExc = srcExc;
    }
}
