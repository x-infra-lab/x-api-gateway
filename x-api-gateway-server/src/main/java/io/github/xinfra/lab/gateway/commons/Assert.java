package io.github.xinfra.lab.gateway.commons;

import java.util.Collection;

public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
