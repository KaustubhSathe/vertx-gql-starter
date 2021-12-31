package com.kaustubh.vertx.gql.starter;

import com.kaustubh.vertx.commons.utils.ConfigUtils;
import com.kaustubh.vertx.gql.starter.config.HttpConfig;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.schema.VertxDataFetcher;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.RxHelper;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.rxjava3.ext.web.handler.StaticHandler;
import io.vertx.rxjava3.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.rxjava3.ext.web.handler.graphql.GraphiQLHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Slf4j
public class AbstractGraphQLVerticle extends AbstractVerticle {
    private final String packageName;
    private HttpServer httpServer;
    private HttpConfig httpConfig;
    public AbstractGraphQLVerticle(String packageName){
        this.packageName = packageName;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        httpConfig = ConfigUtils.fromConfigFile("config/http-server/http-server-%s.conf", HttpConfig.class);
    }

    private Single<HttpServer> startHttpServer(){
        HttpServerOptions options = new HttpServerOptions()
                .setHost(httpConfig.getHost())
                .setPort(httpConfig.getPort())
                .setIdleTimeout(httpConfig.getIdleTimeOut())
                .setUseAlpn(httpConfig.isUseAlpn());

        Router router = getRouter();
        val server = vertx.createHttpServer(options);
        val handleRequests = server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(req -> {
                    log.error("Dropping request with status 503");
                    req.response().setStatusCode(503).end();
                })
                .observeOn(RxHelper.scheduler(new io.vertx.rxjava3.core.Context(this.context)))
                .doOnNext(req -> router.handle(req))
                .map(HttpServerRequest::resume)
                .doOnError(error -> log.error("Uncaught ERROR while handling request", error))
                .ignoreElements();

        return server
                .rxListen()
                .doOnSuccess(res -> log.info("Started http server at " + options.getPort() + " package : " + this.packageName))
                .doOnError(error -> log.error("Failed to start http server at port : " + options.getPort() + " with error " + error.getMessage()))
                .doOnSubscribe(disposable -> handleRequests.subscribe());
    }

    protected Router getRouter(){
        Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        router.route().handler(StaticHandler.create());
        router.get("/liveness").handler(ctx -> ctx.response().end("Success!"));
        var graphQlHandler = GraphQLHandler.create(setupGraphQL());
        router.route("/graphql").handler(graphQlHandler);
        GraphiQLHandlerOptions options = new GraphiQLHandlerOptions()
                .setEnabled(true);
        router.route("/graphiql/*").handler(GraphiQLHandler.create(options));
        return router;
    }

    private GraphQL setupGraphQL(){
        try(var paths = Files.walk(Paths.get("/graphqls"))){
            TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
            SchemaParser schemaParser = new SchemaParser();
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            paths.filter(path -> com.google.common.io.Files.getFileExtension(path.getFileName().toString()) == "graphqls")
                        .forEach(x -> typeDefinitionRegistry.merge(schemaParser.parse(x.toFile())));
            var runtimeWiring = newRuntimeWiring();
            AnnotationUtils.abstractDataFetcherList(this.packageName)
                    .forEach(df -> runtimeWiring.type(newTypeWiring(df.getType()).dataFetcher(df.getParameter(), df)));
            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry,runtimeWiring.build());
            return GraphQL.newGraphQL(graphQLSchema).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Completable rxStart() {
        return startHttpServer().doOnSuccess(server -> {
            this.httpServer = server;
        }).ignoreElement();
    }

    @Override
    public Completable rxStop() {
        return super.rxStop();
    }
}
