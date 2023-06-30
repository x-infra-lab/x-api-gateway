package io.github.xinfra.lab.gateway.serializer;

import reactor.core.publisher.Mono;

public interface Serializer {

    <T> Mono<byte[]> serialize(T t);

    <T> Mono<T> deserialize(byte[] data, Class<T> type);
}
