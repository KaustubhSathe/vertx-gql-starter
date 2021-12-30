package com.kaustubh.vertx.gql.starter;


import com.kaustubh.vertx.commons.guice.GuiceContext;
import com.kaustubh.vertx.gql.starter.io.Error;
import io.vertx.ext.web.handler.graphql.schema.VertxDataFetcher;
import io.vertx.rxjava3.ext.web.RoutingContext;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.util.List;

@Slf4j
@Data
public abstract class AbstractDataFetcher<T> extends VertxDataFetcher<T> {
    private static final Error INVALID_PARAM_ERROR = Error.of("MG1000", "Missing Parameters");

    protected ObjectMapper objectMapper;
    private List<String> requiredHeaders;
    private String type;
    private String parameter;
    private String produces;
    private String consumes;
    private long timeout;

    public AbstractDataFetcher(io.vertx.ext.web.handler.graphql.schema.VertxDataFetcher delegate) {
        super(delegate);
        this.objectMapper = GuiceContext.getInstance(ObjectMapper.class);
    }

    @Override
    public abstract io.vertx.ext.web.handler.graphql.schema.VertxDataFetcher getDelegate();

    private void prepareResponse(RoutingContext context, T response, long startTime){
        if(!context.response().ended()){

        }
    }

}
