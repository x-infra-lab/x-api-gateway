package io.github.xinfra.lab.gateway.commons.binder;



public interface Binder {
    <T> T binder(Class<T> clazz, String config);
}
