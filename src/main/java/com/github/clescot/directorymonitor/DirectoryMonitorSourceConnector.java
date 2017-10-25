package com.github.clescot.directorymonitor;

import com.sun.deploy.util.StringUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;

import java.util.*;

public class DirectoryMonitorSourceConnector extends SourceConnector {


    private Map<String, String> configProperties;
    private DirectoryMonitorConnectorConfig config;

    @Override
    public String version() {
        return "0.0.1-SNAPSHOT";
    }

    @Override
    public void start(Map<String, String> properties) {
        if(properties==null) {
          throw new IllegalArgumentException("properties cannot be null");
        }
            configProperties = properties;
            config = new DirectoryMonitorConnectorConfig(configProperties);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return DirectoryMonitorTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        if(config==null){
            throw new IllegalStateException("config is null. start method need to be called before taskConfigs");
        }
        if(maxTasks<=0){
            throw new IllegalArgumentException("maxTasks need to be positive");
        }
        final Optional<String> directoriesConfig = Optional.ofNullable(config.getString(DirectoryMonitorConnectorConfig.DIRECTORIES));
        if(!directoriesConfig.isPresent()){
            throw new IllegalArgumentException(DirectoryMonitorConnectorConfig.DIRECTORIES +"parameter is required");
        }
        List<String> directories = Arrays.asList((directoriesConfig.get()).split(";"));
            int numGroups = Math.min(directories.size(), maxTasks);
            List<List<String>> diretoriesGrouped = ConnectorUtils.groupPartitions(directories, numGroups);
            List<Map<String, String>> taskConfigs = new ArrayList<>(diretoriesGrouped.size());
            for (List<String> taskDirectories : diretoriesGrouped) {
                Map<String, String> taskProps = new HashMap<>(configProperties);
                taskProps.put(DirectoryMonitorTaskConfig.DIRECTORIES,
                        StringUtils.join(taskDirectories, DirectoryMonitorTask.DIRECTORY_SEPARATOR));
                taskConfigs.add(taskProps);
            }
            return taskConfigs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return DirectoryMonitorConnectorConfig.CONFIG_DEF;
    }
}