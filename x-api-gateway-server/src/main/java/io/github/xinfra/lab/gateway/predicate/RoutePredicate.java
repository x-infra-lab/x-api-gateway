package io.github.xinfra.lab.gateway.predicate;

import reactor.core.publisher.Mono;

public interface RoutePredicate<T> {

    Mono<Boolean> test(T t);


    default RoutePredicate<T> and(RoutePredicate<T> other) {
        return t ->
                Mono.defer(() ->
                        this.test(t).flatMap(
                                b -> b == true ? other.test(t) : Mono.just(false)
                        )
                );

    }

    default RoutePredicate<T> or(RoutePredicate<T> other) {
        return t ->
                Mono.defer(() ->
                        this.test(t).flatMap(
                                b -> b == true ? Mono.just(true) : other.test(t)
                        )
                );

    }

    default RoutePredicate<T> negate() {
        return t ->
                Mono.defer(() ->
                        this.test(t).flatMap(
                                b -> b == true ? Mono.just(false) : Mono.just(true)
                        )
                );

    }
}
