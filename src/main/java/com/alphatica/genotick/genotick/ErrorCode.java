package com.alphatica.genotick.genotick;


enum ErrorCode {
    NO_ERROR(0),
    NO_INPUT(1),
    NO_OUTPUT(2),
    UNKNOWN_ARGUMENT(3),
    INVALID_SESSION(4),
    INSUFFICIENT_DATA(5);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
