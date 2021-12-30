package com.kaustubh.vertx.gql.starter;

import com.kaustubh.vertx.commons.guice.GuiceContext;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class AnnotationUtils {
    private static Reflections ref;

    public static List<Class<?>> annotatedClasses(String packageName, Class<? extends Annotation> annotation) {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        try {
            setRef(packageName);
            annotatedClasses = new ArrayList<>(ref.getTypesAnnotatedWith(annotation));
        } catch (Exception e) {
            log.error("Failed to get classes with annotation {}", annotation, e);
        }
        return annotatedClasses;
    }

    private static synchronized void setRef(String packageName) {
        if (ref == null) {
            ref = new Reflections(packageName);
        }
    }

    public static List<AbstractDataFetcher> abstractDataFetcherList(String packageName) {
        List<AbstractDataFetcher> dataFetchers = new ArrayList<>();
        var classes = AnnotationUtils.annotatedClasses(packageName, Fetcher.class);
        if (classes != null && !classes.isEmpty()) {
            classes.forEach(clazz -> {
                try {
                    Fetcher fetcherAnnotation = clazz.getAnnotation(Fetcher.class);
                    if (fetcherAnnotation != null) {
                        AbstractDataFetcher dataFetcher = (AbstractDataFetcher) GuiceContext.getInstance(clazz);
                        dataFetcher.setType(fetcherAnnotation.type());
                        dataFetcher.setParameter(fetcherAnnotation.parameter());
                        dataFetcher.setRequiredHeaders(Arrays.asList(fetcherAnnotation.requiredHeaders()));
                        dataFetcher.setConsumes(fetcherAnnotation.consumes());
                        dataFetcher.setProduces(fetcherAnnotation.produces());
                        dataFetcher.setTimeout(fetcherAnnotation.timeout());
                        dataFetchers.add(dataFetcher);
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize route", e);
                }
            });
        }
        return dataFetchers;
    }
}
