package com.github.clescot.directorymonitor;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class DirectoryMonitorConnectorConfig extends AbstractConfig {
    private Map<String, String> configProperties;

    public static final ConfigDef CONFIG_DEF = baseConfigDef();

    public DirectoryMonitorConnectorConfig(Map<String, String> properties) {
        super(CONFIG_DEF, properties);
        String mode = getString("");

    }


    public static ConfigDef baseConfigDef() {
        ConfigDef config = new ConfigDef();

        return config;
    }

}
