package com.github.clescot.directorymonitor;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.errors.ConnectException;

import java.util.Map;
import java.util.Optional;

import static com.github.clescot.directorymonitor.DirectoryMonitorTask.CREATE_EVENT;
import static com.github.clescot.directorymonitor.DirectoryMonitorTask.DELETE_EVENT;
import static com.github.clescot.directorymonitor.DirectoryMonitorTask.MODIFY_EVENT;

public class DirectoryMonitorTaskConfig extends AbstractConfig {
    public static final String DIRECTORY = "directory";
    public static final String DIRECTORY_DOC = "directory path to watch";
    public static final String POSITION = "position";
    public static final String FILE = "file";

    public static final String PREFIX = "prefix";
    public static final String PATH_MATCHER = "pathmatcher";
    public static final String PATH_MATCHER_DOC = "define files to watch from directory";
    public static final String KINDS = "kinds";
    public static final String KINDS_DOC = "kinds represent watchEvent.Kind : it can be either '"
            +CREATE_EVENT+"', '"+MODIFY_EVENT+"', or '"+DELETE_EVENT+"' or a combination of some or all kinds";
    private static final ConfigDef CONFIG_DEF = baseConfigDef();
    public DirectoryMonitorTaskConfig(Map<?, ?> originals) {
        super(CONFIG_DEF, Optional.ofNullable(originals).orElseThrow(()->new ConnectException("originals is null")));
    }

    private static ConfigDef baseConfigDef(){
        ConfigDef config = new ConfigDef();
        config.define( DIRECTORY,
                ConfigDef.Type.STRING,
                ConfigDef.Importance.HIGH,
                DIRECTORY_DOC)
        .define(PATH_MATCHER, ConfigDef.Type.STRING,ConfigDef.Importance.HIGH,
                PATH_MATCHER_DOC)
        .define(KINDS,ConfigDef.Type.STRING,ConfigDef.Importance.HIGH,
                KINDS_DOC);
        return config;
    }
}
