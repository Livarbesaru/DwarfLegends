package com.dwarflegends.webapp.domain.exception;
public class AURException extends Exception{
    private final ErrorCodes errorCodes;

    public AURException(String msg,Throwable throwable){
        super(msg,throwable);
        errorCodes = null;
    }

    public AURException(String msg, ErrorCodes errorCodes){
        super(msg);
        this.errorCodes = errorCodes;
    }

    public AURException(String msg, ErrorCodes errorCodes, Throwable throwable){
        super(msg,throwable);
        this.errorCodes = errorCodes;
    }

    public ErrorCodes getErrorCodes() {
        return errorCodes;
    }
}
