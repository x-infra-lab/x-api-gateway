package io.github.xinfra.lab.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class GatewayFilterFactoryManager {

    public static final GatewayFilterFactoryManager INSTANCE = new GatewayFilterFactoryManager();

    private Map<String, GatewayFilterFactory> factoryMap = new HashMap<>();

    public GatewayFilterFactoryManager() {
        scan(GatewayFilterFactoryManager.class.getPackage().getName());
    }


    public GatewayFilterFactory lookup(String name) {
        return factoryMap.get(name);
    }

    public void scan(String packageName) {
        Reflections reflections = new Reflections(packageName);
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
}
