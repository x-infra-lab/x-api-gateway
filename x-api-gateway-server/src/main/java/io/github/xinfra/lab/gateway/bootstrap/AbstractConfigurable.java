package io.github.xinfra.lab.gateway.bootstrap;

public class AbstractConfigurable<C> implements Configurable<C> {

    private Class<C> configClass;

    public AbstractConfigurable(Class<C> configClass) {
        this.configClass = configClass;
    }

    @Override
    public Class<C> getConfigClass() {
        return this.configClass;
    }
}
