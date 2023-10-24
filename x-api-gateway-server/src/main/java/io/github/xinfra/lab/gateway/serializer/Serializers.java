package io.github.xinfra.lab.gateway.serializer;


public class Serializers {

    /**
     * default gsonSerializer
     */
    private static Serializer jsonSerializer = new GsonSerializer();


    public static Serializer jsonSerializer() {
        return jsonSerializer;
    }
    
}
