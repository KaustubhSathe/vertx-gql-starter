package com.kaustubh.vertx.gql.starter.exception;

import com.kaustubh.vertx.gql.starter.io.Error;

public interface RestError {
    String getErrorCode();
    String getErrorMessage();
    int getHttpStatusCode();
    default Error getError() {return Error.of(this.getErrorCode(),this.getErrorMessage());}
}
