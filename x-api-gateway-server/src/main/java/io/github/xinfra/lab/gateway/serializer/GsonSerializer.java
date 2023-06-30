package io.github.xinfra.lab.gateway.serializer;

import com.google.gson.Gson;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class GsonSerializer implements Serializer {

    private Gson gson = new Gson();

    @Override
    public <T> Mono<byte[]> serialize(T t) {
        return Mono.fromSupplier(() ->
                gson.toJson(t).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public <T> Mono<T> deserialize(byte[] data, Class<T> type) {
        return Mono.fromSupplier(() ->
                gson.fromJson(new String(data, StandardCharsets.UTF_8), type));
    }
}
