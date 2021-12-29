import com.kaustubh.vertx.commons.utils.ConfigUtils;
import config.HttpConfig;
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
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.ext.web.Router;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

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
    }

    protected Router getRouter(){
        Router router = Router.router(this.vertx);
        router.route()
    }

    private GraphQL setupGraphQL(){
        try(var paths = Files.walk(Paths.get("/schemas"))){
            TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
            SchemaParser schemaParser = new SchemaParser();
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            paths.filter(path -> com.google.common.io.Files.getFileExtension(path.getFileName().toString()) == "graphqls")
                        .forEach(x -> typeDefinitionRegistry.merge(schemaParser.parse(x.toFile())));
            var runtimeWiring = newRuntimeWiring()
                    .type(newTypeWiring("Query"));
            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry,runtimeWiring);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public Completable rxStart() {
        return super.rxStart();
    }

    private GraphQL setupGraphQL(){


    }

    @Override
    public Completable rxStop() {
        return super.rxStop();
    }
}
