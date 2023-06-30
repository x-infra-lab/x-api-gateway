package io.github.xinfra.lab.gateway.annotations;

import io.github.xinfra.lab.gateway.commons.Ordered;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Order {
    int value() default Ordered.LOWEST_PRECEDENCE;
}
