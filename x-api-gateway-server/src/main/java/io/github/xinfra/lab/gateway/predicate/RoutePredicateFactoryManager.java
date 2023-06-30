package io.github.xinfra.lab.gateway.predicate;


import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class RoutePredicateFactoryManager {

    public static final RoutePredicateFactoryManager INSTANCE = new RoutePredicateFactoryManager();
    /**
     * key: Predicate Name
     */
    private Map<String, RoutePredicateFactory> factoryMap = new HashMap<>();

    public RoutePredicateFactoryManager() {
        scan(RoutePredicateFactoryManager.class.getPackage().getName());
    }


    public RoutePredicateFactory lookup(String name) {
        return factoryMap.get(name);
    }

    public void scan(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends RoutePredicateFactory>> subTypes = reflections.getSubTypesOf(RoutePredicateFactory.class);
        subTypes.forEach(clazz -> {
            try {
                RoutePredicateFactory routePredicateFactory = clazz.newInstance();
                factoryMap.put(routePredicateFactory.getName(), routePredicateFactory);
            } catch (InstantiationException | IllegalAccessException e) {
                log.warn("newInstance for class: {} fail.", clazz.getName(), e);
            }
        });
        log.info("scan RoutePredicateFactory result:{}", factoryMap);
    }
}
