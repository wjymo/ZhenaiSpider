package com.wjy.exception;

import lombok.Data;

@Data
public class ZhenaiException extends RuntimeException {
    private String message;

    public ZhenaiException(String message) {
        super(message);
    }

    public ZhenaiException() {
    }

}
