package com.github.clescot.directorymonitor;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.errors.ConnectException;

import java.util.Map;
import java.util.Optional;

public class DirectoryMonitorTaskConfig extends AbstractConfig {
    public static final String DIRECTORIES = "directories";
    public static final String DIRECTORY_DOC = "directory path to watch";
    public static final String POSITION = "position";
    public static final String FILE = "file";

    private static final ConfigDef CONFIG_DEF = baseConfigDef();
    public static final String TOPIC = "topic";
    public static final String TOPIC_DOC = "destination topic";

    public DirectoryMonitorTaskConfig(Map<?, ?> originals) {
        super(CONFIG_DEF, Optional.ofNullable(originals).orElseThrow(()->new ConnectException("originals is null")));
    }

    private static ConfigDef baseConfigDef(){
        ConfigDef config = new ConfigDef();
        config.define(DIRECTORIES,
                ConfigDef.Type.STRING,
                ConfigDef.Importance.HIGH,
                DIRECTORY_DOC)
        .define(TOPIC,ConfigDef.Type.STRING,
                ConfigDef.Importance.HIGH,TOPIC_DOC);
        return config;
    }
}
