package com.github.clescot.directorymonitor;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class DirectoryMonitorConnectorConfig extends AbstractConfig {
    public static final String DIRECTORIES = DirectoryMonitorTaskConfig.DIRECTORIES;
    public static final String DIRECTORIES_DOC = "directories_doc";

    public static final ConfigDef CONFIG_DEF = baseConfigDef();

    public DirectoryMonitorConnectorConfig(Map<String, String> properties) {
        super(CONFIG_DEF, properties);

    }


    public static ConfigDef baseConfigDef() {
        ConfigDef config = new ConfigDef();
        return config//
                .define(DIRECTORIES, ConfigDef.Type.LIST, ConfigDef.Importance.HIGH, DIRECTORIES_DOC);
    }

}
