package com.kaustubh.vertx.gql.starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Fetcher {
    String type();
    String field();
    String produces() default "application/json";
    String consumes() default "application/json";
    String[] requiredHeaders() default {};
    long timeout() default 20_000L;
}
