package io.github.xinfra.lab.gateway.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GatewayConfigManager {
    public static final String DEFAULT_CONFIG_YML_FILE_NAME = "config.yml";
    private GatewayConfig config;
    private Yaml yaml = new Yaml();

    /**
     * load default config
     */
    public void loadDefault() {
        URI uri;
        try {
            uri = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(DEFAULT_CONFIG_YML_FILE_NAME)
                    .toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("fail get default config file.", e);
        }
        load(uri.getPath());
    }

    public void load(String configPath) {
        try {
            config = yaml.loadAs(Files.newInputStream(Paths.get(configPath)), GatewayConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("fail load path:" + configPath, e);
        }
    }

    public GatewayConfig getGatewayConfig() {
        return config;
    }
}
