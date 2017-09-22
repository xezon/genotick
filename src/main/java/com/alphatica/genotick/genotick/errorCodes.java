package com.alphatica.genotick.genotick;


enum errorCodes {
    NO_ERROR(0),
    NO_INPUT(1),
    NO_OUTPUT(2),
    UNKNOWN_ARGUMENT(3);

    private final int code;

    errorCodes(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
