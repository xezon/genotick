package com.alphatica.genotick.utility;

public class MethodName {
    
    private static final int CLIENT_CODE_STACK_INDEX;

    static {
        int index = 0;
        for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
            index++;
            if (stack.getClassName().equals(MethodName.class.getName())) {
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = index;
    }
    
    public static String get(int depth) {    
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        return stack[CLIENT_CODE_STACK_INDEX + depth].getMethodName();
    }
}
