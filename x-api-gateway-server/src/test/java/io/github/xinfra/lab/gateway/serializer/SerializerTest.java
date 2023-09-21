package io.github.xinfra.lab.gateway.serializer;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SerializerTest {

    @Test
    public void jsonSerializerTest1() {
        Serializer serializer = Serializers.jsonSerializer();

        Map<String, String> map = new HashMap<>();
        map.put("key", "value");

        byte[] bytes = serializer.serialize(map)
                .block();

        Assert.assertEquals("{\"key\":\"value\"}", new String(bytes));
    }

}
