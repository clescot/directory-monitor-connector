package com.github.clescot.directorymonitor;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.errors.ConnectException;

import java.util.Map;
import java.util.Optional;

public class DirectoryMonitorTaskConfig extends AbstractConfig {
    public static final String DIRECTORIES = "directory";
    public static final String DIRECTORY_DOC = "directory path to watch";
    public static final String POSITION = "position";
    public static final String FILE = "file";

    public static final String PREFIX = "prefix";
    private static final ConfigDef CONFIG_DEF = baseConfigDef();
    public DirectoryMonitorTaskConfig(Map<?, ?> originals) {
        super(CONFIG_DEF, Optional.ofNullable(originals).orElseThrow(()->new ConnectException("originals is null")));
    }

    private static ConfigDef baseConfigDef(){
        ConfigDef config = new ConfigDef();
        config.define(DIRECTORIES,
                ConfigDef.Type.LIST,
                ConfigDef.Importance.HIGH,
                DIRECTORY_DOC);
        return config;
    }
}
