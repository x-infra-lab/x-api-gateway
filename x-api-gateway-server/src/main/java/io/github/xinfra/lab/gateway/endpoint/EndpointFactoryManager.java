package io.github.xinfra.lab.gateway.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class EndpointFactoryManager {
    private static final Map<String, EndpointFactory> factoryMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections();
        Set<Class<? extends EndpointFactory>> subTypes = reflections.getSubTypesOf(EndpointFactory.class);
        subTypes.forEach(clazz -> {
            try {
                EndpointFactory endpointFactory = clazz.newInstance();
                factoryMap.put(endpointFactory.getName(), endpointFactory);
            } catch (InstantiationException | IllegalAccessException e) {
                log.warn("newInstance for class: {} fail.", clazz.getName(), e);
            }
        });
        log.info("scan EndpointFactory result:{}", factoryMap);
    }

    public static EndpointFactory lookup(String name) {
        return factoryMap.get(name);
    }
}
