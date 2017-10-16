package com.github.clescot.directorymonitor;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.List;
import java.util.Map;

public class DirectoryMonitorConnector extends SourceConnector {

    private Map<String, String> configProperties;
    private DirectoryMonitorConnectorConfig config;

    @Override
    public String version() {
        return "0.0.1-SNAPSHOT";
    }

    @Override
    public void start(Map<String, String> properties) {
        try {
            configProperties = properties;
            config = new DirectoryMonitorConnectorConfig(configProperties);
        } catch (ConfigException e) {
            throw new ConnectException("Couldn't start JdbcSourceConnector due to configuration "
                    + "error", e);
        }

        final String dbUrl = config.getString("");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return DirectoryMonitorTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        return null;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return DirectoryMonitorConnectorConfig.CONFIG_DEF;
    }
}