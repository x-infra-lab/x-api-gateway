package io.github.xinfra.lab.gateway.serializer;

import io.github.xinfra.lab.gateway.exception.ErrorCode;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class SerializerTest {

    @Test
    public void gsonSerializerTest(){
        GsonSerializer serializer = Serializers.jsonSerializer();

        serializer.serialize(ErrorCode.SYSTEM_EXCEPTION)
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .doOnNext(s -> System.out.println(s));
    }
}
