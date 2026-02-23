package com.knowledge.core.common.result;


public interface ResultCode {

    Integer SUCCESS = 200;
    Integer ERROR = 500;
    Integer VALIDATION_FAILED = 400;
    Integer UNAUTHORIZED = 401;
    Integer FORBIDDEN = 403;
    Integer NOT_FOUND = 404;
}