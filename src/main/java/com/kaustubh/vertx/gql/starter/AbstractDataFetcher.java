package com.kaustubh.vertx.gql.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaustubh.vertx.commons.entity.VertxEntity;
import com.kaustubh.vertx.commons.guice.GuiceContext;
import com.kaustubh.vertx.gql.starter.exception.RestException;
import com.kaustubh.vertx.gql.starter.io.Error;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.MultiMap;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletionStage;


@Slf4j
@Data
public abstract class AbstractDataFetcher<T> implements DataFetcher<CompletionStage<T>> {
    private static final Error INVALID_PARAM_ERROR = Error.of("MG1000", "Missing Parameters");
    private static final RestException INVALID_REST_EXCEPTION = new RestException("Invalid or Missing Request Params.", INVALID_PARAM_ERROR);

    protected ObjectMapper objectMapper;
    private List<String> requiredHeaders;
    private String type;
    private String field;
    private String produces;
    private String consumes;
    private long timeout;

    protected Single<Request> validateRequest(Request request) {
        try {
            validateRequestHeaders(request.getHeaders());
            return Single.just(request);
        } catch (Exception e) {
            log.error("Error in request! headers required : {}, headers received : {}", requiredHeaders, request.getHeaders(), e);
            return Single.error(e);
        }
    }

    protected void validateRequestHeaders(MultiMap headers) throws Exception {
        if (!Optional.ofNullable(getRequiredHeaders())
                .orElse(new ArrayList<>())
                .stream()
                .allMatch(headers::contains)) {
            throw INVALID_REST_EXCEPTION;
        }
    }

    @Override
    public CompletionStage<T> get(DataFetchingEnvironment env) throws Exception {
        final long startTime = System.currentTimeMillis();
        var headers = ((RoutingContext)env.getContext()).request().headers();
        var jsonBody = ((RoutingContext)env.getContext()).getBody().toJson();
        final Request request = new Request(
                env,
                headers,
                (JsonObject) jsonBody
        );

        return validateRequest(request)
                .flatMap(req -> this.get(req))
                .toCompletionStage();
    }

    public abstract Single<T> get(Request request) throws Exception;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (AbstractDataFetcher<?>) o;
        return Objects.equals(this.getType(), that.getType()) && Objects.equals(this.getField(), that.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getType(), this.getField());
    }
}
