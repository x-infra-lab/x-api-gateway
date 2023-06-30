package io.github.xinfra.lab.gateway.commons;

import io.github.xinfra.lab.gateway.annotations.Order;

import java.util.Comparator;
import java.util.List;

public class OrderedAwareComparator implements Comparator {
    private OrderedAwareComparator() {
    }

    public static final OrderedAwareComparator INSTANCE = new OrderedAwareComparator();

    @Override
    public int compare(Object o1, Object o2) {
        int order1 = findOrder(o1);
        int order2 = findOrder(o2);
        return Integer.compare(order1, order2);
    }

    private int findOrder(Object obj) {
        if (obj instanceof Ordered) {
            Ordered ordered = (Ordered) obj;
            return ordered.getOrder();
        }

        if (obj.getClass().isAnnotationPresent(Order.class)) {
            Order order = obj.getClass().getAnnotation(Order.class);
            return order.value();
        }

        // return default order
        return Ordered.LOWEST_PRECEDENCE;
    }

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }
}
