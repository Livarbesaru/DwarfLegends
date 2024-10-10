package com.dwarflegends.webapp.domain.exception;

public enum ErrorCodes {
    ERROR_400("BAD REQUEST"),
    ERROR_500("INTERNAL SERVER ERROR");

    private String errorMsg;
    ErrorCodes(String errorMsg){
        this.errorMsg = errorMsg;
    }
}
