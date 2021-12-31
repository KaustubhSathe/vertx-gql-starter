package com.kaustubh.vertx.gql.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaustubh.vertx.gql.starter.exception.RestException;
import com.kaustubh.vertx.gql.starter.io.Error;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletionStage;


@Slf4j
@Data
public abstract class AbstractDataFetcher<T> implements DataFetcher<T> {
    private static final Error INVALID_PARAM_ERROR = Error.of("MG1000", "Missing Parameters");
    private static final RestException INVALID_REST_EXCEPTION = new RestException("Invalid or Missing Request Params.", INVALID_PARAM_ERROR);

    protected ObjectMapper objectMapper;
    private List<String> requiredHeaders;
    private String type;
    private String parameter;
    private String produces;
    private String consumes;
    private long timeout;

    private void prepareResponse(RoutingContext context, T response, long startTime){
        if(!context.response().ended()){

        }
    }

    protected HttpServerResponse setResponseHeader(HttpServerResponse httpServerResponse, T response){
        httpServerResponse.putHeader("content-type", produces);
        return httpServerResponse;
    }

    protected void validateRequestHeaders(MultiMap headers) throws Exception {
        if(!Optional.ofNullable(getRequiredHeaders())
                .orElse(new ArrayList<>())
                .stream()
                .allMatch(headers::contains)){
            throw INVALID_REST_EXCEPTION;
        }
    }

    private Map<String,String> getMap(MultiMap multiMap){
        Map<String,String> map = new HashMap<>();
        for(var entry : multiMap.entries()){
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }

    @Override
    public T get(DataFetchingEnvironment var1) throws Exception {
            
    }

    public abstract Single<T> get(Request request) throws Exception;

}
