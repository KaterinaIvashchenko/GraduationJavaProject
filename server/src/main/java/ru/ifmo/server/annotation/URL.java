package ru.ifmo.server.annotation;

import ru.ifmo.server.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface URL {
    HttpMethod[] method() default {HttpMethod.ANY};
    String value();
}
