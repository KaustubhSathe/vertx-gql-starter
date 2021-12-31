package com.kaustubh.vertx.gql.starter;

import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@AllArgsConstructor
@Slf4j
public class Request {
    DataFetchingEnvironment environment;
    MultiMap headers;
    JsonObject body;


    public String getHeader(String name){
        return headers != null ? headers.get(name) : null;
    }

    public Object getBodyParam(String name){
        return body != null ? body.getValue(name) : null;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder("Request{");
        sb.append("body=").append(body != null ? body.toString() : "no body");
        sb.append(", headers=").append(headers != null ? headers.toString().replace("\n", ", ") : "no headers");
        sb.append("}");
        return sb.toString();
    }


}
