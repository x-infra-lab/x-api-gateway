package io.github.xinfra.lab.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class GatewayFilterFactoryManager {

    private static final Map<String, GatewayFilterFactory> factoryMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections();
        Set<Class<? extends GatewayFilterFactory>> subTypes = reflections.getSubTypesOf(GatewayFilterFactory.class);
        subTypes.forEach(clazz -> {
            try {
                GatewayFilterFactory gatewayFilterFactory = clazz.newInstance();
                factoryMap.put(gatewayFilterFactory.getName(), gatewayFilterFactory);
            } catch (InstantiationException | IllegalAccessException e) {
                log.warn("newInstance for class: {} fail.", clazz.getName(), e);
            }
        });
        log.info("scan GatewayFilterFactory result:{}", factoryMap);
    }

    public static GatewayFilterFactory lookup(String name) {
        return factoryMap.get(name);
    }
}
