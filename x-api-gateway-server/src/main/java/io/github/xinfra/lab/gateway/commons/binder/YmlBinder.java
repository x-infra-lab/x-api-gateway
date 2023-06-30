package io.github.xinfra.lab.gateway.commons.binder;

import org.yaml.snakeyaml.Yaml;

public class YmlBinder implements Binder {

    private Yaml yaml;

    public YmlBinder() {
        this.yaml = new Yaml();
    }

    @Override
    public <T> T binder(Class<T> clazz, String config) {
        return yaml.loadAs(config, clazz);
    }

}
