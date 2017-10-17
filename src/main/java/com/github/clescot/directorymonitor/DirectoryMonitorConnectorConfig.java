package com.github.clescot.directorymonitor;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class DirectoryMonitorConnectorConfig extends AbstractConfig {
    public static final String PATTERNS = "patterns";
    public static final String PATTERNS_DOC = "patterns";

    public static final ConfigDef CONFIG_DEF = baseConfigDef();

    public DirectoryMonitorConnectorConfig(Map<String, String> properties) {
        super(CONFIG_DEF, properties);

    }


    public static ConfigDef baseConfigDef() {
        ConfigDef config = new ConfigDef();
        return config//
                .define(PATTERNS, ConfigDef.Type.LIST, ConfigDef.Importance.HIGH,PATTERNS_DOC);
    }

}
