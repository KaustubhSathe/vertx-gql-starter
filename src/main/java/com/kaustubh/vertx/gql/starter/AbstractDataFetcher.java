package com.kaustubh.vertx.gql.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaustubh.vertx.gql.starter.exception.RestException;
import com.kaustubh.vertx.gql.starter.io.Error;
import com.kaustubh.vertx.gql.starter.io.Response;
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
public abstract class AbstractDataFetcher<T> implements DataFetcher<CompletionStage<Response<T>>> {
    private static final Error INVALID_PARAM_ERROR = Error.of("MG1000", "Missing Parameters");
    private static final RestException INVALID_REST_EXCEPTION = new RestException("Invalid or Missing Request Params.", INVALID_PARAM_ERROR);

    protected ObjectMapper objectMapper;
    private List<String> requiredHeaders;
    private String type;
    private String parameter;
    private HttpMethod httpMethod;
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

    protected Single<Request> validateRequest(Request request){
        try{
           if(httpMethod.equals(HttpMethod.POST)){
               validateRequestHeaders(request.getHeaders());
               return Single.just(request);
           }else{
               throw new Exception("HTTP Method must be POST");
           }
        }catch (Exception e) {
            log.error("Error in request! headers required : {}, headers received : {}", requiredHeaders,request.getHeaders(),e);
            return Single.error(e);
        }
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
    public CompletionStage<Response<T>> get(DataFetchingEnvironment env) throws Exception {
         final long startTime = System.currentTimeMillis();
         final Request request = new Request(
                 env,
                 null,
                 null
         );

         return validateRequest(request)
                 .flatMap(req -> this.get(req))
                 .toCompletionStage();
    }

    public abstract Single<Response<T>> get(Request request) throws Exception;

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || this.getClass() != o.getClass()){
            return false;
        }
        var that = (AbstractDataFetcher<?>) o;
        return Objects.equals(this.getType(), that.getType()) && Objects.equals(this.getParameter(), that.getParameter());
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.getType(), this.getParameter());
    }

    protected Throwable handleError(Throwable throwable){
        return throwable;
    }

 
}
