package io.github.xinfra.lab.gateway.serializer;

public class Serializers {

    private static GsonSerializer gsonSerializer = new GsonSerializer();


    public static GsonSerializer jsonSerializer() {
        // default gsonSerializer
        return gsonSerializer;
    }
}
