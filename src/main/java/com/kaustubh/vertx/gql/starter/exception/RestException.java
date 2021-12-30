package com.kaustubh.vertx.gql.starter.exception;

import com.kaustubh.vertx.gql.starter.io.Error;
import io.vertx.core.json.JsonObject;

public class RestException extends RuntimeException{
    private final Error error;
    private int httpStatusCode = 400;

    public RestException(Throwable cause, Error error, int httpStatusCode) {
        super(cause);
        this.error = error;
        this.httpStatusCode = httpStatusCode;
    }

    public RestException(String message, Error error, int httpStatusCode) {
        super(message);
        this.error = error;
        this.httpStatusCode = httpStatusCode;
    }

    public RestException(Throwable cause, Error error, int httpStatusCode, boolean oldErrorKeys) {
        super(cause);
        this.error = error;
        this.httpStatusCode = httpStatusCode;
    }

    public RestException(String message, Error error, int httpStatusCode, boolean oldErrorKeys) {
        super(message);
        this.error = error;
        this.httpStatusCode = httpStatusCode;
    }

    public RestException(Throwable cause, Error error) {
        super(cause);
        this.error = error;
    }

    public RestException(String message, Error error) {
        super(message);
        this.error = error;
    }

    public RestException(Throwable cause, Error error, boolean oldErrorKeys) {
        super(cause);
        this.error = error;
    }

    public RestException(String message, Error error, boolean oldErrorKeys) {
        super(message);
        this.error = error;
    }

    public RestException(RestError restError) {
        super(restError.getErrorMessage());
        this.error = restError.getError();
        this.httpStatusCode = restError.getHttpStatusCode();
    }

    public RestException(RestError restError, Throwable cause) {
        super(restError.getErrorMessage(), cause);
        this.error = restError.getError();
        this.httpStatusCode = restError.getHttpStatusCode();
    }

    public Error getError() {
        return error;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public JsonObject toJson() {
        JsonObject errorJson = new JsonObject()
                .put("message", this.error.getMessage())
                .put("cause", this.getCause() == null ? this.getMessage() : this.getCause().getMessage())
                .put("code", this.error.getCode());

        return new JsonObject()
                .put("error", errorJson);
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}

